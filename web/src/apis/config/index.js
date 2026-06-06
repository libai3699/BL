// 是否用云厂商存储
export const getConfigUploadFileDest = () => {
  return uni.$u.http.request({
    url: '/config/getAppConfigValue?key=uploadFileDest',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 关于我们
export const reqAboutUs = () => {
  return uni.$u.http.request({
    url: '/user/aboutUs.do',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 跟单制度表格（跟单等级配置）
export const reqQueryTradeZD = () => {
  return uni.$u.http.request({
    url: '/config/queryTradeZD.do',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 批量按 key 数组查 app_config（H5Url / appDownloadUrl 等）
export const reqGetKeys = (keys = []) => {
  return uni.$u.http.request({
    url: '/config/getKeys.do',
    method: 'post',
    data: { array: keys },
    custom: {
      hideLoadingToast: true
    }
  });
};

// 检查最新版本
export const reqGetVersion = (data) => {
  return uni.$u.http.request({
    url: '/config/getVersion.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 查询配置组（默认 question 组：常见问题）
export const reqQueryQuestion = (group = 'question') => {
  return uni.$u.http.request({
    url: '/config/queryQuestion.do',
    method: 'post',
    data: { group },
    custom: {
      hideLoadingToast: true
    }
  });
};
