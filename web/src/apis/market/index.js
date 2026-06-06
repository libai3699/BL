// 市场相关接口

/**
 * 获取交易所列表
 */
export const getExchangeList = () => {
  return uni.$u.http.request({
    url: '/stock/getExchangeList.do',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取股票列表
 * @param {Object} params
 * @param {String} params.keyWords - 关键词
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 * @param {String} params.stockPlate - 股票板块
 * @param {String} params.stockType - 股票类型
 */
export const getStockList = (params) => {
  return uni.$u.http.request({
    url: '/stock/getStockList.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取大盘指数信息
 */
export const getMarket = () => {
  return uni.$u.http.request({
    url: '/stock/getMarket.do',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取指定股票的K线
 * @param {Object} params
 * @param {String} params.gid - 股票ID
 * @param {String} params.interval - 时间间隔
 * @param {Number} params.type - 类型
 */
export const getStockKline = (params) => {
  return uni.$u.http.request({
    url: '/stock/getStockKline.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};
