<script setup>
import { ref, computed, watch } from 'vue';
import { onShow } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';

import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack, navigateTo } from '@/utils/navigate';
import { reqGetCurrencyList, reqGetExchangeRate, reqTransfer } from '@/apis/transfer';

const { t } = useI18n();
const commonStore = useCommonStore();
const userStore = useUserStore();

const refCurrencyList = ref([]);
const refFromCurrency = ref(null);
const refToCurrency = ref(null);
const refExchangeRate = ref(null);
const refAmount = ref('');
const refBalances = ref({
  usd: 0,
  mxn: 0
});

const accountLabel = (currency) => {
  if (!currency) return '';
  return currency.amountType === 1 ? t('transfer.fromAccount') : t('transfer.toAccount');
};

const currencyCode = (currency) => currency?.nameEn || currency?.code || '';

const availableBalance = computed(() => {
  if (!refFromCurrency.value) return 0;
  return refBalances.value[refFromCurrency.value.code] || 0;
});

const toBalance = computed(() => {
  if (!refToCurrency.value) return 0;
  return refBalances.value[refToCurrency.value.code] || 0;
});

const estimatedAmount = computed(() => {
  if (!refAmount.value || !refExchangeRate.value) return 0;
  const amount = parseFloat(refAmount.value);
  if (Number.isNaN(amount)) return 0;
  return amount * refExchangeRate.value.actualRate;
});

const amountLabel = computed(() =>
  t('transfer.amountWithCurrency', {
    currency: currencyCode(refFromCurrency.value) || 'MXN'
  })
);

const canSubmit = computed(() => {
  const amt = parseFloat(refAmount.value);
  return amt > 0 && amt <= availableBalance.value;
});

function toggleDirection() {
  const temp = refFromCurrency.value;
  refFromCurrency.value = refToCurrency.value;
  refToCurrency.value = temp;
  refAmount.value = '';
}

function onAmountInput(e) {
  refAmount.value = e.detail?.value ?? '';
}

function setMaxAmount() {
  refAmount.value = availableBalance.value.toString();
}

function goRecord() {
  navigateTo('/pages/fund-record/index');
}

async function confirmTransfer() {
  if (!refAmount.value || parseFloat(refAmount.value) <= 0) {
    uni.showToast({
      title: t('transfer.amountPlaceholder'),
      icon: 'none'
    });
    return;
  }

  if (parseFloat(refAmount.value) > availableBalance.value) {
    uni.showToast({
      title: t('common.balance') + t('common.none'),
      icon: 'none'
    });
    return;
  }

  const res = await reqTransfer({
    amount: parseFloat(refAmount.value),
    fromCurrency: refFromCurrency.value.code,
    toCurrency: refToCurrency.value.code
  });

  if (res) {
    refBalances.value.usd = res.usdBalance || 0;
    refBalances.value.mxn = res.mxnBalance || 0;

    uni.showToast({
      title: t('transfer.transferSuccess'),
      icon: 'success'
    });

    refAmount.value = '';
    userStore.getUserInfo();
    getBalance();
  }
}

async function getExchangeRate() {
  if (!refFromCurrency.value || !refToCurrency.value) return;

  const res = await reqGetExchangeRate({
    fromCurrency: refFromCurrency.value.code,
    toCurrency: refToCurrency.value.code
  });

  if (res) {
    refExchangeRate.value = res;
  }
}

async function getCurrencyList() {
  const res = await reqGetCurrencyList();

  if (res && res.length > 0) {
    refCurrencyList.value = res;

    const usd = res.find((item) => item.amountType === 1);
    const mxn = res.find((item) => item.amountType === 2);

    if (usd && mxn) {
      refFromCurrency.value = usd;
      refToCurrency.value = mxn;
    }
  }
}

async function getBalance() {
  await userStore.getUserInfo();
  const userInfo = userStore.userInfo.value;

  refBalances.value = {
    usd: Number(userInfo?.usdAmt || 0),
    mxn: Number(userInfo?.mxAmt || 0)
  };
}

watch([refFromCurrency, refToCurrency], () => {
  getExchangeRate();
});

