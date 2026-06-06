// cos腾讯云上传方法

async function uploadImage({
  baseURL,
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
    // 获取预签名URL
    const tokenInfo = await getUploadToken(baseURL, 'image', file.name);
    // console.log('[COS腾讯云] - 预签名URL', tokenInfo);

    // 使用预签名URL上传
    await uploadWithPresignedUrl(file, tokenInfo);
    // console.log('[COS腾讯云] - 上传结果', result);

    const result = {
      uri: tokenInfo.uri,
      url: tokenInfo.url
    };

    onSuccess?.(result);

    if (needLoadingToast) {
      uni.hideLoading();
    }

    return result;
  } catch (error) {
    console.error('[COS腾讯云] - 上传失败', error);
    onError?.(error);

    if (needLoadingToast) {
      uni.hideLoading();
    }
  }
}

async function uploadVideo({
  baseURL,
  needLoadingToast = false,
  file,
  onError,
  onSuccess
} = {}) {
  file.path = file.tempFilePath; // 统一一下
  if (needLoadingToast) {
    uni.showLoading({
      title: uni.$t('loading.upload'),
      mask: true
    });
  }
  try {
    // 获取预签名URL
    const tokenInfo = await getUploadToken(baseURL, 'video', file.name);
    // console.log('[COS腾讯云] - 预签名URL', tokenInfo);

    // 使用预签名URL上传
    // H5的file有tempFile, App没有，App直接取file
    await uploadWithPresignedUrl(file.tempFile || file, tokenInfo);
    // console.log('[COS腾讯云] - 上传结果', result);
    onSuccess?.(file, {
      data: {
        success: true,
        fileName: tokenInfo.fileName,
        coverUri: '',
        coverUrl: '',
        videoUri: tokenInfo.uri,
        videoUrl: tokenInfo.url
      }
    });

    if (needLoadingToast) {
      uni.hideLoading();
    }
  } catch (error) {
    console.error('[COS腾讯云] - 上传失败', error);
    onError?.(file, error);

    if (needLoadingToast) {
      uni.hideLoading();
    }
  }
}

async function uploadFile({
  baseURL,
  item,
  debug,
  option,
  onError,
  onSuccess,
  onProgress,
  // 是否来自webview
  isFromWebview = false
} = {}) {
  item.type = 'loading';
  delete item.responseText;
  try {
    // 获取预签名URL
    const tokenInfo = await getUploadToken(
      baseURL,
      'file',
      item.file.name,
      isFromWebview
    );
    // console.log('[COS腾讯云] - 预签名URL', tokenInfo);

    // 使用预签名URL上传
    if (isFromWebview) {
      await uploadWithPresignedUrlWebview(item.file, tokenInfo);
    } else {
      await uploadWithPresignedUrl(item.file, tokenInfo);
    }
    // console.log('[COS腾讯云] - 上传结果', result);
    item.type = 'success';
    item.responseText = JSON.stringify({
      code: 200,
      data: {
        success: true,
        fileName: tokenInfo.fileName,
        uri: tokenInfo.uri,
        url: tokenInfo.url
      }
    });
    onSuccess?.(item);
    return item;
  } catch (error) {
    console.error('[COS腾讯云] - 上传失败', error);
    item.type = 'fail';
    onError?.(item, error);
    return item;
  }
}

// 获取图片上传预签名URL
async function getUploadToken(baseURL, type = 'image', fileName, isFromWebview = false) {
  try {
    let response;
    let result;
    if (isFromWebview) {
      response = await fetch(
        `${baseURL}/user/${type}/uploadToken?fileName=${encodeURIComponent(fileName)}`
      );
      result = await response.json();
    } else {
      response = await uni.$u.http.request({
        url: `${baseURL}/user/${type}/uploadToken?fileName=${encodeURIComponent(fileName)}`
      });
      result = response;
      return result;
    }

    if (result.code === 200) {
      return result.data;
    } else {
      throw new Error(result.message || '获取预签名URL失败');
    }
  } catch (error) {
    console.error('获取图片上传预签名URL失败', error);
    throw error;
  }
}

// 使用预签名URL上传文件 = webview使用
function uploadWithPresignedUrlWebview(file, tokenInfo) {
  return new Promise((resolve, reject) => {
    uploadWithPresignedUrlH5(file, tokenInfo, resolve, reject);
  });
}

// 使用预签名URL上传文件
function uploadWithPresignedUrl(file, tokenInfo) {
  return new Promise((resolve, reject) => {
    // #ifdef H5
    uploadWithPresignedUrlH5(file, tokenInfo, resolve, reject);
    // #endif
    // #ifndef H5
    uploadWithPresignedUrlApp(file, tokenInfo, resolve, reject);
    // #endif
  });
}

