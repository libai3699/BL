<script setup>
import { onMounted, ref, watch } from 'vue';

import { reqUserRechargeList } from '@/apis/user/recharge';
import { solvePagingData } from '@/utils/solveData';

const props = defineProps({
  searchKey: {
    type: String,
    default: ''
  }
});

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

// 监控搜索
watch(
  () => props.searchKey,
  () => {
    // 重新请求列表数据
    getData(true);
  }
);

// 获取状态样式
function getStyle(item) {
  return {
    's-success': item.orderStatus === 1,
    's-error': item.orderStatus === 2,
    's-minor': item.orderStatus === 0
  };
}

// 请求数据 - 详情
function getData(isFirstPage = true) {
  const reqParams = {};
  if (props.searchKey) {
    reqParams.searchKey = props.searchKey;
  }
  solvePagingData({
    refData: refDataBoxList,
    isFirstPage,
    reqMethod: reqUserRechargeList,
    reqParams,
    onSuccess(res) {}
  });
}

onMounted(() => {
  getData();
});

defineExpose({
  getData
});
</script>

<template>
  <data-box
    :loading="refDataBoxList.loading"
    :isError="refDataBoxList.error"
    :errorText="refDataBoxList.errorText"
    :isEmpty="refDataBoxList.isEmpty()"
    @onErrorRetry="getData"
    :loadmoreStatus="refDataBoxList.loadmoreStatus"
    @scrolltolower="getData(false)"
    @loadmore="getData(false)"
  >
    <view class="m-record-list">
      <view class="u-item" v-for="item in refDataBoxList.details" :key="item.id">
        <view class="u-record-row">
          <view class="u-label">
            {{ $t('mine.recharge.record.list.field.coinType') }}
          </view>
          <view class="u-value" :class="getStyle(item)">{{
            item.amtType === 1 ? 'USD' : 'MXN'
          }}</view>
        </view>

        <view class="u-record-row">
          <view class="u-label">
            {{ $t('mine.recharge.record.list.field.progress') }}
          </view>
          <view class="u-value" :class="getStyle(item)">
            {{ $t(`mine.recharge.record.list.options.progress.${item.orderStatus}`) }}
          </view>
        </view>

        <view class="u-record-row">
          <view class="u-label">
            {{ $t('mine.recharge.record.list.field.payAmount') }}
          </view>
          <view class="u-value">
            <c-amount :value="item.payAmt" />
            {{ item.amtType === 1 ? 'USD' : 'MXN' }}</view
          >
        </view>

        <view class="u-record-row">
          <view class="u-label">
            {{ $t('mine.recharge.record.list.field.createTime') }}
          </view>
          <view class="u-value">{{ item.payTime }}</view>
        </view>
      </view>
    </view>
  </data-box>
</template>

<style lang="scss" scoped></style>
