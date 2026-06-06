<script setup>
import { ref } from 'vue';
import SERIES from '@/configs/series';
import { switchTab } from '@/utils/navigate';

const props = defineProps({
  page: {
    type: Object,
    default: () => ({})
  }
});

const tabs = SERIES.tabbar.list;
const tradeSheetVisible = ref(false);

const isActive = (item) => {
  return item.route === props.page?.path || item.route === props.page?.fullPath;
};

const goTab = (item) => {
  if (item.route) {
    switchTab(item.route);
  }
};

/** 中间交易按钮：对齐原型，打开交易底部弹层（静态演示） */
const onTradeCenter = () => {
  tradeSheetVisible.value = true;
};
</script>

<template>
  <view class="m-tab-bar s-fixed">
    <view
      v-for="item in tabs.slice(0, 2)"
      :key="item.key"
      class="u-item"
      :class="{ 's-active': isActive(item) }"
      @tap="goTab(item)"
    >
      <svg-icon class="u-cur-icon" :name="item.icon" width="44" height="44" />
      <view class="u-text">{{ item.name?.() }}</view>
    </view>

    <tab-bar-center-btn @click="onTradeCenter" />

    <view
      v-for="item in tabs.slice(2)"
      :key="item.key"
      class="u-item"
      :class="{ 's-active': isActive(item) }"
      @tap="goTab(item)"
    >
      <svg-icon class="u-cur-icon" :name="item.icon" width="44" height="44" />
      <view class="u-text">{{ item.name?.() }}</view>
    </view>

    <trade-bottom-sheet v-model="tradeSheetVisible" />
  </view>
</template>

<style lang="scss" scoped>
@use '@/styles/vars.scss' as *;

.m-tab-bar {
  display: flex;
  justify-content: space-around;
  align-items: center;
  box-sizing: border-box;
  padding: var(--tab-bar-padding);
  height: var(--tab-bar-height);
  border-radius: var(--tab-bar-border-radius);
  border: var(--tab-bar-border);
  border-top: var(--tab-bar-border-top);
  background: var(--tab-bar-bg);
  backdrop-filter: blur(20rpx);
  max-width: var(--page-max-width, #{$max-width});

  &.s-fixed {
    position: fixed;
    bottom: var(--tab-bar-position-bottom);
    left: var(--tab-bar-position-left);
    right: var(--tab-bar-position-right);
    margin: 0 auto;
    z-index: 9;
  }

  .u-item {
    flex: 1;
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 6rpx;
    padding-top: 12rpx;

    .u-cur-icon {
      flex-shrink: 0;
      width: 44rpx !important;
      height: 44rpx !important;
      color: var(--tab-bar-item-text-color);

      :deep(svg) {
        color: inherit;
      }
    }

    .u-text {
      font-size: 22rpx;
      color: var(--tab-bar-item-text-color);
    }

    &.s-active {
      .u-cur-icon {
        color: var(--tab-bar-item-text-color-active);
      }

      .u-text {
        color: var(--tab-bar-item-text-color-active);
        font-weight: 700;
      }
    }
  }
}
</style>
