<script setup>
import { computed, reactive, ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { goBack } from '@/utils/navigate';
import { reqUserAuth, reqUserGetRealNameStatus } from '@/apis/user';
import { solveNormalData } from '@/utils/solveData';
import { isRequireParamFilled, ruleNormalText } from '@/utils/rule';
import defaultPicFront from '@/static/images/common/front.png';
import defaultPicBack from '@/static/images/common/back.png';

import uploadItem from './components/upload-item.vue';
import statusFail from './components/status-fail.vue';
import statusSuccess from './components/status-success.vue';
import statusPending from './components/status-pending.vue';

const commonStore = useCommonStore();

const refDataBoxDetail = ref({
  detail: null,
  isEmpty: () =>
    refDataBoxDetail.value.detail == null ||
    Object.keys(refDataBoxDetail.value.detail)?.length === 0,
  isNotGetData: () =>
    refDataBoxDetail.value.loading ||
    refDataBoxDetail.value.error ||
    refDataBoxDetail.value.isEmpty(),
  loading: true,
  error: false,
  errorText: ''
});
const refForm = ref(null);
const refFormIsFormValid = ref(false);
const refFormModel = reactive({
  params: {
    realName: '',
    idCard: '',
    phone: '',
    beneficiary: '',
    front: '',
    back: ''
  }
});
const refMainBtn = ref({
  loading: false
});

// 状态值映射
const statusName = computed(() => {
  switch (refDataBoxDetail.value.detail?.status) {
    case -1:
      return 'none';
    case 1:
      return 'success';
    case 2:
      return 'fail';
    case 0:
      return 'pending';
  }
});
const rules = computed(() => {
  const result = {
    'params.realName': ruleNormalText({
      isRequired: true,
      langKey: 'realName'
    }),
    'params.idCard': ruleNormalText({
      isRequired: true,
      langKey: 'idCard'
    }),
    'params.phone': ruleNormalText({
      isRequired: true,
      langKey: 'phone'
    }),
    'params.front': ruleNormalText({
      isRequired: true,
      langKey: 'front'
    }),
    'params.back': ruleNormalText({
      isRequired: true,
      langKey: 'back'
    })
  };
  return result;
});

function getData() {
  solveNormalData({
    refData: refDataBoxDetail,
    reqMethod: reqUserGetRealNameStatus,
    onSuccess(res) {
      // 测试代码,可随意改状态
      // refDataBoxDetail.value.detail.status = 3;
    }
  });
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

// 文件上传成功回调
function onUploadSuccess(type, file) {
  refFormModel.params[type] = file.url;
  onChangeForm();
}

// 提交
function doEnsure() {
  const reqParams = {
    ...refFormModel.params
  };
  refMainBtn.value.loading = true;
  reqUserAuth(reqParams)
    .then((res) => {
      getData();
    })
    .finally((err) => {
      refMainBtn.value.loading = false;
    });
}
// 重新认证
function doAgain() {
  // 回显数据
  refFormModel.params.realName = refDataBoxDetail.value.detail.realName;
  refFormModel.params.idCard = refDataBoxDetail.value.detail.idCard;
  refFormModel.params.phone = refDataBoxDetail.value.detail.phone;
  refFormModel.params.beneficiary = refDataBoxDetail.value.detail.beneficiary;
  refFormModel.params.front = refDataBoxDetail.value.detail.front;
  refFormModel.params.back = refDataBoxDetail.value.detail.back;
  refDataBoxDetail.value.detail.status = -1;
  // 表单检测
  onChangeForm();
}

// 页面初始化回调
const onPageInit = async () => {
  getData();
};

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
  commonStore.getAndSetConfigUploadFileDest();
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar :title="$t('mine.authentication.title')" />

      <!-- 未认证状态展示表单 -->
      <view v-if="statusName === 'none'" class="auth-form-card">
        <u--form
          class="m-form s-form-style-2"
          ref="refForm"
          :model="refFormModel"
          :rules="rules"
        >
          <c-form-item
            prop="params.realName"
            :name="$t('form.normalText.realName.title')"
            :rule="rules['params.realName']"
            v-model="refFormModel.params.realName"
            @change="onChangeForm"
            maxlength="36"
            :placeholder="$t('form.normalText.realName.placeholder')"
          />
          <c-form-item
            prop="params.idCard"
            :name="$t('form.normalText.idCard.title')"
            :rule="rules['params.idCard']"
            v-model="refFormModel.params.idCard"
            @change="onChangeForm"
            maxlength="36"
            :placeholder="$t('form.normalText.idCard.placeholder')"
          />
          <c-form-item
            prop="params.phone"
            :name="$t('form.normalText.phone.title')"
            :rule="rules['params.phone']"
            v-model="refFormModel.params.phone"
            @change="onChangeForm"
            maxlength="36"
            :placeholder="$t('form.normalText.phone.placeholder')"
          />
          <c-form-item
            prop="params.beneficiary"
            :name="$t('form.normalText.beneficiary.title')"
            :rule="rules['params.beneficiary']"
            v-model="refFormModel.params.beneficiary"
            @change="onChangeForm"
            maxlength="36"
            :placeholder="$t('form.normalText.beneficiary.placeholder')"
          />
          <c-form-item
            formType="none"
            prop="params.front"
            :name="$t('form.normalText.front.title')"
            :rule="rules['params.front']"
            v-model="refFormModel.params.front"
            @change="onChangeForm"
          >
            <upload-item
              :text-simple="$t('form.normalText.front.simple')"
              :text-desc="$t('form.normalText.front.desc')"
              :pic="refFormModel.params.front || defaultPicFront"
              @success="onUploadSuccess('front', $event)"
            />
          </c-form-item>
          <c-form-item
            formType="none"
            prop="params.back"
            :name="$t('form.normalText.back.title')"
            :rule="rules['params.back']"
            v-model="refFormModel.params.back"
            @change="onChangeForm"
          >
            <upload-item
              :text-simple="$t('form.normalText.back.simple')"
              :text-desc="$t('form.normalText.back.desc')"
              :pic="refFormModel.params.back || defaultPicBack"
              @success="onUploadSuccess('back', $event)"
            />
          </c-form-item>
        </u--form>
      </view>

      <!-- 按钮区 -->
      <!-- 未认证 -->
      <view class="u-btn-area" v-if="statusName === 'none'">
        <c-main-btn
          @tap="doEnsure"
          :isLoading="refMainBtn.loading"
          :diyDisabled="!refFormIsFormValid"
        />
      </view>

      <!-- 认证失败 -->
      <status-fail
        v-if="statusName === 'fail'"
        :data-source="refDataBoxDetail.detail"
        @confirm="doAgain"
      />

      <!-- 认证通过 -->
      <status-success v-if="statusName === 'success'" @confirm="goBack" />

      <!-- 认证中 -->
      <status-pending v-if="statusName === 'pending'" />
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar(24rpx);
  padding-bottom: 24rpx;
  padding-left: 24rpx;
  padding-right: 24rpx;
  background: var(--stock-page-bg, #eef2f8);
  min-height: 100vh;
  box-sizing: border-box;

  .auth-form-card {
    @include stock-card;
    background: #fff;
    border-radius: 24rpx;
    padding: 8rpx 24rpx 32rpx;
    overflow: hidden;
  }

  :deep(.u-authen-status) {
    margin-top: 260rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    .u-img {
      width: 222rpx;
      height: 188rpx;
    }
    .u-text {
      margin-top: 20rpx;
      color: var(--text-key-color);
      font-weight: bold;
    }
    .u-desc {
      color: var(--text-minor-color);
      margin-top: 20rpx;
    }
  }

  .u-btn-area {
    margin-top: 48rpx;
  }
}
</style>
