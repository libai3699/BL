<script setup>
import { computed, onMounted, ref } from 'vue';

import { solvePagingData } from '@/utils/solveData';
import { reqUserRechargeChannel } from '@/apis/user/recharge';

const emits = defineEmits(['choose']);
const props = defineProps({
  // 选择的对象
  chooseItem: {
    type: Object,
    default: null
  },
  // 搜索关键词
  searchKey: {
    type: String,
    default: ''
  }
});

// 数据 - 列表
const refDataBoxList = ref({
  details: null,
  isEmpty: () => cpdFinalDetails.value?.length === 0,
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

// 过滤后的最终列表
const cpdFinalDetails = computed(() => {
  if (!props.searchKey) {
    return refDataBoxList.value.details;
  } else {
    return refDataBoxList.value.details.filter((item) =>
      item.name.includes(props.searchKey)
    );
  }
});

// 请求数据 - 详情
function getData(isFirstPage = true) {
  solvePagingData({
    refData: refDataBoxList,
    isFirstPage,
    reqMethod: reqUserRechargeChannel,
    solveRes(res) {
      return {
        list: res
      };
    },
    showNomore: false,
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
    :isPosition="true"
    :loading="refDataBoxList.loading"
    :isError="refDataBoxList.error"
    :errorText="refDataBoxList.errorText"
    :isEmpty="refDataBoxList.isEmpty()"
    @onErrorRetry="getData"
    :loadmoreStatus="refDataBoxList.loadmoreStatus"
    @scrolltolower="getData(false)"
    @loadmore="getData(false)"
  >
    <view class="m-bank-list">
      <view
        class="u-item"
        :class="{ 's-cur': chooseItem?.id === item.id }"
        v-for="item in cpdFinalDetails"
        :key="item.id"
        @tap="emits('choose', item)"
      >
        <view class="u-item-name"> {{ item.name }} </view>
        <view class="u-item-radio">
          <svg-icon name="correct-2" class="u-svg-icon" />
        </view>
      </view>
    </view>
  </data-box>
</template>

<style lang="scss" scoped>
.m-bank-list {
  .u-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 46rpx 40rpx 46rpx 0;
    border-bottom: 2rpx solid var(--border-color);
    .u-item-radio {
      display: none;
      align-items: center;
      justify-content: center;
      width: 40rpx;
      height: 40rpx;
      border: 2rpx solid var(--border-color);
      border-radius: 20rpx;
      .u-svg-icon {
        width: 20rpx !important;
      }
    }
    &.s-cur {
      .u-item-radio {
        display: flex;
        background-color: var(--primary-color);
        border-color: var(--primary-color);
      }
    }
  }
}
</style>
