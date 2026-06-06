// 订单相关接口

/**
 * 获取用户账户资产信息
 * @param {String} type - 账户类型(usd/mxn)，为空默认返回usd
 */
export const getUserAccount = (type = '') => {
  return uni.$u.http.request({
    url: '/user/getUserAccount.do',
    method: 'post',
    data: { type },
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 买入股票
 * @param {Object} params
 * @param {Number} params.buyNum - 买入数量
 * @param {Number} params.buyType - 买入类型：0-买涨，1-买跌
 * @param {Number} params.profitTarget - 止盈价格
 * @param {Number} params.stockId - 股票ID
 * @param {Number} params.stopTarget - 止损价格
 */
export const buyStock = (params) => {
  return uni.$u.http.request({
    url: '/stock/buy.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 平仓操作
 * @param {String} positionSn - 持仓单号
 */
export const sellStock = (positionSn) => {
  return uni.$u.http.request({
    url: '/stock/sell.do',
    method: 'post',
    data: { positionSn }
  });
};

/**
 * 修改止盈止损
 * @param {Object} params
 * @param {String} params.positionSn - 持仓单号
 * @param {Number} params.profitTarget - 止盈价格
 * @param {Number} params.stopTarget - 止损价格
 */
export const updateProfitTarget = (params) => {
  return uni.$u.http.request({
    url: '/stock/updateProfitTarget.do',
    method: 'post',
    data: params
  });
};

/**
 * 查询持仓列表
 * @param {Object} params
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 * @param {Number} params.state - 状态（0持仓中 1已平仓）
 * @param {String} params.stockCode - 股票代码
 * @param {String} params.stockSpell - 股票简拼
 */
export const getPositionList = (params) => {
  return uni.$u.http.request({
    url: '/user/position/list.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取持仓详情
 * @param {Object} params
 * @param {String} params.positionSn - 持仓编号
 */
export const getPositionDetail = (params) => {
  return uni.$u.http.request({
    url: '/user/position/detail.do',
    method: 'post',
    data: params
  });
};

/**
 * 获取买卖手续费
 * @param {Object} params
 * @param {String} params.gid - 股票GID
 */
export const getTradeFee = (params) => {
  return uni.$u.http.request({
    url: '/stock/getTradeFee.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取股票详情
 * @param {String} gid - 股票GID
 */
export const getStockDetail = (params) => {
  return uni.$u.http.request({
    url: '/stock/getStock.do',
    method: 'post',
    data: params
  });
};

/**
 * 查询用户增发记录
 * @param {Object} params
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 * @param {Number} params.status - 状态(0 全部, 2待审核, 3 待付款, 4 待转持仓, 5 已转持仓)
 * @param {Number} params.type - 类型(1 增发预售, 2 增发认购)
 */
export const getSubscribeAddList = (params) => {
  return uni.$u.http.request({
    url: '/user/getSubscribeAddList.do',
    method: 'post',
    data: params
  });
};
