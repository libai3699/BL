<script setup>
import { ref, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { getTradeFee } from '@/apis/order';
import { submitSubscribe } from '@/apis/ipo';
import { submitSubscribeAdd } from '@/apis/issuance';

const { t } = useI18n();

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  ipoItem: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:visible', 'success']);

const paymentFee = ref(0);
const paymentTotal = ref(0);
const isLoading = ref(false);

// 计算付款金额（不含手续费）
const paymentAmount = computed(() => {
  return (props.ipoItem?.buyPrice || 0) * (props.ipoItem?.applyNumber || 0);
});

// 获取手续费
const fetchFee = async () => {
  // 重置手续费
  paymentFee.value = 0;
  paymentTotal.value = paymentAmount.value;

  const gid = props.ipoItem?.newType + props.ipoItem?.newCode;
  if (!gid) {
    console.warn('缺少股票代码，无法获取手续费');
    return;
  }

  const feeRes = await getTradeFee({ gid });

  if (feeRes && feeRes.buyFee !== undefined) {
    // 手续费 = 付款金额 * 手续费率
    paymentFee.value = paymentAmount.value * feeRes.buyFee;
    // 总计 = 付款金额 + 手续费
    paymentTotal.value = paymentAmount.value + paymentFee.value;
  }
};

const handleClose = () => {
  emit('update:visible', false);
};

const handleConfirm = async () => {
  if (isLoading.value) return;

  isLoading.value = true;

  // 根据类型选择不同的接口
  // type: 1 = IPO, 2 = 增发预售
  const apiCall = props.ipoItem.type === 2 ? submitSubscribeAdd : submitSubscribe;

  // 调用缴费接口，只需要传 id
  const res = await apiCall({
    id: props.ipoItem.id
  });

  if (res.status == 0 || res.code == 200) {
    uni.showToast({
      title: t('order.paymentSuccess'),
      icon: 'success'
    });

    handleClose();
    emit('success');
  } else {
    uni.showToast({
      title: res.msg || t('error.system'),
      icon: 'none'
    });
  }
  isLoading.value = false;
};

fetchFee()
</script>

<template>
  <u-popup :show="visible" mode="bottom" :z-index="20" :round="20" @close="handleClose">
    <view class="payment-modal">
      <view class="modal-header">
        <text class="modal-title">{{ $t('order.payment') }}</text>
        <view class="close-btn" @click="handleClose">
          <text class="close-icon">×</text>
        </view>
      </view>

      <view class="modal-body">
        <view class="info-row">
          <text class="info-label">{{ $t('order.fpoStockName') }}</text>
          <text class="info-value">{{ ipoItem?.newName }}</text>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('order.fpoIssuePrice') }}</text>
          <view class="info-value">
            <c-amount :value="ipoItem?.buyPrice" />
            <text> {{ ipoItem?.newType == 'mxg' ? 'MXN' : 'USD' }}</text>
          </view>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('order.fpoWinningNum') }}</text>
          <view class="info-value">
            <text>{{ ipoItem?.applyNumber || 0 }} {{ $t('ipo.shareUnit') }}</text>
          </view>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('order.paymentAmount') }}</text>
          <view class="info-value">
            <c-amount :value="paymentAmount" />
            <text> {{ ipoItem?.currency || 'MXN' }}</text>
          </view>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('trade.fee') }}</text>
          <view class="info-value">
            <c-amount :value="paymentFee" :decimals="5" />
            <text> {{ ipoItem?.currency || 'MXN' }}</text>
          </view>
        </view>

        <view class="info-row total-row">
          <text class="info-label">{{ $t('order.totalPayment') }}</text>
          <view class="info-value highlight">
            <c-amount :value="paymentTotal" />
            <text> {{ ipoItem?.currency || 'MXN' }}</text>
          </view>
        </view>
      </view>

      <view class="modal-footer">
        <view class="confirm-btn" @click="handleConfirm">
          {{ $t('common.confirm') }}
        </view>
      </view>
    </view>
  </u-popup>
</template>

<style lang="scss" scoped>
.payment-modal {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
  position: relative;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333;
}

.close-btn {
  position: absolute;
  right: 32rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  font-size: 38rpx;
  color: #999;
  line-height: 1;
}

.modal-body {
  padding: 32rpx;
  max-height: 600rpx;
  overflow-y: auto;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;

  &:last-child {
    margin-bottom: 0;
  }

  &.total-row {
    padding-top: 16rpx;
    border-top: 2rpx solid #f0f0f0;
    margin-top: 16rpx;
  }
}

.info-label {
  font-size: 28rpx;
  color: #666;
  flex-shrink: 0;
}

.info-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  text-align: right;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;

  &.highlight {
    color: #00bcd4;
    font-size: 32rpx;
    font-weight: 600;
  }
}

.modal-footer {
  padding: 32rpx;
  border-top: 1rpx solid #f0f0f0;
}

.confirm-btn {
  width: 100%;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #00bcd4;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 600;
  color: #fff;
}
</style>
