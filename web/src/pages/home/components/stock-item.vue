<script setup>
import { computed } from 'vue';

const emit = defineEmits(['click']);

const props = defineProps({
  item: {
    type: Object,
    default: () => ({})
  },
  stockType: {
    type: String,
    default: 'mexico'
  },
  hideIcon: {
    type: Boolean,
    default: false
  }
});

const currentPrice = computed(() => Number(props.item.nowPrice || 0));

const currentRate = computed(() => Number(props.item.hcrate || 0));

const pctClass = computed(() => {
  const rate = currentRate.value;
  if (rate > 0) return 'stock-pill-up';
  if (rate < 0) return 'stock-pill-down';
  return 'stock-pill-flat';
});

const rateSymbol = computed(() => {
  const rate = currentRate.value;
  if (rate === 0) return '';
  return rate > 0 ? '+' : '';
});

const formattedRate = computed(() => Number(currentRate.value).toFixed(2));

const avatarBg = computed(() => {
  const colors = ['#0B5394', '#7C3AED', '#059669', '#DC2626', '#D97706', '#2563EB'];
  const code = props.item.spell || props.item.code || 'A';
  return colors[code.charCodeAt(0) % colors.length];
});

const stockInitial = computed(() => {
  const spell = props.item.spell || props.item.name || '';
  return spell.charAt(0).toUpperCase();
});
</script>

<template>
  <view class="c-stock-row" @tap="emit('click')">
    <view v-if="!hideIcon" class="u-avatar" :style="{ background: avatarBg }">
      {{ stockInitial }}
    </view>
    <view class="u-info">
      <text class="u-name">{{ item.name }}</text>
      <text class="u-code"
        >{{ item.stockPlate ? item.stockPlate + ' · ' : ''
        }}{{ item.spell || item.code }}</text
      >
    </view>
    <view class="u-right">
      <text class="u-price stock-tnum">
        <c-amount :value="currentPrice" />
      </text>
      <text class="u-pill" :class="pctClass">{{ rateSymbol }}{{ formattedRate }}%</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.c-stock-row {
  display: flex;
  align-items: center;
  padding: 28rpx;
  border-bottom: 1rpx solid var(--stock-line, #e5e9f2);
  gap: 20rpx;
  background: #fff;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background: #fafbfd;
  }

  .u-avatar {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 26rpx;
    font-weight: 700;
    flex-shrink: 0;
  }

  .u-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    gap: 4rpx;

    .u-name {
      font-size: 28rpx;
      font-weight: 700;
      color: var(--stock-ink, #0f172a);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .u-code {
      font-size: 22rpx;
      color: var(--stock-ink-3, #64748b);
    }
  }

  .u-right {
    min-width: 150rpx;
    text-align: right;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    gap: 8rpx;

    .u-price {
      font-size: 28rpx;
      font-weight: 700;
      color: var(--stock-ink, #0f172a);
    }

    .u-pill {
      padding: 4rpx 12rpx;
      border-radius: 8rpx;
      font-size: 22rpx;
      font-weight: 700;
      line-height: 1.2;
    }
  }
}
</style>
