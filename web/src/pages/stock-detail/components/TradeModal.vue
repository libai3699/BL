<script setup>
import { ref, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { getAccountBalance, getCurrency } from '@/utils/account';
import { getTradeFee } from '@/apis/order';

const { t } = useI18n();

const props = defineProps({
    visible: {
        type: Boolean,
        default: false
    },
    type: {
        type: String, // 'buy' or 'sell'
        required: true
    },
    stockInfo: {
        type: Object,
        required: true
    }
});

const emit = defineEmits(['update:visible', 'confirm']);

const quantity = ref(0); // 初始1手
const percentage = ref(0);
const enableStopProfit = ref(false);
const stopProfit = ref('');
const stopLoss = ref('');

// 账户信息
const accountInfo = ref({
    availableBalance: 0,
    currency: 'MXN',
    stockType: 'mxg'
});
const availableShares = ref(0); // 持仓数量需要从持仓接口获取

// 手续费信息
const feeInfo = ref({
    buyFee: 0,
    sellFee: 0
});

// 货币单位
const currency = computed(() => getCurrency(props.stockInfo.stockType));

// 计算实际股数（直接使用输入的数量）
const actualShares = computed(() => {
    return quantity.value;
});

// 计算总价
const totalAmount = computed(() => {
    return actualShares.value * Number(props.stockInfo.nowPrice || 0);
});

// 计算手续费
const tradeFee = computed(() => {
    const total = actualShares.value * props.stockInfo.nowPrice;
    const feeRate = props.type === 'buy' ? feeInfo.value.buyFee : feeInfo.value.sellFee;
    const fee = total * feeRate;
    console.log('手续费计算:', {
        手数: quantity.value,
        股数: actualShares.value,
        单价: props.stockInfo.nowPrice,
        总金额: total,
        手续费率: feeRate,
        手续费: fee
    });
    return fee;
});

// 计算实际需要金额（买入时包含手续费）
const actualAmount = computed(() => {
    const total = Number(totalAmount.value);
    const fee = Number(tradeFee.value);
    const actual = props.type === 'buy' ? total + fee : total;

    console.log('实际需要金额:', {
        类型: props.type,
        数量: quantity.value,
        单价: props.stockInfo.nowPrice,
        总金额: total,
        手续费: fee,
        实际需要: actual,
        余额: accountInfo.value.availableBalance,
        是否超出: actual > accountInfo.value.availableBalance
    });

    return actual;
});

// 计算可买数量
const maxBuyQuantity = computed(() => {
    // 考虑手续费的可买数量
    const price = Number(props.stockInfo.nowPrice) || 0; // 单价
    const feeRate = Number(feeInfo.value.buyFee) || 0;
    const balance = Number(accountInfo.value.availableBalance) || 0;
    const priceWithFee = price * (1 + feeRate); // 单价+手续费
    const maxQty = Math.floor(balance / priceWithFee);

    console.log('可买数量计算:', {
        余额: balance,
        单价: price,
        手续费率: feeRate,
        含手续费单价: priceWithFee,
        最大可买数量: maxQty,
        验证: maxQty * priceWithFee
    });

    return maxQty;
});

// 获取账户余额
const fetchAccountInfo = async () => {
    const info = await getAccountBalance(props.stockInfo.stockType);
    accountInfo.value = info;
};

// 获取手续费
const fetchTradeFee = async () => {
    const res = await getTradeFee({ gid: props.stockInfo.gid });
    console.log('手续费接口返回:', res);
    if (res && res.gid) {
        feeInfo.value = {
            buyFee: res.buyFee || 0,
            sellFee: res.sellFee || 0
        };
        console.log('手续费率:', feeInfo.value);
    }
};

// 监听弹窗显示，获取账户余额
watch(() => props.visible, (newVal) => {
    if (newVal) {
        fetchAccountInfo();
    }
});

// 监听stockInfo变化，获取手续费（页面加载时就获取）
watch(() => props.stockInfo.gid, (newGid) => {
    if (newGid) {
        fetchTradeFee();
    }
}, { immediate: true });

// 重置表单
const resetForm = () => {
    quantity.value = 0; // 重置为1手
    percentage.value = 0;
    enableStopProfit.value = false;
    stopProfit.value = '';
    stopLoss.value = '';
};

// 关闭弹框
const handleClose = () => {
    emit('update:visible', false);
    resetForm();
};

// 增加数量
const increaseQuantity = () => {
    // 买入和卖出都使用可买手数作为上限
    if (quantity.value < maxBuyQuantity.value) {
        quantity.value = parseFloat((parseFloat(quantity.value) + 1).toFixed(2));
    }
};

// 减少数量
const decreaseQuantity = () => {
    if (quantity.value > 1) {
        quantity.value = Math.max(1, parseFloat((parseFloat(quantity.value) - 1).toFixed(2)));
    }
};

// 设置百分比
const setPercentage = (percent) => {
    percentage.value = percent;
    const maxQty = maxBuyQuantity.value;
    const calculatedQty = Math.floor(maxQty * percent / 100);
    quantity.value = calculatedQty || 0;

    console.log('设置百分比:', {
        百分比: percent,
        最大数量: maxQty,
        计算数量: calculatedQty,
        实际设置: quantity.value
    });
};

// 监听止盈止损开关
watch(enableStopProfit, (val) => {
    if (val && !stopProfit.value) {
        stopProfit.value = props.stockInfo.nowPrice;
    }
    if (val && !stopLoss.value) {
        stopLoss.value = props.stockInfo.nowPrice;
    }
});

// 确认交易
const handleConfirm = () => {
    // 验证数量
    if (!quantity.value || quantity.value <= 0) {
        uni.showToast({
            title: t('error.invalidQuantity'),
            icon: 'none',
            duration: 2000
        });
        return;
    }

    // 验证买入数量不超过可买数量（考虑手续费）
    if (props.type === 'buy') {
        const needAmount = parseFloat(actualAmount.value);
        if (needAmount > accountInfo.value.availableBalance) {
            uni.showToast({
                title: t('trade.insufficientBalance'),
                icon: 'none',
                duration: 2000
            });
            return;
        }
    }

    // 卖出不需要验证持仓数量，允许做空

    // 验证止盈止损
    if (enableStopProfit.value) {
        const profitPrice = parseFloat(stopProfit.value);
        const lossPrice = parseFloat(stopLoss.value);
        const currentPrice = parseFloat(props.stockInfo.nowPrice);

        if (!stopProfit.value || isNaN(profitPrice) || profitPrice <= 0) {
            uni.showToast({
                title: t('error.invalidStopProfit'),
                icon: 'none',
                duration: 2000
            });
            return;
        }

        if (!stopLoss.value || isNaN(lossPrice) || lossPrice <= 0) {
            uni.showToast({
                title: t('error.invalidStopLoss'),
                icon: 'none',
                duration: 2000
            });
            return;
        }

        // 买涨：止盈价格应该大于当前价格，止损价格应该小于当前价格
        if (props.type === 'buy') {
            if (profitPrice <= currentPrice) {
                uni.showToast({
                    title: t('error.stopProfitTooLow'),
                    icon: 'none',
                    duration: 2000
                });
                return;
            }
            if (lossPrice >= currentPrice) {
                uni.showToast({
                    title: t('error.stopLossTooHigh'),
                    icon: 'none',
                    duration: 2000
                });
                return;
            }
        }

        // 买跌：止盈价格应该小于当前价格，止损价格应该大于当前价格
        if (props.type === 'sell') {
            if (profitPrice >= currentPrice) {
                uni.showToast({
                    title: t('error.stopProfitTooHigh'),
                    icon: 'none',
                    duration: 2000
                });
                return;
            }
            if (lossPrice <= currentPrice) {
                uni.showToast({
                    title: t('error.stopLossTooLow'),
                    icon: 'none',
                    duration: 2000
                });
                return;
            }
        }
    }

    const data = {
        gid: props.stockInfo.gid,
        buyNum: actualShares.value, // 提交实际股数
        buyType: props.type === 'buy' ? 0 : 1,
        profitTarget: enableStopProfit.value ? parseFloat(stopProfit.value) : null,
        stopTarget: enableStopProfit.value ? parseFloat(stopLoss.value) : null
    };
    emit('confirm', data);
};
</script>

<template>
    <view class="trade-modal" v-if="visible" @click="handleClose">
        <view class="modal-content" @click.stop>
            <!-- 标题 -->
            <view class="modal-header">
                <text class="modal-title">{{ type === 'buy' ? $t('trade.buy') : $t('trade.sell') }}</text>
                <text class="modal-close" @click="handleClose">×</text>
            </view>

            <view class="modal-body">
                <!-- 股票信息 -->
                <view class="stock-info">
                    <text class="stock-name">{{ stockInfo.name }}</text>
                    <text class="stock-code">{{ stockInfo.spell }}</text>
                </view>

                <!-- 当前价格 -->
                <view class="price-row">
                    <text class="price-label">{{ $t('trade.currentPrice') }}</text>
                    <view class="price-value-group">
                        <view class="price-value"><c-amount :value="stockInfo.nowPrice" /></view>
                        <text class="price-currency">{{ currency }}</text>
                    </view>
                </view>

                <!-- 涨跌幅 -->
                <view class="change-row">
                    <text class="change-label">{{ $t('trade.change') }}</text>
                    <text class="change-value" :style="{ color: stockInfo.hcrate >= 0 ? '#00C087' : '#FF4D4F' }">
                        {{ stockInfo.hcrate >= 0 ? '+' : '' }}{{ Number(stockInfo.hcrate || 0).toFixed(2) }}%
                    </text>
                </view>

                <!-- 数量选择 -->
                <view class="quantity-section">
                    <text class="quantity-label">数量</text>
                    <view class="quantity-controls">
                        <view class="quantity-btn" @click="decreaseQuantity">-</view>
                        <input class="quantity-input" type="number" v-model.number="quantity" />
                        <view class="quantity-btn" @click="increaseQuantity">+</view>
                    </view>
                </view>

                <!-- 百分比选择 -->
                <view class="percentage-section">
                    <view class="percentage-btn" v-for="percent in [0, 25, 50, 100]" :key="percent"
                        :class="{ active: percentage === percent }" @click="setPercentage(percent)">
                        {{ percent }}%
                    </view>
                </view>

                <!-- 可用余额/持仓 -->
                <view class="balance-info">
                    <view class="balance-row">
                        <text class="balance-label">{{ $t('trade.availableBalance') }}</text>
                        <view class="balance-value">
                            <c-amount :value="accountInfo.availableBalance" />
                            <text> {{ currency }}</text>
                        </view>
                    </view>
                    <view class="balance-row">
                        <text class="balance-label">{{ type === 'buy' ? $t('trade.estimatedCost') :
                            $t('trade.estimatedAmount') }}</text>
                        <view class="balance-value">
                            <c-amount :value="totalAmount" />
                            <text> {{ currency }}</text>
                        </view>
                    </view>
                    <view class="balance-row">
                        <text class="balance-label">{{ $t('trade.fee') }}</text>
                        <view class="balance-value">
                            <c-amount :value="tradeFee" :decimals="5" />
                            <text> {{ currency }}</text>
                        </view>
                    </view>
                    <view class="balance-row" v-if="type === 'buy'">
                        <text class="balance-label">{{ $t('trade.actualAmount') }}</text>
                        <view class="balance-value highlight">
                            <c-amount :value="actualAmount" />
                            <text> {{ currency }}</text>
                        </view>
                    </view>
                </view>

                <!-- 止盈止损 -->
                <view class="stop-section">
                    <view class="stop-header" @click="enableStopProfit = !enableStopProfit">
                        <view class="checkbox" :class="{ checked: enableStopProfit }">
                            <text v-if="enableStopProfit">✓</text>
                        </view>
                        <text class="stop-title">{{ $t('trade.stopProfitLoss') }}</text>
                        <text class="stop-arrow">›</text>
                    </view>

                    <view class="stop-inputs" v-if="enableStopProfit">
                        <!-- 止盈 -->
                        <view class="stop-input-group">
                            <text class="stop-label">{{ $t('trade.stopProfit') }}</text>
                            <view class="stop-input-wrapper">
                                <view class="stop-input-btn"
                                    @click="stopProfit = (parseFloat(stopProfit || stockInfo.nowPrice) - 0.5).toFixed(2)">
                                    -</view>
                                <input class="stop-input" type="digit" v-model="stopProfit"
                                    :placeholder="String(stockInfo.nowPrice)" />
                                <view class="stop-input-btn"
                                    @click="stopProfit = (parseFloat(stopProfit || stockInfo.nowPrice) + 0.5).toFixed(2)">
                                    +</view>
                            </view>
                            <text class="stop-hint">{{ $t('trade.stopProfitHint') }}</text>
                        </view>

                        <!-- 止损 -->
                        <view class="stop-input-group">
                            <text class="stop-label">{{ $t('trade.stopLoss') }}</text>
                            <view class="stop-input-wrapper">
                                <view class="stop-input-btn"
                                    @click="stopLoss = (parseFloat(stopLoss || stockInfo.nowPrice) - 0.5).toFixed(2)">-
                                </view>
                                <input class="stop-input" type="digit" v-model="stopLoss"
                                    :placeholder="String(stockInfo.nowPrice)" />
                                <view class="stop-input-btn"
                                    @click="stopLoss = (parseFloat(stopLoss || stockInfo.nowPrice) + 0.5).toFixed(2)">+
                                </view>
                            </view>
                            <text class="stop-hint">{{ $t('trade.stopLossHint') }}</text>
                        </view>
                    </view>
                </view>
            </view>

            <!-- 确认按钮 -->
            <view class="modal-footer">
                <view class="confirm-btn" :class="type" @click="handleConfirm">
                    {{ type === 'buy' ? $t('trade.confirmBuy') : $t('trade.confirmSell') }}
                </view>
            </view>
        </view>
    </view>
</template>

<style lang="scss" scoped>
.trade-modal {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: flex-end;
    z-index: 999;
}

.modal-content {
    width: 100%;
    background: #fff;
    border-radius: 32rpx 32rpx 0 0;
    display: flex;
    flex-direction: column;
}

.modal-header {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 22rpx;
    border-bottom: 1rpx solid #f0f0f0;
    position: relative;
}

.modal-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
}

