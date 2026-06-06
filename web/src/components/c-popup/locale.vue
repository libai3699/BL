<script setup>
import { onMounted, watch, useAttrs, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { reqUserGetLanList, reqUserUpdateLan } from '@/apis/user';
import { locales } from '@/locales';
import { updateUrlParam } from '@/utils';
import ZINDEX from '@/configs/zindex';

// 国际化处理
const { locale } = useI18n();

// 透传的属性和事件对象
const attrs = useAttrs();
const emits = defineEmits(['onEnsure']);
const props = defineProps({});

const commonStore = useCommonStore();
const userStore = useUserStore();

const isCurShow = ref(false);
// 语言列表
const refLocales = ref(null);

watch(
  () => attrs.show,
  (newV) => {
    isCurShow.value = newV;
  }
);

// 语言切换
const doChangeLocale = (localeItem) => {
  function cb() {
    commonStore.setLocale(localeItem.code, (finalLocale) => {
      locale.value = finalLocale;
      // 通知上层
      emits('onEnsure', finalLocale);
      // 修改url参数
      updateUrlParam('locale', finalLocale);
      onClose();
    });
  }
  if (userStore.isLogin) {
    // 登录了,调用接口保存语言选择
    reqUserUpdateLan({
      lanKey: localeItem.code
    }).then(cb);
  } else {
    cb();
  }
};

const onOpen = () => {
  reqUserGetLanList()
    .then((res) => {
      refLocales.value = res.map((item) => {
        return {
          name: item.country,
          code: item.lanKey,
          locale: locales.find((locale) => locale.code === item.lanKey)
        };
      });
    })
    .catch(() => {
      onClose();
      emits('close');
    });
};

const onClose = () => {
  refLocales.value = null;
};

onMounted(() => {});
</script>

<template>
  <u-popup
    mode="center"
    @open="onOpen"
    @close="onClose"
    :show="refLocales && isCurShow"
    :closeable="!!refLocales?.length"
    :overlayStyle="{ zIndex: ZINDEX.popupLocaleOverlay }"
    :z-index="ZINDEX.popupLocale"
    v-bind="attrs"
  >
    <view class="m-popup-locale" v-if="refLocales">
      <view class="u-title">{{ attrs.title }}</view>
      <view class="u-list">
        <view
          v-for="item in refLocales"
          :key="item"
          class="u-item"
          :class="{ 's-active': item.code == locale }"
          @tap="doChangeLocale(item)"
        >
          <!-- 暂时不显示图标 -->
          <!-- <view class="u-icon"><svg-icon :name="`locale-${item.code}`" /></view> -->
          <view class="u-name">{{ item.name }}</view>
          <svg-icon class="u-correct" name="correct-1" v-if="item.code == locale" />
        </view>
      </view>
    </view>
  </u-popup>
</template>

<style lang="scss" scoped>
.m-popup-locale {
  width: 630rpx;
  background: var(--popup-locale-bg);
  backdrop-filter: var(--popup-locale-backdrop-filter);
  box-shadow: var(--popup-locale-box-shadow);
  color: var(--text-key-color);
  border-radius: 20rpx;
  padding: 48rpx 48rpx 0;
  box-sizing: border-box;

  .u-title {
    font-size: 32rpx;
    text-align: center;
    padding-bottom: 10rpx;
  }

  .u-list {
    padding: 22rpx 0 32rpx;
    max-height: 740rpx;
    overflow-y: auto;

    .u-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 32rpx 0;
      border-bottom: 1px solid var(--border-color);

      &.s-active {
        color: var(--primary-color);
      }

      &:last-child {
        border-bottom: none;
      }

      .u-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        height: 38rpx;
        flex-shrink: 0;
        margin-right: 20rpx;
      }

      .u-name {
        flex: 1;
        font-size: 28rpx;
        height: 38rpx;
        line-height: 38rpx;
      }

      .u-correct {
        flex-shrink: 0;
      }
    }
  }
}
</style>
