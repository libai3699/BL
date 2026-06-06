<script setup>
import { useCommonStore } from '@/stores/common';

const commonStore = useCommonStore();

const props = defineProps({
  title: {
    type: String,
    default: ''
  },
  showBack: {
    type: Boolean,
    default: true
  },
  backgroundColor: {
    type: String,
    default: '#fff'
  },
  color: {
    type: String,
    default: '#333'
  }
});

const emit = defineEmits(['back']);

const handleBack = () => {
  commonStore.fnBack();
  emit('back');
};
</script>

<template>
  <view class="nav-bar" :style="{ background: backgroundColor, color }">
    <view class="nav-bar-content">
      <view class="nav-bar-left" v-if="showBack" @click="handleBack">
        <view class="back-icon">
          <text class="icon">←</text>
        </view>
      </view>
      <view class="nav-bar-title">{{ title }}</view>
      <view class="nav-bar-right">
        <slot name="right"></slot>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@use '@/styles/vars.scss' as *;

.nav-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 99;
  padding-top: var(--status-bar-height);
  max-width: var(--page-max-width, #{$max-width});
  margin: 0 auto;
}

.nav-bar-content {
  display: flex;
  align-items: center;
  height: var(--nav-bar-height);
  padding: 0 32rpx;
  position: relative;
}

.nav-bar-left {
  position: absolute;
  left: 32rpx;
  display: flex;
  align-items: center;
}

.back-icon {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  font-size: 40rpx;
  font-weight: bold;
}

.nav-bar-title {
  flex: 1;
  text-align: center;
  font-size: 28rpx;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding: 0 80rpx;
}

.nav-bar-right {
  position: absolute;
  right: 32rpx;
  display: flex;
  align-items: center;
}
</style>
