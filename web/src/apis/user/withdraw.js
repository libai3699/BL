import CONFIG, { mockApiBase } from '@/configs';

// 用户提现
export const reqUserWithdrawOutMoney = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/withdraw/outMoney.json`
      : '/user/withdraw/outMoney.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 提现记录列表
export const reqUserWithdrawList = (pageNum = 1, pageSize = CONFIG.pageSize, data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/withdraw/list.json`
      : `/user/withdraw/list.do`,
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data: {
      pageNum,
      pageSize,
      ...data
    },
    // 自定义参数示例
    custom: {
      hideErrorToast: true,
      hideLoadingToast: true
    }
  });
};

// 提现说明
export const reqUserWithdrawDesc = () => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/config/userWithdrawDesc.json`
      : '/config/userWithdrawDesc.do',
    method: 'get',
    custom: {
      hideErrorToast: true,
      hideLoadingToast: true
    }
  });
};