.modal-close {
    position: absolute;
    right: 32rpx;
    font-size: 48rpx;
    color: #999;
    line-height: 1;
}

.modal-body {
    padding: 0 32rpx;
}

.stock-info {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
    margin-bottom: 24rpx;
}

.stock-name {
    font-size: 32rpx;
    font-weight: 600;
    color: #00C087;
}

.stock-code {
    font-size: 24rpx;
    color: #999;
}

.price-row,
.change-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16rpx;
}

.price-label,
.change-label {
    font-size: 26rpx;
    color: #666;
}

.price-value-group {
    display: flex;
    align-items: baseline;
    gap: 8rpx;
}

.price-value {
    font-size: 32rpx;
    font-weight: 600;
    color: #333;
}

.price-currency {
    font-size: 22rpx;
    color: #999;
}

.change-value {
    font-size: 26rpx;
    font-weight: 500;
}

.quantity-section {
    margin: 32rpx 0 24rpx;
}

.quantity-label {
    display: block;
    font-size: 26rpx;
    color: #666;
    margin-bottom: 12rpx;
}

.quantity-controls {
    display: flex;
    align-items: center;
    background: #f5f5f5;
    border-radius: 16rpx;
    padding: 16rpx;
}

.quantity-btn {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 12rpx;
    font-size: 40rpx;
    color: #333;
    font-weight: 500;
}

