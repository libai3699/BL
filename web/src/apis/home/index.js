// 首页相关接口

/**
 * 获取轮播图
 */
export const getBannerByPlat = (params) => {
  return uni.$u.http.request({
    url: '/site/getBannerByPlat.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取客服链接
 */
export const getKeFu = (hideLoadingToast = true) => {
  return uni.$u.http.request({
    url: '/config/keFu.do',
    method: 'get',
    custom: {
      hideLoadingToast
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
 * 获取热门股票列表
 * @param {Object} params
 * @param {String} params.endTime - 结束时间
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 * @param {String} params.startTime - 开始时间
 * @param {String} params.stockType - 股票类型 (mxg, us)
 */
export const getHotStockList = (params) => {
  return uni.$u.http.request({
    url: '/stock/getHotStockList.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取新闻列表
 * @param {Object} params
 * @param {Number} params.pageNum - 页码
 * @param {Number} params.pageSize - 每页数量
 * @param {Number} params.type - 类型
 * @param {String} params.sort - 排序
 */
export const getNewsList = (params) => {
  return uni.$u.http.request({
    url: '/news/getNewsList.do',
    method: 'post',
    data: params,
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取弹窗图
 */
export const getPopup = () => {
  return uni.$u.http.request({
    url: '/stock/getPopup.do',
    method: 'GET',
    custom: {
      hideLoadingToast: true
    }
  });
};

/**
 * 获取首页中部市场开盘状态（美股/墨股）
 * 返回 [{ key, isOpen, nextEventTime(UTC ms), nextEventType: open|close, pct }]
 * 文案与倒计时由前端按 i18n + (nextEventTime - now) 计算
 */
export const getMarketStatus = () => {
  return uni.$u.http.request({
    url: '/site/getMarketStatus.do',
    method: 'post',
    custom: {
      hideLoadingToast: true
    }
  });
};
