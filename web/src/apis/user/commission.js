// 获取返佣记录
export const getCommissionRecords = (data) => {
  return uni.$u.http.request({
    url: '/user/commission/records',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};
