<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

const newsId = ref('');
const newsDetail = ref(null);
const loading = ref(true);

onLoad((options) => {
  if (options.id) {
    newsId.value = options.id;
    fetchNewsDetail();
  }
});

// 获取新闻详情
const fetchNewsDetail = async () => {
  try {
    loading.value = true;
    // 这里使用传入的新闻数据，因为列表已经有完整数据
    // 如果需要调用接口获取详情，可以在这里添加
    const newsData = uni.getStorageSync('currentNews');
    if (newsData) {
      newsDetail.value = JSON.parse(newsData);
    }
  } catch (error) {
    console.error('获取新闻详情失败', error);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <view class="news-detail-page">
    <nav-bar :title="$t('home.newsList')" />

    <view class="news-detail-container">
      <view v-if="loading" class="loading">
        <text>{{ $t('common.loading') }}</text>
      </view>

      <view v-else-if="newsDetail" class="news-content">
        <!-- 新闻标题 -->
        <view class="news-title">{{ newsDetail.title }}</view>

        <!-- 新闻时间 -->
        <view class="news-time">{{ newsDetail.addTime }}</view>

        <!-- 新闻内容 -->
        <view class="news-body">
          <rich-text :nodes="newsDetail.content"></rich-text>
        </view>
      </view>

      <view v-else class="no-data">
        <text>{{ $t('common.noData') }}</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.news-detail-page {
  @include hasNavBar();
  background: #fff;
  overflow: auto;
}

.news-detail-container {
  padding: 32rpx;
}

.loading,
.no-data {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}

.news-content {
  .news-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
    line-height: 1.5;
    margin-bottom: 24rpx;
  }

  .news-time {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 32rpx;
  }

  .news-body {
    font-size: 28rpx;
    color: black;
    line-height: 1.8;

    :deep(p) {
      margin-bottom: 20rpx;
    }

    :deep(img) {
      max-width: 100%;
      height: auto;
      display: block;
      margin: 20rpx 0;
    }
  }
}
</style>
