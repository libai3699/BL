<script setup>
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { reLaunch } from '@/utils/navigate';
import { ensureMaintainChecked, isMaintainBypassPage } from '@/utils/maintain';
import SERIES from '@/configs/series';

const commonStore = useCommonStore();
const userStore = useUserStore();

const { locale } = useI18n();

const notNeedLoginPages = [
  '/pages/account/login',
  '/pages/account/register',
  '/pages/account/forgot-password',
  '/pages/error/index',
  '/pages/customer-service/index',
  '/pages/rules/common'
];

const emits = defineEmits(['onPageInit']);

const refCPopupNormal = ref(null);
const refIsLogin = ref(false);
const refIsShowSlot = ref(false);
const refIsTabletLayout = ref(false);

// 语言切换成功
const doEnsureLocale = (locale) => {
  commonStore.popup.locale.show = false;
  // 执行选择回调
  commonStore.popup.locale?.cbChoosed(locale);
};

function detectTabletLayout() {
  try {
    const systemInfo = uni.getSystemInfoSync();
    const windowWidth = Number(systemInfo.windowWidth || systemInfo.screenWidth || 0);
    const platform = `${systemInfo.platform || ''}`.toLowerCase();
    const model = `${systemInfo.model || ''} ${systemInfo.system || ''}`.toLowerCase();
    const deviceType = `${systemInfo.deviceType || ''}`.toLowerCase();

    let isTablet = false;

    if (windowWidth >= 768) {
      isTablet =
        deviceType === 'pad' ||
        /ipad|tablet|pad/.test(model) ||
        (/(ios|android)/.test(platform) && windowWidth >= 768);

      // H5 下 iPadOS 可能以 Mac 身份返回，这里补一层触控判断
      // #ifdef H5
      if (!isTablet && typeof navigator !== 'undefined') {
        isTablet =
          navigator.platform === 'MacIntel' &&
          navigator.maxTouchPoints > 1 &&
          windowWidth >= 768;
      }
      // #endif
    }

    refIsTabletLayout.value = isTablet;
  } catch (e) {
    refIsTabletLayout.value = false;
  }
}

async function showPageContent() {
  refIsLogin.value = userStore.isLogin;

  if (refIsLogin.value && userStore.needGetUserInfo) {
    userStore.getUserInfo();
    userStore.needGetUserInfo = false;
  }

  const curPage = uni.$u.page();
  const notNeedLogin = notNeedLoginPages.includes(curPage);

  if (notNeedLogin) {
    refIsShowSlot.value = true;
    emits('onPageInit');
    return;
  }

  if (refIsLogin.value) {
    refIsShowSlot.value = true;
    emits('onPageInit');
  } else {
    reLaunch('/pages/account/login');
  }
}

async function init() {
  uni.setNavigationBarTitle({
    title: SERIES.showName
  });

  detectTabletLayout();

  await ensureMaintainChecked();

  if (commonStore.maintenance.needMaintenance === '1' && !isMaintainBypassPage()) {
    return;
  }

  await showPageContent();
}

// 监控控制普通提示弹层
watch(
  () => commonStore.maintenance.needMaintenance,
  (val) => {
    if (val === 'loading') return;
    if (isMaintainBypassPage()) {
      if (!refIsShowSlot.value) {
        showPageContent();
      }
      return;
    }
    if (val === '1') {
      refIsShowSlot.value = false;
      return;
    }
    if (!refIsShowSlot.value) {
      showPageContent();
    }
  }
);

watch(
  () => commonStore.popup.normal.show,
  (newVal) => {
    if (newVal) {
      const options = commonStore.popup.normal.options || {};
      if (!options.onClose) {
        options.onClose = () => {
          commonStore.popup.normal.show = false;
        };
      }
      refCPopupNormal.value?.open(options);
    } else {
      refCPopupNormal.value?.close();
    }
  }
);

init();
</script>

<template>
  <view
    :class="[
      'g-layout',
      'g-theme-common',
      commonStore.getThemeClass,
      `g-lang-${locale}`,
      { 's-tablet': refIsTabletLayout }
    ]"
  >
    <maintenance-box>
      <slot v-if="refIsShowSlot"></slot>
    </maintenance-box>

    <!-- 语言切换弹层 -->
    <c-popup-locale
      :title="$t('common.locale')"
      :show="commonStore.popup.locale.show"
      @close="commonStore.popup.locale.show = false"
      @onEnsure="doEnsureLocale"
    />

    <!-- 普通提示弹层 -->
    <c-popup-normal ref="refCPopupNormal" />
  </view>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.g-layout {
  --page-max-width: 450px;
  width: 100%;
  max-width: var(--page-max-width);
  margin: 0 auto;
  font-size: 28rpx;
  color: var(--text-key-color);
  @include minvh100;
}

.g-layout.s-tablet {
  --page-max-width: 1024px;
}
</style>
