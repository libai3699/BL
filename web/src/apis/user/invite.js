import CONFIG, { mockApiBase } from '@/configs';

// 获取邀请码和邀请信息
export const reqUserInviteCode = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/invite/inviteCode.json`
      : `/user/invite/inviteCode`,
    method: CONFIG.isUseStaticData ? 'get' : 'get',
    data,
    // 自定义参数示例
    custom: {}
  });
};

// 获取佣金统计
export const reqCommissionStats = () => {
  return uni.$u.http.request({
    url: `/user/invite/commissionStats`,
    method: 'get',
    custom: {}
  });
};
