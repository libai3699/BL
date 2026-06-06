<script setup>
import { computed, reactive, ref } from 'vue';
import { onShow, onUnload } from '@dcloudio/uni-app';

import ZINDEX from '@/configs/zindex';
import { reqUserGetUserInfo } from '@/apis/user';
import { useCommonStore } from '@/stores/common';
import { goBack, navigateTo, redirectTo } from '@/utils/navigate';
import { isRequireParamFilled, ruleNormalText, rulePassword } from '@/utils/rule';
import { reqUserWithdrawDesc, reqUserWithdrawOutMoney } from '@/apis/user/withdraw';

const commonStore = useCommonStore();

// Tabs 配置
const refTabs = ref([
  {
    label: () => uni.$t('mine.withdraw.index.tabs.bank'),
    value: '1'
  }
]);
const refTab = ref('1');
const refForm = ref(null);
const refFormIsFormValid = ref(false);
const refFormModel = reactive({
  params: {
    amt: '',
    bankId: '',
    withPwd: ''
  }
});
const refMainBtn = ref({
  loading: false
});
const refCPopupSelectBankAccount = ref(null);
const refWithdrawDesc = ref('');
const refShowForceUpdatePayPwdPopup = ref(false);

const rules = computed(() => {
  const result = {
    'params.amt': ruleNormalText({
      isRequired: true,
      langKey: 'withAmt'
    }),
    'params.bankId': ruleNormalText({
      isRequired: true,
      langKey: 'bankId'
    }),
    'params.withPwd': rulePassword({
      isRequired: true,
      langKey: 'payPass'
    })
  };
  return result;
});

// 去记录页
function goRecord() {
  if (refShowForceUpdatePayPwdPopup.value) return;
  navigateTo('/pages/mine/withdraw/record/index');
}

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

// 打开选择充值渠道弹层
function openPopupSelectChannel() {
  if (refShowForceUpdatePayPwdPopup.value) return;
  refCPopupSelectBankAccount.value.open();
}

// 选择充值渠道回调
function onSelectBankAccount(item) {
  if (refShowForceUpdatePayPwdPopup.value) return;
  // 将名称显示，真正的id保存起来，提交时使用
  refFormModel.params.bankId = item.bankAccount;
  refFormModel.params.bankIdTrue = item.id;
  onChangeForm();
}

// 保存
function doSave() {
  if (refShowForceUpdatePayPwdPopup.value) return;
  refMainBtn.value.loading = true;
  const reqParams = {
    ...refFormModel.params,
    bankId: refFormModel.params.bankIdTrue
  };
  delete reqParams.bankIdTrue;
  reqUserWithdrawOutMoney(reqParams)
    .then((res) => {
      // 重置表单
      refForm.value.resetFields();
      // 清除校验
      setTimeout(() => {
        refForm.value.clearValidate();
      }, 0);

      // 弹出提示
      commonStore.openNormalPopup({
        text: uni.$t(`mine.withdraw.index.success`),
        onEnsure() {
          commonStore.closeNormalPopup();
          goRecord();
        }
      });
    })
    .finally(() => {
      refMainBtn.value.loading = false;
    });
}

async function getWithdrawDesc() {
  try {
    const res = await reqUserWithdrawDesc();
    refWithdrawDesc.value =
      typeof res === 'string'
        ? res
        : (res?.desc ?? res?.content ?? res?.remark ?? res?.msg ?? '');
  } catch (e) {
    refWithdrawDesc.value = '';
  }
}

function goPayPasswordPage() {
  refShowForceUpdatePayPwdPopup.value = false;
  redirectTo(
    '/pages/mine/pay-password/index?forceUpdate=1&redirectUrl=%2Fpages%2Fmine%2Fwithdraw%2Findex'
  );
}

async function checkDefaultWithPwd() {
  try {
    const userInfo = await reqUserGetUserInfo();
    const isDefaultWithPwd = Number(
      userInfo?.isDefaultWithPwd ?? userInfo?.data?.isDefaultWithPwd ?? 0
    );

    if (isDefaultWithPwd === 1) {
      refCPopupSelectBankAccount.value?.close?.();
      refShowForceUpdatePayPwdPopup.value = true;
      return true;
    }
  } catch (e) {
    return false;
  }

  refShowForceUpdatePayPwdPopup.value = false;
  return false;
}

// 页面初始化回调
const onPageInit = async () => {
  const needForceUpdatePayPwd = await checkDefaultWithPwd();
  if (needForceUpdatePayPwd) return;
  getWithdrawDesc();
};

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});

