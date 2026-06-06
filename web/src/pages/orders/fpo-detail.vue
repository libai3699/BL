<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { onLoad, onShow, onHide } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack } from '@/utils/navigate';
import { getPositionDetail } from '@/apis/order';
import { checkAndConnectWebSocket } from '@/utils/websocket';

const { t } = useI18n();
const commonStore = useCommonStore();
const userStore = useUserStore();

const fpoDetail = ref(null);
const detailType = ref('');
const positionSn = ref('');
const loading = ref(false);

// 判断是否是持仓中状态（可以动态更新）
const isHoldingPosition = computed(() => {
    return detailType.value === 'position' && fpoDetail.value?.positionStatus === 0;
});

const getStatusText = (status) => {
    // 如果是持仓类型，根据 positionStatus 判断是持仓中还是历史持仓
    if (detailType.value === 'position') {
        // positionStatus: 0=持仓中，1=已平仓（历史交易）
        return fpoDetail.value?.positionStatus === 1 ? t('order.historyTab') : t('order.holding');
    }

    const statusMap = {
        1: t('order.fpoWaitWinning'),
        2: detailType.value === 'ipo' ? t('order.ipoPendingReview') : t('order.fpoNotWinning'),
        3: detailType.value === 'ipo' ? t('order.ipoPendingPayment') : t('order.fpoNotWinning'),
        4: t('order.fpoWaitTransfer'),
        5: t('order.fpoTransferred'),
        6: t('common.cancel')
    };
    return statusMap[status] || t('order.fpoNotWinning');
};

const getPageTitle = () => {
    if (detailType.value === 'ipo') return 'IPO详情';
    if (detailType.value === 'fpo') return t('order.fpoDetail');
    if (detailType.value === 'position') {
        // positionStatus: 0=持仓中，1=已平仓（历史交易）
        return fpoDetail.value?.positionStatus === 1 ? t('order.positionHistory') : t('order.positionDetail');
    }
    if (detailType.value === 'bulk') return t('bulk.detail');
    return t('common.detail');
};

const getTypeLabel = () => {
    if (detailType.value === 'ipo') return 'IPO';
    if (detailType.value === 'fpo') return 'FPO';
    if (detailType.value === 'position') return t('order.position');
    if (detailType.value === 'bulk') return t('bulk.bulkTrading');
    return '';
};

// 复制功能
const handleCopy = (text) => {
    if (!text) {
        uni.showToast({
            title: t('error.system'),
            icon: 'none'
        });
        return;
    }

    uni.setClipboardData({
        data: String(text),
        success: () => {
            uni.showToast({
                title: t('common.copy') + t('common.ensure'),
                icon: 'success',
                duration: 1500
            });
        },
        fail: () => {
            uni.showToast({
                title: t('error.system'),
                icon: 'none'
            });
        }
    });
};

// 获取持仓详情
const fetchPositionDetail = async () => {
    if (!positionSn.value) {
        console.error('缺少持仓编号');
        return;
    }

    loading.value = true;
    loading.value = true;

    const res = await getPositionDetail({ positionSn: positionSn.value });

    if (res) {
        fpoDetail.value = res;

        fpoDetail.value = {
            ...fpoDetail.value,
            profitRate: (
                parseFloat((Number(fpoDetail.value.profitAndLoseRate)).toFixed(4)) * 100
            ).toFixed(2) + '%',
        };
        // 获取详情后，设置 WebSocket 监听
        setupWebSocketListener();
    } else {
        uni.showToast({
            title: t('error.system'),
            icon: 'none'
        });
    }
    loading.value = false;
};

