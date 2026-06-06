<script setup>
import { computed, ref, useAttrs } from 'vue';

const attrs = useAttrs();
const props = defineProps({
  // 表单显示名称
  name: {
    type: String,
    default: ''
  },
  // 表单规则
  rule: {
    type: Object,
    default: () => ({})
  },
  // 是否显示密码切换按钮，如果这个为true，代表是密码类型
  isShowPasswordToggle: {
    type: Boolean,
    default: false
  },
  // 表单类型 input-输入框|select-下拉框(功能外部实现)|none-无
  formType: {
    type: String,
    default: 'input'
  },
  // 下拉框类型
  selectType: {
    type: String,
    default: 'arrow-bottom' // arrow-bottom-向下箭头|triangle-bottom-向下三角形
  }
});

// 输入框类型
// 如果isShowPasswordToggle为true，则根据refPasswordShow来决定是text还是password
// 否则，使用传入的type属性，默认text
const cpdType = computed(() => {
  if (props.isShowPasswordToggle) {
    return refPasswordShow.value ? 'text' : 'password';
  } else {
    return attrs.type || 'text';
  }
});

const refPasswordShow = ref(false);
</script>

<template>
  <u-form-item class="u-item" :prop="$attrs.prop">
    <view class="u-item-name">
      {{ name }}
      <text class="u-star">{{ rule?.[0]?.required ? '*' : '' }}</text>
    </view>
    <view class="u-item-control" v-if="formType === 'input'">
      <u--input
        v-bind="$attrs"
        class="u-ipt"
        :type="cpdType"
        :border="'none'"
        @change="$emit('change')"
        :passwordVisibilityToggle="false"
      />
      <svg-icon
        v-if="isShowPasswordToggle"
        :name="refPasswordShow ? 'eye-close' : 'eye-show'"
        class="u-svg-icon-eye"
        @tap="refPasswordShow = !refPasswordShow"
      />
      <svg-icon
        v-if="$attrs.modelValue !== ''"
        name="close"
        class="u-svg-icon-close"
        @tap="($emit('update:modelValue', ''), $emit('change'))"
      />
    </view>
    <view class="u-item-right">
      <slot name="right"></slot>
    </view>
    <view class="u-item-control" v-if="formType === 'select'">
      <view class="u-cover"></view>
      <u--input
        v-bind="$attrs"
        class="u-ipt"
        type="input"
        :border="'none'"
        :passwordVisibilityToggle="false"
        readonly
      />
      <view class="u-btn-wrap" :class="`s-${selectType}-wrap`">
        <svg-icon
          v-if="selectType === 'arrow-bottom'"
          name="arrow-bottom"
          class="u-svg-icon-arrow-bottom"
        />
        <view v-if="selectType === 'triangle-bottom'" class="u-triangle-bottom" />
      </view>
    </view>
    <template v-if="formType === 'none'">
      <slot></slot>
    </template>
  </u-form-item>
</template>

<style lang="scss" scoped></style>
