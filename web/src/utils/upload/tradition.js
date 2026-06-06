// 传统上传方法
import { useCommonStore } from '@/stores/common';
// import { isCanParse } from '@/common';
import { reqUploadFilesByBackend } from '@/apis/upload';

// 上传图片
export async function uploadImage({
  needLoadingToast = false,
  file,
  onError,
  onSuccess
} = {}) {
  if (needLoadingToast) {
    uni.showLoading({
      title: uni.$t('loading.upload'),
      mask: true
    });
  }

  try {
    const result = await reqUploadFilesByBackend(file);
    if (needLoadingToast) {
      uni.hideLoading();
    }
    onSuccess?.(result);
    return result;
  } catch (error) {
    if (needLoadingToast) {
      uni.hideLoading();
    }
    onError?.(error);
    throw error;
  }
}

// 下面的方法暂时没用到，所以不改了
// 上传视频
export function uploadVideo({ needLoadingToast = true, file, onError, onSuccess } = {}) {
  const commonStore = useCommonStore();
  if (needLoadingToast) {
    uni.showLoading({
      title: uni.$t('loading.upload'),
      mask: true
    });
  }
  uni.uploadFile({
    url: commonStore.storage_domain + '/video/upload',
    header: {
      accessToken: uni.getStorageSync('loginInfo').accessToken
    },
    filePath: file.tempFilePath, // 要上传文件资源的路径
    name: 'file',
    success: (res) => {
      if (needLoadingToast) {
        uni.hideLoading();
      }
      // // 不能parse的话，不要继续了
      // if (!isCanParse(res.data)) return;
      let data = decryptAes(JSON.parse(res.data));
      if (data.code != 200) {
        uni.showToast({
          icon: 'none',
          title: data.message
        });
        onError?.(file, data);
      } else {
        onSuccess?.(file, data);
      }
    },
    fail: (err) => {
      if (needLoadingToast) {
        uni.hideLoading();
      }
      onError?.(file, err);
    }
  });
}

// 上传文件
export function uploadFile({ item, debug, option, onError, onSuccess, onProgress } = {}) {
  item.type = 'loading';
  delete item.responseText;
  return new Promise((resolve, reject) => {
    debug && console.log('option', JSON.stringify(option));
    let { url, name, method = 'POST', header, formData } = option;
    let form = new FormData();
    for (let keys in formData) {
      form.append(keys, formData[keys]);
    }
    form.append(name, item.file);
    let xmlRequest = new XMLHttpRequest();
    xmlRequest.open(method, url, true);
    for (let keys in header) {
      xmlRequest.setRequestHeader(keys, header[keys]);
    }

    xmlRequest.upload.addEventListener(
      'progress',
      (event) => {
        if (event.lengthComputable) {
          let progress = Math.ceil((event.loaded * 100) / event.total);
          if (progress <= 100) {
            item.progress = progress;
            onProgress(item);
          }
        }
      },
      false
    );

    xmlRequest.ontimeout = () => {
      item.type = 'fail';
      onError?.(item, '请求超时');
      return resolve();
    };

    xmlRequest.onreadystatechange = (ev) => {
      if (xmlRequest.readyState == 4) {
        if (xmlRequest.status == 200) {
          debug && console.log('上传完成：' + xmlRequest.responseText);
          item['responseText'] = xmlRequest.responseText;
          item.type = 'success';
          onSuccess?.(item);
          return resolve();
        } else if (xmlRequest.status == 0) {
          console.error(
            'status = 0 :请检查请求头Content-Type与服务端是否匹配，服务端已正确开启跨域，并且nginx未拦截阻止请求'
          );
        }
        item.type = 'fail';
        onError?.(item, '--ERROR--：status = ' + xmlRequest.status);
        return resolve();
      }
    };
    xmlRequest.send(form);
  });
}