onLoad((options) => {
    console.log("页面参数：", options);

    if (options.type) {
        detailType.value = options.type;
    }

    // 统一使用 positionSn 获取详情
    if (options.positionSn) {
        positionSn.value = options.positionSn;
        fetchPositionDetail();
    } else if (options.item) {
        // 兼容 FPO/IPO 等非持仓类型（这些没有 positionSn）
        try {
            const itemData = JSON.parse(options.item);
            console.log("解析的数据：", itemData);
            fpoDetail.value = itemData;
        } catch (error) {
            console.error('解析数据失败', error);
            uni.showToast({
                title: t('error.system'),
                icon: 'none'
            });
        }
    } else {
        uni.showToast({
            title: t('error.system'),
            icon: 'none'
        });
        setTimeout(() => {
            uni.navigateBack();
        }, 1500);
    }
});

// 当前监听的 WebSocket 事件名称
const currentWsEvent = ref('');

// WebSocket消息处理
const handleStockMessage = (data) => {
    // 只在持仓中状态处理消息
    if (!isHoldingPosition.value || !fpoDetail.value) {
        return;
    }

    const stockId = data.pid || data.gid || data.code;
    if (!stockId) return;

    // 检查是否是当前股票
    if (stockId !== fpoDetail.value.stockCode && stockId !== fpoDetail.value.gid) {
        return;
    }

    const newPrice = data.last_numeric;
    if (!newPrice || newPrice === fpoDetail.value.now_price) return;

    // 更新当前价格
    const quantity = fpoDetail.value.orderNum || 0;
    const buyPrice = fpoDetail.value.buyOrderPrice || 0;
    const buyTotalPrice = fpoDetail.value.buyTotalPrice || 0;

    // 计算新的市值
    const currentValue = newPrice * quantity;

    // 根据买卖方向计算盈亏
    // 买: (现价 - 买入价) * 数量
    // 卖: (买入价 - 现价) * 数量
    let profitAndLose = 0;
    if (fpoDetail.value.orderDirection === 0) { // 买
        profitAndLose = (newPrice - buyPrice) * quantity;
    } else { // 卖
        profitAndLose = (buyPrice - newPrice) * quantity;
    }

    let profitRate = '--';
    const buyTotal = buyPrice * quantity;
    profitRate = buyTotal > 0 ? (parseFloat((profitAndLose / buyTotal).toFixed(4)) * 100).toFixed(2) + '%' : '--';

    // 更新数据
    fpoDetail.value = {
        ...fpoDetail.value,
        now_price: newPrice,
        profitRate: profitRate,
        profitAndLose: profitAndLose
    };
};

// 设置 WebSocket 监听
const setupWebSocketListener = () => {
    // 先移除之前的监听
    if (currentWsEvent.value) {
        uni.$off(currentWsEvent.value, handleStockMessage);
        currentWsEvent.value = '';
    }

    // 只在持仓中状态且有数据时监听
    if (!isHoldingPosition.value || !fpoDetail.value) {
        return;
    }

    // 检查并连接 WebSocket，连接成功后设置监听
    checkAndConnectWebSocket(userStore, setupWebSocketListenerInternal, 1000);
};

// 内部方法：设置 WebSocket 监听
const setupWebSocketListenerInternal = () => {
    if (!isHoldingPosition.value || !fpoDetail.value) {
        return;
    }

    // 根据股票类型确定监听哪个 WebSocket
    // stockType: 'mxg' = 墨西哥股票, 'us' = 美股
    const stockType = fpoDetail.value.stockType || 'mxg';
    currentWsEvent.value = stockType === 'us' ? 'ws:us' : 'ws:mexico';

    // 监听对应的 WebSocket 消息
    uni.$on(currentWsEvent.value, handleStockMessage);
    console.log(`✅ 开始监听 ${currentWsEvent.value} 消息，股票代码: ${fpoDetail.value.stockCode}`);
};

// 移除 WebSocket 监听
const removeWebSocketListener = () => {
    if (currentWsEvent.value) {
        uni.$off(currentWsEvent.value, handleStockMessage);
        console.log(`停止监听 ${currentWsEvent.value} 消息`);
        currentWsEvent.value = '';
    }
};

onMounted(() => {
    commonStore.fnBack = goBack;
});

// 页面隐藏时移除监听
onHide(() => {
    removeWebSocketListener();
});

