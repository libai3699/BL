<script setup>
const emits = defineEmits(['confirm']);
const props = defineProps({
  dataSource: {
    type: Object,
    default: {}
  }
});
</script>

<template>
  <view class="u-authen-status">
    <image class="u-img" src="@/static/images/common/authentication-fail.png" />
    <view class="u-text">
      {{ $t('mine.authentication.status.fail') }}
    </view>
    <view class="u-desc">
      {{ dataSource.rejectReason }}
    </view>
  </view>
  <view class="u-btn-area m-fixed-bottom-btn">
    <c-main-btn
      @tap="emits('confirm')"
      :defaultText="$t('mine.authentication.btn.fail')"
    />
  </view>
</template>

<style lang="scss" scoped></style>
