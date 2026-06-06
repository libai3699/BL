<script setup>
import { computed, reactive, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import { reqUserGetUserAccount } from '@/apis/user';
import { reqUserRechargeInMoney, reqUserRechargeChannel } from '@/apis/user/recharge';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo } from '@/utils/navigate';
import { solveNormalData } from '@/utils/solveData';

const { t } = useI18n();
const commonStore = useCommonStore();

const refChannels = ref([]);
const refSelectedChannelId = ref('');
const refAmountTab = ref('');
const refFormModel = reactive({
  params: {
    amt: '',
    payId: '',
    payIdTrue: ''
  }
});
const refMainBtn = ref({ loading: false });
const showGuide = ref(false);

const refAmountTabs = ref([
  { label: '100', value: '100' },
  { label: '500', value: '500' },
  { label: '1,000', value: '1000' },
  { label: '5,000', value: '5000' },
  { label: '10,000', value: '10000' }
]);

const refAccount = ref({
  detail: null,
  loading: true,
  error: false
});

const currency = 'MXN';

const cpdFormValid = computed(() => {
  const amt = String(refFormModel.params.amt || '').trim();
  return Boolean(amt && refFormModel.params.payIdTrue);
});

const cpdAccount = computed(() => refAccount.value.detail?.accounts?.[0] || {});

const cpdSelectedChannel = computed(() =>
  refChannels.value.find((c) => String(c.id) === String(refSelectedChannelId.value))
);

const cpdBankInfoRows = computed(() => {
  const ch = cpdSelectedChannel.value;
  if (!ch) return [];
  const rows = [
    { key: 'bank', value: ch.bankName || ch.bank || ch.receiveBank },
    { key: 'account', value: ch.bankAccount || ch.accountNo || ch.account },
    { key: 'holder', value: ch.accountName || ch.holderName || ch.userName },
    {
      key: 'remark',
      value: ch.remark || ch.remarkCode || ch.memo || ch.memoCode,
      gold: true
    }
  ];
  return rows.filter((r) => r.value);
});

const cpdBtnText = computed(() => {
  const raw = String(refFormModel.params.amt || '').replace(/,/g, '');
  if (!raw || Number.isNaN(Number(raw))) {
    return t('mine.recharge.index.btn.title');
  }
  const formatted = Number(raw).toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
  return t('mine.recharge.index.btnNext', { amount: formatted, currency });
});

const syncAmountTab = () => {
  if (refFormModel.params.amt !== refAmountTab.value) {
    refAmountTab.value = '';
  }
};

const onAmountInput = (e) => {
  refFormModel.params.amt = e.detail?.value ?? '';
  syncAmountTab();
};

const onChooseAmount = (val) => {
  refAmountTab.value = val.value;
  refFormModel.params.amt = val.value;
};

const getChannelIcon = (item) => {
  const name = String(item?.name || '').toUpperCase();
  if (/OXXO/i.test(name)) return '🏪';
  if (/SPEI/i.test(name)) return '⚡';
  if (/USDT|BTC|ETH/i.test(name)) return '₮';
  if (/银行|BANK|CARD|BBVA/i.test(name)) return '🏦';
  return '💳';
};

const selectChannel = (item) => {
  if (!item) return;
  refSelectedChannelId.value = item.id;
  refFormModel.params.payId = item.name;
  refFormModel.params.payIdTrue = item.id;
};

const loadChannels = () => {
  reqUserRechargeChannel({}).then((res) => {
    const list = Array.isArray(res) ? res : res?.list || [];
    refChannels.value = list;
    if (list.length) {
      const cur = list.find((c) => String(c.id) === String(refSelectedChannelId.value));
      selectChannel(cur || list[0]);
    }
  });
};

const loadAccount = () => {
  solveNormalData({
    refData: refAccount,
    reqMethod: reqUserGetUserAccount,
    reqParams: {},
    needLoading: true
  });
};

