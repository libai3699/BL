<script setup>
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';

import List from './components/list.vue';

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
      <nav-bar :title="$t('mine.withdraw.record.title')" />

      <list />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar(40rpx);
  padding-left: 24rpx;
  padding-right: 24rpx;
}
</style>
