<script setup>
import { ref } from 'vue';

const emits = defineEmits(['onEnsure']);
const props = defineProps({});

const refShow = ref(false);
const refSearchKey = ref('');
const refChooseItem = ref(null);

// 确认选择
function doChoose() {
  emits('onEnsure', refChooseItem.value);
  close();
}

// 选择回调
function onChoose(item) {
  refChooseItem.value = item;
}

// 打开弹层
function open() {
  refShow.value = true;
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
    class="m-popup-bottom-wrap"
  >
    <view class="m-popup s-select-bank">
      <view class="u-title">{{ $t('popup.select-bank.title') }}</view>
      <!-- 搜索栏 -->
      <c-search v-model="refSearchKey" />
      <!-- 银行卡列表 -->
      <c-bank-list
        class="u-cur-bank-list"
        :search-key="refSearchKey"
        @choose="onChoose"
        :chooseItem="refChooseItem"
      />
      <!-- 按钮 -->
      <c-main-btn
        class="u-cur-btn"
        styleMode="circle"
        :defaultText="$t('common.ensure')"
        @tap="doChoose"
        :diyDisabled="refChooseItem == null"
      />
    </view>
  </u-popup>
</template>

<style lang="scss" scope>
.s-select-bank {
  height: 100%;
  display: flex;
  flex-direction: column;
  .u-cur-bank-list {
    flex: 1;
    overflow-y: auto;
    margin-top: 30rpx;
    height: 100rpx;
    // 允许拖动
    -webkit-overflow-scrolling: touch;
  }
  .u-cur-btn {
    margin-top: 40rpx;
  }
}
</style>
