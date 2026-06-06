<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { getMentorInfo, getRecommendList } from '@/apis/advisor';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const mentorList = ref([]);
const recommendList = ref([]);
const loading = ref(false);

const fetchMentorInfo = async () => {
  const res = await getMentorInfo();
  // getMentorInfo接口返回的是一个数组
  if (Array.isArray(res)) {
    mentorList.value = res.map((item) => ({
      ...item,
      // 处理aceTag标签
      aceTags: Array.isArray(item.aceTags)
        ? item.aceTags
        : (item.aceTag || '')
          .split(',')
          .map((tag) => tag.trim())
          .filter(Boolean)
    }));
  }
};

const fetchRecommendList = async () => {
  const res = await getRecommendList();
  if (Array.isArray(res)) {
    recommendList.value = res.map((item) => ({
      ...item,
      tags: Array.isArray(item.tags)
        ? item.tags
        : String(item.tags || '')
          .split(',')
          .map((tag) => tag.trim())
          .filter(Boolean),
      stockList: Array.isArray(item.stockList) ? item.stockList : []
    }));
  } else if (res?.list) {
    recommendList.value = res.list.map((item) => ({
      ...item,
      tags: Array.isArray(item.tags)
        ? item.tags
        : String(item.tags || '')
          .split(',')
          .map((tag) => tag.trim())
          .filter(Boolean),
      stockList: Array.isArray(item.stockList) ? item.stockList : []
    }));
  }
};

const initPage = async () => {
  if (loading.value) return;
  loading.value = true;
  await Promise.all([fetchMentorInfo(), fetchRecommendList()]);
  loading.value = false;
};

const handleViewDetail = (mentorInfo) => {
  const payload = encodeURIComponent(JSON.stringify(mentorInfo || {}));
  navigateTo(`/pages/advisor/elite-investment-advisory-detail/index?mentorInfo=${payload}`);
};

const backHome = () => {
  uni.switchTab({
    url: '/pages/home/index'
  });
};

const handleRecommendClick = () => {
  if (mentorList.value.length > 0) {
    handleViewDetail(mentorList.value[0]);
  }
};

onShow(() => {
  commonStore.fnBack = backHome;
});

initPage();
</script>

<template>
  <page-wrapper>
    <view class="elite-page">
      <nav-bar :title="$t('advisor.chiefAdvisor')" @back="backHome" />

      <view class="elite-content">
        <template v-if="mentorList.length">
          <view v-for="mentor in mentorList" :key="mentor.id" class="mentor-section">
            <view class="section-title">
              <text class="title-text">{{ $t('advisor.starAdvisor') }}</text>
            </view>

            <view class="mentor-card">
              <image class="mentor-cover" :src="mentor.simpleUrl" mode="widthFix" />

              <view class="service-banner">
                <image class="badge-icon" src="/static/images/series/default/advisor/service_vip.png" mode="widthFix" />
                <text class="badge">{{ $t('advisor.aceService') }}</text>
                <view class="tag-group">
                  <text class="tag" v-for="(tag, idx) in mentor.aceTags" :key="idx">{{ tag }}</text>
                </view>
              </view>

              <view class="summary-box" v-if="mentor.tag">
                <image class="summary-icon" src="/static/images/series/default/advisor/service_tag.png"
                  mode="widthFix" />
                <text class="summary-text">{{ mentor.tag }}</text>
              </view>

              <view class="action-area">
                <button class="primary-button" @click="handleViewDetail(mentor)">{{ $t('advisor.viewDetail') }}</button>
              </view>
            </view>
          </view>
        </template>

        <no-data v-else />

        <view class="section-title">
          <text class="title-text">{{ $t('advisor.elitePortfolio') }}</text>
        </view>

        <view class="group-list" v-if="recommendList.length">
          <view class="group-item" v-for="item in recommendList" :key="item.id" @click="handleRecommendClick">
            <view class="group-header">
              <text class="group-name">{{ item.groupName }}</text>
              <text class="group-rate">{{ ((item.incomeRate || 0) * 100).toFixed(2) }}%</text>
            </view>
            <view class="group-tags">
              <text class="group-tag" v-for="(tag, idx) in item.tags" :key="idx">{{ tag }}</text>
            </view>
            <view class="stock-list">
              <view class="stock-item" v-for="(stock, idx) in item.stockList" :key="idx">
                <text class="stock-name">{{ stock.stockName }}</text>
                <text class="stock-rate">{{ ((stock.historyRate || 0) * 100).toFixed(2) }}%</text>
                <text class="stock-label">{{ $t('advisor.historicalRecord') }}</text>
              </view>
            </view>
          </view>
        </view>

        <no-data v-else />
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.elite-page {
  @include hasNavBar();
  min-height: 100vh;
  background: #f5f5f5;
}

