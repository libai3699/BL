<script setup>
import { ref } from 'vue';
import { onShow } from '@dcloudio/uni-app';

import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack, navigateTo } from '@/utils/navigate';
import { reqUserSaveAvatar, reqUserUpdateUserName } from '@/apis/user';

const commonStore = useCommonStore();
const userStore = useUserStore();
const user = () => userStore.userInfo.value;

// Tabs 配置
const refTabs = ref([
  {
    label: () => uni.$t('mine.profile.tabs.info'),
    value: 'info'
  }
]);
// 默认选中的tab
const refTab = ref('info');
const refSavingStatus = ref('nonme');

// 上传头像成功后回调
function onAvatarSuccess(file) {
  refSavingStatus.value = 'uploading';
  reqUserSaveAvatar({
    headImgUrl: file.url
  })
    .then(() => {
      // 更新pinia
      user().headImage = file.url;
      refSavingStatus.value = 'success';
    })
    .catch(() => (refSavingStatus.value = 'fail'));
}

// 修改昵称
function doModifyNickName() {
  commonStore.openNormalPopup({
    title: uni.$t('mine.profile.modifyNickName'),
    hasInput: true,
    inputValue: user().nickName,
    inputMaxLength: 32,
    onEnsure(res) {
      const nickName = res.inputValue;
      if (nickName === '' || nickName == null) {
        uni.$u.toast(uni.$t('mine.profile.modifyNickNameEmpty'));
        return;
      }
      reqUserUpdateUserName({
        nickName
      }).then(() => {
        user().nickName = nickName;
        commonStore.closeNormalPopup();
      });
    }
  });
}

// 页面初始化回调
const onPageInit = async () => {};

onShow(() => {
  // 返回方法
  commonStore.fnBack = goBack;
  commonStore.getAndSetConfigUploadFileDest();
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="m-content">
      <nav-bar :title="$t('mine.profile.title')" />
      <view class="u-top">
        <c-avatar
          :src="user()?.headImage"
          size="152rpx"
          isOpenUpload
          isAutoUpload
          :uploadStatus="refSavingStatus"
          @success="onAvatarSuccess"
        />
        <view class="u-title">
          {{ user()?.nickName || $t('common.notSetNickName') }}
        </view>
        <view class="u-subtitle">
          {{ user()?.userName }}
        </view>
      </view>

      <view class="u-main">
        <c-tabs
          class="u-cur-tabs-type"
          :tabs="refTabs"
          :tab="refTab"
          mode="big"
          align="center"
        />

        <!-- 链接盒子 -->
        <view class="m-link-box">
          <!-- 实名认证 -->
          <view class="u-link-item" @tap="navigateTo('/pages/mine/authentication/index')">
            <view class="u-link-name">
              {{ $t('mine.profile.link.authentication') }}
            </view>
            <view class="u-link-value">
              {{ $t(`options.authentication.${user()?.isActive}`) }}
            </view>
            <svg-icon name="arrow-right" class="u-svg-icon-arrow-right" />
          </view>
          <!-- 银行账户 -->
          <view class="u-link-item" @tap="navigateTo('/pages/mine/bank-account/index')">
            <view class="u-link-name">
              {{ $t('mine.profile.link.bankAccount') }}
            </view>
            <svg-icon name="arrow-right" class="u-svg-icon-arrow-right" />
          </view>
          <!-- 交易密码 -->
          <view class="u-link-item" @tap="navigateTo('/pages/mine/pay-password/index')">
            <view class="u-link-name">
              {{ $t('mine.profile.link.payPass') }}
            </view>
            <svg-icon name="arrow-right" class="u-svg-icon-arrow-right" />
          </view>
          <!-- 昵称 -->
          <view class="u-link-item" @tap="doModifyNickName">
            <view class="u-link-name">
              {{ $t('mine.profile.link.nickName') }}
            </view>
            <view class="u-link-value">
              {{ user()?.nickName || $t('common.notSetNickName') }}
            </view>
            <svg-icon name="arrow-right" class="u-svg-icon-arrow-right" />
          </view>
        </view>
      </view>
    </view>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;
.m-content {
  @include hasNavBar();

  .u-top {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20rpx 24rpx;
    font-size: 28rpx;
    line-height: 38rpx;
    .u-title {
      margin-top: 8rpx;
    }
  }

  .u-main {
    margin-top: 22rpx;
    padding: 0 24rpx;
  }
}
</style>
