<script setup>
import { computed, reactive, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack, navigateTo, redirectTo, switchTab } from '@/utils/navigate';
import { isRequireParamFilled, ruleNormalText, rulePassword } from '@/utils/rule';
import { reqUserLogin } from '@/apis/user';

const commonStore = useCommonStore();
const userStore = useUserStore();

const refTacVerification = ref(null);
const refAgree = ref(true);
const refForm = ref(null);
const refFormIsFormValid = ref(false);
const showPwd = ref(false);
const refFormModel = reactive({
  params: {
    userName: userStore.savedAccount?.value?.userName || '',
    userPwd: userStore.savedAccount?.value?.userPwd || ''
  }
});
const refMainBtn = ref({
  loading: false
});

const isApp = process.env.IS_APP;

const localeLabel = computed(() => {
  const map = { zh_CN: '中文', en_US: 'EN', es_MX: 'ES-MX', es_PE: 'ES-PE' };
  return map[commonStore.locale] || '中文';
});

const rules = computed(() => {
  const result = {
    'params.userName': ruleNormalText({
      isRequired: true,
      langKey: 'userName'
    }),
    'params.userPwd': rulePassword({
      isRequired: true,
      langKey: 'userPwd'
    })
  };
  return result;
});

const canSubmit = computed(
  () => refFormIsFormValid.value && refAgree.value && !refMainBtn.value.loading
);

const onChangeForm = () => {
  setTimeout(() => {
    if (isRequireParamFilled(refFormModel, rules.value)) {
      refForm.value
        ?.validate()
        .then(() => {
          refFormIsFormValid.value = true;
        })
        .catch(() => {
          refFormIsFormValid.value = false;
        });
    } else {
      refFormIsFormValid.value = false;
    }
  }, 0);
};

const clearAccount = () => {
  refFormModel.params.userName = '';
  onChangeForm();
};

const doEnsureLogin = async () => {
  if (!canSubmit.value) return;
  // 弹出 gp-verification 文字点选验证码 → 通过后回调 onVerifySuccess（带 captcha id）
  refTacVerification.value?.start();
};

function getVertificationRequestData() {
  return {
    account: refFormModel.params.userName
  };
}

const onVerifySuccess = (vertificationRes) => {
  refMainBtn.value.loading = true;
  const reqParams = {
    ...refFormModel.params,
    id: vertificationRes?.data?.id
  };
  reqUserLogin(reqParams)
    .then((res) => {
      userStore.setTokenFinish(res.token);
      userStore.getUserInfo();
      userStore.needGetUserInfo = false;
      switchTab('/pages/home/index');
      if (userStore.savedAccount?.open) {
        userStore.savedAccount.value = reqParams;
      } else {
        userStore.savedAccount.value = {};
      }
    })
    .finally(() => {
      refMainBtn.value.loading = false;
    });
};

function doForgetPass() {
  navigateTo('/pages/account/forgot-password');
}

function doShowChangeLocale() {
  commonStore.popup.locale.show = true;
}

function onSwitchLine() {}

