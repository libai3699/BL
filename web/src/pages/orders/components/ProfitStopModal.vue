<script setup>
import { ref } from 'vue';

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

const profitTarget = ref(Number(props.position.profitTargetPrice) || null);
const stopTarget = ref(Number(props.position.stopTargetPrice) || null);

const handleClose = () => {
  emit('update:visible', false);
};

const handleProfitChange = (type) => {
  const step = 1;
  if (type === 'add') {
    profitTarget.value = parseFloat((parseFloat(profitTarget.value) + step).toFixed(2));
  } else {
    profitTarget.value = Math.max(0, parseFloat((parseFloat(profitTarget.value) - step).toFixed(2)));
  }
};

const handleStopChange = (type) => {
  const step = 1;
  if (type === 'add') {
    stopTarget.value = parseFloat((parseFloat(stopTarget.value) + step).toFixed(2));
  } else {
    stopTarget.value = Math.max(0, parseFloat((parseFloat(stopTarget.value) - step).toFixed(2)));
  }
};

const handleConfirm = () => {
  emit('confirm', {
    positionSn: props.position.positionSn,
    profitTarget: parseFloat(profitTarget.value),
    stopTarget: parseFloat(stopTarget.value)
  });
};
</script>

<template>
  <u-popup :show="visible" mode="bottom" :z-index="20" :round="20" @close="handleClose">
    <view class="profit-stop-modal">
      <view class="modal-header">
        <text class="modal-title">{{ $t('order.stopOrLimit') }}</text>
        <view class="close-btn" @click="handleClose">
          <text class="close-icon">×</text>
        </view>
      </view>

      <view class="modal-body">
        <view class="info-section">
          <view class="info-row">
            <text class="info-label">{{ $t('order.direction') }}</text>
            <text class="info-value" :class="position.stockType">
              {{ $t(`order.${position.stockType}`) }}
            </text>
          </view>

          <view class="info-row">
            <text class="info-label">{{ $t('order.buyPrice') }}({{ position.currency }})</text>
            <text class="info-value">{{ position.buyPrice?.toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2
            }) }}</text>
          </view>

          <view class="info-row">
            <text class="info-label">{{ $t('order.currentPrice') }}({{ position.currency }})</text>
            <text class="info-value">{{ position.currentPrice?.toLocaleString('en-US', {
              minimumFractionDigits: 2,
              maximumFractionDigits: 2
            }) }}</text>
          </view>

          <view class="info-row">
            <text class="info-label">{{ $t('order.dailyChange') }}</text>
            <text class="info-value" :class="{ up: position.profitRate >= 0, down: position.profitRate < 0 }">
              {{ position.profitRate || '--' }}
            </text>
          </view>
        </view>

        <view class="control-section">
          <view class="control-item">
            <text class="control-label">{{ $t('order.profitTarget') }}</text>
            <view class="control-input">
              <view class="control-btn" @click="handleProfitChange('minus')">
                <text class="btn-text">−</text>
              </view>
              <input class="input-field" type="number" v-model="profitTarget" />
              <view class="control-btn" @click="handleProfitChange('add')">
                <text class="btn-text">+</text>
              </view>
            </view>
            <text class="control-tip">{{ $t('order.profitTargetTip') }}</text>
          </view>

          <view class="control-item">
            <text class="control-label">{{ $t('order.stopTarget') }}</text>
            <view class="control-input">
              <view class="control-btn" @click="handleStopChange('minus')">
                <text class="btn-text">−</text>
              </view>
              <input class="input-field" type="number" v-model="stopTarget" />
              <view class="control-btn" @click="handleStopChange('add')">
                <text class="btn-text">+</text>
              </view>
            </view>
            <text class="control-tip">{{ $t('order.stopTargetTip') }}</text>
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
.profit-stop-modal {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  max-height: 80vh;
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
  max-height: 60vh;
  overflow-y: auto;
}

.info-section {
  margin-bottom: 32rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.info-label {
  font-size: 26rpx;
  color: #666;
}

.info-value {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;

  &.buy {
    color: #00C087;
  }

  &.sell {
    color: #FF4D4F;
  }

  &.up {
    color: #00C087;
  }

  &.down {
    color: #FF4D4F;
  }
}

.control-section {
  margin-bottom: 32rpx;
}

.control-item {
  margin-bottom: 32rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.control-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.control-input {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 12rpx;
  background: #f5f5f5;
  border-radius: 5rpx;
}

.control-btn {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12rpx;
}

.btn-text {
  font-size: 40rpx;
  color: #333;
  font-weight: 500;
}

.input-field {
  flex: 1;
  height: 80rpx;
  text-align: center;
  font-size: 32rpx;
  color: #333;
  border-radius: 12rpx;
}

.control-tip {
  display: block;
  font-size: 24rpx;
  color: #999;
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