// 组件卸载时也清理
onUnmounted(() => {
    removeWebSocketListener();
});
</script>

<template>
    <page-wrapper>
        <nav-bar :title="getPageTitle()" />
        <view class="m-content">
            <view class="fpo-detail-container" v-if="fpoDetail">
                <view class="status-section">
                    <image class="status-icon" src="/static/svgs/weizhongqian.svg" mode="aspectFit" />
                    <text class="status-text">{{ getStatusText(fpoDetail.status) }}</text>
                </view>

                <view class="info-section">
                    <template v-if="detailType === 'position'">
                        <!-- 订单号 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.orderNo') }}</text>
                            <view class="info-value-with-copy">
                                <text class="info-value">{{ fpoDetail.positionSn || '--' }}</text>
                                <image v-if="fpoDetail.positionSn" class="copy-icon" src="/static/svgs/copy.svg"
                                    mode="aspectFit" @click="handleCopy(fpoDetail.positionSn)" />
                            </view>
                        </view>
                        <!-- 创建时间 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.createTime') }}</text>
                            <text class="info-value">{{ fpoDetail.buyOrderTime || '--' }}</text>
                        </view>
                        <!-- 平仓时间（仅历史持仓显示） -->
                        <view class="info-row" v-if="fpoDetail.positionStatus === 1">
                            <text class="info-label">{{ $t('order.closeTime') }}</text>
                            <text class="info-value">{{ fpoDetail.sellTime || '--' }}</text>
                        </view>
                        <!-- 买卖方向 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.tradeType') }}</text>
                            <text class="info-value"
                                :style="{ color: fpoDetail.orderDirection === 0 ? '#009f6a' : '#ff4d4f' }">
                                {{ fpoDetail.orderDirection === 0 ? t('order.buy') : t('order.sell') }}
                            </text>
                        </view>
                        <!-- 数量 -->
                        <view class="info-row"
                            style="border-bottom: 2rpx solid #f0f0f0; padding-bottom: 16rpx; margin-bottom: 16rpx;">
                            <text class="info-label">{{ $t('order.quantity') }}</text>
                            <text class="info-value">{{ fpoDetail.orderNum }}</text>
                        </view>

                        <!-- 买入价格 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.buyPrice') }}</text>
                            <view class="info-value">
                                <c-amount :value="fpoDetail.buyOrderPrice" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                        <!-- 买入资金 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.buyAmount') }}</text>
                            <view class="info-value">
                                <c-amount :value="fpoDetail.buyTotalPrice" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                        <!-- 当前价格（仅持仓中显示） -->
                        <view class="info-row" v-if="fpoDetail.positionStatus === 0">
                            <text class="info-label">{{ $t('order.currentPrice') }}</text>
                            <view class="info-value">
                                <c-amount :value="fpoDetail.now_price" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                        <!-- 市值（仅持仓中显示） -->
                        <view class="info-row" v-if="fpoDetail.positionStatus === 0">
                            <text class="info-label">{{ $t('order.marketValue') }}</text>
                            <view class="info-value">
                                <c-amount :value="(fpoDetail.now_price || 0) * (fpoDetail.orderNum || 0)" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                        <!-- 平仓价（仅历史持仓显示） -->
                        <view class="info-row" v-if="fpoDetail.positionStatus === 1">
                            <text class="info-label">{{ $t('order.closePrice') }}</text>
                            <view class="info-value">
                                <c-amount :value="fpoDetail.sellOrderPrice" defaultValue="--" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                        <!-- 盈亏 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.profit') }}</text>
                            <view class="info-value"
                                :style="{ color: fpoDetail.profitAndLose >= 0 ? '#009f6a' : '#ff4d4f' }">
                                <text>{{ fpoDetail.profitAndLose >= 0 ? '+' : '' }}</text>
                                <c-amount :value="fpoDetail.profitAndLose" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                        <!-- 盈亏比 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.profitRate') }}</text>
                            <text class="info-value"
                                :style="{ color: fpoDetail.profitAndLose >= 0 ? '#009f6a' : '#ff4d4f' }">
                                {{ fpoDetail.profitRate }}
                            </text>
                        </view>
                        <!-- 止盈/止损 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.stopOrLimit') }}</text>
                            <view class="info-value">
                                <c-amount :value="fpoDetail.profitTargetPrice" defaultValue="0" />
                                <text> {{ fpoDetail.currency }}/</text>
                                <c-amount :value="fpoDetail.stopTargetPrice" defaultValue="0" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                        <!-- 交易费用 -->
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.tradeFee') }}</text>
                            <view class="info-value">
                                <c-amount :value="fpoDetail.orderFee" decimals="5" />
                                <text> {{ fpoDetail.currency }}</text>
                            </view>
                        </view>
                    </template>

                    <template v-else-if="detailType === 'bulk'">
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoType') }}</text>
                            <text class="info-value">{{ getTypeLabel() }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoStockName') }}</text>
                            <text class="info-value">{{ fpoDetail.stockName }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('bulk.orderStatus') }}</text>
                            <text class="info-value">{{ fpoDetail.orderDirection === 0 ? $t('bulk.success') :
                                $t('bulk.pending') }}</text>
                        </view>
                        <view class="info-row"
                            style="border-bottom: 2rpx solid #f0f0f0; padding-bottom: 16rpx; margin-bottom: 16rpx;">
                            <text class="info-label">{{ $t('bulk.applyNum') }}</text>
                            <text class="info-value">{{ fpoDetail.orderNum }} {{ $t('ipo.shareUnit') }}</text>
                        </view>
                        <view class="info-row" v-if="fpoDetail.state === 1">
                            <text class="info-label">{{ $t('bulk.marketPrice') }}</text>
                            <view class="info-value"><c-amount :value="fpoDetail.orderTotalPrice" /></view>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('bulk.discountPrice') }}</text>
                            <view class="info-value"><c-amount :value="fpoDetail.buyOrderPrice" /></view>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('bulk.discount') }}</text>
                            <view class="info-value">
                                <c-amount :value="fpoDetail.buyDiscount ? Number(fpoDetail.buyDiscount * 100) : 0" />
                                <text>%</text>
                            </view>
                        </view>
                        <view class="info-row" v-if="fpoDetail.fee !== undefined">
                            <text class="info-label">{{ $t('bulk.fee') }}</text>
                            <view class="info-value"><c-amount :value="fpoDetail.fee" decimals="5" /></view>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('bulk.orderTime') }}</text>
                            <text class="info-value">{{ fpoDetail.buyOrderTime || fpoDetail.listTime || '--' }}</text>
                        </view>
                    </template>

                    <template v-else>
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoType') }}</text>
                            <text class="info-value">{{ getTypeLabel() }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoStockName') }}</text>
                            <text class="info-value">{{ fpoDetail.newName }}</text>
                        </view>
                        <view class="info-row" v-if="detailType === 'ipo' && fpoDetail.orderNo">
                            <text class="info-label">{{ $t('order.orderNo') }}</text>
                            <view class="info-value-with-copy">
                                <text class="info-value">{{ fpoDetail.orderNo }}</text>
                                <image class="copy-icon" src="/static/svgs/copy.svg" mode="aspectFit"
                                    @click="handleCopy(fpoDetail.orderNo)" />
                            </view>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoApplyStatus') }}</text>
                            <text class="info-value">{{ getStatusText(fpoDetail.status) }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoApplyNum') }}</text>
                            <text class="info-value">{{ fpoDetail.applyNums }} {{ $t('ipo.shareUnit') }}</text>
                        </view>
                        <view class="info-row" v-if="detailType === 'ipo' && fpoDetail.applyNumber">
                            <text class="info-label">{{ $t('order.fpoWinningNum') }}</text>
                            <text class="info-value">{{ fpoDetail.applyNumber }} {{ $t('ipo.shareUnit') }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoIssuePrice') }}</text>
                            <view class="info-value"><c-amount :value="fpoDetail.buyPrice" /></view>
                        </view>
                        <view class="info-row" v-if="detailType === 'ipo' && fpoDetail.bond">
                            <text class="info-label">{{ $t('order.ipoBond') }}</text>
                            <view class="info-value"><c-amount :value="fpoDetail.bond" /></view>
                        </view>
                        <view class="info-row" v-if="detailType === 'ipo'">
                            <text class="info-label">{{ $t('order.ipoApplyTime') }}</text>
                            <text class="info-value">{{ fpoDetail.submitTime || '--' }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ $t('order.fpoSubscribeDate') }}</text>
                            <text class="info-value">{{ fpoDetail.subscriptionTime || '--' }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ detailType === 'ipo' ? $t('order.ipoReviewTime') :
                                $t('order.fpoWinningDate') }}</text>
                            <text class="info-value">{{ fpoDetail.endTime || '--' }}</text>
                        </view>
                        <!-- 增发认购,是没有这个的 只有增发预售,需要缴费 -->
                        <!-- <view class="info-row" v-if="detailType === 'fpo'">
                            <text class="info-label">{{ $t('order.fpoPayStartDate') }}</text>
                            <text class="info-value">{{ fpoDetail.payStartTime || '--' }}</text>
                        </view>
                        <view class="info-row" v-if="detailType === 'fpo'">
                            <text class="info-label">{{ $t('order.fpoPayEndDate') }}</text>
                            <text class="info-value">{{ fpoDetail.payEndTime || '--' }}</text>
                        </view> -->
                        <view class="info-row" v-if="detailType === 'ipo' && fpoDetail.payTime">
                            <text class="info-label">{{ $t('order.ipoPaymentDate') }}</text>
                            <text class="info-value">{{ fpoDetail.payTime }}</text>
                        </view>
                        <view class="info-row">
                            <text class="info-label">{{ detailType === 'ipo' ? $t('order.ipoTransferTime') :
                                $t('order.fpoListingDate') }}</text>
                            <text class="info-value">{{ fpoDetail.fixTime || '--' }}</text>
                        </view>
                        <view class="info-row" v-if="detailType === 'ipo' && fpoDetail.remarks">
                            <text class="info-label">{{ $t('order.remarks') }}</text>
                            <text class="info-value">{{ fpoDetail.remarks }}</text>
                        </view>
                    </template>
                </view>
            </view>

            <view class="no-data" v-if="!fpoDetail">
                <text>{{ $t('common.noData') }}</text>
            </view>
        </view>
    </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-content {
    @include hasNavBar();
}

.fpo-detail-container {
    background: #fff;
    border-radius: 16rpx;
    overflow: hidden;
    margin-top: 16rpx;
}

.status-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 48rpx 32rpx 32rpx;
    border-bottom: 2rpx solid #f0f0f0;

    .status-icon {
        width: 120rpx;
        height: 120rpx;
        margin-bottom: 24rpx;
    }

    .status-text {
        font-size: 28rpx;
        color: #666;
    }
}

.info-section {
    padding: 32rpx;
}

.info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;

    &:last-child {
        border-bottom: none;
    }

    .info-label {
        font-size: 24rpx;
        color: #999;
    }

    .info-value {
        font-size: 24rpx;
        color: #333;
        font-weight: 500;
        text-align: right;
        max-width: 60%;
        word-break: break-all;
    }

    .info-value-with-copy {
        display: flex;
        align-items: center;
        gap: 12rpx;
        flex: 1;
        justify-content: flex-end;
        max-width: 60%;
        overflow: hidden;

        .info-value {
            max-width: calc(100% - 40rpx);
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            text-align: right;
        }

        .copy-icon {
            width: 28rpx;
            height: 28rpx;
            flex-shrink: 0;
        }
    }
}

.no-data {
    text-align: center;
    padding: 200rpx 0;
    font-size: 28rpx;
    color: #999;
}
</style>