.elite-content {
  padding: 16rpx 32rpx 64rpx;
}

.mentor-section {
  margin-bottom: 40rpx;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;

  .title-text {
    font-size: 36rpx;
    font-weight: 700;
    color: #111;
  }

  .title-icon {
    width: 120rpx;
    margin-left: 16rpx;
  }
}

.mentor-card {
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
  box-shadow: 0 16rpx 32rpx rgba(0, 182, 230, 0.12);
  margin-bottom: 40rpx;

  .mentor-cover {
    width: 100%;
    border-radius: 16rpx;
    margin-bottom: 24rpx;
  }
}

.service-banner {
  display: flex;
  align-items: center;
  gap: 16rpx;
  margin-bottom: 24rpx;

  .badge-icon {
    width: 52rpx;
    height: 52rpx;
  }

  .badge {
    font-size: 28rpx;
    font-weight: 600;
    color: black;
    white-space: nowrap;
    padding: 8rpx 0;
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
      padding: 6rpx 20rpx;
      border-radius: 24rpx;
    }
  }
}

.summary-box {
  display: flex;
  align-items: center;
  gap: 16rpx;
  background: #f0f0f0;
  border-radius: 16rpx;
  padding: 20rpx;
  margin-bottom: 32rpx;

  .summary-icon {
    width: 56rpx;
    height: 56rpx;
  }

  .summary-text {
    font-size: 28rpx;
    color: #00b6e6;
    font-weight: 500;
  }
}

.action-area {
  display: flex;
  justify-content: center;
  margin-bottom: 20rpx;

  .primary-button {
    width: 60%;
    height: 78rpx;
    line-height: 78rpx;
    background: #00b6e6;
    color: #fff;
    font-size: 30rpx;
    border: none;
    border-radius: 14rpx;
  }
}

.group-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.group-item {
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx 28rpx;
  box-shadow: 0 12rpx 28rpx rgba(0, 182, 230, 0.08);
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;

  .group-name {
    font-size: 32rpx;
    font-weight: 600;
    color: #111;
  }

  .group-rate {
    font-size: 32rpx;
    font-weight: 700;
    color: #00b6e6;
  }
}

.group-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 24rpx;

  .group-tag {
    font-size: 24rpx;
    color: #00b6e6;
    background: rgba(0, 182, 230, 0.12);
    padding: 8rpx 20rpx;
    border-radius: 24rpx;
  }
}

.stock-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180rpx, 1fr));
  gap: 16rpx;

  .stock-item {
    background: #f8fbff;
    border-radius: 16rpx;
    padding: 16rpx;
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    .stock-name {
      font-size: 26rpx;
      font-weight: 600;
      color: #111;
    }

    .stock-rate {
      font-size: 28rpx;
      font-weight: 600;
      color: #ff4d4f;
      margin: 12rpx 0;
    }

    .stock-label {
      font-size: 22rpx;
      color: #888;
    }
  }
}

.empty {
  margin-top: 24rpx;
  text-align: center;
  color: #999;
  font-size: 26rpx;
}
</style>
