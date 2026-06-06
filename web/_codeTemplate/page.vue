<script setup>
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';

const commonStore = useCommonStore();

// 页面初始化回调
const onPageInit = async () => {};

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar :title="$t('')" />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar();
}
</style>