.quantity-input {
    flex: 1;
    text-align: center;
    font-size: 30rpx;
    color: #333;
}

.percentage-section {
    display: flex;
    gap: 16rpx;
    margin-bottom: 32rpx;
}

.percentage-btn {
    flex: 1;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2rpx solid #f5f5f5;
    border-radius: 12rpx;
    font-size: 26rpx;
    color: #666;

    &.active {
        background: #00BCD4;
        color: #fff;
    }
}

.balance-info {
    background: #f8f8f8;
    border-radius: 12rpx;
    padding: 12rpx 24rpx;
    margin-bottom: 32rpx;
}

.balance-row {
    display: flex;
    align-items: center;
    margin-bottom: 16rpx;

    &:last-child {
        margin-bottom: 0;
    }
}

.balance-label {
    font-size: 26rpx;
    color: #666;
}

.balance-value {
    font-size: 26rpx;
    color: #333;
    font-weight: 500;
    margin-left: 10rpx;

    &.highlight {
        color: #00BCD4;
        font-weight: 600;
    }
}

.stop-section {
    border-radius: 12rpx;
    padding: 12rpx 24rpx;
}

.stop-header {
    display: flex;
    align-items: center;
    gap: 16rpx;
}

.checkbox {
    width: 40rpx;
    height: 40rpx;
    border: 2rpx solid #ddd;
    border-radius: 8rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24rpx;
    color: #fff;

    &.checked {
        background: #00BCD4;
        border-color: #00BCD4;
    }
}

