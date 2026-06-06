<script setup>
import { computed, onMounted, ref } from 'vue';

import { reqUserBankGetBankList } from '@/apis/user/bank';
import { solvePagingData } from '@/utils/solveData';

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

function normalizeBankSearchText(value) {
  return String(value ?? '')
    .trim()
    .replace(/\s+/g, '')
    .toLowerCase();
}

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
  const details = Array.isArray(refDataBoxList.value.details)
    ? refDataBoxList.value.details
    : [];
  const searchKeyLower = normalizeBankSearchText(props.searchKey);

  if (!searchKeyLower) {
    return details;
  }

  return details.filter((item) => {
    const bankName = normalizeBankSearchText(item?.bankName);
    const bankCode = normalizeBankSearchText(item?.bankCode);

    return bankName.includes(searchKeyLower) || bankCode.includes(searchKeyLower);
  });
});

// 请求数据 - 详情
function getData(isFirstPage = true) {
  solvePagingData({
    refData: refDataBoxList,
    isFirstPage,
    reqMethod: reqUserBankGetBankList,
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
        :class="{ 's-cur': chooseItem?.bankCode === item.bankCode }"
        v-for="item in cpdFinalDetails"
        :key="item.id"
        @tap="emits('choose', item)"
      >
        <view class="u-item-name"> {{ item.bankName }} ({{ item.bankCode }}) </view>
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
    padding: 46rpx 40rpx 46rpx 54rpx;
    border-bottom: 2rpx solid var(--border-color);
    .u-item-radio {
      display: flex;
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
        background-color: var(--primary-color);
        border-color: var(--primary-color);
      }
    }
  }
}
</style>
