<script setup>
import { computed, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const mentorInfo = ref({
  id: '',
  detailsUrl: '',
  aceTag: '',
  introduce: '',
  serviceIntroduction: '',
  serviceContent: ''
});

const aceTags = computed(() => {
  return (mentorInfo.value.aceTag || '')
    .split(',')
    .map((item) => item.trim())
    .filter(Boolean);
});

const ensureMentorInfo = async (payload) => {
  if (payload) {
    try {
      const parsed = JSON.parse(payload);
      mentorInfo.value = {
        ...mentorInfo.value,
        ...parsed
      };
    } catch (error) {
      console.warn('解析投顾详情参数失败', error);
    }
  }
};

const handleFollow = () => {
  const mentorId = mentorInfo.value.id || '';
  const query = mentorId ? `?mentorId=${mentorId}` : '';
  navigateTo(`/pages/advisor/advisor-follow/index${query}`);
};

onLoad((options) => {
  ensureMentorInfo(options?.mentorInfo);
});

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper>
    <view class="detail-page">
      <nav-bar :title="$t('advisor.targetDetail')" />

      <view class="detail-content">
        <view class="cover-wrapper" v-if="mentorInfo.detailsUrl">
          <image class="cover" :src="mentorInfo.detailsUrl" mode="widthFix" />
        </view>

        <view class="service-banner">
          <image class="badge-icon" src="/static/images/series/default/advisor/service_vip.png" mode="widthFix" />
          <text class="badge">{{ $t('advisor.aceService') }}</text>
          <view class="tag-group">
            <text class="tag" v-for="(tag, index) in aceTags" :key="index">{{ tag }}</text>
          </view>
        </view>

        <view class="info-section">
          <text class="section-title">{{ $t('advisor.advisorIntro') }}</text>
          <view class="section-body">
            <rich-text :nodes="mentorInfo.introduce || `<p>${$t('advisor.noIntro')}</p>`" />
          </view>
        </view>

        <view class="info-section">
          <text class="section-title">{{ $t('advisor.serviceIntro') }}</text>
          <view class="section-body">
            <rich-text :nodes="mentorInfo.serviceIntroduction || `<p>${$t('advisor.noServiceIntro')}</p>`" />
          </view>
        </view>

        <view class="info-section">
          <text class="section-title">{{ $t('advisor.serviceContent') }}</text>
          <view class="section-body">
            <rich-text :nodes="mentorInfo.serviceContent || `<p>${$t('advisor.noServiceContent')}</p>`" />
          </view>
        </view>
      </view>

      <view class="follow-button" @click="handleFollow">
        <text>{{ $t('advisor.subscribeNow') }}</text>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.detail-page {
  @include hasNavBar();
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 166rpx;
  position: relative;
}

.detail-content {
  padding: 16rpx 32rpx 32rpx;
}

.cover-wrapper {
  margin-bottom: 24rpx;

  .cover {
    width: 100%;
    border-radius: 24rpx;
  }
}

.service-banner {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  align-items: center;
  margin-bottom: 32rpx;

  .badge-icon {
    width: 56rpx;
    height: 56rpx;
  }

  .badge {
    font-size: 30rpx;
    font-weight: 600;
    color: black;
    padding: 10rpx 0 10rpx 0;
    border-radius: 32rpx;
  }

  .tag-group {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;

    .tag {
      font-size: 24rpx;
      color: #00b6e6;
      background: rgba(0, 182, 230, 0.12);
      padding: 8rpx 20rpx;
      border-radius: 24rpx;
    }
  }
}

.info-section {
  border-radius: 24rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;

  .section-title {
    font-size: 32rpx;
    font-weight: 600;
    color: #111;
    margin-bottom: 20rpx;
  }

  .section-body {
    font-size: 28rpx;
    color: #555;

    :deep(p) {
      margin-bottom: 16rpx;
      line-height: 1.7;
      color: #555;
    }

    :deep(img) {
      max-width: 100%;
      display: block;
      border-radius: 16rpx;
      margin: 16rpx 0;
    }
  }
}

.follow-button {
  position: fixed;
  left: 64rpx;
  right: 64rpx;
  bottom: 64rpx;
  height: 96rpx;
  border-radius: 18rpx;
  background: #00b6e6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: 600;
  box-shadow: 0 24rpx 36rpx rgba(0, 182, 230, 0.25);
}
</style>