.stop-title {
    flex: 1;
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
}

.stop-arrow {
    font-size: 32rpx;
    color: #999;
    transform: rotate(90deg);
}

.stop-inputs {
    margin-top: 24rpx;
}

.stop-input-group {
    margin-bottom: 24rpx;

    &:last-child {
        margin-bottom: 0;
    }
}

.stop-label {
    display: block;
    font-size: 26rpx;
    color: #666;
    margin-bottom: 12rpx;
}

.stop-input-wrapper {
    display: flex;
    align-items: center;
    background: #f5f5f5;
    border-radius: 12rpx;
    padding: 12rpx;
    margin-bottom: 8rpx;
}

.stop-input-btn {
    width: 80rpx;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8rpx;
    font-size: 40rpx;
    color: #333;
    font-weight: 500;
}

.stop-input {
    flex: 1;
    text-align: center;
    font-size: 30rpx;
    color: #333;
}

.stop-hint {
    font-size: 22rpx;
    color: #999;
}

.modal-footer {
    padding: 24rpx 32rpx;
    padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}

.confirm-btn {
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 16rpx;
    font-size: 32rpx;
    font-weight: bold;
    color: #fff;

    &.buy {
        background: #00C087;
    }

    &.sell {
        background: #FF4D4F;
    }
}
</style>
