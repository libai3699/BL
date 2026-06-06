<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import { getFinanceProduct, applyFinance, getFinanceRecord, appendFinance } from '@/apis/finance';
import { getAccountBalance } from '@/utils/account';

const { t } = useI18n();

// 当前Tab: product-产品, myFinance-我的理财
const currentTab = ref('product');
// 产品列表
const productList = ref([]);
// 我的理财列表
const myFinanceList = ref([]);
// 加载状态
const loading = ref(false);
// 分页
const pageNum = ref(1);
const pageSize = ref(10);
const hasMore = ref(true);

// 显示弹窗
const showModal = ref(false);
const modalType = ref('buy'); // 'buy' 或 'append'
const selectedProduct = ref(null);
const selectedFinance = ref(null);
const inputAmount = ref('');

// 账户信息
const accountInfo = ref({
    availableBalance: 0,
    currency: 'MXN',
    stockType: 'mxg'
});

// 获取产品列表
const fetchProductList = async () => {
    try {
        loading.value = true;
        const res = await getFinanceProduct(
            { type: 'mxg' }
        );
        if (res && Array.isArray(res)) {
            productList.value = res;
        }
    } catch (error) {
        console.error('获取产品列表失败', error);
    } finally {
        loading.value = false;
    }
};

// 获取我的理财列表
const fetchMyFinanceList = async (loadMore = false) => {
    if (loading.value) return;

    try {
        loading.value = true;
        const currentPage = loadMore ? pageNum.value + 1 : 1;

        const res = await getFinanceRecord({
            pageNum: currentPage,
            pageSize: pageSize.value,
            startTime: '',
            endTime: ''
        });

        if (res && Array.isArray(res.list)) {
            if (loadMore) {
                myFinanceList.value = [...myFinanceList.value, ...res.data.list];
            } else {
                myFinanceList.value = res.list;
            }
            pageNum.value = currentPage;
            hasMore.value = res.nextPage;
        }
    } catch (error) {
        console.error('获取理财记录失败', error);
    } finally {
        loading.value = false;
    }
};

// 切换Tab
const switchTab = (tab) => {
    if (currentTab.value === tab) return;
    currentTab.value = tab;
    pageNum.value = 1;
    hasMore.value = true;

    if (tab === 'myFinance' && myFinanceList.value.length === 0) {
        fetchMyFinanceList();
    }
};

// 打开购买弹窗
const openBuyModal = async (product) => {
    modalType.value = 'buy';
    selectedProduct.value = product;
    selectedFinance.value = null;
    inputAmount.value = '';

    // 获取对应账户余额
    const stockType = product.type === 'mxg' ? 'mxg' : 'us';
    const balance = await getAccountBalance(stockType);
    accountInfo.value = balance;

    showModal.value = true;
};

// 关闭弹窗
const closeModal = () => {
    showModal.value = false;
    selectedProduct.value = null;
    selectedFinance.value = null;
    inputAmount.value = '';
};

// 全部金额
const setMaxAmount = () => {
    inputAmount.value = String(Number(accountInfo.value.availableBalance).toFixed(2));
};

// 确认操作
const confirmAction = async () => {
    if (!inputAmount.value || parseFloat(inputAmount.value) <= 0) {
        uni.showToast({
            title: t('finance.enterAmount'),
            icon: 'none'
        });
        return;
    }

    const amount = parseFloat(inputAmount.value);

    // 购买时检查最低金额
    if (modalType.value === 'buy' && amount < selectedProduct.value.minAmount) {
        uni.showToast({
            title: t('finance.minAmountTip', { amount: selectedProduct.value.minAmount }),
            icon: 'none'
        });
        return;
    }

    if (modalType.value === 'buy') {
        // 购买
        await applyFinance({
            amount,
            productId: selectedProduct.value.id
        });
        uni.showToast({
            title: t('finance.buySuccess'),
            icon: 'success'
        });
        closeModal();
        fetchProductList();
    } else {
        // 追加
        await appendFinance({
            amount,
            orderNo: selectedFinance.value.orderNo
        });
        uni.showToast({
            title: t('finance.appendSuccess'),
            icon: 'success'
        });
        closeModal();
        fetchMyFinanceList();
    }
};

