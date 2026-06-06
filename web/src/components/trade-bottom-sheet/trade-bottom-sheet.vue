<script setup>
/**
 * 交易底部弹层（静态演示，对齐原型 #bottomSheet）
 * 后续可接入真实标的、下单接口
 */
import { ref, computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  /** 打开时默认买卖方向 */
  defaultSide: {
    type: String,
    default: 'buy',
    validator: (v) => ['buy', 'sell'].includes(v)
  }
});

const emit = defineEmits(['update:modelValue']);

const { t } = useI18n();

// —— 静态演示数据（原型 NVIDIA） ——
const DEMO = {
  name: 'NVIDIA Corp',
  code: 'NASDAQ:NVDA',
  price: 225.83,
  changePct: 2.28,
  balance: 383796.3,
  maxQty: 1699,
  feeRate: 0.0005
};

const side = ref('buy');
const orderType = ref('limit');
const price = ref(DEMO.price);
const qty = ref(100);
const activePct = ref(null);

const orderTypes = [
  { key: 'limit', labelKey: 'tradeSheet.limitOrder' },
  { key: 'market', labelKey: 'tradeSheet.marketOrder' },
  { key: 'stop', labelKey: 'tradeSheet.stopOrder' },
  { key: 'conditional', labelKey: 'tradeSheet.conditionalOrder' }
];

const pctOptions = [25, 50, 75, 100];

const visible = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v)
});

const estAmount = computed(() => price.value * qty.value);
const estFee = computed(() => estAmount.value * DEMO.feeRate);
const estTotal = computed(() =>
  side.value === 'buy' ? estAmount.value + estFee.value : estAmount.value - estFee.value
);

const changeClass = computed(() =>
  DEMO.changePct >= 0 ? 'trade-sheet__pill--up' : 'trade-sheet__pill--down'
);

const submitLabel = computed(() => {
  const action = side.value === 'buy' ? t('trade.buy') : t('trade.sell');
  return `${action} ${DEMO.name.split(' ')[0]}`;
});

watch(
  () => props.modelValue,
  (open) => {
    if (open) {
      side.value = props.defaultSide;
      price.value = DEMO.price;
      qty.value = 100;
      activePct.value = null;
      orderType.value = 'limit';
    }
  }
);

const close = () => {
  visible.value = false;
};

const setSide = (next) => {
  side.value = next;
};

const setOrderType = (key) => {
  orderType.value = key;
};

const stepPrice = (delta) => {
  price.value = Math.max(0, Math.round((price.value + delta) * 100) / 100);
  activePct.value = null;
};

const stepQty = (delta) => {
  qty.value = Math.max(0, Math.round(qty.value + delta));
  activePct.value = null;
};

const setPct = (pct) => {
  activePct.value = pct;
  qty.value = Math.max(0, Math.floor((DEMO.maxQty * pct) / 100));
};

const formatMoney = (n) =>
  n.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const onSubmit = () => {
  uni.showToast({
    title: t('tradeSheet.demoSubmit'),
    icon: 'none'
  });
  close();
};
</script>