const doSave = () => {
  refMainBtn.value.loading = true;
  const reqParams = {
    amt: refFormModel.params.amt,
    payId: refFormModel.params.payIdTrue
  };
  reqUserRechargeInMoney(reqParams)
    .then((res) => {
      refFormModel.params.amt = '';
      refFormModel.params.payId = '';
      refFormModel.params.payIdTrue = '';
      refAmountTab.value = '';
      location.href = res;
    })
    .finally(() => {
      refMainBtn.value.loading = false;
    });
};

const openGuide = () => {
  showGuide.value = true;
};

const closeGuide = () => {
  showGuide.value = false;
};

const goRecord = () => {
  closeGuide();
  navigateTo('/pages/mine/recharge/record/index');
};

const onPageInit = async () => {
  loadAccount();
  loadChannels();
};

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="deposit-page">
      <nav-bar :title="$t('mine.recharge.index.title')">
        <template #right>
          <text class="deposit-nav-right" @tap="openGuide">{{
            $t('mine.recharge.index.guide')
          }}</text>
        </template>
      </nav-bar>

      <view class="deposit-scroll">
        <view class="deposit-scroll__inner">
          <view class="sp-balance-card">
            <text class="sp-balance-card__k">{{
              $t('mine.recharge.index.balanceLabel')
            }}</text>
            <view class="sp-balance-card__v">
              <c-amount
                :loading="refAccount.loading"
                :value="cpdAccount.availableAmount"
              />
              <text class="sp-balance-card__unit">{{ currency }}</text>
            </view>
            <text class="sp-balance-card__meta">{{
              $t('mine.recharge.index.balanceMeta')
            }}</text>
          </view>

          <text class="deposit-section-label">{{
            $t('mine.recharge.index.selectMethod')
          }}</text>
          <scroll-view class="deposit-methods" scroll-x :show-scrollbar="false">
            <view class="deposit-methods__row">
              <view
                v-for="item in refChannels"
                :key="item.id"
                class="deposit-method-pill"
                :class="{ active: String(refSelectedChannelId) === String(item.id) }"
                @tap="selectChannel(item)"
              >
                <text>{{ getChannelIcon(item) }} {{ item.name }}</text>
              </view>
            </view>
          </scroll-view>

          <view class="deposit-amount-block">
            <text class="deposit-amount-block__label">{{
              $t('mine.recharge.index.amountLabel')
            }}</text>
            <view class="sp-amount">
              <text class="sp-amount__cur">{{ currency }}</text>
              <input
                class="sp-amount__input"
                type="digit"
                :value="refFormModel.params.amt"
                :placeholder="$t('form.normalText.amt.placeholder')"
                @input="onAmountInput"
              />
            </view>
            <view class="sp-qa">
              <view
                v-for="pill in refAmountTabs"
                :key="pill.value"
                class="sp-qa__pill"
                :class="{ active: refAmountTab === pill.value }"
                @tap="onChooseAmount(pill)"
              >
                {{ pill.label }}
              </view>
            </view>
          </view>

          <view v-if="cpdBankInfoRows.length" class="sp-info-card">
            <view v-for="row in cpdBankInfoRows" :key="row.key" class="sp-info-card__ir">
              <text class="sp-info-card__k">{{
                $t(`mine.recharge.index.info.${row.key}`)
              }}</text>
              <text class="sp-info-card__v" :class="{ gold: row.gold }" translate="no">
                {{ row.value }}
              </text>
            </view>
          </view>

          <view class="sp-notice">
            <text class="sp-notice__title">{{
              $t('mine.recharge.index.noticeTitle')
            }}</text>
            <text class="sp-notice__line">· {{ $t('mine.recharge.index.notice1') }}</text>
            <text class="sp-notice__line">· {{ $t('mine.recharge.index.notice2') }}</text>
            <text class="sp-notice__line">
              · {{ $t('mine.recharge.index.notice3Prefix')
              }}<text class="sp-notice__bold">{{
                $t('mine.recharge.index.notice3Bold')
              }}</text
              >{{ $t('mine.recharge.index.notice3Suffix') }}
            </text>
          </view>
        </view>
      </view>

      <view class="sp-cta">
        <button
          class="sp-cta__btn"
          :class="{ disabled: !cpdFormValid || refMainBtn.loading }"
          :disabled="!cpdFormValid || refMainBtn.loading"
          @tap="doSave"
        >
          {{ refMainBtn.loading ? $t('button.loading') + '...' : cpdBtnText }}
        </button>
      </view>

      <u-popup
        :show="showGuide"
        mode="bottom"
        :round="20"
        :z-index="200"
        @close="closeGuide"
      >
        <view class="deposit-guide">
          <view class="deposit-guide__head">
            <text class="deposit-guide__title">{{
              $t('mine.recharge.index.guide')
            }}</text>
            <text class="deposit-guide__close" @tap="closeGuide">{{
              $t('common.confirm')
            }}</text>
          </view>
          <view class="sp-notice deposit-guide__body">
            <text class="sp-notice__line">· {{ $t('mine.recharge.index.notice1') }}</text>
            <text class="sp-notice__line">· {{ $t('mine.recharge.index.notice2') }}</text>
            <text class="sp-notice__line">
              · {{ $t('mine.recharge.index.notice3Prefix')
              }}<text class="sp-notice__bold">{{
                $t('mine.recharge.index.notice3Bold')
              }}</text
              >{{ $t('mine.recharge.index.notice3Suffix') }}
            </text>
          </view>
          <text class="deposit-guide__record" @tap="goRecord">{{
            $t('mine.recharge.index.viewRecord')
          }}</text>
        </view>
      </u-popup>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.deposit-page {
  height: 100vh;
  max-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--stock-page-bg, #eef2f8);
  @include hasNavBar();
}

