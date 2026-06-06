<script setup>
import { computed } from 'vue';

const props = defineProps({
  size: { type: String, default: 'md' }
});

const sizeMap = {
  sm: { box: 108, radius: 14 },
  md: { box: 128, radius: 18 },
  lg: { box: 192, radius: 22 }
};

const s = computed(() => sizeMap[props.size] || sizeMap.md);

const style = computed(() => ({
  width: `${s.value.box}rpx`,
  height: `${s.value.box}rpx`,
  borderRadius: `${s.value.radius}rpx`
}));
</script>

<template>
  <image
    class="c-ap-logo"
    src="/static/images/series/default/logo/logo.png"
    mode="aspectFit"
    :style="style"
  />
</template>

<style lang="scss" scoped>
.c-ap-logo {
  display: block;
  flex-shrink: 0;
}
</style>
