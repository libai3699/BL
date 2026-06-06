// 投顾相关接口

const jsonHeaders = {
  'Content-Type': 'application/json;charset=UTF-8'
};

// 获取明星投顾信息
export const getMentorInfo = () => {
  return uni.$u.http.request({
    url: '/mentor/infos.do',
    method: 'post',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 获取精英投顾推荐列表
export const getRecommendList = () => {
  return uni.$u.http.request({
    url: '/mentor/recommend.do',
    method: 'get',
    custom: {
      hideLoadingToast: true
    }
  });
};

// 获取近20日操盘记录
export const getTradingRecords = (data) => {
  return uni.$u.http.request({
    url: '/follow/getTradingRecords.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取团队统计
export const getTeam = () => {
  return uni.$u.http.request({
    url: '/follow/getTeam.do',
    method: 'get',
    header: jsonHeaders
  });
};

// 获取团队详细信息（队员明细分页）
export const getTeamInfo = (data) => {
  return uni.$u.http.request({
    url: '/follow/getTeamInfo.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 队长分佣明细列表
export const getAgentSalaryList = (data) => {
  return uni.$u.http.request({
    url: '/follow/getAgentSalaryList.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 队长分佣区间列表
export const getAgentSalaryPeriodList = (data) => {
  return uni.$u.http.request({
    url: '/follow/getAgentSalaryPeriodList.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取收益统计
export const getFollowIncome = (params) => {
  return uni.$u.http.request({
    url: '/follow/getFollowIncome.do',
    method: 'get',
    params: params,
    header: jsonHeaders
  });
};

// 查询配置
export const queryConfig = (data) => {
  return uni.$u.http.request({
    url: '/config/queryConfig.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取当日委托信息
export const queryDayPackage = (data) => {
  return uni.$u.http.request({
    url: '/package/queryDayPackage.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取当日委托说明
export const queryDayPackageInfo = (data) => {
  return uni.$u.http.request({
    url: '/package/queryDayPackageInfo.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取定期委托信息
export const queryMorePackage = (data) => {
  return uni.$u.http.request({
    url: '/package/queryMorePackage.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 申请当日委托
export const applySingleFollow = (data) => {
  return uni.$u.http.request({
    url: '/package/applySingleFollow.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 申请定期委托
export const applyMoreFollow = (data) => {
  return uni.$u.http.request({
    url: '/package/applyMoreFollow.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 查询投顾记录
export const queryFollowRecord = (data) => {
  return uni.$u.http.request({
    url: '/followRecord/queryFollowRecord.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 查询投顾记录详情
export const queryFollowRecordDetail = (data) => {
  return uni.$u.http.request({
    url: '/followRecord/queryFollowRecordDetail.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 追加投顾金额
export const appendFollow = (data) => {
  return uni.$u.http.request({
    url: '/followRecord/appendFollow.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 撤销投顾
export const cancelFollow = (data) => {
  return uni.$u.http.request({
    url: '/followRecord/cancelFollow.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 申请项目操盘
export const applyFinance = (data) => {
  return uni.$u.http.request({
    url: '/finance/applyFinance.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 获取项目操盘记录
export const getFinanceRecords = (data) => {
  return uni.$u.http.request({
    url: '/finance/financeRecord.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};

// 追加项目操盘金额
export const appendFinance = (data) => {
  return uni.$u.http.request({
    url: '/finance/appendFinance.do',
    method: 'post',
    data,
    header: jsonHeaders
  });
};