<template>
  <view v-if="visible" class="trade-sheet-root">
    <view class="trade-sheet__overlay" @tap="close" />
    <view class="trade-sheet" @tap.stop>
      <view class="trade-sheet__header">
        <view class="trade-sheet__handle" />
        <view class="trade-sheet__top">
          <view class="trade-sheet__head">
            <text class="trade-sheet__name">{{ DEMO.name }}</text>
            <view class="trade-sheet__sub">
              <text>{{ t('tradeSheet.spotPrice') }}</text>
              <text class="trade-sheet__sub-price">{{ DEMO.price.toFixed(2) }}</text>
              <text class="trade-sheet__pill" :class="changeClass">
                {{ DEMO.changePct >= 0 ? '+' : '' }}{{ DEMO.changePct }}%
              </text>
              <text>· {{ DEMO.code }}</text>
            </view>
          </view>
          <view class="trade-sheet__close" @tap="close">
            <svg
              width="14"
              height="14"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2.5"
              stroke-linecap="round"
            >
              <path d="M6 6l12 12M18 6L6 18" />
            </svg>
          </view>
        </view>
      </view>

      <view class="trade-sheet__body">
        <view class="trade-sheet__body-inner">
          <view class="trade-sheet__side">
            <view
              class="trade-sheet__side-tab"
              :class="{
                'trade-sheet__side-tab--active trade-sheet__side-tab--buy': side === 'buy'
              }"
              @tap="setSide('buy')"
            >
              {{ t('trade.buy') }}
            </view>
            <view
              class="trade-sheet__side-tab"
              :class="{
                'trade-sheet__side-tab--active trade-sheet__side-tab--sell':
                  side === 'sell'
              }"
              @tap="setSide('sell')"
            >
              {{ t('trade.sell') }}
            </view>
          </view>

          <view class="trade-sheet__order-type">
            <view
              v-for="ot in orderTypes"
              :key="ot.key"
              class="trade-sheet__ot-pill"
              :class="{ 'trade-sheet__ot-pill--active': orderType === ot.key }"
              @tap="setOrderType(ot.key)"
            >
              {{ t(ot.labelKey) }}
            </view>
          </view>

          <view class="trade-sheet__input-row">
            <view class="trade-sheet__lab">
              <text>{{ t('tradeSheet.entrustPrice') }}</text>
              <text
                >{{ t('tradeSheet.bid1') }}
                <text class="trade-sheet__lab-b">{{ DEMO.price.toFixed(2) }}</text></text
              >
            </view>
            <view class="trade-sheet__num-input">
              <view
                class="trade-sheet__num-btn"
                hover-class="trade-sheet__num-btn--pressed"
                @tap="stepPrice(-0.05)"
                >−</view
              >
              <input
                class="trade-sheet__num-field"
                type="digit"
                :value="price.toFixed(2)"
                disabled
              />
              <view
                class="trade-sheet__num-btn"
                hover-class="trade-sheet__num-btn--pressed"
                @tap="stepPrice(0.05)"
                >+</view
              >
            </view>
          </view>

          <view class="trade-sheet__input-row">
            <view class="trade-sheet__lab">
              <text>{{ t('tradeSheet.quantity') }}</text>
              <text>
                {{ t('tradeSheet.maxBuy') }}
                <text class="trade-sheet__lab-b">{{ DEMO.maxQty.toLocaleString() }}</text>
                {{ t('tradeSheet.sharesUnit') }}
              </text>
            </view>
            <view class="trade-sheet__num-input">
              <view
                class="trade-sheet__num-btn"
                hover-class="trade-sheet__num-btn--pressed"
                @tap="stepQty(-100)"
                >−</view
              >
              <input
                class="trade-sheet__num-field"
                type="number"
                :value="String(qty)"
                disabled
              />
              <view
                class="trade-sheet__num-btn"
                hover-class="trade-sheet__num-btn--pressed"
                @tap="stepQty(100)"
                >+</view
              >
            </view>
          </view>

          <view class="trade-sheet__pct">
            <view
              v-for="p in pctOptions"
              :key="p"
              class="trade-sheet__pct-btn"
              :class="{ 'trade-sheet__pct-btn--active': activePct === p }"
              @tap="setPct(p)"
            >
              {{ p }}%
            </view>
          </view>

          <view class="trade-sheet__summary">
            <view class="trade-sheet__summary-row">
              <text>{{ t('trade.estimatedAmount') }}</text>
              <text>{{ formatMoney(estAmount) }} MXN</text>
            </view>
            <view class="trade-sheet__summary-row">
              <text>{{ t('tradeSheet.feeRate', { rate: '0.05' }) }}</text>
              <text>~ {{ formatMoney(estFee) }} MXN</text>
            </view>
            <view class="trade-sheet__summary-row trade-sheet__summary-row--total">
              <text>{{ t('tradeSheet.total') }}</text>
              <text>{{ formatMoney(estTotal) }} MXN</text>
            </view>
          </view>
        </view>
      </view>

      <view class="trade-sheet__footer">
        <view class="trade-sheet__balance">
          <text>{{ t('trade.availableBalance') }}</text>
          <text class="trade-sheet__balance-b">{{ formatMoney(DEMO.balance) }} MXN</text>
        </view>
        <view
          class="trade-sheet__action"
          :class="
            side === 'buy' ? 'trade-sheet__action--buy' : 'trade-sheet__action--sell'
          "
          hover-class="trade-sheet__action--pressed"
          @tap="onSubmit"
        >
          {{ submitLabel }}
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
@use '@/styles/vars.scss' as *;

