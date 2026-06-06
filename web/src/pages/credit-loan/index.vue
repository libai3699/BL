<script setup>
import { ref, computed } from 'vue';
import { goBack } from '@/utils/navigate';

const amt = ref(50000);
const months = ref(6);
const rate = computed(() => ({ 3: 5.2, 6: 5.8, 12: 6.4, 24: 7.2 })[months.value]);
const monthlyPay = computed(() =>
  Math.round((amt.value * (1 + (rate.value / 100) * (months.value / 12))) / months.value)
);
const total = computed(() => monthlyPay.value * months.value);
const fmt = (n) =>
  Number(n || 0).toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
const onApply = () =>
  uni.showToast({ title: uni.$t('creditLoan.submitted'), icon: 'success' });
</script>

<template>
  <page-wrapper>
    <view class="page">
      <view class="hdr dark">
        <view class="back" @tap="goBack">‹</view>
        <text class="t">{{ $t('creditLoan.title') }}</text>
        <text class="r white">{{ $t('creditLoan.repay') }}</text>
      </view>
      <scroll-view scroll-y class="body">
        <view class="hero">
          <text class="lbl">{{ $t('creditLoan.scoreLabel') }}</text>
          <text class="score">782</text>
          <text class="grade">{{ $t('creditLoan.grade') }}</text>
          <view class="meta">
            <view
              ><text class="v">150,000</text
              ><text class="k">{{ $t('creditLoan.limit') }}</text></view
            >
            <view
              ><text class="v gold">5.2%</text
              ><text class="k">{{ $t('creditLoan.minRate') }}</text></view
            >
            <view
              ><text class="v">24h</text
              ><text class="k">{{ $t('creditLoan.fast') }}</text></view
            >
          </view>
        </view>
        <text class="st">{{ $t('creditLoan.chooseAmount') }}</text>
        <view class="quick">
          <view
            v-for="v in [10000, 30000, 50000, 100000]"
            :key="v"
            class="q"
            :class="{ active: amt === v }"
            @tap="amt = v"
          >
            {{ v.toLocaleString() }}<text class="u">MXN</text>
          </view>
        </view>
        <text class="st">{{ $t('creditLoan.chooseTerm') }}</text>
        <view class="terms">
          <view
            v-for="m in [3, 6, 12, 24]"
            :key="m"
            class="term"
            :class="{ active: months === m }"
            @tap="months = m"
          >
            {{ m }} {{ $t('creditLoan.month') }}
            <text class="r">{{ { 3: 5.2, 6: 5.8, 12: 6.4, 24: 7.2 }[m] }}%</text>
          </view>
        </view>
        <view class="calc">
          <view class="r"
            ><text>{{ $t('creditLoan.loanAmount') }}</text
            ><text class="b">{{ fmt(amt) }} MXN</text></view
          >
          <view class="r"
            ><text>{{ $t('creditLoan.termRate') }}</text
            ><text class="b"
              >{{ months }} {{ $t('creditLoan.month') }} / {{ rate }}%</text
            ></view
          >
          <view class="r"
            ><text>{{ $t('creditLoan.monthly') }}</text
            ><text class="b">{{ fmt(monthlyPay) }} MXN</text></view
          >
          <view class="r total"
            ><text>{{ $t('creditLoan.totalRepay') }}</text
            ><text class="b">{{ fmt(total) }} MXN</text></view
          >
        </view>
        <text class="warn">{{ $t('creditLoan.features') }}</text>
      </scroll-view>
      <view class="cta">
        <view class="btn" @tap="onApply">{{ $t('creditLoan.apply') }}</view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--stock-page-bg);
}
.hdr {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx 24rpx;
  &.dark {
    background: linear-gradient(135deg, #15803d, #10b981);
    color: #fff;
  }
}
.back {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
}
.hdr .t {
  flex: 1;
  text-align: center;
  font-size: 30rpx;
  font-weight: 800;
}
.hdr .r {
  padding: 0 24rpx;
  font-size: 24rpx;
  font-weight: 600;
  &.white {
    color: #fff;
  }
}
.body {
  flex: 1;
  padding-bottom: 200rpx;
}
.hero {
  background: linear-gradient(135deg, #15803d, #10b981);
  color: #fff;
  padding: 28rpx 36rpx 44rpx;
}
.hero .lbl {
  display: block;
  font-size: 22rpx;
  opacity: 0.85;
}
.hero .score {
  display: block;
  font-size: 96rpx;
  font-weight: 800;
  @include stock-tnum;
  line-height: 1.1;
}
.hero .grade {
  display: inline-block;
  margin-top: 16rpx;
  padding: 8rpx 24rpx;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 16rpx;
  font-size: 24rpx;
  font-weight: 700;
}
.hero .meta {
  display: flex;
  gap: 36rpx;
  margin-top: 28rpx;
  font-size: 22rpx;
  .v {
    display: block;
    font-size: 30rpx;
    font-weight: 800;
    @include stock-tnum;
    &.gold {
      color: #fde68a;
    }
  }
  .k {
    display: block;
    opacity: 0.7;
    margin-top: 4rpx;
  }
}
.st {
  display: block;
  margin: 28rpx 28rpx 0;
  font-size: 22rpx;
  color: var(--stock-ink-2);
  font-weight: 700;
}
.quick {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12rpx;
  margin: 16rpx 24rpx;
}
.q {
  padding: 24rpx 0;
  background: #fff;
  border-radius: 20rpx;
  text-align: center;
  font-size: 24rpx;
  font-weight: 700;
  @include stock-tnum;
  box-shadow: 0 4rpx 12rpx rgba(15, 30, 71, 0.05);
  .u {
    display: block;
    font-size: 16rpx;
    font-weight: 500;
    opacity: 0.7;
    margin-top: 4rpx;
  }
  &.active {
    background: linear-gradient(135deg, var(--stock-up), #059669);
    color: #fff;
  }
}
.terms {
  display: flex;
  gap: 16rpx;
  margin: 16rpx 24rpx 0;
}
.term {
  flex: 1;
  padding: 18rpx 0;
  text-align: center;
  background: var(--stock-fill-soft);
  border: 3rpx solid var(--stock-line);
  border-radius: 20rpx;
  font-size: 24rpx;
  font-weight: 600;
  .r {
    display: block;
    font-size: 19rpx;
    color: var(--stock-ink-3);
    margin-top: 4rpx;
  }
  &.active {
    background: var(--stock-navy);
    color: #fff;
    border-color: var(--stock-navy);
    .r {
      color: rgba(255, 255, 255, 0.8);
    }
  }
}
.calc {
  margin: 28rpx 24rpx;
  @include stock-card;
  padding: 28rpx 36rpx;
}
.calc .r {
  display: flex;
  justify-content: space-between;
  padding: 16rpx 0;
  font-size: 24rpx;
  color: var(--stock-ink-3);
  border-bottom: 2rpx dashed var(--stock-line);
  .b {
    color: var(--stock-ink);
    font-weight: 700;
    @include stock-tnum;
  }
  &.total {
    padding: 24rpx 0 0;
    margin-top: 10rpx;
    border-top: 4rpx solid var(--stock-navy);
    border-bottom: none;
    font-size: 28rpx;
    font-weight: 800;
    color: var(--stock-ink);
  }
}
.warn {
  display: block;
  margin: 0 24rpx 20rpx;
  padding: 8rpx;
  font-size: 20rpx;
  color: var(--stock-ink-3);
  line-height: 1.5;
}
.cta {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 16rpx 24rpx 36rpx;
  background: #fff;
  border-top: 2rpx solid var(--stock-line);
}
.btn {
  height: 96rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, var(--stock-navy), var(--stock-blue));
  color: #fff;
  font-size: 28rpx;
  font-weight: 800;
  text-align: center;
  line-height: 96rpx;
}
</style>
