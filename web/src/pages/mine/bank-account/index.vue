<script setup>
import { ref } from 'vue';
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const refSearchKey = ref('');
const refBankAccountList = ref(null);
// Tabs 配置
const refTabs = ref([
  {
    label: () => uni.$t('mine.bank-account.index.tabs.account'),
    value: 'account'
  }
]);
const refTab = ref('account');

// 搜索回调
function onSearch() {}

// 页面初始化回调
const onPageInit = async () => {};

onLoad(() => {
  uni.$on('bankAccountListNeedRefresh', () => {
    // 重新请求列表数据
    refBankAccountList.value?.getData(true);
  });
});

onUnload(() => {
  uni.$off('bankAccountListNeedRefresh');
});

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar :title="$t('mine.bank-account.index.title')" />
      <!-- 搜索框 -->
      <c-search v-model="refSearchKey" @input="onSearch" />

      <c-tabs class="u-cur-tabs" :tabs="refTabs" :tab="refTab" mode="normal-black" />

      <!-- 银行账户列表 -->
      <c-bank-account-list
        :search-key="refSearchKey"
        class="u-cur-bank-account-list"
        ref="refBankAccountList"
      />

      <!-- 底部按钮 -->
      <c-main-btn
        class="m-fixed-bottom-btn"
        :defaultText="$t('mine.bank-account.index.bottomBtnText')"
        @tap="navigateTo('/pages/mine/bank-account/edit/index')"
      />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar(40rpx);
  @include hasBottomBtn();
  padding-left: 24rpx;
  padding-right: 24rpx;
  .u-cur-tabs {
    margin-top: 38rpx;
    :deep(.u-item) {
      margin-left: 0;
    }
  }
  .u-cur-bank-account-list {
    margin-top: 30rpx;
  }
}
</style>
