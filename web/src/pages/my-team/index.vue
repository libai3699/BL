<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar :title="$t('invite.myTeamTitle')" />

      <!-- 顶部统计区域 -->
      <view class="stats-area">
        <view class="stats-row">
          <view class="stats-item">
            <text class="stats-label">{{ $t('invite.groupNum') }}</text>
            <text class="stats-value">
              {{ $t('invite.peopleNum', { num: teamInfo?.totalCount }) }}
            </text>
          </view>
          <view class="stats-item">
            <text class="stats-label">{{ $t('invite.teamTotalAmount') }}</text>
            <text class="stats-value">
              {{ teamInfo?.teamTotalAmount }}
            </text>
          </view>
          <view class="stats-item">
            <text class="stats-label">{{ $t('invite.activeUserCount') }}</text>
            <text class="stats-value">
              {{ $t('invite.peopleNum', { num: teamInfo?.activeUserCount }) }}
            </text>
          </view>
          <view class="stats-item">
            <text class="stats-label">
              {{
                $t('invite.generation', {
                  generation: 1
                })
              }}
            </text>
            <text class="stats-value">
              {{
                $t('invite.peopleNum', {
                  num: teamInfo?.level1Count
                })
              }}
            </text>
          </view>
          <view class="stats-item">
            <text class="stats-label">
              {{
                $t('invite.generation', {
                  generation: 2
                })
              }}
            </text>
            <text class="stats-value">
              {{
                $t('invite.peopleNum', {
                  num: teamInfo?.level2Count
                })
              }}
            </text>
          </view>
          <view class="stats-item">
            <text class="stats-label">
              {{
                $t('invite.generation', {
                  generation: 3
                })
              }}
            </text>
            <text class="stats-value">
              {{
                $t('invite.peopleNum', {
                  num: teamInfo?.level3Count
                })
              }}
            </text>
          </view>
        </view>
      </view>

      <!-- 邀请列表标题 -->
      <view class="section-title">
        <view class="title-bar"></view>
        <text class="title-text">{{ $t('invite.inviteList') }}</text>
      </view>

      <!-- Tab 切换 -->
      <view class="tabs-wrap">
        <view
          v-for="level in levelTabs"
          :key="level.value"
          class="tab-item"
          :class="{ active: currentLevel === level.value }"
          @tap="switchTab(level.value)"
        >
          <text>{{ level.label }}</text>
        </view>
      </view>

      <!-- 列表 -->
      <view class="list-container">
        <view v-if="list.length > 0">
          <view class="table-row" v-for="(item, index) in list" :key="index">
            <view class="row-item">
              <text class="item-label">{{ $t('invite.userName') }}</text>
              <text class="item-value">{{ item.name || '-' }}</text>
            </view>
            <view class="row-item">
              <text class="item-label">{{ $t('invite.teamUserId') }}</text>
              <text class="item-value">{{ item.id || '-' }}</text>
            </view>
            <view class="row-item">
              <text class="item-label">{{ $t('invite.boundPhone') }}</text>
              <text class="item-value">{{ item.account || '-' }}</text>
            </view>
            <view class="row-item">
              <text class="item-label">{{ $t('invite.vip') }}</text>
              <text class="item-value">{{ item.vip || '-' }}</text>
            </view>
            <view class="row-item">
              <text class="item-label">{{ $t('invite.personalAsset') }}</text>
              <text class="item-value">{{ item.asset || '-' }}</text>
            </view>
            <view class="row-item">
              <text class="item-label">{{ $t('invite.active') }}</text>
              <text class="item-value">{{
                item.active ? $t('invite.yes') : $t('invite.no')
              }}</text>
            </view>
            <view class="row-item">
              <text class="item-label">{{ $t('invite.inviteLevel1UserNum') }}</text>
              <text class="item-value">{{ item.inviteLevel1UserNum ?? '-' }}</text>
            </view>
            <view class="row-item">
              <text class="item-label">{{ $t('invite.inviteLevel1UserActiveNum') }}</text>
              <text class="item-value">{{ item.inviteLevel1UserActiveNum ?? '-' }}</text>
            </view>
          </view>
        </view>

        <!-- 加载状态 -->
        <view class="loading-status">
          <u-loading-icon v-if="loading"></u-loading-icon>
          <text v-else-if="noMore && list.length > 0" class="no-more">
            {{ $t('common.noMore') }}~
          </text>
        </view>

        <!-- 空状态 -->
        <view v-if="list.length === 0 && !loading" class="empty-wrap">
          <u-empty mode="data" :text="$t('invite.noTeamMembers')" />
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { onReachBottom } from '@dcloudio/uni-app';
import { getTeamList } from '@/apis/user';
import { reqUserInviteCode } from '@/apis/user/invite';

