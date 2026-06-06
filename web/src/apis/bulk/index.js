/**
 * 获取大宗交易列表
 * @param {Object} params - 请求参数
 * @param {string} params.startTime - 开始时间
 * @param {string} params.endTime - 结束时间
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 */
export const getDzList = (params) => {
  return uni.$u.http.request({
    url: '/stock/getDzList.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取大宗下单列表
 * @param {Object} params - 请求参数
 * @param {string} params.startTime - 开始时间
 * @param {string} params.endTime - 结束时间
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 */
export const buyStockDzList = (params) => {
  return uni.$u.http.request({
    url: '/user/buyStockDzList.do',
    method: 'post',
    data: params
  });
};

/**
 * 大宗下单
 * @param {Object} params - 请求参数
 * @param {number} params.num - 数量
 * @param {string} params.password - 密码
 * @param {number} params.stockDzId - 大宗交易ID
 */
export const buyStockDz = (params) => {
  return uni.$u.http.request({
    url: '/user/buyStockDz.do',
    method: 'post',
    data: params
  });
};
