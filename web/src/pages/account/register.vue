<script setup>
import { computed, reactive, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo, redirectTo } from '@/utils/navigate';
import { isRequireParamFilled, ruleNormalText, rulePassword } from '@/utils/rule';
import { reqUserReg } from '@/apis/user';

const commonStore = useCommonStore();

const refForm = ref(null);
const refTacVerification = ref(null);
const refFormIsFormValid = ref(false);
const refFormModel = reactive({
  params: {
    userName: '',
    userPwd: '',
    userPwdConfirm: '',
    yqm: ''
  }
});
const refMainBtn = ref({
  loading: false
});
const refAgree = ref(false);
const showPwd = ref(false);
const showPwdConfirm = ref(false);

const rules = computed(() => {
  const result = {
    'params.userName': ruleNormalText({
      isRequired: true,
      langKey: 'userName',
      hasLimit: true,
      limitMin: 5
    }),
    'params.userPwd': rulePassword({
      isRequired: true,
      langKey: 'userPwd',
      limitMin: 6
    }),
    'params.userPwdConfirm': rulePassword({
      isRequired: true,
      langKey: 'userPwdConfirm',
      isConfirm: true,
      limitMin: 6,
      comparePackage: {
        source: refFormModel.params,
        field: 'userPwd'
      }
    }),
    'params.yqm': ruleNormalText({
      isRequired: true,
      langKey: 'inviteCode'
    })
  };
  return result;
});

const canSubmit = computed(
  () => refFormIsFormValid.value && refAgree.value && !refMainBtn.value.loading
);

const pwdStrength = computed(() => {
  const v = refFormModel.params.userPwd || '';
  let lvl = 0;
  if (v.length >= 6) lvl = 1;
  if (v.length >= 8 && /[A-Za-z]/.test(v) && /\d/.test(v)) lvl = 2;
  if (v.length >= 10 && /[A-Za-z]/.test(v) && /\d/.test(v) && /[^A-Za-z0-9]/.test(v))
    lvl = 3;
  return lvl;
});

const pwdStrengthLabels = computed(() => [
  'account.register.pwdStrength.default',
  'account.register.pwdStrength.weak',
  'account.register.pwdStrength.medium',
  'account.register.pwdStrength.strong'
]);

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

function onBlur(field) {
  if (field === 'userName') {
    refFormModel.params.userName = refFormModel.params.userName
      .replace(/\s/g, '')
      .replace(/[\u{1F300}-\u{1F9FF}\u{2600}-\u{27BF}\u{FE00}-\u{FE0F}]/gu, '');
  }
}

const clearField = (field) => {
  refFormModel.params[field] = '';
  onChangeForm();
};

const doEnsureRegister = async () => {
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
  delete reqParams.userPwdConfirm;
  reqUserReg(reqParams)
    .then(() => {
      refForm.value.resetFields();
      redirectTo('/pages/account/login');
    })
    .finally(() => {
      refMainBtn.value.loading = false;
    });
};

const onPageInit = async () => {
  onChangeForm();
};

onLoad((options) => {
  if (options?.inviteCode) {
    refFormModel.params.yqm = options.inviteCode;
    onChangeForm();
  }
});

