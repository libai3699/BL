<script setup>
import { ref } from 'vue';
import { goBack } from '@/utils/navigate';

const cond = ref('above');
const price = ref('230.00');
const existing = ref([
  {
    condKey: 'alert.above',
    val: '$230.00',
    valClass: 'up',
    meta: 'NVDA · 创建于今天',
    on: true
  },
  {
    condKey: 'alert.below',
    val: '$220.00',
    valClass: 'down',
    meta: 'NVDA · 创建于昨天',
    on: true
  },
  {
    condKey: 'alert.pct',
    val: '±3.0%',
    valClass: 'gold',
    meta: 'NVDA · 创建于 3 天前',
    on: false
  }
]);
const opts = [
  { k: 'above', nKey: 'alert.above' },
  { k: 'below', nKey: 'alert.below' },
  { k: 'pct', nKey: 'alert.pct' },
  { k: 'vol', nKey: 'alert.vol' }
];
const onAdd = () => uni.showToast({ title: uni.$t('alert.added'), icon: 'success' });
</script>

<template>
  <page-wrapper>
    <view class="page">
      <view class="hdr">
        <view class="back" @tap="goBack">‹</view>
        <text class="t">{{ $t('alert.title') }}</text>
        <text class="r">{{ $t('alert.history') }}</text>
      </view>
      <view class="stock">
        <view class="avatar">N</view>
        <view class="info">
          <text class="n">NVIDIA Corp</text>
          <text class="c">NASDAQ:NVDA</text>
        </view>
        <view class="rp">
          <text class="p">225.83</text>
          <text class="ch">+2.28%</text>
        </view>
      </view>
      <scroll-view scroll-y class="body">
        <view class="block">
          <text class="h">{{ $t('alert.chooseCond') }}</text>
          <view class="opts">
            <view
              v-for="o in opts"
              :key="o.k"
              class="opt"
              :class="{ active: cond === o.k }"
              @tap="cond = o.k"
            >
              <text>{{ $t(o.nKey) }}</text>
            </view>
          </view>
          <text class="lbl">{{ $t('alert.targetPrice') }}</text>
          <view class="input">
            <text class="cur">$</text>
            <input v-model="price" type="digit" />
          </view>
          <view class="hint">
            <text>{{ $t('alert.current', { price: '225.83' }) }}</text>
            <text class="up">{{ $t('alert.distance', { pct: '+1.85%' }) }}</text>
          </view>
        </view>
        <text class="title">{{ $t('alert.existing', { count: 3 }) }}</text>
        <view class="list">
          <view v-for="(e, i) in existing" :key="i" class="r">
            <view class="info">
              <text class="ttl"
                >{{ $t(e.condKey) }} <text :class="e.valClass">{{ e.val }}</text></text
              >
              <text class="m">{{ e.meta }}</text>
            </view>
            <view class="switch" :class="{ on: e.on }" @tap="e.on = !e.on"></view>
          </view>
        </view>
        <view class="tip">{{ $t('alert.tip') }}</view>
      </scroll-view>
      <view class="cta">
        <view class="btn" @tap="onAdd">{{ $t('alert.addBtn') }}</view>
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
  background: #fff;
  border-bottom: 2rpx solid var(--stock-line);
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
  color: var(--stock-gold);
  font-weight: 600;
}
.stock {
  padding: 36rpx 32rpx;
  background: #fff;
  border-bottom: 2rpx solid var(--stock-line);
  display: flex;
  align-items: center;
  gap: 28rpx;
}
.avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  background: #76b900;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  font-weight: 800;
}
.stock .info {
  flex: 1;
}
.stock .info .n {
  font-size: 32rpx;
  font-weight: 800;
  display: block;
}
.stock .info .c {
  font-size: 22rpx;
  color: var(--stock-ink-3);
  margin-top: 6rpx;
  display: block;
}
.rp {
  text-align: right;
}
.rp .p {
  font-size: 36rpx;
  font-weight: 800;
  color: var(--stock-up);
  @include stock-tnum;
  display: block;
}
.rp .ch {
  font-size: 22rpx;
  color: var(--stock-up);
  font-weight: 700;
  margin-top: 4rpx;
  display: block;
}
.body {
  flex: 1;
  padding-bottom: 200rpx;
}
.block {
  margin: 28rpx 24rpx;
  @include stock-card;
  padding: 32rpx;
}
.h {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  margin-bottom: 24rpx;
}
.opts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
}
.opt {
  padding: 28rpx 24rpx;
  background: var(--stock-fill-soft);
  border: 3rpx solid var(--stock-line);
  border-radius: 22rpx;
  font-size: 24rpx;
  font-weight: 600;
  text-align: center;
  &.active {
    background: linear-gradient(135deg, #f0f5ff, #e0e7ff);
    border-color: var(--stock-blue);
    color: var(--stock-blue);
  }
}
.lbl {
  display: block;
  margin-top: 32rpx;
  font-size: 22rpx;
  color: var(--stock-ink-3);
  font-weight: 600;
}
.input {
  margin-top: 16rpx;
  background: var(--stock-fill-soft);
  border: 3rpx solid var(--stock-line);
  border-radius: 22rpx;
  padding: 0 28rpx;
  height: 96rpx;
  display: flex;
  align-items: center;
  gap: 16rpx;
}
.input .cur {
  font-size: 24rpx;
  color: var(--stock-ink-3);
  font-weight: 600;
}
.input input {
  flex: 1;
  font-size: 40rpx;
  font-weight: 800;
  @include stock-tnum;
}
.hint {
  display: flex;
  justify-content: space-between;
  margin-top: 16rpx;
  font-size: 22rpx;
  color: var(--stock-ink-3);
  .up {
    color: var(--stock-up);
  }
}
.title {
  display: block;
  margin: 28rpx 28rpx 12rpx;
  font-size: 24rpx;
  font-weight: 700;
}
.list {
  margin: 0 24rpx;
  @include stock-card;
  overflow: hidden;
}
.list .r {
  display: flex;
  align-items: center;
  gap: 24rpx;
  padding: 24rpx 28rpx;
  border-bottom: 2rpx solid var(--stock-line);
  &:last-child {
    border-bottom: none;
  }
}
.list .info {
  flex: 1;
}
.list .ttl {
  font-size: 25rpx;
  font-weight: 600;
  display: block;
  .up {
    color: var(--stock-up);
  }
  .down {
    color: var(--stock-down);
  }
  .gold {
    color: var(--stock-gold);
  }
}
.list .m {
  font-size: 20rpx;
  color: var(--stock-ink-3);
  margin-top: 4rpx;
  display: block;
}
.switch {
  width: 76rpx;
  height: 44rpx;
  background: var(--stock-line);
  border-radius: 22rpx;
  position: relative;
  flex-shrink: 0;
  &::after {
    content: '';
    position: absolute;
    width: 32rpx;
    height: 32rpx;
    background: #fff;
    border-radius: 50%;
    top: 6rpx;
    left: 6rpx;
  }
  &.on {
    background: var(--stock-up);
    &::after {
      left: 38rpx;
    }
  }
}
.tip {
  margin: 28rpx 24rpx;
  padding: 24rpx;
  background: #f0f5ff;
  border-radius: 20rpx;
  font-size: 21rpx;
  color: #1e40af;
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
