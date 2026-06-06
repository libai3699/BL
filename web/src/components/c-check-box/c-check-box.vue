<script setup>
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  label: {
    type: String,
    default: ''
  }
});
</script>

<template>
  <view
    class="m-check-box"
    :class="{ 's-checked': modelValue }"
    @click="$emit('update:modelValue', !modelValue)"
  >
    <view class="u-box-main">
      <svg-icon name="correct" class="u-svg-icon" />
    </view>
    <view class="u-box-label" v-if="label">{{ label }}<slot></slot></view>
  </view>
</template>

<style lang="scss" scoped>
.m-check-box {
  display: inline-flex;
  align-items: center;

  .u-box-main {
    flex-shrink: 0;
    display: flex;
    justify-content: center;
    width: 28rpx;
    height: 28rpx;
    box-sizing: border-box;
    border: 2rpx solid var(--text-minor-color);
    border-radius: 6rpx;
    overflow: hidden;
    .u-svg-icon {
      width: 16rpx !important;
      height: 16rpx !important;
      display: none;
      transform: translateY(-60%);
    }
  }
  .u-box-label {
    margin-left: 10rpx;
    font-size: 24rpx;
    color: var(--text-minor-color);
    word-break: break-all;
  }

  &.s-checked {
    .u-box-main {
      background: var(--primary-color);
      border-color: var(--primary-color);
      .u-svg-icon {
        display: block;
      }
    }
  }
}
</style>
