<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

const kefuUrl = ref('');
const loading = ref(true);

onLoad((options) => {
  if (options.url) {
    kefuUrl.value = decodeURIComponent(options.url);
  }
});

const handleWebviewLoad = () => {
  loading.value = false;
};

const handleWebviewError = (e) => {
  console.error('webview加载失败', e);
  loading.value = false;
  uni.showToast({
    title: uni.$t('error.loadFailed'),
    icon: 'none'
  });
};
</script>

<template>
  <page-wrapper>
    <view class="customer-service-page">
      <nav-bar :title="$t('common.customerService')" />
      <view class="webview-wrapper">
        <view v-if="loading" class="loading-container">
          <u-loading-icon mode="circle" size="30"></u-loading-icon>
          <text class="loading-text">{{ $t('common.loading') }}</text>
        </view>
        <web-view
          v-if="kefuUrl"
          :src="kefuUrl"
          @load="handleWebviewLoad"
          @error="handleWebviewError"
        ></web-view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.customer-service-page {
  @include hasNavBar();
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.webview-wrapper {
  flex: 1;
  width: 100%;
  position: relative;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: 24rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}
</style>