// 跳转到记录页面
const goToRecord = (orderNo, productName) => {
    uni.navigateTo({
        url: `/pages/finance/record?orderNo=${orderNo}&productName=${encodeURIComponent(productName)}`
    });
};

// 打开追加弹窗
const openAppendModal = async (finance) => {
    modalType.value = 'append';
    selectedFinance.value = finance;
    selectedProduct.value = null;
    inputAmount.value = '';

    // 获取对应账户余额
    const stockType = finance.type === 'mxg' ? 'mxg' : 'us';
    const balance = await getAccountBalance(stockType);
    accountInfo.value = balance;

    showModal.value = true;
};

// 格式化理财状态
const formatFinanceStatus = (status) => {
    const statusMap = {
        0: t('finance.statusInProgress'),
        1: t('finance.statusEnded'),
        2: t('finance.statusSettled')
    };
    return statusMap[status] || '';
};

// 格式化理财状态颜色
const getFinanceStatusColor = (status) => {
    const colorMap = {
        0: '#FF3B30',
        1: '#999',
        2: '#34C759'
    };
    return colorMap[status] || '#999';
};

// 下拉刷新
const onRefresh = async () => {
    pageNum.value = 1;
    hasMore.value = true;
    if (currentTab.value === 'product') {
        await fetchProductList();
    } else {
        await fetchMyFinanceList();
    }
};

// 触底加载
const onLoadMore = () => {
    if (!hasMore.value || loading.value) return;
    if (currentTab.value === 'myFinance') {
        fetchMyFinanceList(true);
    }
};

onLoad(() => {
    fetchProductList();
});
</script>

