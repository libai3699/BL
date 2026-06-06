<!-- 选择选项组公用弹层 -->
<script setup>
import { ref } from 'vue';

const props = defineProps({});

const refShow = ref(false);
const refTitle = ref('');
const refSuccess = ref(null);
const refOptions = ref([]);
const refChooseItem = ref(null);
// 选项是否需要国际化
const refOptionsNeedLang = ref(true);

// 选择回调
function onChoose(item) {
  refSuccess.value?.(item);
  close();
  refChooseItem.value = item;
}

// 打开弹层
function open({ title, options, success, value, optionsNeedLang = true } = {}) {
  refShow.value = true;
  refTitle.value = title;
  refOptions.value = options;
  refSuccess.value = success;
  refChooseItem.value = options.find((item) => item.value === value);
  refOptionsNeedLang.value = optionsNeedLang;

  // 禁止body滚动
  uni.$u.stopBodyScroll();
}

// 关闭弹层
function close() {
  refShow.value = false;
  // 恢复body滚动
  uni.$u.resumeBodyScroll();
}

defineExpose({
  open,
  close
});
</script>

<template>
  <u-popup
    v-bind="$attrs"
    mode="bottom"
    :show="refShow"
    @close="close"
    class="m-popup-bottom-wrap s-height-auto"
  >
    <view class="m-popup s-select-options">
      <view class="u-title">{{ refTitle }}</view>
      <view class="u-options">
        <view
          class="u-item"
          v-for="item in refOptions"
          :key="item.value"
          @tap="onChoose(item)"
          :class="{
            's-cur': refChooseItem?.value === item.value
          }"
        >
          {{ refOptionsNeedLang ? $t(item.label) : item.label }}
        </view>
      </view>
    </view>
  </u-popup>
</template>

<style lang="scss" scope>
.s-select-options {
  max-height: 100%;
  display: flex;
  flex-direction: column;

  .u-options {
    margin-top: 40rpx;
    flex: 1;
    overflow-y: auto;
    display: flex;
    flex-wrap: wrap;
    gap: 22rpx;
    max-height: 800rpx;
    .u-item {
      width: 340rpx;
      box-sizing: border-box;
      text-align: center;
      padding: 16rpx 20rpx;
      border: 2rpx solid var(--border-color-weak);
      border-radius: 10rpx;
      font-size: 24rpx;
      &.s-cur {
        border-color: var(--text-key-color);
      }
    }
  }
}
</style>
