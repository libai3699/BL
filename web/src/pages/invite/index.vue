<script setup>
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';
import { imageMapCommon } from '@/static/images';
import { solveNormalData } from '@/utils/solveData';
import { reqUserInviteCode, reqCommissionStats } from '@/apis/user/invite';
import { reqClaimVipReward, reqVipRewardList } from '@/apis/vip';
import { doCopy } from '@/utils/uniUtils';

const { locale, t } = useI18n();

const commonStore = useCommonStore();

// 监听语言切换，重新获取佣金数据
watch(
  () => commonStore.locale,
  () => {
    getCommissionStats();
  }
);

// 数据
const refDataBoxDetail = ref({
  detail: null,
  isEmpty: () =>
    refDataBoxDetail.value.detail == null ||
    Object.keys(refDataBoxDetail.value.detail)?.length === 0,
  isNotGetData: () =>
    refDataBoxDetail.value.loading ||
    refDataBoxDetail.value.error ||
    refDataBoxDetail.value.isEmpty(),
  loading: true,
  error: false,
  errorText: ''
});

// 佣金统计数据
const commissionStats = ref({
  list: [],
  loading: true
});

// VIP 奖励列表（status: 0 待领取 1 已领取）
const vipRewardList = ref([]);

async function getVipRewardList() {
  try {
    const res = await reqVipRewardList();
    vipRewardList.value = res?.list ?? res ?? [];
  } catch (e) {
    console.error('获取VIP奖励列表失败', e);
    vipRewardList.value = [];
  }
}

function formatRewardDesc(value) {
  if (value == null || value === '') return '';
  const num = Number(value);
  return Number.isFinite(num) ? num.toFixed(2) : String(value);
}

function getData() {
  solveNormalData({
    refData: refDataBoxDetail,
    reqMethod: reqUserInviteCode,
    onSuccess(res) {}
  });
}

// 按钮文字格式化，英文/西班牙语等两个单词时换行显示
function formatBtnText(text) {
  if (!text) return '';
  const parts = String(text).split(' ');
  // 只处理两个及以上单词的情况，使用换行符分隔
  if (parts.length > 1) {
    return parts.join('\n');
  }
  return text;
}

// 获取佣金统计
async function getCommissionStats() {
  commissionStats.value.loading = true;
  try {
    const res = await reqCommissionStats();
    if (res && res.list) {
      commissionStats.value.list = res.list;
    }
  } catch (e) {
    console.error('获取佣金统计失败', e);
  } finally {
    commissionStats.value.loading = false;
  }
}

// 领取奖励（传入列表项，用 item.id 调接口）
async function doReceive(item) {
  if (item.status === 1) return;
  const id = item.id;
  if (id == null || id === '') {
    uni.showToast({ title: t('invite.noRewardToClaim'), icon: 'none' });
    return;
  }
  try {
    await reqClaimVipReward(id);
    uni.showToast({ title: t('invite.claimSuccess'), icon: 'success' });
    getVipRewardList();
  } catch (e) {
    console.error('领取奖励失败', e);
  }
}

function goMore() {
  navigateTo('/pages/my-team/index');
}

