<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils';
import { solveNormalData } from '@/utils/ref';
import { tgShowBack } from '@/utils/telegram';

const commonStore = useCommonStore();

// 数据
const refDataBoxDetail = ref({
  detail: null,
  isEmpty: () =>
    refDataBoxDetail.value.detail == null ||
    Object.keys(refDataBoxDetail.value.detail)?.length === 0,
  // 如果数据是数组
  // isEmpty: () => refDataBoxDetail.value.detail?.length === 0,
  isNotGetData: () =>
    refDataBoxDetail.value.loading ||
    refDataBoxDetail.value.error ||
    refDataBoxDetail.value.isEmpty(),
  loading: true,
  error: false,
  errorText: ''
});

// 请求数据 - 详情
const doReq = () => {
  solveNormalData({
    refData: refDataBoxDetail,
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
    :class="{ 'g-flex-whole-wrapper': refDataBoxDetail.isNotGetData() }"
  >
    <view
      class="m-content"
      :class="{ 'g-flex-whole-wrap': refDataBoxDetail.isNotGetData() }"
    >
      <data-box
        :isPosition="true"
        :loading="refDataBoxDetail.loading"
        :isError="refDataBoxDetail.error"
        :errorText="refDataBoxDetail.errorText"
        :isEmpty="refDataBoxDetail.isEmpty()"
        @onErrorRetry="doReq"
      >
        <view class="m-replace"></view>
      </data-box>
    </view>
  </login-box>
</template>

<style lang="scss" scoped>
// .m-replace {
// }
</style>
