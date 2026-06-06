<script setup>
import { onMounted } from 'vue';

const emits = defineEmits(['loadmore']);
const props = defineProps({
  loadmoreStatus: {
    type: String,
    default: ''
  }
});

onMounted(() => {});
</script>

<template>
  <!-- 只要loadmoreStatus不是hide(隐藏)或者init(初始化)，就显示加载更多组件 -->
  <view
    class="u-loadmore-wrap"
    v-if="loadmoreStatus !== 'hide' && loadmoreStatus !== 'init'"
  >
    <u-loadmore
      line
      :status="loadmoreStatus"
      :loadmoreText="$t('component.dataBox.loadmore.loadmoreText')"
      :loadingText="$t('component.dataBox.loadmore.loadingText')"
      :nomoreText="$t('component.dataBox.loadmore.nomoreText')"
      @loadmore="emits('loadmore')"
    />
  </view>
</template>

<style lang="scss" scoped>
.u-loadmore-wrap {
  :deep(.u-loadmore__content__text) {
    color: var(--text-normal-dark-white-color) !important;
  }
}
</style>
