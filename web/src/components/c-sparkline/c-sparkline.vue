<script setup>
import { computed } from 'vue';

const props = defineProps({
  data: { type: Array, required: true },
  width: { type: Number, default: 116 },
  height: { type: Number, default: 40 },
  fill: { type: Boolean, default: false }
});

const color = computed(() => {
  if (props.data.length < 2) return 'var(--stock-flat, #94A3B8)';
  const first = props.data[0];
  const last = props.data[props.data.length - 1];
  if (last > first) return 'var(--stock-up, #10B981)';
  if (last < first) return 'var(--stock-down, #EF4444)';
  return 'var(--stock-flat, #94A3B8)';
});

const points = computed(() => {
  if (!props.data.length) return '';
  const w = props.width;
  const h = props.height;
  const min = Math.min(...props.data);
  const max = Math.max(...props.data);
  const range = Math.max(0.001, max - min);
  return props.data
    .map((v, i) => {
      const x = (i / (props.data.length - 1 || 1)) * w;
      const y = h - ((v - min) / range) * (h - 4) - 2;
      return `${x.toFixed(1)},${y.toFixed(1)}`;
    })
    .join(' ');
});

const areaPath = computed(() => {
  if (!props.fill || !props.data.length) return '';
  return `M0,${props.height} L${points.value.replace(/ /g, ' L')} L${props.width},${props.height} Z`;
});
</script>

<template>
  <view class="c-sparkline">
    <svg :width="width" :height="height" :viewBox="`0 0 ${width} ${height}`">
      <path v-if="fill" :d="areaPath" :fill="color" fill-opacity="0.15" />
      <polyline
        :points="points"
        fill="none"
        :stroke="color"
        stroke-width="1.5"
        stroke-linejoin="round"
      />
    </svg>
  </view>
</template>

<style lang="scss" scoped>
.c-sparkline {
  display: inline-flex;
  align-items: center;
}
</style>
