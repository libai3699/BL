<script setup>
import { computed, reactive, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack, redirectTo } from '@/utils/navigate';
import { isRequireParamFilled, rulePassword } from '@/utils/rule';
import { reqUserUpdateWithPwd } from '@/apis/user';

const commonStore = useCommonStore();

const refCPopupNormal = ref(null);
const refForm = ref(null);
const refFormIsFormValid = ref(false);
const refIsForceUpdate = ref(false);
const refRedirectUrl = ref('');
const refFormModel = reactive({
  params: {
    oldPwd: '',
    newPwd: '',
    newPwdConfirm: ''
  }
});
const refMainBtn = ref({
  loading: false
});

const payPasswordTips = computed(() => {
  return refIsForceUpdate.value
    ? uni.$t('mine.pay-password.forceTips')
    : uni.$t('mine.pay-password.tips');
});

const rules = computed(() => {
  const result = {
    'params.oldPwd': rulePassword({
      isRequired: true,
      langKey: 'oldPwd'
    }),
    'params.newPwd': rulePassword({
      isRequired: true,
      langKey: 'newPwd'
    }),
    'params.newPwdConfirm': rulePassword({
      isRequired: true,
      langKey: 'userPwdConfirm',
      isConfirm: true,
      comparePackage: {
        source: refFormModel.params,
        field: 'newPwd'
      }
    })
  };
  return result;
});

// 表单变化时，控制按钮是否禁用
const onChangeForm = () => {
  // 做定时器，避免没拿到最新值
  setTimeout(() => {
    if (isRequireParamFilled(refFormModel, rules.value)) {
      refForm.value
        .validate()
        .then((res) => {
          refFormIsFormValid.value = true;
        })
        .catch((errors) => {
          refFormIsFormValid.value = false;
        });
    } else {
      refFormIsFormValid.value = false;
    }
  }, 0);
};

// 确认
const doEnsureLogin = async () => {
  refMainBtn.value.loading = true;
  const reqParams = {
    ...refFormModel.params
  };
  delete reqParams.newPwdConfirm;
  reqUserUpdateWithPwd(reqParams)
    .then((res) => {
      // 重置表单
      refForm.value.resetFields();
      // 弹出提示
      refCPopupNormal.value.open({
        text: uni.$t('mine.pay-password.success'),
        onEnsure() {
          refCPopupNormal.value.close();
          if (refRedirectUrl.value) {
            redirectTo(refRedirectUrl.value);
            return;
          }
          goBack();
        }
      });
    })
    .finally(() => {
      refMainBtn.value.loading = false;
    });
};

// 页面初始化回调
const onPageInit = async () => {};

onLoad((options) => {
  refIsForceUpdate.value = String(options?.forceUpdate || '') === '1';
  refRedirectUrl.value = options?.redirectUrl
    ? decodeURIComponent(options.redirectUrl)
    : '';
});

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="pwd-page pwd-page--pay">
      <nav-bar :title="$t('mine.pay-password.title')" />

      <scroll-view scroll-y class="pwd-body">
        <view class="pwd-body__inner">
          <view class="pwd-tips-box">
            <c-tips>
              {{ payPasswordTips }}
            </c-tips>
          </view>

          <view class="pwd-form-card">
            <u--form
              class="m-form s-form-style-1 pwd-form"
              ref="refForm"
              :model="refFormModel"
              :rules="rules"
            >
              <c-form-item
                isShowPasswordToggle
                prop="params.oldPwd"
                :name="$t('form.password.oldPwd.title')"
                :rule="rules['params.oldPwd']"
                v-model="refFormModel.params.oldPwd"
                @change="onChangeForm"
                maxlength="16"
                :placeholder="$t('form.password.oldPwd.placeholderPay')"
              />
              <c-form-item
                isShowPasswordToggle
                prop="params.newPwd"
                :name="$t('form.password.newPwd.title')"
                :rule="rules['params.newPwd']"
                v-model="refFormModel.params.newPwd"
                @change="onChangeForm"
                maxlength="16"
                :placeholder="$t('form.password.newPwd.placeholder')"
              />
              <c-form-item
                isShowPasswordToggle
                prop="params.newPwdConfirm"
                :name="$t('form.password.userPwdConfirm.title')"
                :rule="rules['params.newPwdConfirm']"
                v-model="refFormModel.params.newPwdConfirm"
                @change="onChangeForm"
                maxlength="16"
                :placeholder="$t('form.password.userPwdConfirm.placeholder')"
              />
            </u--form>
          </view>
        </view>
      </scroll-view>

      <view class="pwd-cta">
        <c-main-btn
          @tap="doEnsureLogin"
          :isLoading="refMainBtn.loading"
          :diyDisabled="!refFormIsFormValid"
        />
      </view>
    </view>

    <c-popup-normal ref="refCPopupNormal" closeable />
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/pwd-page.scss';
</style>