const onPageInit = async () => {
  await getCurrencyList();
  await getBalance();
};

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="transfer-page">
      <nav-bar :title="$t('transfer.pageTitle')">
        <template #right>
          <text class="transfer-nav-right" @tap="goRecord">{{
            $t('transfer.viewRecord')
          }}</text>
        </template>
      </nav-bar>

      <scroll-view scroll-y class="transfer-scroll">
        <view class="transfer-scroll__inner">
          <view class="transfer-account-card">
            <text class="transfer-account-card__k">{{
              $t('transfer.fromAccountLabel')
            }}</text>
            <text class="transfer-account-card__name">{{
              accountLabel(refFromCurrency)
            }}</text>
            <text class="transfer-account-card__meta">
              {{ $t('transfer.balanceAvailable') }}
              <c-amount :value="availableBalance" />
              {{ currencyCode(refFromCurrency) }}
            </text>
          </view>

          <view class="transfer-account-card transfer-account-card--to">
            <view class="transfer-swap" @tap="toggleDirection">
              <text>⇅</text>
            </view>
            <text class="transfer-account-card__k">{{
              $t('transfer.toAccountLabel')
            }}</text>
            <text class="transfer-account-card__name">{{
              accountLabel(refToCurrency)
            }}</text>
            <text class="transfer-account-card__meta">
              {{ $t('transfer.balanceLabel') }}
              <c-amount :value="toBalance" />
              {{ currencyCode(refToCurrency) }}
            </text>
          </view>

          <view v-if="refExchangeRate" class="transfer-rate-bar">
            <text>{{ $t('transfer.rateLabel') }}</text>
            <text class="transfer-rate-bar__val">{{
              refExchangeRate.rateDescription
            }}</text>
          </view>

          <view class="transfer-amount-block">
            <text class="transfer-amount-block__label">{{ amountLabel }}</text>
            <view class="sp-amount">
              <text class="sp-amount__cur">{{ currencyCode(refFromCurrency) }}</text>
              <input
                class="sp-amount__input"
                type="digit"
                :value="refAmount"
                :placeholder="$t('transfer.amountPlaceholder')"
                @input="onAmountInput"
              />
              <text class="sp-amount__all" @tap="setMaxAmount">{{
                $t('transfer.all')
              }}</text>
            </view>
          </view>

          <view class="sp-info-card">
            <view class="sp-info-card__ir">
              <text class="sp-info-card__k">{{ $t('transfer.actualReceive') }}</text>
              <view class="sp-info-card__v up">
                <c-amount :value="estimatedAmount" />
                <text>{{ currencyCode(refToCurrency) }}</text>
              </view>
            </view>
            <view class="sp-info-card__ir">
              <text class="sp-info-card__k">{{ $t('transfer.fee') }}</text>
              <text class="sp-info-card__v">{{ $t('transfer.feeFree') }}</text>
            </view>
            <view class="sp-info-card__ir">
              <text class="sp-info-card__k">{{ $t('transfer.arrivalTime') }}</text>
              <text class="sp-info-card__v">{{ $t('transfer.arrivalInstant') }}</text>
            </view>
          </view>

          <view class="transfer-notice">
            <text class="transfer-notice__line">· {{ $t('transfer.notice1') }}</text>
            <text class="transfer-notice__line">· {{ $t('transfer.notice2') }}</text>
          </view>
        </view>
      </scroll-view>

      <view class="sp-cta">
        <button
          class="sp-cta__btn"
          :class="{ disabled: !canSubmit }"
          :disabled="!canSubmit"
          @tap="confirmTransfer"
        >
          {{ $t('transfer.confirmTransfer') }}
        </button>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.transfer-page {
  height: 100vh;
  max-height: 100vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--stock-page-bg, #eef2f8);
  @include hasNavBar();
}

.transfer-nav-right {
  font-size: 26rpx;
  color: var(--stock-ink-3, #64748b);
  font-weight: 600;
  padding-right: 8rpx;
}

.transfer-scroll {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.transfer-scroll__inner {
  padding: 24rpx 24rpx 32rpx;
}

.transfer-account-card {
  @include stock-card;
  position: relative;
  background: #fff;
  border-radius: 28rpx;
  padding: 28rpx 32rpx;
  border: 1rpx solid var(--stock-line, #e5e9f2);
  box-shadow: 0 8rpx 28rpx rgba(15, 30, 71, 0.05);

  &--to {
    margin-top: 48rpx;
  }

  &__k {
    display: block;
    font-size: 22rpx;
    color: var(--stock-ink-3, #64748b);
  }

  &__name {
    display: block;
    margin-top: 8rpx;
    font-size: 36rpx;
    font-weight: 800;
    color: var(--stock-ink, #0f172a);
  }

  &__meta {
    display: block;
    margin-top: 12rpx;
    font-size: 22rpx;
    color: var(--stock-ink-3, #64748b);
    @include stock-tnum;
  }
}

.transfer-swap {
  position: absolute;
  left: 50%;
  top: -36rpx;
  transform: translateX(-50%);
  width: 72rpx;
  height: 72rpx;
  border-radius: 50%;
  background: linear-gradient(
    135deg,
    var(--stock-navy, #0b1e47),
    var(--stock-blue, #2563eb)
  );
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.4);
  z-index: 2;
}

.transfer-rate-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: 28rpx 8rpx 0;
  padding: 16rpx 24rpx;
  background: var(--stock-fill, #f1f5f9);
  border-radius: 16rpx;
  font-size: 22rpx;
  color: var(--stock-ink-2, #475569);

  &__val {
    font-weight: 700;
    color: var(--stock-ink, #0f172a);
    @include stock-tnum;
  }
}

.transfer-amount-block {
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

  &__all {
    flex-shrink: 0;
    font-size: 22rpx;
    font-weight: 700;
    color: var(--stock-gold, #d4a24c);
  }
}

.sp-info-card {
  margin-top: 8rpx;
  padding: 28rpx 32rpx;
  background: #fff;
  border-radius: 24rpx;
  border: 1rpx solid var(--stock-line, #e5e9f2);

  &__ir {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 24rpx;
    padding: 12rpx 0;
    font-size: 24rpx;
  }

  &__k {
    color: var(--stock-ink-3, #64748b);
  }

  &__v {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8rpx;
    color: var(--stock-ink, #0f172a);
    font-weight: 600;
    text-align: right;
    @include stock-tnum;

    &.up {
      color: var(--stock-green, #16a34a);
    }
  }
}

.transfer-notice {
  margin: 24rpx 8rpx 0;
  font-size: 22rpx;
  color: var(--stock-ink-3, #64748b);
  line-height: 1.65;

  &__line {
    display: block;
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
</style>
