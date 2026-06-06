<script setup></script>

<template>
  <view class="m-tips">
    <svg-icon name="tips-primary" class="u-svg-icon" />
    <view class="u-tips-main">
      <slot></slot>
    </view>
  </view>
</template>

<style lang="scss" scoped>
  .m-tips{
    display: flex;
    background: var(--primary-color-weak);
    border-radius: 10rpx;
    padding: 32rpx 24rpx 40rpx;
    border-radius: 10rpx;
    .u-svg-icon{
      flex-shrink: 0;
      width: 34rpx!important;
      margin-right: 12rpx;
    }
    .u-tips-main{
      flex: 1;
      margin-top: 8rpx;
      font-size: 24rpx;
      line-height: 32rpx;
    }
  }
</style>
