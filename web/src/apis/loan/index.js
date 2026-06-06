// 贷款相关接口（信用贷 loanType=1 / VIP贷 loanType=2）

// 申请贷款
export const reqLoanApply = (data) => {
  return uni.$u.http.request({
    url: '/user/loan/apply',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 我的贷款列表
export const reqLoanList = (loanType, status) => {
  const data = {};
  if (loanType !== undefined && loanType !== null) data.loanType = loanType;
  if (status !== undefined && status !== null) data.status = status;
  return uni.$u.http.request({
    url: '/user/loan/list',
    method: 'get',
    data,
    custom: {
      hideLoadingToast: true,
      hideErrorToast: true
    }
  });
};
