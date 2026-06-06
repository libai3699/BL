// 优惠券相关接口

// 我的优惠券列表（status: 0=未使用 1=已使用 2=已过期，不传=全部）
export const reqUserCouponList = (status) => {
  return uni.$u.http.request({
    url: '/user/coupon/list',
    method: 'get',
    data: status === undefined ? {} : { status },
    custom: {
      hideLoadingToast: true,
      hideErrorToast: true
    }
  });
};

// 可领取的优惠券列表
export const reqAvailableCoupons = () => {
  return uni.$u.http.request({
    url: '/user/coupon/available',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 领取优惠券
export const reqClaimCoupon = (couponConfigId) => {
  return uni.$u.http.request({
    url: '/user/coupon/claim',
    method: 'post',
    data: { id: couponConfigId },
    custom: {
      hideLoadingToast: true
    }
  });
};
