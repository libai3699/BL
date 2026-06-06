// 短信验证码相关接口

// 发送注册短信验证码
export const reqSendRegSms = (data) => {
  return uni.$u.http.request({
    url: '/sms/sendRegSms.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

// 发送忘记密码短信验证码
export const reqSendForgetSms = (data) => {
  return uni.$u.http.request({
    url: '/sms/sendForgetSms.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};