<template>
    <page-wrapper>
        <view class="finance-page">
            <!-- 导航栏 -->
            <nav-bar :title="$t('finance.title')" :show-back="true" />

            <!-- Tab切换 -->
            <view class="tabs">
                <view class="tab-item" :class="{ active: currentTab === 'product' }" @click="switchTab('product')">
                    {{ $t('finance.product') }}
                </view>
                <view class="tab-item" :class="{ active: currentTab === 'myFinance' }" @click="switchTab('myFinance')">
                    {{ $t('finance.myFinance') }}
                </view>
            </view>

            <!-- 产品列表 -->
            <scroll-view v-if="currentTab === 'product'" class="content-scroll" scroll-y refresher-enabled
                :refresher-triggered="loading" @refresherrefresh="onRefresh" @scrolltolower="onLoadMore">
                <view class="product-list">
                    <view v-for="item in productList" :key="item.id" class="product-card" @click="openBuyModal(item)">
                        <view class="product-header">
                            <view class="product-type">{{ item.productName || '' }}</view>
                            <!-- <view class="product-type">{{ item.type === 'mxg' ? 'MXN' : 'USD' }}</view> -->
                            <view class="buy-btn">{{ $t('finance.buy') }}</view>
                        </view>
                        <view class="product-info">
                            <text class="info-label">{{ $t('finance.period') }}：</text>
                            <text class="info-value">{{ item.period }}{{ $t('common.day') }}</text>
                            <text class="info-label ml">{{ $t('finance.rate') }}：</text>
                            <text class="info-value">{{ item.rateMin }}%</text>
                            <!-- <text class="info-label ml">{{ $t('finance.ratio') }}：</text>
                            <text class="info-value">{{ item.ratio }}%</text> -->
                            <text class="info-label ml">{{ item.minAmount }}{{ item.type === 'mxg' ? 'MXN' : 'USD' }}{{
                                $t('finance.minAmount') }}</text>
                        </view>
                    </view>
                </view>

                <no-data v-if="!loading && productList.length === 0" />
            </scroll-view>

            <!-- 我的理财列表 -->
            <scroll-view v-if="currentTab === 'myFinance'" class="content-scroll" scroll-y refresher-enabled
                :refresher-triggered="loading" @refresherrefresh="onRefresh" @scrolltolower="onLoadMore">
                <view class="my-finance-list">
                    <view v-for="item in myFinanceList" :key="item.id" class="finance-card">
                        <view class="finance-header">
                            <view class="product-type">{{ item.productName || '' }}</view>
                            <!-- <view class="product-type">{{ item.type === 'mxg' ? 'MXN' : 'USD' }}</view> -->
                            <view class="product-status" :style="{ color: getFinanceStatusColor(item.status) }">
                                {{ formatFinanceStatus(item.status) }}
                            </view>
                        </view>

                        <view class="finance-info">
                            <view class="info-row">
                                <view class="info-item">
                                    <text class="info-label">{{ $t('finance.period') }}：</text>
                                    <text class="info-value">{{ item.period }}{{ $t('common.day') }}</text>
                                </view>
                                <view class="info-item">
                                    <text class="info-label">{{ $t('finance.rate') }}：</text>
                                    <text class="info-value">{{ item.rateMin }}%</text>
                                </view>
                            </view>
                            <view class="info-row">
                                <view class="info-item">
                                    <text class="info-label">{{ $t('finance.investAmount') }}：</text>
                                    <text class="info-value">{{ item.investAmount || 0 }}</text>
                                </view>
                            </view>
                            <view class="info-row">
                                <view class="info-item">
                                    <text class="info-label">{{ $t('finance.appendAmount') }}：</text>
                                    <text class="info-value">{{ item.appendAmount || 0 }}</text>
                                </view>
                            </view>
                        </view>

                        <view class="finance-times">
                            <view class="finance-time">
                                <text class="time-label">{{ $t('finance.buyTime') }}：</text>
                                <text class="time-value">{{ item.createTime }}</text>
                            </view>
                            <view class="finance-time">
                                <text class="time-label">{{ $t('finance.stopTime') }}：</text>
                                <text class="time-value">{{ item.settlementTime || '--' }}</text>
                            </view>
                        </view>

                        <view class="finance-footer">
                            <view class="action-btn append-btn" v-if="item.status !== 2"
                                @click.stop="openAppendModal(item)">
                                {{ $t('finance.append') }}
                            </view>

                            <view class="action-btn record-btn"
                                @click.stop="goToRecord(item.orderNo, item.productName)">
                                {{ $t('finance.record') }}
                            </view>
                        </view>
                    </view>
                </view>

                <no-data v-if="!loading && myFinanceList.length === 0" />

                <view v-if="!hasMore && myFinanceList.length > 0" class="footer-tip">
                    {{ $t('common.noMore') }}
                </view>
            </scroll-view>
        </view>

        <!-- 购买/追加弹窗 -->
        <u-popup :show="showModal" v-if="showModal" mode="center" :z-index="20" :round="16" @close="closeModal">
            <view class="buy-modal">
                <view class="modal-title">{{ modalType === 'buy' ? $t('finance.buy') : $t('finance.append') }}</view>

                <view class="input-group">
                    <text class="input-label">{{ $t('finance.availableBalance') }}：</text>
                    <text class="input-value">{{ accountInfo.availableBalance.toLocaleString('en-US', {
                        minimumFractionDigits: 2, maximumFractionDigits: 2 }) }} {{ accountInfo.currency }}</text>
                </view>

                <view class="input-group">
                    <text class="input-label">{{ modalType === 'buy' ? $t('finance.buyAmount') :
                        $t('finance.appendAmount') }}：</text>
                    <view class="input-wrapper">
                        <input v-model="inputAmount" type="digit" class="amount-input"
                            :placeholder="modalType === 'buy' ? $t('finance.enterBuyAmount') : $t('finance.enterAppendAmount')" />
                        <text class="all-btn" @click="setMaxAmount">{{ $t('finance.all') }}</text>
                    </view>
                </view>

                <view class="modal-actions">
                    <view class="action-btn cancel-btn" @click="closeModal">
                        {{ $t('common.cancel') }}
                    </view>
                    <view class="action-btn confirm-btn" @click="confirmAction">
                        {{ $t('finance.confirm') }}
                    </view>
                </view>
            </view>
        </u-popup>
    </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.finance-page {
    @include hasNavBar();
}

.record-icon {
    width: 40rpx;
    height: 40rpx;
}

