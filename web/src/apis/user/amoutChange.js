import CONFIG, { mockApiBase } from '@/configs';

// 获取所有的账遍类型
export const reqUserGetAmountChangeType = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/AmountChange/getAmountChangeType.json`
      : `/user/AmountChange/getAmountChangeType`,
    method: CONFIG.isUseStaticData ? 'get' : 'get',
    data,
    // 自定义参数示例
    custom: {}
  });
};

// 查询账变记录
export const reqUserAmountChangeListOmitInfo = (
  pageNum = 1,
  pageSize = CONFIG.pageSize,
  data
) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/AmountChange/listOmitInfo.json`
      : '/user/AmountChange/listOmitInfo',
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
