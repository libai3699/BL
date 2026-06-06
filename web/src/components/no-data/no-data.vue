<template>
  <view class="no-data">
    <image class="no-data-icon" src="/static/svgs/no_data.svg" mode="widthFix" />
    <text class="no-data-text">{{ text || $t('common.noData') }}</text>
  </view>
</template>

<script setup>
defineProps({
  text: {
    type: String,
    default: ''
  }
});
</script>

<style lang="scss" scoped>
.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;

  .no-data-icon {
    width: 320rpx;
    margin-bottom: 32rpx;
  }

  .no-data-text {
    font-size: 28rpx;
    color: #999;
  }
}
</style>