.trade-sheet-root {
  position: fixed;
  inset: 0;
  z-index: 1000;
  max-width: var(--page-max-width, #{$max-width});
  margin: 0 auto;
  pointer-events: none;
}

.trade-sheet__overlay {
  position: absolute;
  inset: 0;
  background: rgba(11, 30, 71, 0.45);
  pointer-events: auto;
  animation: trade-sheet-fade-in 0.25s ease;
}

.trade-sheet {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  height: 88vh;
  max-height: 88vh;
  overflow: hidden;
  background: #fff;
  border-radius: 44rpx 44rpx 0 0;
  box-shadow: 0 -20rpx 60rpx rgba(11, 30, 71, 0.18);
  pointer-events: auto;
  animation: trade-sheet-slide-up 0.3s cubic-bezier(0.4, 0, 0.2, 1);

  &__header {
    flex-shrink: 0;
    padding: 20rpx 36rpx 24rpx;
    background: #fff;
    border-radius: 44rpx 44rpx 0 0;
  }

  &__handle {
    width: 80rpx;
    height: 8rpx;
    margin: 0 auto 24rpx;
    background: #cbd5e1;
    border-radius: 4rpx;
  }

  &__top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
  }

  &__body {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    overflow-x: hidden;
    -webkit-overflow-scrolling: touch;
    overscroll-behavior: contain;
    box-sizing: border-box;
  }

  &__body-inner {
    padding: 0 36rpx 16rpx;
    box-sizing: border-box;
  }

  &__footer {
    flex-shrink: 0;
    padding: 20rpx 36rpx calc(24rpx + env(safe-area-inset-bottom));
    background: #fff;
    border-top: 1rpx solid var(--line-color, #e5e9f2);
    box-shadow: 0 -8rpx 24rpx rgba(11, 30, 71, 0.06);
  }

  &__name {
    font-size: 34rpx;
    font-weight: 800;
    color: var(--stock-ink, #0f172a);
    letter-spacing: 0.4rpx;
  }

  &__sub {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 8rpx;
    margin-top: 8rpx;
    font-size: 23rpx;
    color: var(--stock-ink-3, #64748b);
  }

  &__sub-price {
    font-weight: 700;
    font-size: 26rpx;
    color: var(--stock-ink, #0f172a);
  }

  &__pill {
    padding: 2rpx 12rpx;
    border-radius: 8rpx;
    font-size: 22rpx;
    font-weight: 600;

    &--up {
      color: #10b981;
      background: rgba(16, 185, 129, 0.12);
    }

    &--down {
      color: #ef4444;
      background: rgba(239, 68, 68, 0.12);
    }
  }

  &__close {
    width: 64rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f1f5fb;
    border-radius: 18rpx;
    color: var(--stock-ink-2, #475569);
  }

  &__side {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 0;
    padding: 6rpx;
    margin-bottom: 28rpx;
    background: #f1f5fb;
    border-radius: 22rpx;
  }

  &__side-tab {
    padding: 20rpx 0;
    text-align: center;
    font-size: 28rpx;
    font-weight: 700;
    color: var(--stock-ink-2, #475569);
    border-radius: 18rpx;
    transition: 0.2s;

    &--active.trade-sheet__side-tab--buy {
      color: #fff;
      background: linear-gradient(135deg, #10b981, #059669);
      box-shadow: 0 6rpx 16rpx rgba(16, 185, 129, 0.3);
    }

    &--active.trade-sheet__side-tab--sell {
      color: #fff;
      background: linear-gradient(135deg, #ef4444, #dc2626);
      box-shadow: 0 6rpx 16rpx rgba(239, 68, 68, 0.3);
    }
  }

  &__order-type {
    display: flex;
    flex-wrap: wrap;
    gap: 16rpx;
    margin-bottom: 28rpx;
  }

  &__ot-pill {
    padding: 14rpx 28rpx;
    border-radius: 28rpx;
    border: 1rpx solid var(--line-color, #e5e9f2);
    font-size: 24rpx;
    font-weight: 600;
    color: var(--stock-ink-2, #475569);
    background: #fff;

    &--active {
      color: #fff;
      background: var(--stock-navy, #0b1e47);
      border-color: var(--stock-navy, #0b1e47);
    }
  }

  &__input-row {
    margin-bottom: 24rpx;
  }

  &__lab {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12rpx;
    font-size: 23rpx;
    color: var(--stock-ink-3, #64748b);
  }

  &__lab-b {
    font-weight: 600;
    color: var(--stock-ink-2, #475569);
  }

  &__num-input {
    display: flex;
    align-items: center;
    height: 92rpx;
    padding: 8rpx;
    background: #f4f6fb;
    border-radius: 22rpx;
    border: 1rpx solid var(--line-color, #e5e9f2);
  }

  &__num-btn {
    width: 72rpx;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 36rpx;
    font-weight: 600;
    color: var(--stock-ink, #0f172a);
    background: #fff;
    border-radius: 16rpx;
    box-shadow: 0 2rpx 4rpx rgba(15, 23, 42, 0.06);

    &--pressed {
      transform: scale(0.94);
    }
  }

  &__num-field {
    flex: 1;
    text-align: center;
    font-size: 34rpx;
    font-weight: 800;
    color: var(--stock-ink, #0f172a);
  }

  &__pct {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16rpx;
    margin-bottom: 28rpx;
  }

  &__pct-btn {
    padding: 18rpx 0;
    text-align: center;
    font-size: 25rpx;
    font-weight: 700;
    color: var(--stock-ink-2, #475569);
    background: #f4f6fb;
    border-radius: 18rpx;
    border: 1rpx solid var(--line-color, #e5e9f2);

    &--active {
      color: #fff;
      background: var(--stock-navy, #0b1e47);
      border-color: var(--stock-navy, #0b1e47);
    }
  }

  &__summary {
    padding: 24rpx 28rpx;
    margin-bottom: 32rpx;
    background: #f8fafc;
    border-radius: 22rpx;
    border: 1rpx solid var(--line-color, #e5e9f2);
  }

  &__summary-row {
    display: flex;
    justify-content: space-between;
    padding: 8rpx 0;
    font-size: 24rpx;
    color: var(--stock-ink-2, #475569);

    &--total {
      padding-top: 16rpx;
      margin-top: 10rpx;
      border-top: 1rpx dashed var(--line-color, #e5e9f2);
      font-size: 28rpx;
      font-weight: 800;
      color: var(--stock-ink, #0f172a);
    }
  }

  &__balance {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20rpx;
    font-size: 23rpx;
    color: var(--stock-ink-3, #64748b);
  }

  &__balance-b {
    font-weight: 700;
    color: var(--stock-ink-2, #475569);
  }

  &__action {
    width: 100%;
    height: 104rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 26rpx;
    font-size: 31rpx;
    font-weight: 800;
    color: #fff;
    letter-spacing: 1rpx;

    &--buy {
      background: linear-gradient(135deg, #10b981, #059669);
      box-shadow: 0 12rpx 28rpx rgba(16, 185, 129, 0.3);
    }

    &--sell {
      background: linear-gradient(135deg, #ef4444, #dc2626);
      box-shadow: 0 12rpx 28rpx rgba(239, 68, 68, 0.3);
    }

    &--pressed {
      opacity: 0.92;
      transform: scale(0.98);
    }
  }
}

@keyframes trade-sheet-fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes trade-sheet-slide-up {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}
</style>
