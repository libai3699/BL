<script setup>
import ZINDEX from '@/configs/zindex';
import { useAttrs, ref } from 'vue';

// 透传的属性和事件对象
const attrs = useAttrs();

const emits = defineEmits(['onEnsure']);

const refIsShow = ref(false);
const refTitle = ref('');
const refText = ref('');
// 按钮类型 single 单个 double 双份
const refButtonType = ref('single');
const refOkText = ref('');
const refCancelText = ref('');

const refHasInput = ref(false);
const refInputValue = ref('');
const refInputMaxLength = ref(100);

// 回调
const refOnEnsure = ref(() => {});
const refOnClose = ref(() => {});

// 确认
const doEnsure = () => {
  const cbData = {
    inputValue: refInputValue.value
  };
  emits('onEnsure', cbData);
  refOnEnsure?.value?.(cbData);
};

// 打开弹层
function open({
  title = '',
  text = '',
  buttonType = 'single',
  okText = '',
  cancelText = '',

  // 是否含有输入框
  hasInput = refHasInput.value,
  // 输入框默认值
  inputValue = refInputValue.value,
  // 输入框最大长度
  inputMaxLength = refInputMaxLength.value,

  onEnsure,
  onClose
} = {}) {
  refIsShow.value = true;
  refTitle.value = title;
  refText.value = text;
  refButtonType.value = buttonType;
  refOkText.value = okText || uni.$t('common.ensure');
  refCancelText.value = cancelText || uni.$t('common.cancel');

  // 输入框相关
  refHasInput.value = hasInput;
  refInputValue.value = inputValue;
  refInputMaxLength.value = inputMaxLength;

  refOnEnsure.value = onEnsure;
  refOnClose.value = onClose;
  // 禁止body滚动
  uni.$u.stopBodyScroll();
}

// 关闭弹层
function close() {
  refIsShow.value = false;
  refOnClose?.value?.();
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
    mode="center"
    :show="refIsShow"
    @close="close"
    :overlayStyle="{ zIndex: ZINDEX.popupNormalOverlay }"
    :z-index="ZINDEX.popupNormal"
    v-bind="attrs"
  >
    <view class="m-popup s-normal">
      <view class="u-title" v-if="refTitle">{{ refTitle }}</view>
      <view class="u-content">
        {{ refText }}
        <slot />
        <up-input
          class="u-ipt"
          v-if="refHasInput"
          v-model="refInputValue"
          :maxlength="refInputMaxLength"
        ></up-input>
      </view>
      <view class="u-btn-area" v-if="refButtonType === 'single'">
        <up-button class="u-btn-3" @tap="doEnsure">{{ refOkText }}</up-button>
      </view>
      <view class="u-btn-area s-double" v-if="refButtonType === 'double'">
        <up-button type="primary" class="u-btn" @tap="close" plain>
          {{ refCancelText }}
        </up-button>
        <up-button type="primary" class="u-btn" @tap="doEnsure">
          {{ refOkText }}
        </up-button>
      </view>
    </view>
  </u-popup>
</template>

<style lang="scss" scope>
.s-normal {
  background: var(--popup-normal-bg);
  border-radius: var(--popup-normal-border-radius);
  .u-title {
    text-align: center;
    font-size: 36rpx;
    padding: 48rpx 24rpx 8rpx;
  }
  .u-ipt {
    border: 2rpx solid var(--border-color);
    width: 100%;
    box-sizing: border-box;
  }
}
</style>
