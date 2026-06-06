import CONFIG, { mockApiBase } from '@/configs';

// 获取币种列表
export const reqGetCurrencyList = () => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/transfer/getCurrencyList.json`
      : '/user/transfer/getCurrencyList.do',
    method: 'post',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 获取汇率信息
export const reqGetExchangeRate = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/transfer/getExchangeRate.json`
      : '/user/transfer/getExchangeRate.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 执行转账
export const reqTransfer = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/transfer/transfer.json`
      : '/user/transfer/transfer.do',
    method: 'post',
    data
  });
};
