<script setup></script>

<template>
  <view class="search-container">
    <view class="search-box">
      <image class="search-icon" src="/static/svgs/search.svg" mode="aspectFit" />
      <input class="search-input" :placeholder="$t('common.search')" v-bind="$attrs" />
    </view>
  </view>
</template>

<style lang="scss" scoped>
// 搜索框样式
.search-container {
  background: #fff;
}
.search-box {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 48rpx;
  padding: 0 24rpx;
  height: 72rpx;
}
.search-icon {
  width: 33rpx;
  height: 33rpx;
  flex-shrink: 0;
  margin-right: 12rpx;
}
.search-input {
  flex: 1;
  font-size: 28rpx;
  color: #333;

  &::placeholder {
    color: #999;
  }
}
</style>