.deposit-nav-right {
  font-size: 26rpx;
  color: var(--stock-ink-3, #64748b);
  font-weight: 600;
  padding-right: 8rpx;
}

.deposit-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
  overscroll-behavior: contain;
  padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

.deposit-scroll__inner {
  padding-bottom: 24rpx;
}

.sp-balance-card {
  margin: 28rpx 24rpx;
  padding: 36rpx;
  border-radius: 32rpx;
  background: linear-gradient(135deg, #0b1e47, #1e407f);
  color: #fff;
  position: relative;
  overflow: hidden;
  box-shadow: 0 16rpx 40rpx rgba(11, 30, 71, 0.2);

  &::before {
    content: '';
    position: absolute;
    width: 320rpx;
    height: 320rpx;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(212, 162, 76, 0.22), transparent 70%);
    top: -120rpx;
    right: -80rpx;
    pointer-events: none;
  }

  &__k,
  &__v,
  &__meta {
    position: relative;
    z-index: 2;
  }

  &__k {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.7);
  }

  &__v {
    display: flex;
    align-items: baseline;
    gap: 12rpx;
    margin-top: 8rpx;
    font-size: 52rpx;
    font-weight: 800;
    @include stock-tnum;

    :deep(.c-amount) {
      color: #fff;
      font-size: 52rpx;
      font-weight: 800;
    }
  }

  &__unit {
    font-size: 26rpx;
    font-weight: 600;
    opacity: 0.7;
  }

  &__meta {
    display: block;
    margin-top: 16rpx;
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.65);
  }
}

