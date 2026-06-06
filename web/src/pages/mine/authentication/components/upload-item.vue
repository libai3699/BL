<script setup>
import { ref } from 'vue';

const emits = defineEmits(['success']);
const props = defineProps({
  textSimple: {
    type: String,
    default: ''
  },
  textDesc: {
    type: String,
    default: ''
  },
  pic: {
    type: String,
    default: ''
  }
});

const refUploadStatus = ref('none');
const refFile = ref(null);
const refPic = ref(props.pic);

// 上传图片
const onChangeFiles = (event) => {
  refFile.value = event[event.length - 1];
  refPic.value = refFile.value.thumb;
  refUploadStatus.value = refFile.value.status;
  if (refUploadStatus.value === 'success') {
    emits('success', refFile.value);
  } else if (refUploadStatus.value === 'fail') {
    onUploadFail();
  }
};

// 上传失败处理
function onUploadFail() {
  reset();
}

// 重置
function reset() {
  // 文件置空
  refFile.value = null;
  refPic.value = props?.pic;
}
</script>

<template>
  <view class="u-idcard-upload">
    <view class="u-upload-l">
      <view class="u-upload-simple">
        {{ textSimple }}
      </view>
      <view class="u-upload-desc">
        {{ textDesc }}
      </view>
    </view>
    <view class="u-upload-r">
      <c-upload
        class="m-upload"
        ref="refUploadFront"
        @change="onChangeFiles"
        autoUpload
        :maxCount="1"
        :capture="['album', 'camera']"
        :sourceType="['album', 'camera']"
      >
        <image class="u-img" mode="aspectFill" :src="refPic" />
        <view class="u-loading" v-if="refUploadStatus === 'uploading'">
          <up-loading-icon color="#fff" mode="semicircle"></up-loading-icon>
        </view>
      </c-upload>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.u-idcard-upload {
  width: 100%;
  box-sizing: border-box;
  border: 2rpx solid var(--border-color-weak);
  border-radius: 10rpx;
  display: flex;
  justify-content: space-between;
  padding: 28rpx 44rpx 28rpx 24rpx;

  .u-upload-l {
    flex: 1;
    color: var(--text-minor-color);
    font-size: 24rpx;
    line-height: 32rpx;
    margin-right: 20rpx;

    .u-upload-desc {
      margin-top: 14rpx;
    }
  }

  .u-upload-r {
    flex-shrink: 0;

    .m-upload {
      position: relative;

      .u-img {
        width: 348rpx;
        height: 240rpx;
      }

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
      }
    }
  }
}
</style>
