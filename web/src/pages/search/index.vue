<script setup>
import { ref } from 'vue';
import { goBack } from '@/utils/navigate';

const keyword = ref('');
const hot = ['NVDA', 'BAYZ Holdings', 'AAPL', 'TSLA', 'AI 概念', '新能源'];
const recent = ref(['NVIDIA Corporation', 'IMB Industries', 'Tesla Inc.']);
const sectors = [
  { ico: 'laptop', name: '科技', pct: '+2.41%', up: true },
  { ico: 'bolt', name: '能源', pct: '+1.18%', up: true },
  { ico: 'bank', name: '金融', pct: '-0.32%', up: false },
  { ico: 'pill', name: '医药', pct: '+0.84%', up: true },
  { ico: 'cart', name: '消费', pct: '+0.21%', up: true },
  { ico: 'factory', name: '工业', pct: '-0.56%', up: false }
];

const onCancel = () => goBack();
const onClearRecent = () => {
  recent.value = [];
  uni.showToast({ title: uni.$t('search.cleared'), icon: 'none' });
};
</script>

<template>
  <page-wrapper>
    <view class="p-search">
      <view class="m-top">
        <view class="u-back" @tap="onCancel">
          <svg-icon name="arrow-left" :size="36" />
        </view>
        <view class="u-input">
          <svg-icon name="search" :size="28" color="#94A3B8" />
          <input
            v-model="keyword"
            :placeholder="$t('market.searchPlaceholder')"
            placeholder-class="ph"
          />
        </view>
        <text class="u-cancel" @tap="onCancel">{{ $t('common.cancel') }}</text>
      </view>
      <scroll-view scroll-y class="m-content">
        <view class="m-section-title">{{ $t('search.hot') }}</view>
        <view class="m-tags">
          <text
            v-for="(tag, i) in hot"
            :key="tag"
            class="u-tag"
            :class="{ hot: i < 2 }"
            >{{ tag }}</text
          >
        </view>
        <view class="m-section-title">
          {{ $t('search.recent') }}
          <text class="u-clear" @tap="onClearRecent">{{ $t('search.clear') }}</text>
        </view>
        <view class="m-recent" v-if="recent.length">
          <view v-for="r in recent" :key="r" class="u-row">
            <svg-icon name="search" :size="28" color="#94A3B8" />
            <text class="u-name">{{ r }}</text>
            <text class="u-arrow">›</text>
          </view>
        </view>
        <view class="m-section-title">{{ $t('search.sectors') }}</view>
        <view class="m-sectors">
          <view v-for="s in sectors" :key="s.name" class="u-sec">
            <view class="u-ico"><svg-icon :name="`stock-${s.ico}`" /></view>
            <text class="u-name">{{ s.name }}</text>
            <text class="u-pct" :class="s.up ? 'up' : 'down'">{{ s.pct }}</text>
          </view>
        </view>
      </scroll-view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.p-search {
  min-height: 100vh;
  background: var(--stock-page-bg);
  display: flex;
  flex-direction: column;
}
.m-top {
  display: flex;
  align-items: center;
  padding: 12rpx 16rpx 24rpx;
  gap: 12rpx;
  background: #fff;
  border-bottom: 2rpx solid var(--stock-line);
}
.u-back {
  width: 72rpx;
  height: 72rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.u-input {
  flex: 1;
  height: 72rpx;
  background: var(--stock-fill-soft);
  border-radius: 20rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.u-input input {
  flex: 1;
  font-size: 26rpx;
}
.u-cancel {
  font-size: 26rpx;
  color: var(--stock-blue);
  font-weight: 600;
  padding: 0 12rpx;
}
.m-content {
  flex: 1;
  height: calc(100vh - 100rpx);
}
.m-section-title {
  margin: 32rpx 28rpx 16rpx;
  font-size: 24rpx;
  color: var(--stock-ink-2);
  font-weight: 700;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.u-clear {
  font-size: 22rpx;
  color: var(--stock-ink-3);
  font-weight: 500;
}
.m-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  padding: 0 28rpx;
}
.u-tag {
  padding: 12rpx 24rpx;
  background: #fff;
  border: 2rpx solid var(--stock-line);
  border-radius: 28rpx;
  font-size: 24rpx;
  color: var(--stock-ink-2);
  &.hot {
    background: linear-gradient(135deg, #fef3c7, #fcd34d);
    color: #8b5a0e;
    border: none;
  }
}
.m-recent {
  background: #fff;
}
.m-recent .u-row {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx 28rpx;
  border-bottom: 2rpx solid var(--stock-line);
  font-size: 26rpx;
}
.m-recent .u-name {
  flex: 1;
}
.u-arrow {
  color: var(--stock-ink-3);
  font-size: 32rpx;
}
.m-sectors {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  background: #fff;
  border-top: 2rpx solid var(--stock-line);
}
.u-sec {
  padding: 36rpx 16rpx;
  text-align: center;
  border-right: 2rpx solid var(--stock-line);
  border-bottom: 2rpx solid var(--stock-line);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
}
.u-sec:nth-child(3n) {
  border-right: none;
}
.u-sec .u-name {
  font-size: 24rpx;
  font-weight: 600;
}
.u-sec .u-pct {
  font-size: 20rpx;
  font-weight: 700;
  @include stock-tnum;
  &.up {
    color: var(--stock-up);
  }
  &.down {
    color: var(--stock-down);
  }
}
</style>
