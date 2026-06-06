// 新股申购相关接口

const jsonHeaders = {
  'Content-Type': 'application/json;charset=UTF-8'
};

// 获取新股列表
export const getNewGu = (data) => {
  return uni.$u.http.request({
    url: '/user/newStockList.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 顶格申购
export const getNewAdd = (data) => {
  return uni.$u.http.request({
    url: '/user/add.do',
    method: 'post',
    data,
    header: jsonHeaders,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 用户申购记录
export const getUserNewGuList = (data) => {
  return uni.$u.http.request({
    url: '/user/getOneSubscribeByUserId.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 中签记录
export const getSubscribeWinRecords = (data) => {
  return uni.$u.http.request({
    url: '/user/getAddzqjl.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取新股申购历史记录列表
export const getSubscribeHistoryList = (data) => {
  return uni.$u.http.request({
    url: '/user/getOneSubscribeList.do',
    method: 'post',
    data,
    header: jsonHeaders,
    custom: {
      hideLoadingToast: true
    }
  });
};

// IPO 认缴提交（缴费）
export const submitSubscribe = (data) => {
  return uni.$u.http.request({
    url: '/user/submitSubscribe.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 新股抢筹下单（付款）- 已废弃，使用 submitSubscribe
export const buyNewStockQc = (data) => {
  return uni.$u.http.request({
    url: '/user/buyNewStockQc.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};
