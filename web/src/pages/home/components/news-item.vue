<script setup>
const props = defineProps({
  item: {
    type: Object,
    default: () => ({})
  }
});
</script>

<template>
  <view class="news-item">
    <view class="news-content">
      <view class="news-title">{{ item.title }}</view>
      <view class="news-time">{{ item.addTime }}</view>
    </view>
    <image v-if="item.imgurl" class="news-image" :src="item.imgurl" mode="aspectFill" />
  </view>
</template>

<style lang="scss" scoped>
.news-item {
  display: flex;
  padding: 24rpx 20rpx;
  background: #fff;
  border-radius: 16rpx;
  margin-bottom: 16rpx;
}

.news-content {
  flex: 1;
  margin-right: 24rpx;
}

.news-title {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  margin-bottom: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.news-time {
  font-size: 24rpx;
  color: #999;
}

.news-image {
  width: 200rpx;
  height: 150rpx;
  border-radius: 12rpx;
  flex-shrink: 0;
}
</style>
