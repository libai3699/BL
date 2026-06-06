<script setup>
import { onMounted, useAttrs, ref } from 'vue';
import { useI18n } from 'vue-i18n';

import { uploadImage } from '@/utils/upload';

// 国际化处理
const { t } = useI18n();

// 透传的属性和事件对象
const attrs = useAttrs();
const emits = defineEmits(['change']);
const props = defineProps({
  // 是否选择完自动调上传，如果是false，那父级要自己调用此组件的上传方法：doReqUpload
  autoUpload: {
    type: Boolean,
    default: true
  }
});

const curFiles = ref([]);
const curFilesLength = ref(0);

// 当前组件的afterRead回调
const curAfterRead = async (event) => {
  // 当设置 mutiple 为 true 时, file 为数组格式，否则为对象格式
  let lists = [].concat(event.file);
  curFilesLength.value = curFiles.value.length;
  lists.forEach((item) => {
    curFiles.value.push({
      ...item,
      status: 'uploading',
      message: t('loading.upload')
    });
  });
  emits('change', curFiles.value);
  // 自动上传不为true，则中断
  if (!props.autoUpload) return;

  // 用for循环可以控制一个个传（支持async/await）如果用forEach不支持
  for (let i = 0; i < lists.length; i++) {
    await doReqUpload(lists[i]);
    curFilesLength.value++;
  }
  emits('change', curFiles.value);
};
// 删除图片
const curDelete = (event) => {
  curFiles.value.splice(event.index, 1);
  emits('change', curFiles.value);
};
// 文件超出大小限制
const onOversize = () => {
  uni.showToast({
    title: t('error.fileOversize'),
    icon: 'none'
  });
};

// 执行上传操作
const doReqUpload = async (file, options) => {
  let item = curFiles.value[curFilesLength.value];
  try {
    const result = await uploadImage({ file });
    console.log(result, 'result');
    curFiles.value[curFilesLength.value] = {
      ...item,
      status: 'success',
      message: '',
      url: result.url,
      uri: result.uri
    };
  } catch (e) {
    console.error('Upload error:', e);
    // 确保提示错误，使用多语言
    uni.showToast({
      title: t('error.uploadImage'),
      icon: 'none'
    });
    curFiles.value[curFilesLength.value] = {
      ...item,
      status: 'fail'
    };
  }
  return curFiles.value;
};

// 检查权限
const checkPermission = async () => {
  // #ifdef APP-PLUS
  if (uni.getSystemInfoSync().platform === 'android') {
    const permissions = [
      'android.permission.CAMERA',
      'android.permission.READ_EXTERNAL_STORAGE',
      'android.permission.WRITE_EXTERNAL_STORAGE'
    ];

    for (let i = 0; i < permissions.length; i++) {
      const status = plus.navigator.checkPermission(permissions[i]);
      if (status === 'denied') {
        // 如果被明确拒绝，尝试申请
        return new Promise((resolve) => {
          plus.android.requestPermissions(
            [permissions[i]],
            (e) => {
              // 申请成功
              resolve(true);
            },
            (e) => {
              // 申请失败，提示去设置
              uni.showModal({
                title: t('common.prompt'),
                content: t('permission.tips'), // 需要确保有这个key，或者硬编码
                confirmText: t('common.setting'),
                success: (res) => {
                  if (res.confirm) {
                    const Intent = plus.android.importClass('android.content.Intent');
                    const Settings = plus.android.importClass(
                      'android.provider.Settings'
                    );
                    const Uri = plus.android.importClass('android.net.Uri');
                    const mainActivity = plus.android.runtimeMainActivity();
                    const intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    const uri = Uri.fromParts(
                      'package',
                      mainActivity.getPackageName(),
                      null
                    );
                    intent.setData(uri);
                    mainActivity.startActivity(intent);
                  }
                }
              });
              resolve(false);
            }
          );
        });
      }
    }
  }
  // #endif
  return true;
};

// 手动触发上传
const handleManualUpload = async () => {
  // 1. 检查权限
  // #ifdef APP-PLUS
  // 只要是APP环境都检查一下，更稳妥
  // 简化版权限请求，Android 专用
  if (uni.getSystemInfoSync().platform === 'android') {
    // 使用 uni 提供的 api 或 plus api
    // 这里简单使用 plus api 强制请求
    // 注意：为了防止“第二次点不动”，我们需要确保每次点击都重新判断
    // 直接调用 chooseImage，uni-app 内部会自动申请权限，但如果之前被永久拒绝，就会失败
    // 并在 fail 中返回。
    // 既然用户特别是说“点不动”，可能是 uni-app 内部状态锁死。
    // 我们直接用 uni.chooseImage 代替 u-upload 的点击行为
  }
  // #endif

  // 调用拍照/相册
  uni.chooseImage({
    count: 1, // 默认1
    sizeType: ['original', 'compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      // 构造 u-upload 需要的数据格式
      // 构造 u-upload 需要的数据格式
      const files = res.tempFiles.map((f) => ({
        url: f.path,
        thumb: f.path,
        type: 'image',
        status: 'uploading',
        message: t('loading.upload'),
        name: f.name || '',
        source: f,
        uuid: new Date().getTime() // 简单的uuid
      }));

      // 模拟 u-upload 的 event
      // 注意: u-upload 的 afterRead 事件传递的是 { file, index, name }
      // 单选时 file 是对象，多选时是数组。我们这里先把 files 传过去，curAfterRead 里面有 [].concat 处理
      curAfterRead({ file: files, name: attrs.name || '' });
    },
    fail: (err) => {
      console.error('ChooseImage fail', err);
      // 这里可以判断是否是权限原因，如果是，引导去设置
      // err.errMsg usually contains "auth denied" or similar
      if (
        err.errMsg &&
        (err.errMsg.includes('auth') ||
          err.errMsg.includes('denied') ||
          err.errMsg.includes('permission'))
      ) {
        uni.showModal({
          title: t('common.prompt'),
          content: '需要相机和存储权限才能上传图片，请在设置中开启',
          showCancel: false,
          success: (modalRes) => {
            // 引导去设置页面 (Android)
            // #ifdef APP-PLUS
            if (uni.getSystemInfoSync().platform === 'android') {
              const Intent = plus.android.importClass('android.content.Intent');
              const Settings = plus.android.importClass('android.provider.Settings');
              const Uri = plus.android.importClass('android.net.Uri');
              const mainActivity = plus.android.runtimeMainActivity();
              const intent = new Intent();
              intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
              const uri = Uri.fromParts('package', mainActivity.getPackageName(), null);
              intent.setData(uri);
              mainActivity.startActivity(intent);
            }
            // #endif
          }
        });
      }
    }
  });
};

// 给父组件调用的方法
defineExpose({
  doReqUpload
});

onMounted(() => {});
</script>

<template>
  <!-- 禁用 u-upload 自身的点击，完全接管 -->
  <u-upload
    v-bind="attrs"
    :disabled="true"
    @afterRead="curAfterRead"
    @delete="curDelete"
    @oversize="onOversize"
  >
    <!-- 接管点击事件 -->
    <view @tap.stop="handleManualUpload">
      <slot></slot>
    </view>
  </u-upload>
</template>

<style lang="scss" scoped></style>
