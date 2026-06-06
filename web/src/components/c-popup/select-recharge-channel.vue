<script setup>
import { ref } from 'vue';

const emits = defineEmits(['onEnsure']);
const props = defineProps({});

const refShow = ref(false);
const refChooseItem = ref(null);

// 选择元素后回调
function onChoose(channel) {
  refChooseItem.value = channel;
  emits('onEnsure', refChooseItem.value);
  close();
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
      <view class="u-title">{{ $t('popup.select-recharge-channel.title') }}</view>
      <!-- 充值渠道 -->
      <c-recharge-channel-list
        class="u-cur-recharge-channel"
        @choose="onChoose"
        :chooseItem="refChooseItem"
      />
      <!-- 按钮 -->
      <view class="u-bottom-plain-btn" @tap="close">{{ $t('common.cancel') }}</view>
    </view>
  </u-popup>
</template>

<style lang="scss" scope>
.s-select-bank {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-bottom: 10rpx !important;
  .u-cur-recharge-channel {
    flex: 1;
    overflow-y: auto;
    margin-top: 30rpx;
    // 允许拖动
    -webkit-overflow-scrolling: touch;
  }
}
</style>
