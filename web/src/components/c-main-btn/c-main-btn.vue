<script setup>
const props = defineProps({
  // 样式模式 normal-普通 | circle-圆角
  styleMode: {
    type: String,
    default: 'normal'
  },
  diyDisabled: {
    type: Boolean,
    default: false
  },
  // 是否加载状态
  isLoading: {
    type: Boolean,
    default: false
  },
  // 默认图标
  defaultIcon: {
    type: String,
    default: ''
  },
  // 默认文案
  defaultText: {
    type: String,
    default: ''
  },
  // 读取文案
  loadingText: {
    type: String,
    default: ''
  }
});
</script>

<template>
  <up-button
    :class="`s-${styleMode}`"
    :type="$attrs?.type ?? 'primary'"
    :disabled="diyDisabled || isLoading"
    v-bind="$attrs"
  >
    <block v-if="isLoading"> {{ loadingText || $t('button.loading') }}... </block>
    <block v-else-if="$slots.default">
      <slot></slot>
    </block>
    <block v-else>
      <svg-icon v-if="defaultIcon" class="u-svg-icon" :name="defaultIcon" />
      {{ defaultText || $t('button.submit') }}
    </block>
  </up-button>
</template>

<style lang="scss" scoped>
.u-svg-icon {
  font-size: 32rpx;
}
</style>
