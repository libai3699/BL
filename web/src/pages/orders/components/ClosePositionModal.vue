<script setup>
import { computed } from 'vue';

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  position: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['update:visible', 'confirm']);

const profit = computed(() => {
  // 直接使用传入的盈亏值
  const profitValue = props.position.profitAndLose || 0;
  return Number(profitValue).toFixed(5);
});

const profitColor = computed(() => {
  return parseFloat(profit.value) >= 0 ? '#00C087' : '#FF4D4F';
});

const handleClose = () => {
  emit('update:visible', false);
};

const handleConfirm = () => {
  emit('confirm', props.position);
};
</script>

<template>
  <u-popup :show="visible" mode="bottom" :round="20" :z-index="20" @close="handleClose">
    <view class="close-modal">
      <view class="modal-header">
        <text class="modal-title">{{ $t('order.closePosition') }}</text>
        <view class="close-btn" @click="handleClose">
          <text class="close-icon">×</text>
        </view>
      </view>

      <view class="modal-body">
        <view class="info-row">
          <text class="info-label">{{ $t('order.stock') }}</text>
          <text class="info-value">{{ position.stockName }} ({{ position.stockPlate }})</text>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('order.direction') }}</text>
          <text class="info-value" :class="position.stockType">
            {{ $t(`order.${position.stockType}`) }}
          </text>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('order.buyPrice') }}({{ position.currency }})</text>
          <view class="info-value"><c-amount :value="position.buyPrice" /></view>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('order.currentPrice') }}({{ position.currency }})</text>
          <view class="info-value"><c-amount :value="position.currentPrice" /></view>
        </view>

        <view class="info-row">
          <text class="info-label">{{ $t('order.profit') }}</text>
          <view class="info-value" :style="{ color: profitColor }">
            <c-amount :value="position.profitAndLose" />
            <text> {{ position.currency }}</text>
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
.close-modal {
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
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.info-label {
  font-size: 28rpx;
  color: #666;
}

.info-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;

  &.buy {
    color: #00C087;
  }

  &.sell {
    color: #FF4D4F;
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
