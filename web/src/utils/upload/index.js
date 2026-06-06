import { useCommonStore } from '@/stores/common.js';
import * as trandition from './tradition.js';
import * as cosTencent from './cosTencent.js';

export function uploadImage(options) {
  const commonStore = useCommonStore();
  // 0-传统上传 | 1-cos腾讯云
  const picUpLoadType = commonStore.configUploadFileDest;
  options.baseURL = commonStore.storage_cur.domain;
  if (options.baseURL.substring(0, 1) === '/') {
    options.baseURL = location.origin + options.baseURL;
  }
  if (picUpLoadType === 0) {
    return trandition.uploadImage(options);
  } else if (picUpLoadType === 1) {
    return cosTencent.uploadImage(options);
  }
}

export function uploadVideo(options) {
  const commonStore = useCommonStore();
  // 0-传统上传 | 1-cos腾讯云
  const videoUpLoadType = commonStore.configUploadFileDest;
  options.baseURL = commonStore.storage_cur.domain;
  if (options.baseURL.substring(0, 1) === '/') {
    options.baseURL = location.origin + options.baseURL;
  }
  if (videoUpLoadType === 0) {
    return trandition.uploadVideo(options);
  } else if (videoUpLoadType === 1) {
    return cosTencent.uploadVideo(options);
  }
}

export function uploadFile(options) {
  const commonStore = useCommonStore();
  // 0-传统上传 | 1-cos腾讯云
  const fileUpLoadType = commonStore.configUploadFileDest;
  options.baseURL = commonStore.storage_cur.domain;
  if (options.baseURL.substring(0, 1) === '/') {
    options.baseURL = location.origin + options.baseURL;
  }
  if (fileUpLoadType === 0) {
    return trandition.uploadFile(options);
  } else if (fileUpLoadType === 1) {
    return cosTencent.uploadFile(options);
  }
}
