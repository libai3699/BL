<script setup>
import { onMounted, watch, nextTick, ref } from 'vue';

import { solveSwiperVerticalScroll } from '@/utils';

const emits = defineEmits(['update', 'onChoose']);
const props = defineProps({
  // 风格模式
  // normal-普通
  // normal-black-普通,选中文字为黑色
  // big-尺寸大，选中文字为黑色 - 使用到的地方：个人中心
  // button-按钮样式
  // button-plain-按钮样式 选中无背景色
  mode: {
    type: String,
    default: 'normal'
  },
  modelValue: {
    type: String,
    default: undefined
  },
  // 是否需要底部边框线
  hasBottomLine: {
    type: Boolean,
    default: false
  },
  // 选项卡列表
  tabs: {
    type: Array,
    default: []
  },
  // 选中选项卡的value值
  tab: {
    type: String,
    default: ''
  },
  // 对齐方式
  align: {
    type: String,
    default: 'left'
  },
  // 是否可以选择
  canChoose: {
    type: Boolean,
    default: true
  },
  // 是否需要swiper实现横坐标超出滚动
  needSwiper: {
    type: Boolean,
    default: false
  }
});

watch(
  () => props.tab,
  (newV) => {
    refCurTab.value = newV;
  }
);

watch(
  () => props.modelValue,
  (newV) => {
    refCurTab.value = newV;
  }
);

const refCurTab = ref(null);
const refSwiper = ref(null);
const refId = ref(`t_swiperDomTabs_${Math.round(Math.random() * 100000)}`);

// 选择tab
const doChoose = (item) => {
  if (!props.canChoose) {
    return;
  }
  refCurTab.value = item.value;
  // 使tab双向绑定
  if (props.modelValue !== undefined) {
    emits('update:modelValue', item.value);
  }
  emits('onChoose', item);
};

const doRenderSwiper = () => {
  nextTick(() => {
    if (refSwiper.value) {
      // 如果之前有设置，则先销毁
      refSwiper.value.destroy();
    }
    const id = refId.value;
    refSwiper.value = new Swiper('#' + id, {
      grabCursor: true,
      freeMode: true,
      slidesPerView: 'auto'
    });

    // 处理swiper水平滚动时候禁用页面垂直滚动
    solveSwiperVerticalScroll('#' + id);
  });
};

onMounted(() => {
  refCurTab.value = props.tab;
  if (props.needSwiper) {
    doRenderSwiper();
  }
});
</script>

<template>
  <view
    class="m-tabs"
    :id="refId"
    :class="[
      `s-${mode}`,
      `s-align-${align}`,
      {
        swiper: needSwiper
      },
      {
        's-has-bottom-line': hasBottomLine
      }
    ]"
  >
    <view
      class="u-main"
      :class="{
        'swiper-wrapper': needSwiper
      }"
    >
      <view
        v-for="item in tabs"
        @tap="doChoose(item)"
        :class="[
          'u-item',
          { 's-cur': item.value === refCurTab, 'swiper-slide': needSwiper }
        ]"
      >
        <svg-icon v-if="item.icon" :name="item.icon" class="u-svg-icon" />
        <view class="u-text">
          {{ item.label.constructor.name === 'Function' ? item.label() : item.label }}
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.m-tabs {
  position: relative;
  z-index: 2;

  // 风格模式
  &.s-big {
    border-bottom: 2rpx solid var(--border-color-weak);
    .u-item {
      font-weight: bold;
      font-size: 40rpx;
      padding: 0 0 38rpx;
      &.s-cur {
        color: var(--text-key-color);
      }
    }
  }
  &.s-normal-black {
    .u-item {
      &.s-cur {
        color: var(--text-key-color);
      }
    }
  }
  &.s-button,
  &.s-button-plain {
    .u-item {
      border: 2rpx solid var(--border-color-weak);
      border-radius: 10rpx;
      padding: 0 14rpx;
      font-size: 28rpx;
      height: 54rpx;
      font-size: 24rpx;
      &.s-cur {
        background: var(--text-key-color);
        color: #fff;
        border-color: var(--text-key-color);
        &:after {
          display: none;
        }
      }
    }
  }
  &.s-button-plain {
    .u-item {
      &.s-cur {
        background: none;
        color: var(--text-key-color);
        border-color: var(--text-key-color);
      }
    }
  }

  // 含底部边框线
  &.s-has-bottom-line {
    border-bottom: 2rpx solid var(--border-color-weak);
  }

  // 对齐方式
  &.s-align-center {
    .u-main {
      justify-content: center;
    }
  }

  .u-main {
    display: flex;
    flex-wrap: wrap;
  }

  .swiper-wrapper {
    flex-wrap: nowrap;
    margin: -30rpx 0 -30rpx -30rpx;
    padding: 30rpx 0 30rpx 30rpx;
    .swiper-slide {
      margin-right: 20rpx;
      &:last-child {
        margin-right: 0;
      }
    }
  }

  .u-item {
    position: relative;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 25rpx;
    min-height: 38rpx;
    font-size: 28rpx;
    box-sizing: border-box;
    padding: 0 0 24rpx;
    &:after {
      content: '';
      display: none;
      position: absolute;
      bottom: 0;
      left: 50%;
      transform: translateX(-50%);
      width: 48rpx;
      height: 6rpx;
      background: var(--primary-color);
    }
    .u-svg-icon {
      width: 32rpx !important;
    }
    &.s-cur {
      color: var(--primary-color);
      &:after {
        display: block;
      }
    }
  }
}
</style>
