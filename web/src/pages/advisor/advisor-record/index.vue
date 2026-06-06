<script setup>
import { computed, reactive, ref } from 'vue';
import { onLoad, onReachBottom, onShow } from '@dcloudio/uni-app';

import { appendFollow, cancelFollow, queryFollowRecord } from '@/apis/advisor';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';

const commonStore = useCommonStore();

const tabs = [
  { title: 'advisor.applying', status: 1, allowAdd: false },
  { title: 'advisor.participating', status: 2, allowAdd: true },
  { title: 'advisor.cancelled', status: 4, allowAdd: false },
  { title: 'advisor.finished', status: 5, allowAdd: false },
  { title: 'advisor.rejected', status: 3, allowAdd: false }
];

const activeTab = ref(0);
const mentorId = ref('');

const listState = reactive(
  Object.fromEntries(
    tabs.map((item) => [item.status, { list: [], pageNum: 1, finished: false, loading: false }])
  )
);

const showAddPopup = ref(false);
const addAmount = ref('');
const currentRecordId = ref('');

const currentStatus = computed(() => tabs[activeTab.value].status);

const currentList = computed(() => listState[currentStatus.value].list);
const currentState = computed(() => listState[currentStatus.value]);

const loadRecords = async (status, reset = false) => {
  const state = listState[status];
  if (!state || state.loading) return;

  if (reset) {
    state.pageNum = 1;
    state.finished = false;
    state.list = [];
  }

  if (state.finished) return;

  state.loading = true;
  const res = await queryFollowRecord({
    status,
    pageNum: state.pageNum,
    pageSize: 10
  });

  const listData = Array.isArray(res?.list)
    ? res.list
    : Array.isArray(res?.data?.list)
      ? res.data.list
      : [];
  const total = res?.total ?? res?.data?.total ?? listData.length;

  state.list = state.pageNum === 1 ? listData : state.list.concat(listData);
  state.finished = state.list.length >= total;
  if (!state.finished) {
    state.pageNum += 1;
  }
  state.loading = false;
};

const switchTab = (index) => {
  if (activeTab.value === index) return;
  activeTab.value = index;
  const status = currentStatus.value;
  if (!listState[status].list.length) {
    loadRecords(status, true);
  }
};

const openAddPopup = (recordId) => {
  currentRecordId.value = recordId;
  addAmount.value = '';
  showAddPopup.value = true;
};

const submitAppend = async () => {
  const amount = Number(addAmount.value);
  if (!amount || amount <= 0) {
    uni.showToast({ title: uni.$t('advisor.enterValidAmount'), icon: 'none' });
    return;
  }
  await appendFollow({ id: currentRecordId.value, amount });
  uni.showToast({ title: uni.$t('advisor.appendSuccess'), icon: 'success' });
  showAddPopup.value = false;
  loadRecords(currentStatus.value, true);
};

const handleCancel = (recordId) => {
  uni.showModal({
    title: uni.$t('common.prompt'),
    content: uni.$t('advisor.confirmCancel'),
    cancelText: uni.$t('common.cancel'),
    confirmText: uni.$t('common.confirm'),
    success: async ({ confirm }) => {
      if (!confirm) return;
      await cancelFollow({ id: recordId });
      uni.showToast({ title: uni.$t('advisor.cancelSuccess'), icon: 'success' });
      loadRecords(currentStatus.value, true);
    }
  });
};

const viewDetail = (item) => {
  navigateTo(
    `/pages/advisor/record-detail/index?id=${item.id}&activeTab=${activeTab.value}&mentorId=${mentorId.value}`
  );
};

const handleBack = () => {
  if (mentorId.value) {
    navigateTo(
      `/pages/advisor/apply-follow/index?mentorId=${mentorId.value}`
    );
  } else {
    goBack();
  }
};

const initPage = () => {
  loadRecords(currentStatus.value, true);
};

onLoad((options) => {
  mentorId.value = options?.mentorId || '';
  if (options?.activeTab) {
    const index = Number(options.activeTab);
    if (!Number.isNaN(index) && index >= 0 && index < tabs.length) {
      activeTab.value = index;
    }
  }
  initPage();
});

onShow(() => {
  commonStore.fnBack = handleBack;
});

onReachBottom(() => {
  loadRecords(currentStatus.value);
});
</script>