const onPageInit = async () => {
  onChangeForm();
};

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="p-auth-login">
      <view class="u-bg-decor"></view>

      <view class="u-top">
        <!-- 暂时隐藏左上角关闭按钮
        <view class="u-close" @tap="goBack">
          <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
          >
            <path d="M6 6l12 12M18 6L6 18" />
          </svg>
        </view>
        -->
        <text class="u-lang" @tap="doShowChangeLocale">🌐 {{ localeLabel }}</text>
      </view>

      <view class="u-hero">
        <c-ap-logo size="md" />
        <text class="u-brand">{{ $t('account.login.brand') }}</text>
        <text class="u-tagline">{{ $t('account.login.tagline') }}</text>
      </view>

      <view class="u-form-card">
        <text class="u-form-title">{{ $t('account.login.title') }}</text>

        <u--form ref="refForm" :model="refFormModel" :rules="rules" class="u-form-inner">
          <view class="u-field">
            <u-form-item
              prop="params.userName"
              :border-bottom="false"
              class="u-form-item"
            >
              <input
                v-model="refFormModel.params.userName"
                class="u-input"
                maxlength="36"
                :placeholder="$t('form.normalText.userName.placeholder') + ' *'"
                placeholder-class="u-ph"
                @input="onChangeForm"
              />
              <text
                v-if="refFormModel.params.userName"
                class="u-suffix-x"
                @tap.stop="clearAccount"
              >
                ×
              </text>
            </u-form-item>
          </view>

          <view class="u-field">
            <u-form-item prop="params.userPwd" :border-bottom="false" class="u-form-item">
              <input
                v-model="refFormModel.params.userPwd"
                class="u-input"
                :type="showPwd ? 'text' : 'password'"
                maxlength="16"
                :placeholder="$t('form.password.userPwd.placeholder') + ' *'"
                placeholder-class="u-ph"
                @input="onChangeForm"
              />
              <view class="u-suffix-eye" @tap.stop="showPwd = !showPwd">
                <svg
                  v-if="showPwd"
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                  <circle cx="12" cy="12" r="3" />
                </svg>
                <svg
                  v-else
                  width="20"
                  height="20"
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                  stroke-linecap="round"
                  stroke-linejoin="round"
                >
                  <path
                    d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"
                  />
                  <line x1="1" y1="1" x2="23" y2="23" />
                </svg>
              </view>
            </u-form-item>
          </view>
        </u--form>

        <view class="u-agree" @tap="refAgree = !refAgree">
          <view class="u-check" :class="{ on: refAgree }">
            <svg
              v-if="refAgree"
              width="10"
              height="10"
              viewBox="0 0 24 24"
              fill="none"
              stroke="#fff"
              stroke-width="3"
              stroke-linecap="round"
            >
              <polyline points="20 6 9 17 4 12" />
            </svg>
          </view>
          <text class="u-agree-text">
            {{ $t('account.register.agree') }}
            <text class="u-link" @tap.stop="navigateTo('/pages/rules/common')">{{
              $t('account.register.protocol')
            }}</text>
            &
            <text class="u-link" @tap.stop="navigateTo('/pages/rules/common')">{{
              $t('account.register.privacy')
            }}</text>
          </text>
        </view>

        <view class="u-remember">
          <view
            class="u-rem-left"
            @tap="userStore.savedAccount.open = !userStore.savedAccount.open"
          >
            <view class="u-check sm" :class="{ on: userStore.savedAccount.open }">
              <svg
                v-if="userStore.savedAccount.open"
                width="10"
                height="10"
                viewBox="0 0 24 24"
                fill="none"
                stroke="#fff"
                stroke-width="3"
                stroke-linecap="round"
              >
                <polyline points="20 6 9 17 4 12" />
              </svg>
            </view>
            <text>{{ $t('account.login.remember') }}</text>
          </view>
          <text class="u-forgot" @tap="doForgetPass">{{
            $t('account.login.forgetPassword')
          }}</text>
        </view>

        <view class="u-btn-login" :class="{ disabled: !canSubmit }" @tap="doEnsureLogin">
          <up-loading-icon v-if="refMainBtn.loading" size="32rpx" color="#0B1E47" />
          <text v-else>{{ $t('common.login') }}</text>
        </view>
        <view class="u-btn-login outline" @tap="redirectTo('/pages/account/register')">
          {{ $t('common.register') }}
        </view>
      </view>

      <text class="u-footer">{{ $t('account.login.footer') }}</text>

      <view v-if="isApp" class="u-line-wrap">
        <line-switching showTimeout @switch="onSwitchLine" />
      </view>
    </view>

    <tac-verification
      ref="refTacVerification"
      @success="onVerifySuccess"
      :requestData="getVertificationRequestData"
    />
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.p-auth-login {
  min-height: 100vh;
  background: linear-gradient(160deg, #0b1e47 0%, #13306e 60%, #1e407f 100%);
  color: #fff;
  padding: 0 48rpx 60rpx;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;
}

.u-bg-decor {
  position: absolute;
  width: 600rpx;
  height: 600rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(212, 162, 76, 0.22), transparent 70%);
  top: -200rpx;
  right: -180rpx;
  pointer-events: none;
}

.u-top {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 24rpx 0;
  position: relative;
  z-index: 2;
}

.u-close {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.u-lang {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
  padding: 10rpx 24rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.2);
  border-radius: 28rpx;
  max-width: 60%;
  @include ellipsis;
}

.u-hero {
  padding: 24rpx 0 16rpx;
  text-align: center;
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.u-brand {
  display: block;
  margin-top: 28rpx;
  font-size: 36rpx;
  font-weight: 500;
  letter-spacing: 12rpx;
  background: linear-gradient(135deg, #fff, #f1e0bd);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.u-tagline {
  display: block;
  margin-top: 12rpx;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.55);
  letter-spacing: 4rpx;
  text-transform: uppercase;
}

.u-form-card {
  margin-top: 24rpx;
  width: 100%;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.06);
  border: 2rpx solid rgba(255, 255, 255, 0.1);
  border-radius: 36rpx;
  padding: 36rpx;
  backdrop-filter: blur(20rpx);
  position: relative;
  z-index: 2;
}

.u-form-title {
  display: block;
  font-size: 44rpx;
  font-weight: 800;
  color: #fff;
  margin-bottom: 28rpx;
  letter-spacing: 2rpx;
}

.u-form-inner {
  :deep(.u-form-item) {
    align-items: stretch;
  }

  :deep(.u-form-item__body) {
    padding: 0;
    align-items: stretch;
  }

  :deep(.u-form-item__body__right__message) {
    margin-top: 8rpx;
    padding-left: 8rpx;
    color: #fca5a5;
    font-size: 22rpx;
  }
}

