import CONFIG, { mockApiBase } from '@/configs';

// 创建充值订单
export const reqUserRechargeInMoney = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/recharge/inMoney.json`
      : '/user/recharge/inMoney.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 充值记录列表
export const reqUserRechargeList = (pageNum = 1, pageSize = CONFIG.pageSize, data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/recharge/list.json`
      : `/user/recharge/list.do`,
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

// 充值商户列表
export const reqUserRechargeChannel = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/recharge/rechargelist.json`
      : '/user/recharge/rechargelist.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true,
      hideLoadingToast: true
    }
  });
};
