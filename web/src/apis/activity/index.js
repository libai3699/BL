// 活动任务相关接口

// 查询队长成长任务
export const reqQueryActivityTeam = (data) => {
  return uni.$u.http.request({
    url: '/activity/queryActivityTeam.do',
    method: 'get',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 查询日常邀请任务
export const reqQueryActivityInvite = (data) => {
  return uni.$u.http.request({
    url: '/activity/queryActivityInvite.do',
    method: 'get',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};