.u-field {
  margin-bottom: 20rpx;
}

.u-form-item {
  flex: 1;
  width: 100%;

  :deep(.u-form-item__body) {
    flex-direction: row;
    align-items: stretch;
    width: 100%;
  }

  :deep(.u-form-item__body__right) {
    flex: 1;
    min-width: 0;
  }
}

.u-field :deep(.u-form-item) {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  width: 100%;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.08);
  border-radius: 20rpx;
  padding: 0 24rpx;
  min-height: 88rpx;
  border: 2rpx solid transparent;
  transition:
    border-color 0.2s,
    background 0.2s;
}

.u-field:focus-within :deep(.u-form-item) {
  border-color: rgba(212, 162, 76, 0.4);
  background: rgba(255, 255, 255, 0.12);
}

.u-input {
  flex: 1 1 0;
  min-width: 0;
  max-width: 100%;
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
  height: 88rpx;
  line-height: 88rpx;
}

.u-ph {
  color: rgba(255, 255, 255, 0.4);
  font-weight: 500;
}

.u-suffix-x {
  font-size: 40rpx;
  color: rgba(255, 255, 255, 0.45);
  padding: 0 8rpx;
  line-height: 1;
  font-weight: 300;
  flex-shrink: 0;
  align-self: center;
}

.u-suffix-eye {
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.6);
  flex-shrink: 0;
  align-self: center;
  cursor: pointer;

  svg {
    display: block;
    width: 36rpx;
    height: 36rpx;
  }

  &:active {
    color: rgba(255, 255, 255, 0.95);
  }
}

.u-agree {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-top: 8rpx;
}

.u-check {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 4rpx;
  transition: 0.2s;

  &.on {
    background: var(--stock-up, #10b981);
    border-color: var(--stock-up, #10b981);
  }
}

.u-agree-text {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.75);
  line-height: 1.6;
}

.u-link {
  color: var(--stock-gold, #d4a24c);
  font-weight: 600;
}

.u-remember {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 20rpx;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.75);
}

.u-rem-left {
  display: flex;
  align-items: center;
  gap: 16rpx;

  .u-check {
    margin-top: 0;
  }
}

.u-forgot {
  color: rgba(255, 255, 255, 0.65);

  &:active {
    color: var(--stock-gold, #d4a24c);
  }
}

.u-btn-login {
  margin-top: 32rpx;
  height: 92rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #d4a24c, #f1e0bd);
  color: #0b1e47;
  font-size: 30rpx;
  font-weight: 800;
  text-align: center;
  line-height: 92rpx;
  letter-spacing: 8rpx;
  box-shadow: 0 16rpx 36rpx rgba(212, 162, 76, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;

  &.disabled {
    opacity: 0.5;
    pointer-events: none;
    box-shadow: none;
  }

  &.outline {
    margin-top: 20rpx;
    background: transparent;
    border: 3rpx solid rgba(212, 162, 76, 0.5);
    color: #f1e0bd;
    box-shadow: none;
    letter-spacing: 4rpx;
    line-height: 86rpx;

    &:active {
      background: rgba(212, 162, 76, 0.1);
    }
  }
}

.u-footer {
  display: block;
  text-align: center;
  padding-top: 36rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.4);
  position: relative;
  z-index: 2;
}

.u-line-wrap {
  margin-top: 32rpx;
  position: relative;
  z-index: 2;
}

/* 浏览器保存密码/自动填充导致输入框变白：
 * autofill 通常用 -webkit-box-shadow 覆盖背景，因此需要同时覆盖 box-shadow + background-color
 */
:deep(input:-webkit-autofill),
:deep(input:-webkit-autofill:hover),
:deep(input:-webkit-autofill:focus) {
  background-color: rgba(255, 255, 255, 0.08) !important;
  -webkit-text-fill-color: #ffffff !important;
  -webkit-box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.08) inset !important;
  box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.08) inset !important;
  background-image: none !important;
  transition: background-color 9999s ease-in-out 0s;
}

/* 部分 iOS Safari 下的 autofill */
:deep(input:-internal-autofill-selected) {
  background-color: rgba(255, 255, 255, 0.08) !important;
  -webkit-text-fill-color: #ffffff !important;
  background-image: none !important;
}

/* uview 输入组件的包裹层，进一步兜底（避免选择器命中不到） */
:deep(.u-ipt input:-webkit-autofill),
:deep(.u-ipt input:-webkit-autofill:hover),
:deep(.u-ipt input:-webkit-autofill:focus) {
  background-color: rgba(255, 255, 255, 0.08) !important;
  -webkit-text-fill-color: #ffffff !important;
  -webkit-box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.08) inset !important;
  box-shadow: 0 0 0 1000px rgba(255, 255, 255, 0.08) inset !important;
  background-image: none !important;
}
</style>
