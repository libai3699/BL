<script setup>
import { computed, ref } from 'vue';
import { onShow, onReachBottom, onPullDownRefresh } from '@dcloudio/uni-app';
import {
  getMessageList,
  getUnreadMessageCount,
  readAllMessages,
  updateNoticeStatus
} from '@/apis/message';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack } from '@/utils/navigate';
import { isMaintaining } from '@/utils/maintain';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const commonStore = useCommonStore();
const userStore = useUserStore();

const activeCategory = ref('all');
const messageList = ref([]);
const loading = ref(false);
const pageNum = ref(1);
const pageSize = ref(10);
const hasMore = ref(true);

const categoryTabs = [
  { key: 'all', labelKey: 'message.tabAll' },
  { key: 'trade', labelKey: 'message.tabTrade' },
  { key: 'system', labelKey: 'message.tabSystem' },
  { key: 'activity', labelKey: 'message.tabActivity' }
];

const getItemCategory = (item) => {
  const rawType = item?.type ?? item?.messageType ?? item?.noticeType;
  if (rawType != null && rawType !== '') {
    const n = Number(rawType);
    if (n === 1) return 'trade';
    if (n === 2) return 'system';
    if (n === 3) return 'activity';
  }
  const text = `${item?.typeName || ''} ${item?.content || ''}`.toLowerCase();
  if (/交易|买入|卖出|成交|委托|order|trade|nvda|tsla/i.test(text)) return 'trade';
  if (/活动|vip|优惠|礼|促销|coupon|activity/i.test(text)) return 'activity';
  return 'system';
};

const getMsgVisual = (item) => {
  const cat = getItemCategory(item);
  const map = {
    trade: { emoji: '✓', tone: 'trade' },
    system: { emoji: '🪪', tone: 'system' },
    activity: { emoji: '🎉', tone: 'activity' }
  };
  return map[cat] || { emoji: '⏰', tone: 'system' };
};

const displayList = computed(() => {
  if (activeCategory.value === 'all') return messageList.value;
  return messageList.value.filter(
    (item) => getItemCategory(item) === activeCategory.value
  );
});

const fetchUnreadCount = async () => {
  if (
    commonStore.maintenance.needMaintenance === 'loading' ||
    isMaintaining(commonStore)
  ) {
    return;
  }
  try {
    const res = await getUnreadMessageCount();
    const count = Number(res ?? 0);
    commonStore.setUnreadCount(
      Number.isFinite(count) && count > 0 ? Math.floor(count) : 0
    );
  } catch {
    /* ignore */
  }
};

const fetchMessageList = async (isRefresh = false) => {
  if (loading.value) return;
  if (!hasMore.value && !isRefresh) return;

  loading.value = true;

  if (isRefresh) {
    pageNum.value = 1;
    hasMore.value = true;
  }

  try {
    const res = await getMessageList({
      userId: userStore.userInfo.value?.id || userStore.userInfo.value?.userId,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    });
    const list = res ? (Array.isArray(res) ? res : res.list || []) : [];

    if (isRefresh) {
      messageList.value = list;
    } else {
      messageList.value = [...messageList.value, ...list];
    }

    hasMore.value = list.length >= pageSize.value;
  } catch {
    if (isRefresh) messageList.value = [];
    hasMore.value = false;
  } finally {
    loading.value = false;
  }
};

const handleTabChange = (key) => {
  activeCategory.value = key;
};

const handleReadAll = async () => {
  await readAllMessages();
  uni.showToast({
    title: t('message.readAllSuccess'),
    icon: 'success'
  });
  commonStore.setUnreadCount(0);
  await fetchMessageList(true);
  fetchUnreadCount();
};

const handleMessageClick = async (item) => {
  if (item.status === 1) {
    await updateNoticeStatus({ noticeId: item.id });
    item.status = 2;
    await fetchUnreadCount();
  }
};

onPullDownRefresh(async () => {
  await fetchMessageList(true);
  await fetchUnreadCount();
  uni.stopPullDownRefresh();
});

onReachBottom(() => {
  if (hasMore.value && !loading.value) {
    pageNum.value++;
    fetchMessageList();
  }
});

onShow(() => {
  commonStore.fnBack = goBack;
  fetchUnreadCount();
  fetchMessageList(true);
});
</script>

