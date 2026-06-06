<!-- 该组件用来实现需要横向滚动更多内容的需求，可支持PC端拖拽 -->
<script setup>
import { nextTick, ref } from 'vue';

import { solveSwiperVerticalScroll } from '@/utils';

const props = defineProps({
  // 是否自动执行
  autoRun: {
    type: Boolean,
    default: true
  },
  // swiper额外选项
  extraOptions: {
    type: Object,
    default: () => ({})
  }
});

const refSwiper = ref(null);
const refId = ref(`t_scroll_x_box_${Date.now()}`);

const doRenderSwiper = (extraOptions) => {
  nextTick(() => {
    if (refSwiper.value) {
      // 如果之前有设置，则先销毁
      refSwiper.value.destroy();
    }
    const id = refId.value;
    refSwiper.value = new Swiper('#' + id, {
      grabCursor: true,
      freeMode: true,
      slidesPerView: 'auto',
      ...(extraOptions || props.extraOptions)
    });

    // 处理swiper水平滚动时候禁用页面垂直滚动
    solveSwiperVerticalScroll('#' + id);
  });
};

if (props.autoRun) {
  doRenderSwiper();
}

defineExpose({
  doRenderSwiper,
  swiper: refSwiper
});
</script>

<template>
  <view class="m-scroll-x-box swiper" :id="refId">
    <view class="swiper-wrapper">
      <slot></slot>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.m-scroll-x-box {
  display: block !important;

  .swiper-wrapper {
    margin: -30rpx 0 -30rpx -30rpx;
    padding: 30rpx 0 30rpx 30rpx;
  }
}
</style>