<template>
  <page-wrapper>
    <view class="record-page">
      <nav-bar :title="$t('advisor.advisorRecord')" @back="handleBack" />

      <view class="page-content">
        <view class="tabs">
          <view v-for="(tab, index) in tabs" :key="tab.status" class="tab-item" :class="{ active: index === activeTab }"
            @click="switchTab(index)">
            {{ $t(tab.title) }}
          </view>
        </view>

        <scroll-view class="list" scroll-y>
          <view v-if="currentList.length" class="card" v-for="item in currentList" :key="item.id">
            <view class="card-header">
              <view class="order">
                <text>{{ $t('advisor.orderNo') }}：</text>
                <text class="accent">{{ item.followNo }}</text>
              </view>
              <text class="status-tag" :class="`status-${item.status}`">{{$t(tabs.find((t) => t.status ===
                item.status)?.title) || '-'}}</text>
            </view>

            <view class="card-body">
              <view class="row">
                <text class="label">{{ $t('advisor.targetName') }}</text>
                <text class="value highlight">{{ item.packageName }}</text>
              </view>
              <view class="row">
                <text class="label">{{ $t('advisor.investor') }}</text>
                <text class="value">{{ item.realName || item.investorName || '--' }}</text>
              </view>
              <view class="row">
                <text class="label">{{ $t('advisor.expiryDate') }}</text>
                <text class="value">{{ item.endTime || '--' }}</text>
              </view>
              <view class="row">
                <text class="label">{{ $t('advisor.targetFund') }}</text>
                <text class="value highlight">{{ item.amount }} {{ $t('advisor.yuan') }}</text>
              </view>
              <view class="row">
                <text class="label">{{ $t('advisor.extractCommission') }}</text>
                <text class="value highlight">{{ item.salaryRate }}%</text>
              </view>
              <view class="row">
                <text class="label">{{ $t('advisor.applyTime') }}</text>
                <text class="value">{{ item.applyTime || '--' }}</text>
              </view>
            </view>

            <view class="card-actions">
              <view v-if="tabs[activeTab].allowAdd && item.isAdd === 0" class="action-btn outline"
                @click="openAddPopup(item.id)">{{ $t('advisor.append') }}</view>
              <view v-if="item.status === 1" class="action-btn danger" @click="handleCancel(item.id)">{{
                $t('advisor.cancelOrder') }}</view>
              <view class="action-btn primary" @click="viewDetail(item)">{{ $t('advisor.viewDetail') }}</view>
            </view>
          </view>

          <no-data v-else />

          <view v-if="currentState.loading" class="footer-tip">{{ $t('advisor.loadingMore') }}</view>
          <view v-else-if="currentState.finished && currentList.length" class="footer-tip">
            {{ $t('advisor.noMoreData') }}
          </view>
        </scroll-view>
      </view>

      <u-popup :show="showAddPopup" mode="center" v-if="showAddPopup" @close="showAddPopup = false">
        <view class="popup-content">
          <text class="popup-title">{{ $t('advisor.appendAmount') }}</text>
          <input v-model="addAmount" type="number" class="popup-input" :placeholder="$t('advisor.enterAppendAmount')" />
          <view class="popup-actions">
            <button class="popup-btn cancel" @click="showAddPopup = false">{{ $t('common.cancel') }}</button>
            <button class="popup-btn confirm" @click="submitAppend">{{ $t('common.confirm') }}</button>
          </view>
        </view>
      </u-popup>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.record-page {
  @include hasNavBar();
  background: #f5f5f5;
}

.page-content {
  padding: 16rpx 12rpx 32rpx;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
  height: calc(100vh - var(--status-bar-height) - var(--nav-bar-height));
  box-sizing: border-box;
}

.tabs {
  display: flex;
  background: #fff;
  border-bottom: 2rpx solid #f0f0f0;
  margin-bottom: 16rpx;
}

.tab-item {
  position: relative;
  flex: 1;
  text-align: center;
  padding: 24rpx 0;
  font-size: 24rpx;
  color: #666;
  transition: color 0.3s;

  &.active {
    color: #00b6e6;
    font-weight: 600;

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 60rpx;
      height: 4rpx;
      background: #00b6e6;
      border-radius: 2rpx;
    }
  }
}

.list {
  flex: 1;
  overflow: hidden;
}

.card {
  background: #fff;
  border-radius: 24rpx;
  padding: 28rpx;
  box-shadow: 0 14rpx 28rpx rgba(0, 182, 230, 0.12);
  margin-bottom: 24rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;

  .order {
    font-size: 27rpx;
    color: #111;

    .accent {
      color: #00b6e6;
      margin-left: 8rpx;
    }
  }

  .status-tag {
    font-size: 24rpx;
    padding: 8rpx 16rpx;
    border-radius: 8rpx;

    // 申请中 - 橙色
    &.status-1 {
      background: #fff7e6;
      color: #fa8c16;
    }

    // 参与中 - 蓝色
    &.status-2 {
      background: #e6f7ff;
      color: #1890ff;
    }

    // 已驳回 - 红色
    &.status-3 {
      background: #fff1f0;
      color: #ff4d4f;
    }

    // 已撤销 - 灰色
    &.status-4 {
      background: #f5f5f5;
      color: #999;
    }

    // 已结束 - 绿色
    &.status-5 {
      background: #f6ffed;
      color: #52c41a;
    }
  }
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 12rpx;

  .row {
    display: flex;
    justify-content: space-between;
    font-size: 26rpx;
    color: #333;

    .label {
      color: #666;
    }

    .value {
      max-width: 60%;
      text-align: right;
    }

    .highlight {
      color: #00b6e6;
      font-weight: 600;
    }
  }
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 24rpx;

  .action-btn {
    border-radius: 8rpx;
    font-size: 26rpx;
    padding: 15rpx 35rpx;
    text-align: center;
    line-height: 1;

    &.primary {
      background: #00b6e6;
      color: #fff;
    }

    &.outline {
      background: #fff;
      color: #00b6e6;
      border: 2rpx solid #00b6e6;
    }

    &.danger {
      background: #ff4d4f;
      color: #fff;
    }
  }
}

.empty,
.footer-tip {
  text-align: center;
  color: #999;
  font-size: 26rpx;
  padding: 24rpx 0;
}

.popup-content {
  width: 560rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
}

.popup-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #111;
  margin-bottom: 24rpx;
}

.popup-input {
  width: 100%;
  height: 88rpx;
  border-radius: 16rpx;
  border: 2rpx solid rgba(0, 182, 230, 0.4);
  padding: 0 24rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  margin-bottom: 32rpx;
  margin-top: 25rpx;
}

.popup-actions {
  display: flex;
  justify-content: flex-end;
  gap: 24rpx;
}

.popup-btn {
  min-width: 120rpx;
  height: 72rpx;
  border-radius: 36rpx;
  font-size: 28rpx;
  border: none;
}

.popup-btn.cancel {
  background: #f2f2f2;
  color: #666;
}

.popup-btn.confirm {
  background: #00b6e6;
  color: #fff;
}
</style>
