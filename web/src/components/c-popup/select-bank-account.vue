<script setup>
import { onMounted, onUnmounted, ref } from 'vue';

import ZINDEX from '@/configs/zindex';
import { navigateTo } from '@/utils/navigate';

const emits = defineEmits(['onEnsure']);
const props = defineProps({});

const refBankAccountList = ref(null);
const refShow = ref(false);
const refChooseItem = ref(null);

// 选择元素后回调
function onChoose(channel) {
  refChooseItem.value = channel;
  emits('onEnsure', refChooseItem.value);
  close();
}

onMounted(() => {
  uni.$on('bankAccountListNeedRefresh', () => {
    // 重新请求列表数据
    refBankAccountList.value?.getData(true);
  });
});

onUnmounted(() => {
  uni.$off('bankAccountListNeedRefresh');
});

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
    :overlayStyle="{ zIndex: ZINDEX.popupBottomOverlay }"
    :z-index="ZINDEX.popupBottom"
  >
    <view class="m-popup s-select-bank">
      <view class="u-title">{{ $t('popup.select-bank-account.title') }}</view>
      <!-- 充值渠道 -->
      <c-bank-account-list
        ref="refBankAccountList"
        class="u-cur-bank-account"
        @choose="onChoose"
      />
      <!-- 按钮 -->
      <c-main-btn
        class="u-bottom-btn"
        :defaultText="$t('popup.select-bank-account.bottomBtn.title')"
        @tap="navigateTo('/pages/mine/bank-account/edit/index')"
      ></c-main-btn>
    </view>
  </u-popup>
</template>

<style lang="scss" scope>
.s-select-bank {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding-bottom: 72rpx !important;
  .u-cur-bank-account {
    flex: 1;
    overflow-y: auto;
    margin-top: 30rpx;
    // 允许拖动
    -webkit-overflow-scrolling: touch;
  }
  .u-bottom-btn {
    margin-top: 30rpx;
  }
}
</style>