onUnload(() => {
  refShowForceUpdatePayPwdPopup.value = false;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content" :class="{ 's-locked': refShowForceUpdatePayPwdPopup }">
      <nav-bar :title="$t('mine.withdraw.index.title')">
        <template #right>
          <svg-icon
            name="record-1"
            class="u-svg-icon u-svg-icon-record-1"
            @tap="goRecord"
          />
        </template>
      </nav-bar>

      <!-- 标题tab -->
      <c-tabs class="u-cur-tabs" :tabs="refTabs" :tab="refTab" hasBottomLine />

      <!-- 表单 -->
      <u--form
        class="m-form s-form-style-1"
        ref="refForm"
        :model="refFormModel"
        :rules="rules"
      >
        <c-form-item
          formType="select"
          selectType="triangle-bottom"
          prop="params.bankId"
          :name="$t('form.normalText.bankId.title')"
          :rule="rules['params.bankId']"
          v-model="refFormModel.params.bankId"
          @change="onChangeForm"
          maxlength="36"
          :placeholder="$t('form.normalText.bankId.placeholder')"
          @tap="openPopupSelectChannel"
        />
        <c-form-item
          prop="params.amt"
          :name="$t('form.normalText.withAmt.title')"
          :rule="rules['params.amt']"
          v-model="refFormModel.params.amt"
          @change="onChangeForm"
          maxlength="12"
          :placeholder="$t('form.normalText.withAmt.placeholder')"
          type="digit"
        />
        <c-form-item
          isShowPasswordToggle
          prop="params.withPwd"
          :name="$t('form.password.payPass.title')"
          :rule="rules['params.withPwd']"
          v-model="refFormModel.params.withPwd"
          @change="onChangeForm"
          maxlength="16"
          :placeholder="$t('form.password.payPass.placeholder')"
        />
      </u--form>

      <!-- 按钮 -->
      <c-main-btn
        class="u-cur-btn"
        :defaultText="$t(`mine.withdraw.index.btn.title`)"
        @tap="doSave"
        :isLoading="refMainBtn.loading"
        :diyDisabled="!refFormIsFormValid"
      />

      <rich-text
        v-if="refWithdrawDesc"
        class="u-withdraw-desc"
        :nodes="refWithdrawDesc"
      />
    </view>

    <!-- 选择银行账户 -->
    <c-popup-select-bank-account
      ref="refCPopupSelectBankAccount"
      @onEnsure="onSelectBankAccount"
    />

    <u-popup
      :show="refShowForceUpdatePayPwdPopup"
      mode="center"
      :round="24"
      :z-index="ZINDEX.tacVertification"
      :overlayStyle="{ zIndex: ZINDEX.tacVertificationOverlay }"
      :closeOnClickOverlay="false"
      @close="() => {}"
    >
      <view class="force-pay-pwd-modal">
        <view class="force-pay-pwd-header">
          <view class="header-placeholder"></view>
          <text class="modal-title">{{ $t('common.prompt') }}</text>
          <view class="header-placeholder"></view>
        </view>

        <view class="force-pay-pwd-body">
          <text class="force-pay-pwd-text">
            {{ $t('mine.withdraw.index.forceUpdatePayPwd') }}
          </text>
        </view>

        <view class="force-pay-pwd-footer">
          <button class="confirm-btn" @click="goPayPasswordPage">
            {{ $t('common.ensure') }}
          </button>
        </view>
      </view>
    </u-popup>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar(40rpx);
  padding-left: 24rpx;
  padding-right: 24rpx;

  &.s-locked {
    pointer-events: none;
  }

  .u-svg-icon-record-1 {
    width: 32rpx !important;
  }

  .u-cur-tabs {
    :deep(.u-item) {
      margin-left: 0;
    }
  }

  .u-cur-tabs-coin {
    margin-top: 22rpx;
    :deep(.u-item) {
      margin-left: 0;
    }
  }

  .u-cur-tabs-amount {
    margin-top: 24rpx;
    :deep(.u-item) {
      margin-left: 0;
      margin-right: 16rpx;
    }
  }

  .m-form {
    margin-top: 32rpx;
  }

  .u-cur-btn {
    margin-top: 46rpx;
  }

  .u-withdraw-desc {
    margin-top: 24rpx;
    font-size: 24rpx;
    line-height: 36rpx;
    color: #f04438;
  }
}

.force-pay-pwd-modal {
  background: #fff;
  border-radius: 24rpx;
  width: 650rpx;
  overflow: hidden;
}

.force-pay-pwd-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.header-placeholder {
  width: 40rpx;
  height: 40rpx;
}

.modal-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #333;
}

.force-pay-pwd-body {
  padding: 40rpx 32rpx 32rpx;
}

.force-pay-pwd-text {
  font-size: 28rpx;
  line-height: 44rpx;
  color: #666;
  text-align: center;
  display: block;
}

.force-pay-pwd-footer {
  padding: 0 32rpx 32rpx;
}

.confirm-btn {
  width: 100%;
  height: 72rpx;
  background: #00b6e6;
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
  border-radius: 14rpx;
  border: none;
  line-height: 72rpx;
}
</style>