// 页面初始化回调
const onPageInit = async () => {
  getData();
  getCommissionStats();
  getVipRewardList();
};

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar :title="$t('invite.title')" />
      <view
        class="u-main"
        :style="{
          backgroundImage: `url(${imageMapCommon[locale]['invite-bg']})`,
          backgroundRepeat: 'no-repeat',
          backgroundPosition: '0 0',
          backgroundSize: '750rpx 2044rpx'
        }"
      >
        <view class="u-steps">
          <view class="u-item">
            <view class="u-item-no">1</view>
            <view class="u-item-cnt">
              {{ $t('invite.steps.step1') }}
            </view>
          </view>
          <view class="u-item">
            <view class="u-item-no">2</view>
            <view class="u-item-cnt">
              {{ $t('invite.steps.step2') }}
            </view>
          </view>
          <view class="u-item">
            <view class="u-item-no">3</view>
            <view class="u-item-cnt">
              {{ $t('invite.steps.step3') }}
            </view>
          </view>
        </view>

        <view class="u-group-num">
          <view class="u-num-top">
            <view class="u-name">
              {{ $t('invite.groupNum') }}
            </view>
            <view class="u-num">
              {{
                $t('invite.peopleNum', {
                  num: refDataBoxDetail.detail?.totalCount
                })
              }}
            </view>
          </view>
          <view
            class="u-num-top"
            style="margin-top: 20rpx; padding-top: 10rpx; padding-bottom: 20rpx"
          >
            <view class="u-name">
              {{ $t('invite.teamTotalAmount') }}
            </view>
            <view class="u-num">
              {{ refDataBoxDetail.detail?.teamTotalAmount }}
            </view>
          </view>
          <view
            class="u-num-top"
            style="padding-bottom: 10rpx; padding-top: 10rpx"
            v-for="item in vipRewardList"
            :key="item.id"
          >
            <view class="u-name">
              <image src="@/static/images/common/vip-diamond.png" class="u-vip-diamond" />
              <view class="u-vip-value">{{ item.levelName }}</view>
            </view>
            <view class="u-reward-desc-inline">
              {{ item.rewardDesc }}
            </view>
            <view class="u-num">
              <up-button
                class="u-btn-receive"
                :class="{ 'u-btn-receive--disabled': item.status === 1 }"
                :disabled="item.status === 1"
                @tap="doReceive(item)"
              >
                {{
                  formatBtnText(
                    item.status === 0 ? t('invite.claimReward') : t('common.received')
                  )
                }}
              </up-button>
            </view>
          </view>
          <view class="u-num-list">
            <view class="u-item">
              <view class="u-item-num">
                {{
                  $t('invite.peopleNum', {
                    num: refDataBoxDetail.detail?.level1Count
                  })
                }}
              </view>
              <view class="u-item-name">
                {{
                  $t('invite.generation', {
                    generation: 1
                  })
                }}
              </view>
            </view>
            <view class="u-item">
              <view class="u-item-num">
                {{
                  $t('invite.peopleNum', {
                    num: refDataBoxDetail.detail?.level2Count
                  })
                }}
              </view>
              <view class="u-item-name">
                {{
                  $t('invite.generation', {
                    generation: 2
                  })
                }}
              </view>
            </view>
            <view class="u-item">
              <view class="u-item-num">
                {{
                  $t('invite.peopleNum', {
                    num: refDataBoxDetail.detail?.level3Count
                  })
                }}
              </view>
              <view class="u-item-name">
                {{
                  $t('invite.generation', {
                    generation: 3
                  })
                }}
              </view>
            </view>
          </view>
          <view class="u-check-more" @tap="goMore"> {{ $t('common.viewMore') }} >> </view>
        </view>

        <!-- 佣金统计 - 遍历币种 -->
        <view
          class="u-commission"
          v-for="item in commissionStats.list"
          :key="item.amountType"
        >
          <view class="u-commission-top">
            <view class="u-name">{{ item.totalLabel }}</view>
            <view class="u-num">{{ item.totalCommission }}</view>
          </view>
          <view class="u-commission-today">
            <view class="u-name">{{ item.todayLabel }}</view>
            <view class="u-num">{{ item.todayCommission }}</view>
          </view>
          <view class="u-commission-list">
            <view class="u-item">
              <view class="u-item-num">{{ item.level1Commission }}</view>
              <view class="u-item-name">{{ item.level1Label }}</view>
            </view>
            <view class="u-item">
              <view class="u-item-num">{{ item.level2Commission }}</view>
              <view class="u-item-name">{{ item.level2Label }}</view>
            </view>
            <view class="u-item">
              <view class="u-item-num">{{ item.level3Commission }}</view>
              <view class="u-item-name">{{ item.level3Label }}</view>
            </view>
          </view>
        </view>

        <view class="u-progress">
          <view class="u-item">{{ $t('invite.progress.1') }}</view>
          <view class="u-item">{{ $t('invite.progress.2') }}</view>
          <view class="u-item">{{ $t('invite.progress.3') }}</view>
        </view>

        <view class="u-code">
          {{ $t('invite.inviteCode') }}
          {{ refDataBoxDetail.detail?.inviteCode }}
          <svg-icon
            name="copy-1"
            class="u-svg-icon"
            @tap="doCopy(refDataBoxDetail.detail?.inviteCode)"
          />
        </view>

        <view class="u-btn-area">
          <c-main-btn class="u-btn" @tap="doCopy(refDataBoxDetail.detail?.inviteCode)">
            {{ $t('invite.copyLink') }}
          </c-main-btn>
          <c-main-btn
            v-if="refDataBoxDetail.detail?.inviteUrl"
            class="u-btn u-btn--secondary"
            @tap="doCopy(refDataBoxDetail.detail?.inviteUrl)"
          >
            {{ $t('invite.copyInviteUrl') }}
          </c-main-btn>
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-content {
  @include hasNavBar();

  .u-main {
    min-height: 2044rpx;
    box-sizing: border-box;
    background-color: #fff5e6;
    padding-bottom: calc(48rpx + env(safe-area-inset-bottom));
    padding-bottom: calc(48rpx + constant(safe-area-inset-bottom));

    .g-lang-zh_CN & {
      padding-top: 828rpx;

      .u-steps {
        height: 492rpx;
        padding: 30rpx 80rpx 40rpx;
      }
    }

    .g-lang-en_US &,
    .g-lang-es_MX & {
      padding-top: 830rpx;

      .u-steps {
        height: 510rpx;
        padding: 30rpx 40rpx 55rpx 80rpx;

        .u-item {
          margin-bottom: 25rpx;

          .u-item-cnt {
            font-size: 24rpx;
          }
        }
      }

      .u-group-num {
        .u-num-list {
          margin-top: 30rpx;
        }
      }

      .u-progress {
        height: 90rpx;
        font-size: 24rpx;
      }

      .u-code {
        margin-top: 30rpx;
      }
    }

    .g-lang-es_MX & {
      .u-group-num {
        .u-num-top {
          font-size: 28rpx;
        }

        .u-num-list {
          margin-top: 30rpx;
        }
      }

      .u-code {
        font-size: 38rpx;
      }
    }

    .u-steps {
      display: flex;
      justify-content: center;
      flex-direction: column;
      box-sizing: border-box;

      .u-item {
        display: flex;
        align-items: center;
        margin-bottom: 70rpx;

        &:last-child {
          margin-bottom: 0;
        }

        .u-item-no {
          flex-shrink: 0;
          width: 40rpx;
          height: 40rpx;
          background: var(--primary-color);
          color: #fff;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24rpx;
        }

        .u-item-cnt {
          margin-left: 30rpx;
          line-height: 40rpx;
          font-size: 30rpx;
          font-weight: bold;
        }
      }
    }

    .u-group-num {
      margin: 40rpx 24rpx;
      padding: 30rpx 24rpx;
      min-height: 315rpx;
      box-sizing: border-box;
      background: #fff;
      border-radius: 16rpx;
      box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

      .u-num-top {
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-weight: bold;
        font-size: 32rpx;
        padding-bottom: 10rpx;
        border-bottom: 2rpx solid var(--border-color-weak);

        .u-num {
          color: var(--primary-color);
          flex-shrink: 0;
        }

        .u-reward-desc-inline {
          flex: 1;
          min-width: 0;
          color: var(--primary-color);
          font-size: 24rpx;
          text-align: center;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .u-name {
          display: flex;
          align-items: center;
          flex-shrink: 0;

          .u-vip-diamond {
            width: 76rpx;
            height: 60rpx;
            margin-right: 10rpx;
          }

          .u-vip-value {
            width: 153rpx;
            height: 43rpx;
            line-height: 53rpx;
            font-size: 32rpx;
            padding: 0 21rpx 0 0;
            text-align: right;
            background: url('@/static/images/common/vip-bg.png') no-repeat 0 0 / 100% 100%;
          }
        }

        &:last-of-type {
          border-bottom: none;
        }
      }

      .u-num-list {
        display: flex;
        font-weight: bold;
        border: 2rpx solid var(--border-color-weak);
        margin-top: 4rpx;
        border-radius: 10rpx;
        padding: 24rpx;

        .u-item {
          position: relative;
          flex: 1;
          min-width: 0;
          text-align: center;

          &:after {
            content: '';
            position: absolute;
            top: 0;
            bottom: 0;
            right: 0;
            width: 2rpx;
            background: var(--border-color-weak);
          }

          &:last-child {
            &:after {
              display: none;
            }
          }

          .u-item-num {
            font-size: 24rpx;
            margin-bottom: 20rpx;
            color: var(--primary-color);
            line-height: 1.3;
            word-break: break-word;
            overflow-wrap: anywhere;
          }

          .u-item-name {
            font-size: 24rpx;
            line-height: 1.3;
            word-break: break-word;
            overflow-wrap: anywhere;
          }
        }
      }

      .u-check-more {
        margin-top: 20rpx;
        text-align: center;
        font-size: 28rpx;
        color: var(--primary-color);
      }
    }

    .u-commission {
      margin: 40rpx 24rpx;
      padding: 30rpx 24rpx;
      min-height: 315rpx;
      box-sizing: border-box;
      background: #fff;
      border-radius: 16rpx;
      box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

      .u-commission-top {
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-weight: bold;
        font-size: 32rpx;
        padding-bottom: 10rpx;
        border-bottom: 2rpx solid #eee;

        .u-num {
          color: var(--primary-color);
        }
      }

      .u-commission-today {
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-weight: bold;
        font-size: 28rpx;
        padding: 20rpx 0 10rpx;

        .u-num {
          color: var(--primary-color);
        }
      }

      .u-commission-list {
        display: flex;
        font-weight: bold;
        border: 2rpx solid #eee;
        margin-top: 20rpx;
        border-radius: 10rpx;
        padding: 24rpx;

        .u-item {
          position: relative;
          flex: 1;
          min-width: 0;
          text-align: center;

          &:after {
            content: '';
            position: absolute;
            top: 0;
            bottom: 0;
            right: 0;
            width: 2rpx;
            background: #eee;
          }

          &:last-child {
            &:after {
              display: none;
            }
          }

          .u-item-num {
            font-size: 24rpx;
            margin-bottom: 20rpx;
            color: var(--primary-color);
            line-height: 1.3;
            word-break: break-word;
            overflow-wrap: anywhere;
          }

          .u-item-name {
            font-size: 24rpx;
            line-height: 1.3;
            word-break: break-word;
            overflow-wrap: anywhere;
          }
        }
      }
    }

    .u-progress {
      display: flex;
      font-size: 32rpx;
      height: 80rpx;
      display: flex;
      align-items: center;
      padding: 0;
      font-weight: bold;

      .u-item {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        text-align: center;
        flex: 1;
        box-sizing: border-box;
        padding: 0 40rpx;
      }
    }

    .u-code {
      display: flex;
      justify-content: center;
      font-size: 48rpx;
      color: var(--primary-color);
      margin-top: 40rpx;
      font-weight: bold;

      .u-svg-icon {
        margin-left: 20rpx;
      }
    }

    .u-btn-area {
      margin: 33rpx 66rpx;

      .u-btn {
        height: 59rpx;
        border-radius: 59rpx;

        &--secondary {
          margin-top: 20rpx;
          background: linear-gradient(
            84.49deg,
            var(--primary-color) 8.66%,
            #5dd3f0 51.93%,
            #00a8d6 94.37%
          );
        }
      }
    }
  }

  .u-btn-receive {
    padding: 0 24rpx;
    min-height: 48rpx;
    height: 73rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    white-space: pre-line;
    text-align: center;
    line-height: 1.2;
    background: linear-gradient(84.49deg, #f09f21 8.66%, #ffd155 51.93%, #ffb100 94.37%);
    border: 1px solid #ffedea;
    color: #460801;
    border-radius: 100rpx;
    font-size: 22rpx;

    &--disabled {
      background: #e5e5e5;
      border-color: #d0d0d0;
      color: #999;
      pointer-events: none;
    }
  }
}
</style>
