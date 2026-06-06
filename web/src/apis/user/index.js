import CONFIG, { mockApiBase } from '@/configs';

// 登录
export const reqUserLogin = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData ? `${mockApiBase}/user/login.json` : '/user/login.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true,
      needToken: false
    }
  });
};

// 注册
export const reqUserReg = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData ? `${mockApiBase}/user/reg.json` : '/user/reg.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true,
      needToken: false
    }
  });
};

// 获取用户信息
export const reqUserGetUserInfo = () => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/getUserInfo.json`
      : '/user/getUserInfo.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    // 自定义参数示例
    custom: {
      hideErrorToast: true,
      hideLoadingToast: true
    }
  });
};

// 保存头像
export const reqUserSaveAvatar = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/saveAvatar.json`
      : '/user/saveAvatar.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 修改登录密码
export const reqUserAppUpdatePwd = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/appUpdatePwd.json`
      : '/user/appUpdatePwd.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 修改交易密码
export const reqUserUpdateWithPwd = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/updateWithPwd.json`
      : '/user/updateWithPwd.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 获取语言列表
export const reqUserGetLanList = () => {
  return uni.$u.http.request({
    url: '/user/getLanList.do',
    method: 'post',
    custom: {}
  });
};

// 修改用户语言
export const reqUserUpdateLan = (data) => {
  return uni.$u.http.request({
    url: '/user/updateLan.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 修改昵称
export const reqUserUpdateUserName = (data) => {
  return uni.$u.http.request({
    url: '/user/updateUserName.do',
    method: 'post',
    data,
    custom: {
      // hideLoadingToast: true
    }
  });
};

// 查询实名认证状态
export const reqUserGetRealNameStatus = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/getRealNameStatus.json`
      : '/user/getRealNameStatus.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      // hideLoadingToast: true
    }
  });
};

// 提交实名认证
export const reqUserAuth = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData ? `${mockApiBase}/user/auth.json` : '/user/auth.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 获取用户账户资产信息
export const reqUserGetUserAccount = (data) => {
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/user/getUserAccount.json`
      : '/user/getUserAccount.do',
    method: CONFIG.isUseStaticData ? 'get' : 'post',
    data,
    // 自定义参数示例
    custom: {
      hideLoadingToast: true
    }
  });
};

// 获取团队成员列表
export const getTeamList = (page = 1, limit = 20, level = '1') => {
  return uni.$u.http.request({
    url: '/user/invite/userList',
    method: 'get',
    data: {
      page,
      limit,
      level
    },
    custom: {
      hideLoadingToast: true,
      hideErrorToast: true
    }
  });
};

// 获取当前用户的代理信息
export const reqUserGetUserAgentInfo = () => {
  return uni.$u.http.request({
    url: '/user/getUserAgentInfoVO.do',
    method: 'post',
    custom: {
      hideLoadingToast: true,
      hideErrorToast: true
    }
  });
};