.tabs {
    display: flex;
    background: #fff;
    border-bottom: 2rpx solid #f0f0f0;
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 32rpx 0;
    font-size: 28rpx;
    color: #666;
    position: relative;
    cursor: pointer;
}

.tab-item.active {
    color: #333;
    font-weight: 500;
}

.tab-item.active::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 60rpx;
    height: 4rpx;
    background: #00A8FF;
    border-radius: 2rpx;
}

.product-list {
    display: flex;
    flex-direction: column;
    padding: 24rpx 32rpx;
    gap: 24rpx;
}

.product-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.product-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;
}

.product-type {
    font-size: 35rpx;
    color: #333;
    font-weight: 600;
    line-height: 1;
}

.buy-btn {
    background: transparent;
    color: #00A8FF;
    font-size: 32rpx;
    font-weight: 500;
}

.product-info {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
}

.info-label {
    font-size: 23rpx;
    color: #666;
}

.info-value {
    font-size: 20rpx;
    color: #333;
    font-weight: 500;
    margin-right: 24rpx;
}

.ml {
    margin-left: 0;
}

.footer-tip {
    text-align: center;
    padding: 32rpx 0;
    color: #999;
    font-size: 24rpx;
}

.buy-modal {
    width: 600rpx;
    background: #fff;
    border-radius: 16rpx;
    padding: 48rpx 32rpx;
}

.modal-title {
    font-size: 36rpx;
    font-weight: 500;
    text-align: center;
    margin-bottom: 48rpx;
}

.input-group {
    margin-bottom: 32rpx;
}

.input-group:first-of-type {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.input-label {
    font-size: 28rpx;
    color: #333;
    white-space: nowrap;
}

.input-value {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
}

.input-group:last-of-type .input-label {
    display: block;
    margin-bottom: 16rpx;
}

.input-wrapper {
    display: flex;
    align-items: center;
    border: 2rpx solid #E5E5E5;
    border-radius: 8rpx;
    padding: 0 24rpx;
    height: 68rpx;
    margin-top: 15rpx;
}

.amount-input {
    flex: 1;
    font-size: 28rpx;
}

.all-btn {
    color: #00A8FF;
    font-size: 28rpx;
    padding: 8rpx 16rpx;
}

.modal-actions {
    display: flex;
    gap: 24rpx;
}

.action-btn {
    flex: 1;
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 48rpx;
    font-size: 32rpx;
    font-weight: 500;
}

.cancel-btn {
    background: #F5F5F5;
    color: #666;
}

.confirm-btn {
    background: #00A8FF;
    color: #fff;
}

.my-finance-list {
    display: flex;
    flex-direction: column;
    gap: 24rpx;
    padding: 32rpx;
}

.finance-card {
    background: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.finance-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;
}

.finance-header .product-type {
    font-size: 30rpx;
    color: #fff;
    background-color: black;
    border-radius: 100rpx;
    padding: 12rpx 24rpx;
}

.product-status {
    font-size: 28rpx;
    font-weight: 500;
}

.finance-info {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
    margin-bottom: 16rpx;
}

.finance-info .info-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.finance-info .info-item {
    display: flex;
    align-items: center;
}

.finance-info .info-label {
    font-size: 24rpx;
    color: #666;
}

.finance-info .info-value {
    font-size: 24rpx;
    color: #333;
    font-weight: 500;
}

.finance-times {
    margin-top: 16rpx;
}

.finance-time {
    display: flex;
    margin-bottom: 18rpx;
}

.finance-time:last-child {
    margin-bottom: 0;
}

.time-label {
    font-size: 24rpx;
    color: #999;
}

.time-value {
    font-size: 24rpx;
    color: #666;
}

.finance-footer {
    display: flex;
    justify-content: flex-end;
    gap: 24rpx;
    margin-top: 24rpx;
}

.finance-footer .action-btn {
    flex: 1;
    height: 60rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 12rpx;
    font-size: 28rpx;
    font-weight: 500;
}

.append-btn {
    background: #00A8FF;
    color: #fff;
}

.record-btn {
    background: #F5F5F5;
    color: #333;
    max-width: 50%;
}
</style>
