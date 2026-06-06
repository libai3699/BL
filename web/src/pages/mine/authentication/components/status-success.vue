<script setup>
const emits = defineEmits(['confirm']);
</script>

<template>
  <view class="u-authen-status">
    <image class="u-img" src="@/static/images/common/authentication-success.png" />
    <view class="u-text">
      {{ $t('mine.authentication.status.success') }}
    </view>
  </view>
  <view class="u-btn-area m-fixed-bottom-btn">
    <c-main-btn
      @tap="emits('confirm')"
      :defaultText="$t('mine.authentication.btn.success')"
    />
  </view>
</template>

<style lang="scss" scoped></style>
