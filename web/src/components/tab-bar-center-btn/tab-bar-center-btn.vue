<script setup>
/**
 * 底部导航中间「交易」凸起按钮（对齐原型 .tab.center）
 * 仅负责展示与点击反馈，业务由父级处理（如打开交易弹层）
 */
defineEmits(['click']);
</script>

<template>
  <view
    class="tab-bar-center-btn"
    hover-class="tab-bar-center-btn--pressed"
    @tap="$emit('click')"
  >
    <view class="tab-bar-center-btn__icon" aria-hidden="true">
      <svg
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2.5"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M3 18 9 12l4 4 8-8" />
        <polyline points="14 6 21 6 21 13" />
      </svg>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.tab-bar-center-btn {
  flex: 0 0 112rpx;
  width: 112rpx;
  height: 112rpx;
  margin-top: -44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  color: #fff;
  background: linear-gradient(
    135deg,
    var(--stock-navy, #0b1e47),
    var(--stock-blue, #2563eb)
  );
  box-shadow: 0 20rpx 44rpx rgba(37, 99, 235, 0.4);
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease;

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &--pressed {
    transform: scale(0.94);
    box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.32);
  }
}
</style>
