<script setup>
import { computed } from 'vue';

import { ERR_ILLU } from './err-illustration-data';

const props = defineProps({
  type: {
    type: String,
    default: '404'
  }
});

const illuHtml = computed(() => ERR_ILLU[props.type] || ERR_ILLU['404']);
</script>

<template>
  <!-- 结构、SVG、动画类名与 prototype.html #errIllu 一致 -->
  <view class="err-illustration err-illu float-anim" v-html="illuHtml" />
</template>

<!-- v-html 插入的 SVG 无法命中 scoped，样式与原型 .err-illu 规则对齐 -->
<style lang="scss">
.err-illustration.err-illu {
  width: 400rpx;
  height: 320rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 16rpx 0 32rpx;
  position: relative;
  filter: drop-shadow(0 18px 28px rgba(0, 0, 0, 0.35));
}

.err-illustration.err-illu.float-anim {
  animation: mgp-err-float 3.2s ease-in-out infinite;
}

.err-illustration.err-illu svg {
  width: 100%;
  height: 100%;
  display: block;
  overflow: visible;
}

.err-illustration.err-illu .rotate-slow {
  transform-origin: center;
  animation: mgp-err-rotate 8s linear infinite;
}

.err-illustration.err-illu .blink-slow {
  animation: mgp-err-blink-slow 1.6s ease-in-out infinite;
}

@keyframes mgp-err-float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes mgp-err-rotate {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes mgp-err-blink-slow {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}
</style>
