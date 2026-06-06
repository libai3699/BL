<script setup>
import { ref, computed } from 'vue';
import { goBack } from '@/utils/navigate';

const amount = ref(500000);
const term = ref(30);
const rate = computed(() => ({ 7: 3.6, 30: 4.8, 90: 5.4, 180: 6.0 })[term.value] || 4.8);
const interest = computed(() =>
  Math.round(((amount.value * rate.value) / 100) * (term.value / 365))
);
const fmt = (n) =>
  Number(n || 0).toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
const onApply = () =>
  uni.showToast({ title: uni.$t('vipLoan.submitted'), icon: 'success' });
</script>

<template>
  <page-wrapper>
    <view class="page">
      <view class="hdr dark">
        <view class="back" @tap="goBack">‹</view>
        <text class="t">{{ $t('vipLoan.title') }}</text>
        <text class="r">{{ $t('vipLoan.records') }}</text>
      </view>
      <scroll-view scroll-y class="body">
        <view class="hero">
          <text class="title">{{ $t('vipLoan.heroTitle') }}</text>
          <text class="sub">{{ $t('vipLoan.heroSub') }}</text>
          <text class="lbl">{{ $t('vipLoan.creditLabel') }}</text>
          <text class="credit">993,915.78 <small>MXN</small></text>
          <text class="meta">{{ $t('vipLoan.creditMeta') }}</text>
        </view>
        <view class="slider">
          <text class="k">{{ $t('vipLoan.amount') }}</text>
          <text class="v">{{ fmt(amount) }} <small>MXN</small></text>
          <view class="bar"><view class="fill"></view><view class="knob"></view></view>
        </view>
        <text class="section-title">{{ $t('vipLoan.term') }}</text>
        <view class="terms">
          <view
            v-for="t in [7, 30, 90, 180]"
            :key="t"
            class="term"
            :class="{ active: term === t }"
            @tap="term = t"
          >
            {{ t }}{{ $t('vipLoan.day') }}
            <text class="r"
              >{{ { 7: 3.6, 30: 4.8, 90: 5.4, 180: 6.0 }[t] }}%
              {{ $t('vipLoan.yearRate') }}</text
            >
          </view>
        </view>
        <view class="calc">
          <view class="r"
            ><text>{{ $t('vipLoan.amount') }}</text
            ><text class="b">{{ fmt(amount) }} MXN</text></view
          >
          <view class="r"
            ><text>{{ $t('vipLoan.annualRate') }}</text
            ><text class="b gold">{{ rate }}%</text></view
          >
          <view class="r"
            ><text>{{ $t('vipLoan.interest', { days: term }) }}</text
            ><text class="b">{{ fmt(interest) }} MXN</text></view
          >
          <view class="r total"
            ><text>{{ $t('vipLoan.repay', { days: term }) }}</text
            ><text class="b">{{ fmt(amount + interest) }} MXN</text></view
          >
        </view>
        <text class="warning">{{ $t('vipLoan.warning') }}</text>
      </scroll-view>
      <view class="cta">
        <view class="btn" @tap="onApply"
          >{{ $t('vipLoan.apply') }} {{ fmt(amount) }} MXN</view
        >
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
    background: linear-gradient(160deg, #13306e, #2563eb);
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
  color: #f1e0bd;
  font-weight: 600;
}
.body {
  flex: 1;
  padding-bottom: 200rpx;
}
.hero {
  background: linear-gradient(160deg, #13306e, #2563eb);
  color: #fff;
  padding: 28rpx 36rpx 44rpx;
}
.hero .title {
  display: block;
  font-size: 40rpx;
  font-weight: 800;
}
.hero .sub {
  display: block;
  font-size: 24rpx;
  opacity: 0.85;
  margin-top: 12rpx;
}
.hero .lbl {
  display: block;
  font-size: 22rpx;
  opacity: 0.7;
  margin-top: 32rpx;
}
.hero .credit {
  display: block;
  font-size: 56rpx;
  font-weight: 800;
  @include stock-tnum;
  small {
    font-size: 28rpx;
    opacity: 0.8;
  }
}
.hero .meta {
  display: block;
  font-size: 22rpx;
  opacity: 0.8;
  margin-top: 8rpx;
}
.slider {
  margin: 28rpx 24rpx;
  @include stock-card;
  padding: 36rpx;
}
.slider .k {
  font-size: 22rpx;
  color: var(--stock-ink-3);
  display: block;
}
.slider .v {
  display: block;
  font-size: 60rpx;
  font-weight: 800;
  margin: 12rpx 0 24rpx;
  @include stock-tnum;
  small {
    font-size: 28rpx;
    color: var(--stock-ink-2);
  }
}
.bar {
  position: relative;
  height: 8rpx;
  background: var(--stock-fill-soft);
  border-radius: 4rpx;
  margin: 36rpx 0 16rpx;
  .fill {
    position: absolute;
    left: 0;
    height: 100%;
    width: 55%;
    background: linear-gradient(90deg, var(--stock-gold), var(--stock-blue));
    border-radius: 4rpx;
  }
  .knob {
    position: absolute;
    width: 40rpx;
    height: 40rpx;
    left: calc(55% - 20rpx);
    top: -16rpx;
    background: #fff;
    border: 6rpx solid var(--stock-blue);
    border-radius: 50%;
  }
}
.section-title {
  display: block;
  margin: 0 28rpx;
  font-size: 22rpx;
  color: var(--stock-ink-2);
  font-weight: 700;
}
.terms {
  display: flex;
  gap: 16rpx;
  padding: 16rpx 24rpx;
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
  color: var(--stock-ink-2);
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
    &.gold {
      color: var(--stock-gold);
    }
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
.warning {
  display: block;
  margin: 0 24rpx;
  padding: 20rpx 8rpx;
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