const { t } = useI18n();
const teamInfo = ref({});
const list = ref([]);
const loading = ref(false);
const noMore = ref(false);
const page = ref(1);
const currentLevel = ref('1');

// 根据配置生成 Tab
const levelTabs = computed(() => {
  const tabs = [];
  for (let i = 1; i <= 3; i++) {
    tabs.push({
      value: String(i),
      label: t('invite.generationShort', { generation: i })
    });
  }
  return tabs;
});

// 切换 Tab
function switchTab(level) {
  if (currentLevel.value === level) return;
  currentLevel.value = level;
  getData(true);
}

// 获取团队概览
async function fetchTeamInfo() {
  try {
    const res = await reqUserInviteCode();
    if (res) {
      teamInfo.value = res;
    }
  } catch (e) {
    console.error('获取团队信息失败', e);
  }
}

// 获取团队成员列表
async function getData(isRefresh = false) {
  if (loading.value) return;
  if (!isRefresh && noMore.value) return;

  loading.value = true;

  try {
    if (isRefresh) {
      page.value = 1;
      noMore.value = false;
    }

    const res = await getTeamList(page.value, 20, currentLevel.value);
    const records = res || [];

    if (isRefresh) {
      list.value = records;
    } else {
      list.value = [...list.value, ...records];
    }

    // 判断是否还有更多
    if (records.length < 20) {
      noMore.value = true;
    } else {
      page.value++;
    }
  } catch (e) {
    console.error('获取团队成员列表失败', e);
  } finally {
    loading.value = false;
  }
}

// 加载更多
function loadMore() {
  if (!loading.value && !noMore.value) {
    getData();
  }
}

// 页面初始化
async function onPageInit() {
  fetchTeamInfo();
  getData(true);
}

onReachBottom(() => {
  loadMore();
});
</script>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-content {
  @include hasNavBar();
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f5f5;
}

.stats-area {
  margin: 20rpx;
  padding: 30rpx 20rpx;
  background: var(--primary-color);
  border-radius: 20rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);

  .stats-row {
    display: flex;
    justify-content: space-around;
    flex-wrap: wrap;
    gap: 60rpx 0;

    &:first-child {
      padding: 30rpx 0 10rpx;
    }
  }

  .stats-item {
    flex: 1 0 33.33%;
    display: flex;
    flex-direction: column;
    align-items: center;

    .stats-label {
      font-size: 24rpx;
      color: rgba(255, 255, 255, 0.8);
      margin-bottom: 10rpx;
    }

    .stats-value {
      font-size: 28rpx;
      color: #fff;
      font-weight: bold;
    }
  }
}

.section-title {
  display: flex;
  align-items: center;
  padding: 30rpx 24rpx 20rpx;
  background: #fff;
  box-shadow: 0 6rpx 18rpx rgba(0, 0, 0, 0.04);

  .title-bar {
    width: 8rpx;
    height: 32rpx;
    background: #e74c3c;
    border-radius: 4rpx;
    margin-right: 16rpx;
  }

  .title-text {
    font-size: 30rpx;
    color: #333;
    font-weight: 500;
  }
}

.tabs-wrap {
  display: flex;
  background: #fff;
  padding: 0 24rpx 20rpx;
  box-shadow: 0 6rpx 18rpx rgba(0, 0, 0, 0.04);

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 20rpx 0;
    font-size: 28rpx;
    color: #666;
    position: relative;

    &.active {
      color: #e74c3c;
      font-weight: 500;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 60rpx;
        height: 4rpx;
        background: #e74c3c;
        border-radius: 2rpx;
      }
    }
  }
}

.list-container {
  background: #f5f5f5;
  padding: 0 24rpx 24rpx;
  box-sizing: border-box;
}

.table-row {
  background: #fff;
  border-radius: 12rpx;
  padding: 20rpx 24rpx;
  margin-top: 20rpx;
  box-shadow: 0 8rpx 22rpx rgba(0, 0, 0, 0.06);

  .row-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
    border-bottom: 1rpx solid #f5f5f5;

    &:last-child {
      border-bottom: none;
    }

    .item-label {
      font-size: 24rpx;
      color: #999;
    }

    .item-value {
      font-size: 26rpx;
      color: #333;
      font-weight: 500;
      max-width: 60%;
      text-align: right;
      word-break: break-all;
    }
  }
}

.loading-status {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30rpx;

  .no-more {
    font-size: 24rpx;
    color: #999;
  }
}

.empty-wrap {
  padding: 100rpx 0;
}
</style>
