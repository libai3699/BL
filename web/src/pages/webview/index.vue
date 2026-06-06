<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';

const url = ref('');

onLoad((options) => {
  if (options.url) {
    url.value = decodeURIComponent(options.url);
  }
});
</script>

<template>
  <view class="webview-container">
    <web-view v-if="url" :src="url"></web-view>
  </view>
</template>

<style lang="scss" scoped>
.webview-container {
  width: 100%;
  height: 100vh;
}
</style>
