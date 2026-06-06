<script setup>
import { ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';

const commonStore = useCommonStore();

const detailData = ref({
  stockName: '',
  stockCode: '',
  orderDirection: 0,
  orderNum: 0,
  buyOrderPrice: 0,
  buyDiscount: 0,
  orderTotalPrice: 0,
  buyOrderTime: '',
  listTime: ''
});

onLoad((options) => {
  if (options.data) {
    try {
      detailData.value = JSON.parse(decodeURIComponent(options.data));
    } catch (error) {
      console.error('解析详情数据失败', error);
    }
  }
});

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper>
    <view class="detail-page">
      <nav-bar :title="$t('bulk.detail')" center>
        <template #right>
          <text class="nav-stock-name">{{ detailData.stockName }}</text>
        </template>
      </nav-bar>

      <view class="detail-content">
        <!-- 成功图标 -->
        <view class="success-section">
          <view class="success-icon">
            <text class="icon-check">✓</text>
          </view>
          <text class="status-text">{{ $t('bulk.success') }}</text>
        </view>

        <!-- 详情信息 -->
        <view class="detail-card">
          <view class="detail-row">
            <text class="detail-label">{{ $t('bulk.orderStatus') }}</text>
            <text class="detail-value">{{ detailData.orderDirection === 0 ? $t('bulk.success') : $t('bulk.pending') }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">{{ $t('bulk.marketPrice') }}</text>
            <text class="detail-value">{{ Number(detailData.orderTotalPrice).toFixed(5) }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">{{ $t('bulk.buyPrice') }}</text>
            <text class="detail-value">{{ Number(detailData.buyOrderPrice).toFixed(5) }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">{{ $t('bulk.applyNum') }}</text>
            <text class="detail-value">{{ detailData.orderNum }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">{{ $t('bulk.orderTime') }}</text>
            <text class="detail-value">{{ detailData.buyOrderTime || detailData.listTime }}</text>
          </view>
          <view class="detail-row">
            <text class="detail-label">{{ $t('bulk.fee') }}</text>
            <text class="detail-value">{{ Number(detailData.buyOrderPrice).toFixed(5) }}</text>
          </view>
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.detail-page {
  @include hasNavBar();
}

.detail-content {
  padding: 48rpx 32rpx;
}

.nav-stock-name {
  font-size: 28rpx;
  color: #333;
}

.success-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 48rpx;
}

.success-icon {
  width: 100rpx;
  height: 100rpx;
  background: #00c087;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-check {
  font-size: 72rpx;
  color: #fff;
  font-weight: bold;
}

.status-text {
  font-size: 36rpx;
  font-weight: 600;
  color: #333;
}

.detail-card {
  border-bottom: 2rpx solid #f0f0f0;
  border-radius: 24rpx;
  padding: 32rpx;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 2rpx solid #f5f5f5;

  &:last-child {
    border-bottom: none;
  }
}

.detail-label {
  font-size: 28rpx;
  color: #666;
}

.detail-value {
  font-size: 28rpx;
  color: #333;
  font-weight: 600;
  text-align: right;
}
</style>
