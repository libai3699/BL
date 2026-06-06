// 价格预警相关接口

// 我的预警列表
export const reqAlertList = () => {
  return uni.$u.http.request({
    url: '/user/alert/list',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 新增预警
export const reqAlertAdd = (data) => {
  return uni.$u.http.request({
    url: '/user/alert/add',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 切换预警开关
export const reqAlertToggle = (id) => {
  return uni.$u.http.request({
    url: '/user/alert/toggle',
    method: 'post',
    data: { id },
    custom: {
      hideLoadingToast: true
    }
  });
};

// 删除预警
export const reqAlertDelete = (id) => {
  return uni.$u.http.request({
    url: '/user/alert/delete',
    method: 'post',
    data: { id },
    custom: {
      hideLoadingToast: true
    }
  });
};
