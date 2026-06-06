/**
 * 获取理财产品列表
 * @param {String} type - 类型：mxg-比索，us-美元
 */
export const getFinanceProduct = (params) => {
  return uni.$u.http.request({
    url: '/finance/product.do',
    method: 'get',
    params: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 查询收益记录
 */
export const getIncomeRecord = (data) => {
  return uni.$u.http.request({
    url: '/finance/income.do',
    method: 'POST',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 查询理财记录
 */
export const getFinanceRecord = (data) => {
  return uni.$u.http.request({
    url: '/finance/financeRecord.do',
    method: 'POST',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 申请理财
 */
export const applyFinance = (data) => {
  return uni.$u.http.request({
    url: '/finance/applyFinance.do',
    method: 'POST',
    data,
    custom: {
      hideLoadingToast: false
    }
  });
};

/**
 * 追加理财
 */
export const appendFinance = (data) => {
  return uni.$u.http.request({
    url: '/finance/appendFinance.do',
    method: 'POST',
    data,
    custom: {
      hideLoadingToast: false
    }
  });
};
