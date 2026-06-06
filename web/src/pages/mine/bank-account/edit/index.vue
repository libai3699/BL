<script setup>
import { computed, reactive, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';
import { isRequireParamFilled, ruleNormalText } from '@/utils/rule';
import {
  reqUserBankAdd,
  reqUserBankGetBankInfo,
  reqUserBankUpdate
} from '@/apis/user/bank';

const commonStore = useCommonStore();

const refCPopupSelectBank = ref(null);
const refType = ref('add');
const refId = ref('');
const refForm = ref(null);
const refFormIsFormValid = ref(false);
const refFormModel = reactive({
  params: {
    bankAccount: '',
    bankCode: '',
    bankName: '',
    bankNo: ''
  }
});
const refMainBtn = ref({
  loading: false
});

const rules = computed(() => {
  const result = {
    'params.bankAccount': ruleNormalText({
      isRequired: true,
      langKey: 'bankAccount'
    }),
    'params.bankName': ruleNormalText({
      isRequired: true,
      langKey: 'bankName'
    }),
    'params.bankNo': ruleNormalText({
      isRequired: true,
      langKey: 'bankNo'
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

// 打开选择银行卡弹层
function openPopupSelectBank() {
  refCPopupSelectBank.value.open();
}

// 保存
function doSave() {
  refMainBtn.value.loading = true;
  const reqParams = {
    ...refFormModel.params
  };
  if (refType.value === 'edit') {
    reqParams.id = Number(refId.value);
  }
  const method = refType.value === 'add' ? reqUserBankAdd : reqUserBankUpdate;
  method(reqParams)
    .then((res) => {
      // 重置表单
      refForm.value.resetFields();
      // 清除校验
      setTimeout(() => {
        refForm.value.clearValidate();
      }, 500);
      // 通知账户列表更新
      uni.$emit('bankAccountListNeedRefresh');
      // 弹出提示
      commonStore.openNormalPopup({
        text: uni.$t(`mine.bank-account.edit.success.${refType.value}`),
        onEnsure() {
          commonStore.closeNormalPopup();
          goBack();
        }
      });
    })
    .finally(() => {
      refMainBtn.value.loading = false;
    });
}

// 选择银行后回调
function onSelectBank(bank) {
  refFormModel.params.bankCode = bank.bankCode;
  refFormModel.params.bankName = bank.bankName;
  onChangeForm();
}

// 页面初始化回调
const onPageInit = async () => {
  onChangeForm();
  if (refType.value === 'edit') {
    // 编辑时,更新数据
    reqUserBankGetBankInfo({
      id: Number(refId.value)
    }).then((res) => {
      refFormModel.params.bankAccount = res.bankAccount;
      refFormModel.params.bankCode = res.bankCode;
      refFormModel.params.bankName = res.bankName;
      refFormModel.params.bankNo = res.bankNo;
      onChangeForm();
    });
  }
};

onLoad((options) => {
  if (options.id === '' || options.id == null) {
    refType.value = 'add';
  } else {
    refType.value = 'edit';
    refId.value = options.id;
  }
});

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar :title="$t(`mine.bank-account.edit.title.${refType}`)" />

      <!-- 表单 -->
      <u--form
        class="m-form s-form-style-1"
        ref="refForm"
        :model="refFormModel"
        :rules="rules"
      >
        <c-form-item
          prop="params.bankAccount"
          :name="$t('form.normalText.bankAccount.title')"
          :rule="rules['params.bankAccount']"
          v-model="refFormModel.params.bankAccount"
          @change="onChangeForm"
          maxlength="36"
          :placeholder="$t('form.normalText.bankAccount.placeholder')"
        />
        <c-form-item
          formType="select"
          prop="params.bankName"
          :name="$t('form.normalText.bankName.title')"
          :rule="rules['params.bankName']"
          v-model="refFormModel.params.bankName"
          @change="onChangeForm"
          maxlength="36"
          :placeholder="$t('form.normalText.bankName.placeholder')"
          @tap="openPopupSelectBank"
        />
        <c-form-item
          prop="params.bankNo"
          :name="$t('form.normalText.bankNo.title')"
          :rule="rules['params.bankNo']"
          v-model="refFormModel.params.bankNo"
          @change="onChangeForm"
          maxlength="36"
          :placeholder="$t('form.normalText.bankNo.placeholder')"
        />
      </u--form>

      <!-- 按钮 -->
      <c-main-btn
        class="u-cur-btn"
        :defaultText="$t(`mine.bank-account.edit.btn.${refType}`)"
        @tap="doSave"
        :isLoading="refMainBtn.loading"
        :diyDisabled="!refFormIsFormValid"
      />
    </view>

    <!-- 选择银行卡 -->
    <c-popup-select-bank ref="refCPopupSelectBank" @onEnsure="onSelectBank" />
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar(40rpx);
  padding-left: 24rpx;
  padding-right: 24rpx;
  .u-cur-btn {
    margin-top: 80rpx;
  }
}
</style>
