<script setup>
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { getIncomeRecord } from '@/apis/finance';

// 收益记录列表
const recordList = ref([]);
// 加载状态
const loading = ref(false);
// 分页
const pageNum = ref(1);
const pageSize = ref(10);
const hasMore = ref(true);
// 订单号
const orderNo = ref('');
// 产品名称
const productName = ref('');

// 获取收益记录
const fetchRecordList = async (loadMore = false) => {
  if (loading.value) return;

  loading.value = true;
  const currentPage = loadMore ? pageNum.value + 1 : 1;

  const res = await getIncomeRecord({
    pageNum: currentPage,
    pageSize: pageSize.value,
    startTime: '',
    endTime: '',
    orderNo: orderNo.value
  });

  if (res && Array.isArray(res.list)) {
    if (loadMore) {
      recordList.value = [...recordList.value, ...res.list];
    } else {
      recordList.value = res.list;
    }
    pageNum.value = currentPage;
    hasMore.value = res.nextPage;
  }
  loading.value = false;
};

// 下拉刷新
const onRefresh = async () => {
  pageNum.value = 1;
  hasMore.value = true;
  await fetchRecordList();
};

// 触底加载
const onLoadMore = () => {
  if (!hasMore.value || loading.value) return;
  fetchRecordList(true);
};

onLoad((options) => {
  if (options.orderNo) {
    orderNo.value = options.orderNo;
  }
  if (options.productName) {
    productName.value = options.productName;
  }
  fetchRecordList();
});
</script>

<template>
  <page-wrapper>
    <view class="record-page">
      <!-- 导航栏 -->
      <nav-bar :title="$t('finance.record')" :show-back="true" />

      <!-- 记录列表 -->
      <scroll-view class="content-scroll" scroll-y refresher-enabled :refresher-triggered="loading"
        @refresherrefresh="onRefresh" @scrolltolower="onLoadMore">
        <view class="record-list">
          <view v-for="item in recordList" :key="item.incomeDate" class="record-item">
            <view class="item-left">
              <view class="currency-badge">{{ productName || '--' }}</view>
            </view>
            <view class="item-center">
              <text class="date-text">{{ item.incomeDate }}</text>
            </view>
            <view class="item-right">
              <text class="amount-text positive">+{{ item.income }}</text>
            </view>
          </view>
        </view>

        <no-data v-if="!loading && recordList.length === 0" />

        <view v-if="!hasMore && recordList.length > 0" class="footer-tip">
          {{ $t('common.noMore') }}
        </view>
      </scroll-view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.record-page {
  @include hasNavBar();
}

.record-list {
  display: flex;
  flex-direction: column;
  padding: 24rpx 32rpx;
  gap: 24rpx;
}

.record-item {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.item-left {
  margin-right: 24rpx;
}

.currency-badge {
  background: #333;
  color: #fff;
  font-size: 24rpx;
  padding: 8rpx 24rpx;
  border-radius: 24rpx;
  font-weight: 500;
}

.item-center {
  flex: 1;
}

.date-text {
  font-size: 28rpx;
  color: #666;
}

.item-right {
  text-align: right;
}

.amount-text {
  font-size: 32rpx;
  font-weight: 500;
}

.amount-text.positive {
  color: #34C759;
}

.amount-text.negative {
  color: #FF3B30;
}

.footer-tip {
  text-align: center;
  padding: 32rpx 0;
  color: #999;
  font-size: 24rpx;
}
</style>
