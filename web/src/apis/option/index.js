// 自选股相关接口

// 我的自选股列表
export const reqOptionList = (data) => {
  return uni.$u.http.request({
    url: '/user/option/list.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 加入自选股
export const reqAddOption = (data) => {
  return uni.$u.http.request({
    url: '/stock/addOption.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 移除自选股
export const reqDelOption = (data) => {
  return uni.$u.http.request({
    url: '/stock/delOption.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 是否自选股
export const reqIsOption = (data) => {
  return uni.$u.http.request({
    url: '/stock/isOption.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true,
      hideErrorToast: true
    }
  });
};