function uploadWithPresignedUrlH5(file, tokenInfo, onSuccess, onError) {
  const xhr = new XMLHttpRequest();

  // 监听上传进度
  // 进度先不管
  // xhr.upload.addEventListener('progress', (event) => {
  //   if (event.lengthComputable) {
  //     const percent = Math.round((event.loaded / event.total) * 100);
  //   }
  // });

  // 监听完成事件
  xhr.addEventListener('load', () => {
    if (xhr.status === 200) {
      onSuccess();
    } else {
      onError(new Error(`上传失败，状态码: ${xhr.status}`));
    }
  });

  // 监听错误事件
  xhr.addEventListener('error', () => {
    onError(new Error('上传过程中发生网络错误'));
  });

  // 发送请求
  xhr.open('PUT', tokenInfo.preUrl);
  xhr.setRequestHeader('Content-Type', file.type);
  xhr.send(file.source);
}

function getContentTypeByFile(filePath) {
  if (filePath) {
    let fileType = filePath.split('.').pop().toLowerCase();
    switch (fileType) {
      case 'pdf':
        return 'application/pdf';
      case 'jpg':
      case 'jpeg':
        return 'image/jpeg';
      case 'png':
        return 'image/png';
      case 'bmp':
        return 'image/bmp';
      case 'webp':
        return 'image/webp';
      case 'doc':
        return 'application/msword';
      case 'docx':
        return 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
      case 'pptx':
        return 'application/vnd.openxmlformats-officedocument.presentationml.presentation';
      case 'text':
        return 'text/plain';
      case 'xls':
        return 'application/vnd.ms-excel';
      case 'xlsx':
        return 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      case 'zip':
        return 'application/zip';
    }
  }
  return 'application/octet-stream';
}

function getLocalFilePath(path) {
  if (
    path.startsWith('_www') ||
    path.startsWith('_doc') ||
    path.startsWith('_documents') ||
    path.startsWith('_downloads') ||
    path.startsWith('file://') ||
    path.startsWith('/storage/emulated/0/')
  ) {
    return path;
  }
  if (path.startsWith('/')) {
    const converted = plus.io.convertAbsoluteFileSystem(path);
    if (converted !== path) return converted;
    path = path.substr(1);
  }
  return '_www/' + path;
}

async function uploadWithPresignedUrlApp(file, tokenInfo, onSuccess, onError) {
  const safePath = await copyToAppPath(file.path); // 将 path 从外部路径复制到 _doc/upload/ 下
  file.path = safePath;

  const filePath = file.path;
  const url = tokenInfo.presignedUrl;

  plus.io.resolveLocalFileSystemURL(
    getLocalFilePath(filePath),
    (entry) => {
      entry.file(
        (fileObj) => {
          const fileReader = new plus.io.FileReader();
          fileReader.onloadend = (event) => {
            const base64 = event.target.result.split(',')[1];
            const buffer = uni.base64ToArrayBuffer(base64);

            uni.request({
              url,
              method: 'PUT',
              data: buffer,
              header: {
                'Content-Type': getContentTypeByFile(filePath)
              },
              success(res) {
                if (res.statusCode === 200) {
                  onSuccess?.();
                } else {
                  const err = new Error(`上传失败，状态码: ${res.statusCode}`);
                  onError?.(err);
                }
              },
              fail(err) {
                const error = new Error(`请求失败: ${err.errMsg}`);
                onError?.(error);
              }
            });
          };

          fileReader.onerror = (error) => {
            const err = new Error(`读取文件失败: ${JSON.stringify(error)}`);
            onError?.(err);
          };

          fileReader.readAsDataURL(fileObj, 'utf-8');
        },
        (err) => {
          const error = new Error(`文件读取失败: ${JSON.stringify(err)}`);
          onError?.(error);
        }
      );
    },
    (err) => {
      const error = new Error(`文件路径解析失败: ${JSON.stringify(err)}`);
      onError?.(error);
    }
  );
}

// Android 10+ 对存储权限进行了限制：不能直接访问外部路径
// 所以复制到 _doc/upload/ 再读取
function copyToAppPath(originalPath) {
  return new Promise((resolve, reject) => {
    plus.io.resolveLocalFileSystemURL(
      originalPath,
      function (entry) {
        const newFileName = Date.now() + '_' + entry.name;

        plus.io.resolveLocalFileSystemURL(
          '_doc/upload/',
          function (targetDir) {
            entry.copyTo(
              targetDir,
              newFileName,
              function (newEntry) {
                resolve(newEntry.toLocalURL());
              },
              reject
            );
          },
          function () {
            // 创建文件夹再复制
            plus.io.resolveLocalFileSystemURL(
              '_doc/',
              function (rootDir) {
                rootDir.getDirectory(
                  'upload',
                  { create: true },
                  function (newDir) {
                    entry.copyTo(
                      newDir,
                      newFileName,
                      function (newEntry) {
                        resolve(newEntry.toLocalURL());
                      },
                      reject
                    );
                  },
                  reject
                );
              },
              reject
            );
          }
        );
      },
      reject
    );
  });
}

export { uploadImage, uploadVideo, uploadFile };
