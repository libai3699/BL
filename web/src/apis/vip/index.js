import CONFIG, { mockApiBase } from '@/configs';

// 获取等级说明
export const reqVip = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData ? `${mockApiBase}/vip.json` : '/vip',
    method: CONFIG.isUseStaticData ? 'get' : 'get',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 获取 VIP 奖励列表
export const reqVipRewardList = () => {
  return uni.$u.http.request({
    url: '/vip/reward/list',
    method: 'get',
    custom: {
      hideLoadingToast: true,
      hideErrorToast: true
    }
  });
};

// 领取 VIP 奖励
export const reqClaimVipReward = (id) => {
  return uni.$u.http.request({
    url: '/vip/reward/claim',
    method: 'post',
    data: { id },
    custom: {}
  });
};
