<script setup>
const props = defineProps({
  // 是否定位在外部容器水平垂直居中
  isPosition: {
    type: Boolean,
    default: false
  },
  // 状态显示类型：block：显示为块状（带对应图标） onlyText 只有文本 onlyText-sm 只有文本，尺寸小
  showType: {
    type: String,
    default: 'block'
  },
  // 加载图类型 gif|flower|png
  type: {
    type: String,
    default: 'flower'
  },
  text: {
    type: String,
    default: undefined
  },
  // 等待icon尺寸
  iconSize: {
    type: String
  }
});
</script>

<template>
  <view
    class="m-loading"
    :class="{
      's-position': isPosition,
      's-only-text': showType === 'onlyText',
      's-only-text-sm': showType === 'onlyText-sm'
    }"
  >
    <u-loading-icon
      mode="circle"
      timing-function="linear"
      v-if="type === 'flower'"
      class="u-flower"
      :size="iconSize"
    />
    <image v-else-if="type === 'gif'" mode="widthFix" class="u-img s-default" />
    <image v-else-if="type === 'png'" mode="widthFix" class="u-img s-png" />
    <text class="u-text">{{ text ?? `${$t('common.loading')}...` }}</text>
  </view>
</template>

<style lang="scss">
@use '@/styles/mixin.scss' as *;

.m-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: relative;
  width: 100%;
  height: 100%;
  padding: 50rpx 50rpx;
  box-sizing: border-box;

  &.s-position {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    left: 0;
    padding: 0 100rpx;
  }

  // 只有文本
  &.s-only-text,
  &.s-only-text-sm {
    display: inline-flex;
    width: auto;
    flex-direction: row;
    column-gap: 10rpx;
    padding: 0;
    .u-img {
      display: none;
    }
    .u-text {
      margin-top: 0;
    }
  }
  &.s-only-text-sm {
    .u-flower {
      .u-loading-icon__spinner {
        width: 40rpx !important;
        height: 40rpx !important;
      }
    }
    .u-text {
      font-size: 24rpx;
    }
  }

  .u-img {
    display: block;
    &.s-default {
      width: 360rpx;
      height: 360rpx;
      margin-bottom: 10rpx;
    }
    &.s-png {
      width: 362rpx;
      height: 362rpx;
      margin-bottom: 20rpx;
    }
  }

  .u-text {
    font-size: 28rpx;
    line-height: 38rpx;
    color: #a4a4a4;
    margin-top: 20rpx;
  }
}
</style>
