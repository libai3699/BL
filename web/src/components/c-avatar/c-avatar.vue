<script setup>
import { useAttrs, ref, watch } from 'vue';

import { useUserStore } from '@/stores/user';
import images from '@/static/images';

// 用户
const userStore = useUserStore();
// 用户信息
const user = () => userStore.userInfo.value;

// 透传的属性和事件对象
const attrs = useAttrs();
const emits = defineEmits(['success']);
const props = defineProps({
  size: {
    type: String,
    default: 'md'
  },
  // 默认图片模式
  defaultPicMode: {
    type: String,
    default: 'default'
  },
  // 是否开启上传组件，默认不开启
  isOpenUpload: {
    type: Boolean,
    default: false
  },
  // 是否选择图片后自动上传
  isAutoUpload: {
    type: Boolean,
    default: false
  },
  // 控制上传状态 none-未上传,uploading-上传中,success-上传成功,fail-上传失败
  uploadStatus: {
    type: String,
    default: 'none'
  },
  // 是否显示等级图标
  isHasLevel: {
    type: Boolean,
    default: false
  }
});

const refUpload = ref(null);
const refUploadStatus = ref(props.uploadStatus);
const refFile = ref(null);
const avatar = ref(attrs.src);
// 默认头像字典
const defaultMap = ref({
  default: images.avatar.default
});
// size字典
const sizeMap = ref({
  xs: '32rpx',
  sm: '48rpx',
  nm: '64rpx',
  md: '80rpx',
  lg: '96rpx',
  xl: '112rpx',
  xxl: '128rpx'
});

// 监控父级上传状态
watch(
  () => props.uploadStatus,
  (val) => {
    refUploadStatus.value = val;
    if (val === 'fail') {
      onUploadFail();
    }
  }
);

// 新增图片
const onChangeFiles = (event) => {
  refFile.value = event[event.length - 1];
  avatar.value = refFile.value.thumb;
  refUploadStatus.value = refFile.value.status;
  if (refUploadStatus.value === 'success') {
    emits('success', refFile.value);
  } else if (refUploadStatus.value === 'fail') {
    onUploadFail();
  }
};

// 上传失败处理
function onUploadFail() {
  resetAvatar();
}

// 重置头像
function resetAvatar() {
  // 文件置空
  refFile.value = null;
  avatar.value = attrs?.src;
}

// 给父组件调用的方法
defineExpose({
  // 获取头像文件对象
  getFile() {
    return refFile.value;
  },
  // 上传头像文件
  async doReqUpload() {
    const result = await refUpload.value.doReqUpload(refFile.value);
    // 上传成功后把文件置空
    refFile.value = null;
    // 因为头像只有一个文件，所以取第一个
    return result[0];
  },
  // 重置头像
  resetAvatar
});
</script>

<template>
  <!-- size只能用此组件规定的 -->
  <view class="m-avatar-wrap">
    <c-upload
      class="m-upload"
      ref="refUpload"
      @change="onChangeFiles"
      :autoUpload="isAutoUpload"
      :maxCount="1"
      v-if="props.isOpenUpload"
    >
      <u-avatar
        mode="aspectFill"
        v-bind="attrs"
        :src="avatar ?? attrs?.src"
        :size="sizeMap[size] || size"
        :default-url="attrs?.defaultUrl ?? defaultMap[props?.defaultPicMode ?? 'default']"
      />
      <view class="u-loading" v-if="refUploadStatus === 'uploading'">
        <up-loading-icon color="#fff" mode="semicircle"></up-loading-icon>
      </view>
      <view class="u-upload-icon">
        <view class="u-icon-wrap">
          <svg-icon name="upload" class="u-svg-icon" />
        </view>
      </view>
    </c-upload>
    <u-avatar
      v-else
      mode="aspectFill"
      v-bind="attrs"
      :size="sizeMap[size] || size"
      :default-url="attrs?.defaultUrl ?? defaultMap[props?.defaultPicMode ?? 'default']"
    />
    <svg-icon
      v-if="props.isHasLevel"
      :name="`head-img-v${user()?.level}`"
      class="u-svg-icon-head-img"
    />
  </view>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-avatar-wrap {
  position: relative;
  .u-svg-icon-head-img {
    @include posCenterL;
    top: -15%;
    z-index: 9;
    width: 134%;
    height: 134%;
  }
}
.m-upload {
  position: relative;
  .u-loading {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.3);
    border-radius: 100%;
  }
  .u-upload-icon {
    position: absolute;
    z-index: 10;
    right: 8rpx;
    bottom: 4rpx;
    width: 60rpx;
    height: 60rpx;
    border-radius: 100%;
    background: #fdfdfe;
    display: flex;
    align-items: center;
    justify-content: center;
    .u-icon-wrap {
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 100%;
      width: 52rpx;
      height: 52rpx;
      border: 2rpx solid var(--border-color-weak);
    }
    .u-svg-icon {
      width: 28rpx !important;
    }
  }
}
</style>
