<script setup>
import { onMounted, ref, watch } from 'vue';

import { reqUserBankDelete, reqUserBankGetBankInfoList } from '@/apis/user/bank';
import { navigateTo } from '@/utils/navigate';
import { solvePagingData } from '@/utils/solveData';
import { showConfirm } from '@/utils/uniUtils';

const emits = defineEmits(['choose']);
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

// 请求数据 - 详情
function getData(isFirstPage = true) {
  solvePagingData({
    refData: refDataBoxList,
    isFirstPage,
    reqMethod: reqUserBankGetBankInfoList,
    reqParams: {
      searchKey: props.searchKey
    },
    onSuccess(res) {}
  });
}

// 删除银行账户
function doDel(item) {
  showConfirm(uni.$t('mine.bank-account.index.confirmDelete'), (res) => {
    if (res.confirm) {
      reqUserBankDelete({
        id: item.id
      }).then((res) => {
        // 重新请求列表数据
        getData(true);
      });
    }
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
    <view class="m-bank-account-list">
      <view
        class="u-item"
        @tap="emits('choose', item)"
        v-for="item in refDataBoxList.details"
        :key="item.id"
      >
        <view class="u-item-text">
          {{ item.bankAccount }}
        </view>
        <view class="u-item-text"> {{ item.bankName }} ({{ item.bankCode }}) </view>
        <view class="u-item-text">
          {{ item.bankNo }}
        </view>
        <view class="u-btn-area">
          <up-button
            class="u-btn s-gray"
            @tap="navigateTo(`/pages/mine/bank-account/edit/index?id=${item.id}`)"
          >
            {{ $t('common.edit') }}
          </up-button>
          <up-button class="u-btn s-red" @tap="doDel(item)">
            {{ $t('common.delete') }}
          </up-button>
        </view>
      </view>
    </view>
  </data-box>
</template>

<style lang="scss" scoped>
.m-bank-account-list {
  .u-item {
    padding: 44rpx 46rpx 32rpx 42rpx;
    border: 2rpx solid var(--border-color-weak);
    margin-bottom: 30rpx;
    border-radius: 10rpx;
    &:last-child {
      margin-bottom: 0;
    }
    .u-item-text {
      line-height: 38rpx;
      margin-bottom: 16rpx;
      color: var(--text-minor-color);
    }
    .u-btn-area {
      display: flex;
      justify-content: flex-end;
      border-top: none;
      .u-btn {
        flex-shrink: 0;
        height: 62rpx;
        width: auto;
        margin: 0 0 0 18rpx;
        padding: 0 36rpx;
        border: none;
        border-radius: 10rpx;
        font-weight: bold;
        &:before,
        &:after {
          display: none;
        }
        &.s-gray {
          background: var(--text-weak-color);
          color: var(--text-minor-color);
        }
        &.s-red {
          background: rgba(200, 34, 51, 0.2);
          color: #c82233;
        }
      }
    }
  }
}
</style>