.deposit-section-label {
  display: block;
  padding: 0 28rpx 12rpx;
  font-size: 22rpx;
  color: var(--stock-ink-3, #64748b);
  font-weight: 600;
}

.deposit-methods {
  width: 100%;
  white-space: nowrap;

  &__row {
    display: inline-flex;
    gap: 12rpx;
    padding: 0 24rpx 8rpx;
  }
}

.deposit-method-pill {
  flex-shrink: 0;
  padding: 16rpx 28rpx;
  border-radius: 36rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: var(--stock-ink-2, #475569);
  background: var(--stock-fill, #f1f5f9);
  border: 1rpx solid var(--stock-line, #e5e9f2);

  &.active {
    background: var(--stock-navy, #0b1e47);
    color: #fff;
    border-color: var(--stock-navy, #0b1e47);
  }
}

.deposit-amount-block {
  margin-top: 16rpx;
  padding: 36rpx 32rpx;
  background: #fff;

  &__label {
    display: block;
    font-size: 22rpx;
    color: var(--stock-ink-3, #64748b);
    margin-bottom: 16rpx;
  }
}

.sp-amount {
  display: flex;
  align-items: center;
  gap: 12rpx;
  height: 104rpx;
  padding: 0 28rpx;
  border: 3rpx solid var(--stock-line, #e5e9f2);
  border-radius: 24rpx;
  background: #fff;

  &__cur {
    font-size: 28rpx;
    color: var(--stock-ink-3, #64748b);
    font-weight: 600;
  }

  &__input {
    flex: 1;
    min-width: 0;
    border: none;
    font-size: 44rpx;
    font-weight: 800;
    color: var(--stock-ink, #0f172a);
    @include stock-tnum;
  }
}

.sp-qa {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 24rpx;

  &__pill {
    flex: 1;
    min-width: calc(33.33% - 12rpx);
    padding: 16rpx 0;
    border-radius: 16rpx;
    text-align: center;
    font-size: 23rpx;
    font-weight: 600;
    color: var(--stock-ink-2, #475569);
    background: var(--stock-fill, #f1f5f9);
    border: 1rpx solid var(--stock-line, #e5e9f2);
    @include stock-tnum;

    &.active {
      background: var(--stock-navy, #0b1e47);
      color: #fff;
      border-color: var(--stock-navy, #0b1e47);
    }
  }
}

.sp-info-card {
  margin: 24rpx;
  padding: 28rpx 32rpx;
  background: #fff;
  border-radius: 24rpx;
  border: 1rpx solid var(--stock-line, #e5e9f2);

  &__ir {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 24rpx;
    padding: 12rpx 0;
    font-size: 24rpx;
  }

  &__k {
    color: var(--stock-ink-3, #64748b);
    flex-shrink: 0;
  }

  &__v {
    color: var(--stock-ink, #0f172a);
    font-weight: 600;
    text-align: right;
    @include stock-tnum;

    &.gold {
      color: #a16207;
    }
  }
}

.sp-notice {
  margin: 8rpx 28rpx 0;
  font-size: 22rpx;
  color: var(--stock-ink-3, #64748b);
  line-height: 1.65;

  &__title {
    display: block;
    font-weight: 700;
    color: var(--stock-ink-2, #475569);
    margin-bottom: 8rpx;
  }

  &__line {
    display: block;
  }

  &__bold {
    font-weight: 700;
    color: var(--stock-ink-2, #475569);
  }
}

.sp-cta {
  flex-shrink: 0;
  padding: 16rpx 28rpx calc(36rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid var(--stock-line, #e5e9f2);

  &__btn {
    width: 100%;
    height: 84rpx;
    line-height: 84rpx;
    border: none;
    border-radius: 24rpx;
    font-size: 28rpx;
    font-weight: 800;
    color: #fff;
    letter-spacing: 1rpx;
    background: linear-gradient(
      135deg,
      var(--stock-navy, #0b1e47),
      var(--stock-blue, #2563eb)
    );
    box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.25);

    &.disabled,
    &[disabled] {
      opacity: 0.45;
      color: #fff;
      box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.12);
    }

    &::after {
      border: none;
    }
  }
}

.deposit-guide {
  padding: 32rpx 32rpx calc(32rpx + env(safe-area-inset-bottom));
  background: #fff;

  &__head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
  }

  &__title {
    font-size: 30rpx;
    font-weight: 700;
    color: var(--stock-ink, #0f172a);
  }

  &__close {
    font-size: 28rpx;
    font-weight: 600;
    color: var(--stock-blue, #2563eb);
  }

  &__body {
    margin: 0;
  }

  &__record {
    display: block;
    margin-top: 28rpx;
    text-align: center;
    font-size: 26rpx;
    font-weight: 600;
    color: var(--stock-blue, #2563eb);
  }
}
</style>
