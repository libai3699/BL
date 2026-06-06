<script setup>
import { computed, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack } from '@/utils/navigate';
import { reqVip } from '@/apis/vip';
import { solveNormalData } from '@/utils/solveData';

const commonStore = useCommonStore();
const userStore = useUserStore();
const user = () => userStore.userInfo.value;

const refDataBoxDetail = ref({
  detail: null,
  // 如果数据是数组
  isEmpty: () => refDataBoxDetail.value.detail?.length === 0,
  isNotGetData: () =>
    refDataBoxDetail.value.loading ||
    refDataBoxDetail.value.error ||
    refDataBoxDetail.value.isEmpty(),
  loading: true,
  error: false,
  errorText: ''
});
const refCurrentCard = ref(null);
const refSwiper = ref(null);

// 用户当前等级
const cpdLevel = computed(() => user()?.level);

function getData() {
  solveNormalData({
    refData: refDataBoxDetail,
    reqMethod: reqVip,
    onSuccess(res) {
      const levelIndex =
        refDataBoxDetail.value.detail.findIndex((item) => item.id === cpdLevel.value) ||
        0;
      refCurrentCard.value = refDataBoxDetail.value.detail[levelIndex];
      refSwiper.value.doRenderSwiper({
        initialSlide: levelIndex
      });
    }
  });
}

// 切换卡片
function doChangeCard(item, index) {
  refSwiper.value.swiper.slideTo(index);
  refCurrentCard.value = item;
}

// 页面初始化回调
const onPageInit = async () => {
  getData();
};

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <view class="u-top">
        <nav-bar
          :title="$t('mine.vip.title')"
          backgroundColor="transparent"
          color="#fff"
        />
        <!-- 用户信息 -->
        <view class="u-user-info">
          <c-avatar :src="user()?.headImage" size="lg" />
          <view class="u-info-main">
            <view class="u-title">
              {{ user()?.nickName || $t('common.notSetNickName') }}
            </view>
            <view class="u-subtitle">
              {{ user()?.userName }}
            </view>
          </view>
          <view class="u-right">
            {{ $t('mine.vip.currentLevel') }}
            <view class="u-tags-item s-ochre">
              <svg-icon name="vip" class="u-item-svg-icon s-vip" />
              <view class="u-item-name">{{ user().levelName }}</view>
            </view>
          </view>
        </view>
        <!-- VIP卡片列表 -->
        <scroll-x-box ref="refSwiper" class="u-vip-cards" :autoRun="false">
          <view
            class="u-card-item swiper-slide"
            v-for="(item, index) in refDataBoxDetail.detail"
            :key="item.id"
            @tap="doChangeCard(item, index)"
          >
            <image class="u-card-image" :src="item.levelIcon" mode="aspectFill" />
            <!-- 状态 -->
            <view class="u-active-status">
              <view class="u-status-item s-no" v-if="index >= cpdLevel">{{
                $t('mine.vip.activeNo')
              }}</view>
              <view class="u-status-item s-yes" v-else>{{
                $t('mine.vip.activeYes')
              }}</view>
            </view>
            <!-- 要求 -->
            <view class="u-active-demand">
              {{ $t('mine.vip.applyCondition') }}:
              {{ item.applyContent }}
            </view>
          </view>
        </scroll-x-box>
      </view>

      <view class="u-info">
        <view class="u-top-bar">
          <image class="u-icon" src="@/static/images/common/vip.png" />
          {{ refCurrentCard?.levelName }} {{ $t('mine.vip.award') }}
        </view>
        <view class="u-main">
          <view class="u-info-item">
            <view class="u-item-name">
              {{ $t('mine.vip.awardGrade') }}
            </view>
            <view class="u-item-value">
              {{ refCurrentCard?.remark }}
            </view>
          </view>
          <view class="u-info-item">
            <view class="u-item-name">
              {{ $t('mine.vip.positionRate') }}
            </view>
            <view class="u-item-value"> {{ refCurrentCard?.positionRate }}% </view>
          </view>
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-content {
  .u-top {
    @include hasNavBar();
    background: linear-gradient(to bottom, #20134f, #032e6f);
    height: 712rpx;
    box-sizing: border-box;
  }
}

.u-user-info {
  display: flex;
  padding: 12rpx 26rpx 30rpx;
  color: #fff;

  .m-avatar-wrap {
    flex-shrink: 0;
    margin-right: 16rpx;
  }

  .u-info-main {
    flex: 1;
    padding-top: 6rpx;

    .u-title {
      font-weight: bold;
      min-height: 38rpx;
      line-height: 38rpx;
      @include ellipsis2(2);
    }

    .u-subtitle {
      margin-top: 8rpx;
      font-size: 24rpx;
      min-height: 32rpx;
      line-height: 32rpx;
    }
  }

  .u-right {
    display: flex;
    align-items: center;
    margin-left: 20rpx;

    .u-tags-item {
      margin-left: 14rpx;
      background: rgba(244, 237, 224, 0.2);
    }
  }
}

.u-vip-cards {
  .u-card-item {
    position: relative;
    width: 616rpx;
    height: 288rpx;
    margin: 0 10rpx;

    &:first-child {
      margin-left: 20rpx;
    }

    &:last-child {
      margin-right: 20rpx;
    }

    .u-card-image {
      width: 100%;
      height: 100%;
      border-radius: 20rpx;
    }

    .u-active-status {
      position: absolute;
      top: 48rpx;
      left: 270rpx;

      .u-status-item {
        padding: 0 12rpx;
        height: 38rpx;
        display: flex;
        align-items: center;
        font-size: 24rpx;
        border-radius: 60rpx;

        &.s-no {
          background: #fff;
          color: var(--text-key-color);
        }

        &.s-yes {
          background: var(--text-key-color);
          color: #fff;
        }
      }
    }

    .u-active-demand {
      position: absolute;
      bottom: 8rpx;
      left: 0;
      right: 0;
      padding: 0 22rpx;
      font-size: 20rpx;
      line-height: 32rpx;
      text-align: center;
    }
  }
}

.u-info {
  margin-top: -142rpx;
  background: #fff;
  padding: 0 24rpx;
  border-radius: 20rpx 20rpx 0 0;
  min-height: 200rpx;

  .u-top-bar {
    padding: 40rpx 0;
    display: flex;
    align-items: center;
    border-bottom: 2rpx solid var(--border-color);

    .u-icon {
      width: 38rpx;
      height: 50rpx;
      margin-right: 16rpx;
    }
  }

  .u-main {
    margin-top: 32rpx;
    line-height: 38rpx;
    color: var(--text-minor-color);

    .u-info-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      color: var(--text-key-color);
      margin-bottom: 30rpx;
    }
  }
}
</style>
