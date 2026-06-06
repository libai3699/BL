<script setup>
const emits = defineEmits(['onErrorRetry']);
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
  btnText: {
    type: String,
    default: undefined
  },
  text: {
    type: String,
    default: undefined
  },
  // 图片类型 default|none
  type: {
    type: String,
    default: 'none'
  }
});

function onErrorRetry() {
  emits('onErrorRetry');
}
</script>

<template>
  <view
    class="m-error"
    :class="{
      's-position': isPosition,
      's-no-img': showType === 'noImg',
      's-only-text': showType === 'onlyText',
      's-only-text-sm': showType === 'onlyText-sm'
    }"
  >
    <image v-if="type === 'default'" mode="widthFix" class="u-img s-default" />
    <text class="u-text">{{ text ?? $t('error.system') }}</text>
    <text
      class="u-text-link"
      @tap="onErrorRetry"
      v-if="showType === 'onlyText' || showType === 'onlyText-sm'"
    >
      {{ btnText ?? $t('common.refresh') }}
    </text>
    <button class="u-btn" @tap="onErrorRetry">
      {{ btnText ?? $t('common.refresh') }}
    </button>
  </view>
</template>

<style lang="scss">
@use '@/styles/mixin.scss' as *;

.m-error {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  position: relative;
  width: 100%;
  height: 100%;
  padding: 50rpx 50rpx;
  box-sizing: border-box;
  word-break: break-all;

  // 无图片
  &.s-no-img {
    padding-bottom: 0;
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
    flex-direction: row;
    padding: 0;
    .u-img {
      display: none;
    }
    .u-text,
    .u-text-link {
      margin-top: 0;
    }
    .u-btn {
      display: none;
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
    flex-shrink: 0;

    &.s-default {
      width: 362rpx;
      height: 362rpx;
      margin-bottom: 20rpx;
    }
  }
  .u-text,
  .u-text-link {
    flex-shrink: 0;
    font-size: 28rpx;
    line-height: 38rpx;
    color: #a4a4a4;
    @include ellipsis2(3);
  }
  .u-text-link {
    color: var(--primary-color);
    margin-left: 20rpx;
  }
  .u-btn {
    flex-shrink: 0;
    margin-top: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 240rpx;
    height: 64rpx !important;
    border-radius: 40rpx !important;
    border: 2rpx solid #181818 !important;
    font-size: 28rpx;
    color: #181818;
    background: #fff;
    filter: none;
  }
}
</style>
