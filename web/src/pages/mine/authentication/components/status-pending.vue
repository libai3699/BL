<script setup></script>

<template>
  <view class="u-authen-status">
    <image class="u-img" src="@/static/images/common/authentication-pending.png" />
    <view class="u-text">
      {{ $t('mine.authentication.status.pending') }}
    </view>
  </view>
  <view class="u-btn-area m-fixed-bottom-btn">
    <c-main-btn
      type="default"
      :defaultText="$t('mine.authentication.btn.pending')"
      diyDisabled
    />
  </view>
</template>

<style lang="scss" scoped></style>