<template>
  <page-wrapper>
    <view class="msg-page">
      <nav-bar :title="$t('message.title')">
        <template #right>
          <text class="msg-read-all" @tap="handleReadAll">{{
            $t('message.readAll')
          }}</text>
        </template>
      </nav-bar>

      <view class="msg-tabs">
        <view
          v-for="tab in categoryTabs"
          :key="tab.key"
          class="msg-tab"
          :class="{ active: activeCategory === tab.key }"
          @tap="handleTabChange(tab.key)"
        >
          <text>{{ $t(tab.labelKey) }}</text>
          <text
            v-if="tab.key === 'all' && commonStore.unreadCount > 0"
            class="msg-tab__badge"
          >
            {{ commonStore.unreadCount > 99 ? '99+' : commonStore.unreadCount }}
          </text>
        </view>
      </view>

      <view class="msg-body">
        <view v-if="loading && messageList.length === 0" class="msg-loading">
          <u-loading-icon mode="circle" size="40" />
        </view>

        <template v-else-if="displayList.length > 0">
          <view
            v-for="item in displayList"
            :key="item.id"
            class="msg-row"
            @tap="handleMessageClick(item)"
          >
            <view class="msg-row__ico" :class="getMsgVisual(item).tone">
              <text>{{ getMsgVisual(item).emoji }}</text>
            </view>
            <view class="msg-row__main">
              <view class="msg-row__head">
                <text class="msg-row__title">{{ item.typeName }}</text>
                <view v-if="item.status === 1" class="msg-row__dot" />
              </view>
              <text class="msg-row__desc">{{ item.content }}</text>
              <text class="msg-row__time">{{ item.addTime }}</text>
            </view>
          </view>

          <view v-if="loading" class="msg-loading msg-loading--more">
            <u-loading-icon mode="circle" size="32" />
          </view>
          <view v-else-if="!hasMore" class="msg-no-more">
            <text>{{ $t('common.noMore') }}</text>
          </view>
        </template>

        <view v-else class="msg-empty">
          <text>{{ $t('common.noData') }}</text>
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.msg-page {
  height: 100vh;
  max-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--stock-page-bg, #eef2f8);
  @include hasNavBar();
}

.msg-read-all {
  font-size: 26rpx;
  color: var(--stock-ink-3, #64748b);
  font-weight: 600;
  padding-right: 8rpx;
}

.msg-tabs {
  flex-shrink: 0;
  display: flex;
  align-items: stretch;
  background: #fff;
  padding: 0 8rpx;
  border-bottom: 1rpx solid var(--stock-line, #e5e9f2);
}

.msg-tab {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6rpx;
  padding: 22rpx 24rpx;
  font-size: 26rpx;
  font-weight: 500;
  color: var(--stock-ink-3, #64748b);
  white-space: nowrap;

  &.active {
    color: var(--stock-ink, #0f172a);
    font-weight: 700;

    &::after {
      content: '';
      position: absolute;
      left: 24rpx;
      right: 24rpx;
      bottom: -1rpx;
      height: 5rpx;
      background: var(--stock-gold, #d4a24c);
      border-radius: 3rpx 3rpx 0 0;
    }
  }

  &__badge {
    padding: 2rpx 10rpx;
    border-radius: 16rpx;
    font-size: 18rpx;
    font-weight: 700;
    color: #fff;
    background: var(--stock-red, #ef4444);
    line-height: 1.3;
  }
}

.msg-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
  background: #fff;
}

.msg-row {
  display: flex;
  align-items: flex-start;
  gap: 24rpx;
  padding: 28rpx;
  border-bottom: 1rpx solid var(--stock-line, #e5e9f2);
  background: #fff;

  &:last-child {
    border-bottom: none;
  }

  &__ico {
    width: 76rpx;
    height: 76rpx;
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    font-size: 28rpx;
    color: #fff;

    &.trade {
      background: linear-gradient(135deg, var(--stock-green, #16a34a), #059669);
    }

    &.system {
      background: linear-gradient(
        135deg,
        var(--stock-navy, #0b1e47),
        var(--stock-blue, #2563eb)
      );
    }

    &.activity {
      background: linear-gradient(135deg, #ec4899, #7c3aed);
    }
  }

  &__main {
    flex: 1;
    min-width: 0;
  }

  &__head {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  &__title {
    font-size: 26rpx;
    font-weight: 700;
    color: var(--stock-ink, #0f172a);
    @include ellipsis;
  }

  &__dot {
    flex-shrink: 0;
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    background: var(--stock-red, #ef4444);
  }

  &__desc {
    display: block;
    margin-top: 8rpx;
    font-size: 23rpx;
    color: var(--stock-ink-2, #475569);
    line-height: 1.5;
    @include ellipsis2(2);
  }

  &__time {
    display: block;
    margin-top: 12rpx;
    font-size: 21rpx;
    color: var(--stock-ink-3, #64748b);
  }
}

.msg-loading {
  display: flex;
  justify-content: center;
  padding: 120rpx 0;

  &--more {
    padding: 32rpx 0;
  }
}

.msg-no-more,
.msg-empty {
  padding: 80rpx 0;
  text-align: center;
  font-size: 24rpx;
  color: var(--stock-ink-3, #64748b);
}
</style>
