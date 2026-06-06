<script setup>
import { useCommonStore } from '@/stores/common';
import SERIES from '@/configs/series';
import images from '@/static/images';
import { navigateTo } from '@/utils/navigate';

const props = defineProps({
  // 右侧工具栏显示项
  // lang-选择语言
  // transactions-资金记录
  rightTools: {
    type: Array,
    default: () => ['lang']
  }
});

const commonStore = useCommonStore();

function onOpenLocale() {
  commonStore.popup.locale.show = true;
}
</script>

<template>
  <view class="m-top-info">
    <view class="u-info-l">
      <image :src="images.logo['logo-256']" class="u-logo" />
      <text class="u-title">{{ SERIES.showName }}</text>
    </view>
    <view class="u-info-r">
      <template v-for="tool in rightTools" :key="tool">
        <svg-icon
          v-if="tool === 'lang'"
          name="lang"
          class="u-svg-icon"
          @tap="onOpenLocale"
        />
        <svg-icon
          v-if="tool === 'transactions'"
          name="record-1"
          class="u-svg-icon u-svg-icon-record-1"
          @tap="navigateTo('/pages/fund-record/index')"
        />
      </template>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@use '@/styles/vars.scss' as *;

.m-top-info {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: var(--top-info-height);
  border-bottom: 2rpx solid #f2f3f5;
  padding: 0 var(--h-gap);
  max-width: var(--page-max-width, #{$max-width});
  margin: 0 auto;
  background: #fff;
  .u-info-l {
    display: flex;
    align-items: center;
    .u-logo {
      width: 56rpx;
      height: 56rpx;
      border-radius: 10rpx;
      margin-right: 16rpx;
    }
    .u-title {
      font-size: 28rpx;
      font-weight: bold;
    }
  }
  .u-info-r {
    display: flex;
    .u-svg-icon {
      width: 32rpx !important;
      margin-left: 25rpx;
    }
  }
}
</style>
