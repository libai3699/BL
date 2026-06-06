<script setup>
import { computed } from 'vue';

const props = defineProps({
  value: {
    type: [String, Number]
  },
  // 等待状态
  loading: {
    type: Boolean,
    default: false
  },
  // 尺寸,主要控制等待状态图标
  size: {
    type: String
  },
  // 默认显示值
  defaultValue: {
    type: String,
    default: '--'
  },
  // 是否显示数字跳动效果
  isUseCountTo: {
    type: Boolean,
    default: false
  },
  // 小数位数
  decimals: {
    type: [String, Number],
    default: 2
  }
});

// 获取字体大小样式,未设置则继承父级样式
function getStyleFontSize() {
  const result = {};
  if (props.size) {
    result.fontSize = props.size + '!important';
  }
  return result;
}

// 添加千分位分隔符
function addThousandSeparator(numStr) {
  const parts = numStr.split('.');
  // 整数部分添加千分位
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  return parts.join('.');
}

// 格式化数字：去掉末尾的0，但至少保留2位小数，并添加千分位分隔符
function formatNumber(value, decimals) {
  const num = Number(value);
  if (isNaN(num)) return value;
  
  // 先格式化为指定小数位数
  const fixed = num.toFixed(Number(decimals));
  
  // 转换为数字再转回字符串，自动去掉末尾的0
  const trimmed = parseFloat(fixed).toString();
  
  let result;
  // 检查是否有小数点
  if (trimmed.indexOf('.') === -1) {
    // 没有小数点，添加 .00
    result = trimmed + '.00';
  } else {
    // 有小数点，检查小数位数
    const parts = trimmed.split('.');
    if (parts[1].length < 2) {
      // 小数位数少于2位，补0到2位
      result = trimmed + '0'.repeat(2 - parts[1].length);
    } else {
      result = trimmed;
    }
  }
  
  // 添加千分位分隔符
  return addThousandSeparator(result);
}

// 格式化后的显示值
const formattedValue = computed(() => {
  if (props.value == null || props.value === '') {
    return props.defaultValue;
  }
  return formatNumber(props.value, props.decimals);
});

// 自定义格式化函数，用于 up-count-to（如果组件支持的话）
// 注意：up-count-to 可能不支持自定义 format，如果不支持，会使用默认格式
const customFormatter = (value) => {
  return formatNumber(value, props.decimals);
};
</script>

<template>
  <loading-box v-if="loading" :iconSize="size" showType="onlyText-sm" text="" />
  <text v-else-if="value == null || value === ''" :style="getStyleFontSize()">
    {{ defaultValue }}
  </text>
  <!-- 使用数字跳动效果 -->
  <up-count-to
    v-else-if="isUseCountTo"
    class="u-count-to"
    v-bind="$attrs"
    :endVal="Number(value)"
    separator=","
    :decimals="Number(decimals)"
    :style="getStyleFontSize()"
    :duration="1000"
  ></up-count-to>
  <!-- 普通文本显示 -->
  <text v-else :style="getStyleFontSize()">
    {{ formattedValue }}
  </text>
</template>

<style lang="scss" scoped>
.u-count-to {
  // 使用继承样式
  color: inherit !important;
  font-size: inherit !important;
}
</style>
