const http = uni.$u.http;
import CONFIG from '@/configs';
import { getRequestHeader } from '../request';

// 获取亚马逊上传相关配置
export const reqGetTokenUrl = (data) => {
  return http.request({
    url: '/file/api/getTokenUrl',
    method: 'post',
    data,
    // 自定义参数示例
    custom: {
      // hideErrorToast: true,
      hideLoadingToast: true
    }
  });
};

export const reqUploadFiles = (file, formData = {}) => {
  return new Promise((resolve, reject) => {
    const reqParams = {
      // 默认type是0
      type: 0,
      // 如果传了不一样的type，这里可以覆盖
      ...formData,
      fileName: new Date().getTime() + '-' + file?.name
    };
    const onError = (err) => {
      console.log('err', err);
      const msg = err.constructor === Object ? err?.errMsg : uni.$t('error.uploadImage');
      uni.$u.toast(msg);
      reject(err);
    };
    reqGetTokenUrl(reqParams)
      .then((res) => {
        // 先获取blob数据
        try {
          fetch(file.url)
            .then((response) => response.blob())
            .then((blob) => {
              // 调用接口传输
              return fetch(res.preUrl, {
                method: 'put',
                body: blob
              });
            })
            .then((resUpload) => {
              if (resUpload.ok) {
                // 处理正常返回的json数据，因为亚马逊不会返回上传后的地址，所以用reqGetTokenUrl得到的结果
                resolve(res);
              } else {
                uni.$u.toast(uni.$t('error.uploadImage'));
                reject();
              }
            })
            .catch(onError);
        } catch (err) {
          onError(err);
        }
      })
      .catch(reject);
  });
};

// 上传文件 - 调后端上传接口的处理，因为考虑安全，已弃用，但代码是有用的，以后其他项目可能用得到，放着
export const reqUploadFilesByBackend = (file, formData = {}) => {
  const resolveUploadPath = (originPath) => {
    return new Promise((resolve) => {
      if (!originPath) {
        resolve(originPath);
        return;
      }
      // #ifdef APP-PLUS
      // Android 拍照可能返回 content://，需要转成本地可访问路径
      if (typeof originPath === 'string' && originPath.startsWith('content://')) {
        uni.getImageInfo({
          src: originPath,
          success: (res) => resolve(res.path || res.tempFilePath || originPath),
          fail: () => resolve(originPath)
        });
        return;
      }
      // #endif
      resolve(originPath);
    });
  };

  // 想用公共拦截器，但没成功，先快速解决，以后有空处理
  // return http.request({
  //   url: '/user/upload.do',
  //   method: 'post',
  //   // 自定义参数示例
  //   custom: {
  //     formData,
  //     hideLoadingToast: true
  //   }
  // });

  return new Promise((resolve, reject) => {
    const header = getRequestHeader();
    const rawPath = file?.url || file?.path || file?.thumb;
    resolveUploadPath(rawPath).then((filePath) => {
      if (!filePath) {
        const msg = uni.$t('error.uploadImage');
        uni.$u.toast(msg);
        reject(new Error(msg));
        return;
      }
      uni.uploadFile({
        url: CONFIG.baseURL + '/user/upload.do',
        filePath,
        name: 'upload_file',
        formData,
        header,
        success: (res) => {
          const data = JSON.parse(res.data);
          if (data.status === 0) {
            resolve(data.data);
          } else {
            console.error('Upload API Business Fail:', data);
            uni.$u.toast(data?.msg || uni.$t('error.uploadImage'));
            reject(data);
          }
        },
        fail: (err) => {
          console.error('Upload API Fail:', err);
          const msg = err?.error || uni.$t('error.uploadImage');
          uni.$u.toast(msg);
          reject(err);
        }
      });
    });
  });
};
