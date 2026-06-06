import CONFIG, { mockApiBase } from '@/configs';

// 添加银行卡
export const reqUserBankAdd = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/bank/add.json`
      : '/user/bank/add.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 查询用户银行卡信息
export const reqUserBankGetBankInfo = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/bank/getBankInfo.json`
      : '/user/bank/getBankInfo.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      // hideLoadingToast: true
    }
  });
};

// 分页查询用户银行卡列表
export const reqUserBankGetBankInfoList = (
  pageNum = 1,
  pageSize = CONFIG.pageSize,
  data
) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/bank/getBankInfoList.json`
      : `/user/bank/getBankInfoList.do`,
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

// 查询支持的银行列表
export const reqUserBankGetBankList = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/bank/getBankList.json`
      : '/user/bank/getBankList.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true,
      hideLoadingToast: true
    }
  });
};

// 修改银行卡信息
export const reqUserBankUpdate = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/bank/update.json`
      : '/user/bank/update.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 删除银行卡
export const reqUserBankDelete = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/bank/delete.json`
      : '/user/bank/delete.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {}
  });
};
