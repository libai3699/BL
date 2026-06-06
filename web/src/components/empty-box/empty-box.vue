<script setup>
import SERIES from '@/configs/series';

const props = defineProps({
  // 是否定位在外部容器水平垂直居中
  isPosition: {
    type: Boolean,
    default: false
  },
  // 状态显示类型：block：显示为块状（带对应图标） noImg 无图标块状 onlyText 只有文本 onlyText-sm 只有文本，尺寸小
  showType: {
    type: String,
    default: 'block'
  },
  text: {
    type: String,
    default: undefined
  },
  // 图片类型 default
  type: {
    type: String,
    default: 'default'
  },
  // 自定义图片路径，当type为custom时使用
  customSrc: {
    type: String,
    default: ''
  }
});
</script>

<template>
  <view
    class="m-empty"
    :class="{
      's-position': isPosition,
      's-no-img': showType === 'noImg',
      's-only-text': showType === 'onlyText',
      's-only-text-sm': showType === 'onlyText-sm'
    }"
  >
    <image
      v-if="type === 'default'"
      src="/static/svgs/no_data.svg"
      mode="widthFix"
      class="u-img s-default"
    />
    <text class="u-text">{{ text ?? $t('common.noData') }}</text>
  </view>
</template>

<style lang="scss">
.m-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: relative;
  width: 100%;
  height: 100%;
  padding: 50rpx 50rpx;
  box-sizing: border-box;

  // 无图片
  &.s-no-img,
  &.s-only-text {
    padding-bottom: 0;
    padding-top: 0;
    .u-img {
      display: none;
    }
    .u-text {
      margin-top: 0;
    }
  }

  // 只有文本
  &.s-only-text,
  &.s-only-text-sm {
    display: inline-block;
    padding: 0;
    .u-img {
      display: none;
    }
    .u-text {
      margin-top: 0;
      font-size: 24rpx;
    }
  }

  &.s-position {
    position: absolute;
    top: 50%;
    left: 0;
    transform: translateY(-50%);
    padding: 0 100rpx;
  }

  .u-img {
    display: block;
    &.s-default {
      width: 240rpx;
      margin-bottom: 32rpx;
    }
  }
  .u-text {
    font-size: 28rpx;
    line-height: 38rpx;
    color: #a4a4a4;
  }
}
</style>
