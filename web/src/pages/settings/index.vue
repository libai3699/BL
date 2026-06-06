<script setup>
import { ref } from 'vue';
import { useCommonStore } from '@/stores/common';
import { navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const general = [
  {
    ic: 'globe',
    nameKey: 'settings.language',
    metaKey: 'settings.langZh',
    action: 'locale'
  },
  { ic: 'palette', nameKey: 'settings.theme', metaKey: 'settings.themeAuto' },
  { ic: 'palette', nameKey: 'settings.colorMode', metaKey: 'settings.colorGreenUp' },
  { ic: 'currency', nameKey: 'settings.currency', metaKey: 'settings.currencyMxn' }
];
const notifs = ref([
  { ic: 'bell', nameKey: 'settings.push', on: true },
  { ic: 'newspaper', nameKey: 'settings.news', on: true },
  { ic: 'gift', nameKey: 'settings.activity', on: true },
  { ic: 'speaker', nameKey: 'settings.sound', on: false }
]);
const about = [
  { ic: 'info', nameKey: 'settings.about' },
  {
    ic: 'document',
    nameKey: 'settings.agreement',
    path: '/pages/rules/common?type=user'
  },
  { ic: 'lock', nameKey: 'settings.privacy', path: '/pages/rules/common?type=privacy' },
  { ic: 'refresh', nameKey: 'settings.update', meta: 'v2.0.1' }
];

const toggle = (i) => {
  notifs.value[i].on = !notifs.value[i].on;
};

const onRow = (it) => {
  if (it.action === 'locale') {
    commonStore.popup.locale.show = true;
    return;
  }
  if (it.path) {
    navigateTo(it.path);
  }
};
</script>

<template>
  <page-wrapper>
    <view class="page">
      <nav-bar :title="$t('settings.title')" />
      <scroll-view scroll-y class="body">
        <text class="grp">{{ $t('settings.groupGeneral') }}</text>
        <view class="card">
          <view v-for="(it, i) in general" :key="i" class="row" @tap="onRow(it)">
            <view class="ic"><svg-icon :name="`stock-${it.ic}`" /></view>
            <text class="n">{{ $t(it.nameKey) }}</text>
            <text class="meta">{{ $t(it.metaKey) }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
        <text class="grp">{{ $t('settings.groupNotify') }}</text>
        <view class="card">
          <view v-for="(it, i) in notifs" :key="i" class="row">
            <view class="ic"><svg-icon :name="`stock-${it.ic}`" /></view>
            <text class="n">{{ $t(it.nameKey) }}</text>
            <view class="switch" :class="{ on: it.on }" @tap="toggle(i)"></view>
          </view>
        </view>
        <text class="grp">{{ $t('settings.groupAbout') }}</text>
        <view class="card">
          <view v-for="(it, i) in about" :key="i" class="row" @tap="onRow(it)">
            <view class="ic"><svg-icon :name="`stock-${it.ic}`" /></view>
            <text class="n">{{ $t(it.nameKey) }}</text>
            <text v-if="it.meta" class="meta">{{ it.meta }}</text>
            <text class="arrow">›</text>
          </view>
        </view>
        <text class="ver">ARTISAN PARTNERS v2.0.1</text>
      </scroll-view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.page {
  min-height: 100vh;
  background: var(--stock-page-bg);
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  @include hasNavBar();
}
.body {
  flex: 1;
  padding-bottom: 60rpx;
}
.grp {
  display: block;
  margin: 28rpx 28rpx 12rpx;
  font-size: 22rpx;
  color: var(--stock-ink-3);
  font-weight: 600;
}
.card {
  margin: 0 24rpx;
  @include stock-card;
  overflow: hidden;
}
.row {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 28rpx;
  border-bottom: 2rpx solid var(--stock-line);
  &:last-child {
    border-bottom: none;
  }
}
.ic {
  width: 64rpx;
  height: 64rpx;
  border-radius: 18rpx;
  background: var(--stock-fill-soft);
  border: 2rpx solid #ebeff7;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.n {
  flex: 1;
  font-size: 26rpx;
  font-weight: 600;
}
.meta {
  font-size: 22rpx;
  color: var(--stock-ink-3);
  margin-right: 12rpx;
}
.arrow {
  color: var(--stock-ink-3);
  font-size: 36rpx;
}
.switch {
  width: 88rpx;
  height: 52rpx;
  background: var(--stock-line);
  border-radius: 26rpx;
  position: relative;
  flex-shrink: 0;
  &::after {
    content: '';
    position: absolute;
    width: 40rpx;
    height: 40rpx;
    background: #fff;
    border-radius: 50%;
    top: 6rpx;
    left: 6rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);
  }
  &.on {
    background: var(--stock-up);
    &::after {
      left: 42rpx;
    }
  }
}
.ver {
  display: block;
  margin: 48rpx 0;
  text-align: center;
  font-size: 20rpx;
  color: var(--stock-ink-3);
  letter-spacing: 4rpx;
}
</style>
