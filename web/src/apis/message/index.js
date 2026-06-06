// 消息相关接口

const jsonHeaders = {
  'Content-Type': 'application/json;charset=UTF-8'
};

// 获取未读消息数量（接口暂未启用）
export const getUnreadMessageCount = () => {
  // return uni.$u.http.request({
  //   url: '/user/message/getUnreadCount.do',
  //   method: 'post',
  //   header: jsonHeaders,
  //   custom: {
  //     hideLoadingToast: true,
  //     hideErrorToast: true,
  //     ignoreTokenInvalid: true
  //   }
  // });
  return Promise.resolve(0);
};

// 查询用户站内消息列表
export const getMessageList = (data) => {
  return uni.$u.http.request({
    url: '/user/message/getMessagelist.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 已读全部
export const readAllMessages = () => {
  return uni.$u.http.request({
    url: '/user/message/readAll.do',
    method: 'post',
    header: jsonHeaders
  });
};

// 更新消息状态
export const updateMessageStatus = (data) => {
  return uni.$u.http.request({
    url: '/user/message/updateMessageStatus.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 更新通知状态
export const updateNoticeStatus = (data) => {
  return uni.$u.http.request({
    url: '/user/message/updateNoticeStatus.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};
