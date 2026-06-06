// 增发认购相关接口

const jsonHeaders = {
  'Content-Type': 'application/json;charset=UTF-8'
};

// 获取增发列表
export const getStockAddList = (data) => {
  return uni.$u.http.request({
    url: '/user/stockAddList.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 增发预售提交
export const addStockSubscribe = (data) => {
  return uni.$u.http.request({
    url: '/user/stockAddAdd.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 增发认购提交（带付款）
export const addStockSubscribe2 = (data) => {
  return uni.$u.http.request({
    url: '/user/stockAddAdd2.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取增发认购记录
export const getUserSubscribeAddRecords = (data) => {
  return uni.$u.http.request({
    url: '/user/getOneSubscribeAddByUserId.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取中签记录
export const getSubscribeWinRecords = (data) => {
  return uni.$u.http.request({
    url: '/user/getAddzqjl.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 认缴提交
export const submitSubscribeAdd = (data) => {
  return uni.$u.http.request({
    url: '/user/submitSubscribeAdd.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};