onShow(() => {
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="p-auth-register">
      <view class="u-bg-decor"></view>

      <view class="u-top">
        <view class="u-close" @tap="goBack">
          <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <polyline points="15 18 9 12 15 6" />
          </svg>
        </view>
        <text class="u-login-link" @tap="redirectTo('/pages/account/login')">{{
          $t('account.register.hasAccountLogin')
        }}</text>
      </view>

      <view class="u-hero">
        <c-ap-logo size="sm" />
        <text class="u-wordmark">{{ $t('account.login.brand') }}</text>
        <text class="u-brand">{{ $t('account.register.brand') }}</text>
        <text class="u-tagline">{{ $t('account.register.tagline') }}</text>
      </view>

      <view class="u-form-card">
        <text class="u-form-title">{{ $t('account.register.title') }}</text>

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
                @blur="onBlur('userName')"
              />
              <text
                v-if="refFormModel.params.userName"
                class="u-suffix-x"
                @tap.stop="clearField('userName')"
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
                :placeholder="$t('form.password.accountPassword.placeholder') + ' *'"
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
              <text
                v-if="refFormModel.params.userPwd"
                class="u-suffix-x"
                @tap.stop="clearField('userPwd')"
              >
                ×
              </text>
            </u-form-item>
            <view class="u-pwd-strength" :class="'s' + pwdStrength">
              <view class="u-bar"></view>
              <view class="u-bar"></view>
              <view class="u-bar"></view>
              <text class="u-pwd-label">{{ $t(pwdStrengthLabels[pwdStrength]) }}</text>
            </view>
          </view>

          <view class="u-field">
            <u-form-item
              prop="params.userPwdConfirm"
              :border-bottom="false"
              class="u-form-item"
            >
              <input
                v-model="refFormModel.params.userPwdConfirm"
                class="u-input"
                :type="showPwdConfirm ? 'text' : 'password'"
                maxlength="16"
                :placeholder="$t('form.password.userPwdConfirm.placeholder') + ' *'"
                placeholder-class="u-ph"
                @input="onChangeForm"
              />
              <view class="u-suffix-eye" @tap.stop="showPwdConfirm = !showPwdConfirm">
                <svg
                  v-if="showPwdConfirm"
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

          <view class="u-field">
            <u-form-item prop="params.yqm" :border-bottom="false" class="u-form-item">
              <input
                v-model="refFormModel.params.yqm"
                class="u-input"
                maxlength="10"
                :placeholder="$t('form.normalText.inviteCode.placeholder') + ' *'"
                placeholder-class="u-ph"
                @input="onChangeForm"
              />
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

        <view
          class="u-btn-register"
          :class="{ disabled: !canSubmit }"
          @tap="doEnsureRegister"
        >
          <up-loading-icon v-if="refMainBtn.loading" size="32rpx" color="#0B1E47" />
          <text v-else>{{ $t('common.clickRegister') }}</text>
        </view>
        <view class="u-btn-register outline" @tap="redirectTo('/pages/account/login')">
          {{ $t('common.login') }}
        </view>
      </view>

      <text class="u-footer">{{ $t('account.login.footer') }}</text>
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

.p-auth-register {
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
  justify-content: space-between;
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
  flex-shrink: 0;
}

.u-login-link {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
  padding: 10rpx 24rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.2);
  border-radius: 28rpx;
  max-width: 60%;
  @include ellipsis;
}

.u-hero {
  padding: 8rpx 0 12rpx;
  text-align: center;
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.u-wordmark {
  display: block;
  margin-top: 20rpx;
  font-size: 26rpx;
  font-weight: 500;
  letter-spacing: 10rpx;
  color: rgba(255, 255, 255, 0.7);
}

.u-brand {
  display: block;
  margin-top: 16rpx;
  font-size: 48rpx;
  font-weight: 800;
  letter-spacing: 2rpx;
  background: linear-gradient(135deg, #fff, #f1e0bd);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

.u-tagline {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
}

.u-form-card {
  margin-top: 20rpx;
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

  /* 子 svg 跟随 color 渲染描边（stroke="currentColor"） */
  svg {
    display: block;
    width: 36rpx;
    height: 36rpx;
  }

  &:active {
    color: rgba(255, 255, 255, 0.95);
  }
}

.u-pwd-strength {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-top: 16rpx;

  .u-bar {
    flex: 1;
    height: 6rpx;
    border-radius: 4rpx;
    background: rgba(255, 255, 255, 0.15);
  }

  .u-pwd-label {
    font-size: 20rpx;
    font-weight: 700;
    color: rgba(255, 255, 255, 0.6);
    margin-left: 12rpx;
    flex-shrink: 0;
  }

  &.s1 .u-bar:nth-child(1) {
    background: var(--stock-down, #ef4444);
  }

  &.s1 .u-pwd-label {
    color: var(--stock-down, #ef4444);
  }

  &.s2 .u-bar:nth-child(1),
  &.s2 .u-bar:nth-child(2) {
    background: #f59e0b;
  }

  &.s2 .u-pwd-label {
    color: #f59e0b;
  }

  &.s3 .u-bar {
    background: var(--stock-up, #10b981);
  }

  &.s3 .u-pwd-label {
    color: var(--stock-up, #10b981);
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

.u-btn-register {
  margin-top: 32rpx;
  height: 92rpx;
  border-radius: 24rpx;
  background: linear-gradient(135deg, #d4a24c, #f1e0bd);
  color: #0b1e47;
  font-size: 30rpx;
  font-weight: 800;
  text-align: center;
  line-height: 92rpx;
  letter-spacing: 4rpx;
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
    letter-spacing: 8rpx;
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

:deep(input:-internal-autofill-selected) {
  background-color: rgba(255, 255, 255, 0.08) !important;
  -webkit-text-fill-color: #ffffff !important;
  background-image: none !important;
}
</style>
