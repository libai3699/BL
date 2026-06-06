// 新闻资讯相关接口

// 获取新闻详情
export const reqNewsDetail = (data) => {
  return uni.$u.http.request({
    url: '/news/getDetail.do',
    method: 'post',
    data,
    custom: {
      hideLoadingToast: true
    }
  });
};
