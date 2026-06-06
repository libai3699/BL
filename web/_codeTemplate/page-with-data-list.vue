<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils';
import { solvePagingData } from '@/utils/ref';
import { tgShowBack } from '@/utils/telegram';

const commonStore = useCommonStore();

// 数据 - 列表
const refDataBoxList = ref({
  details: null,
  isEmpty: () => refDataBoxList.value.details?.length === 0,
  isNotGetData: () =>
    refDataBoxList.value.loading ||
    refDataBoxList.value.error ||
    refDataBoxList.value.isEmpty(),
  loading: true,
  error: false,
  errorText: '',
  pageNum: 1,
  hasNextPage: false,
  loadmoreStatus: 'init'
});

// 请求数据 - 详情
const doReq = (isFirstPage = true) => {
  solvePagingData({
    refData: refDataBoxList,
    isFirstPage,
    reqMethod: replace,
    reqParams: replace,
    onSuccess(res) {}
  });
};

// tg登录后回调
const onLogin = async () => {
  doReq();
};

onShow(() => {
  // 显示tg返回按钮
  tgShowBack();
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <login-box
    @onLogin="onLogin"
    :class="{ 'g-flex-whole-wrapper': refDataBoxList.isNotGetData() }"
  >
    <view
      class="m-content"
      :class="{ 'g-flex-whole-wrap': refDataBoxList.isNotGetData() }"
    >
      <data-box
        :isPosition="true"
        :loading="refDataBoxList.loading"
        :isError="refDataBoxList.error"
        :errorText="refDataBoxList.errorText"
        :isEmpty="refDataBoxList.isEmpty()"
        @onErrorRetry="doReq"
        :loadmoreStatus="refDataBoxList.loadmoreStatus"
        @scrolltolower="doReq(false)"
        @loadmore="doReq(false)"
      >
        <view class="m-replace">
          <view v-for="item in refDataBoxList.details" :key="item.id"> </view>
        </view>
      </data-box>
    </view>
  </login-box>
</template>

<style lang="scss" scoped>
// .m-replace {
// }
</style>
