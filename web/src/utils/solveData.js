// 处理数据对象的公共方法

// 处理普通数据
// needLoading 需要等待态
export function solveNormalData({
  refData,
  reqMethod,
  reqParams,
  onSuccess,
  onError,
  needLoading = true,
  isUseDataBox = false
}) {
  return new Promise((resolve, reject) => {
    if (needLoading) {
      refData.value.loading = true;
    }
    reqMethod(reqParams, isUseDataBox)
      .then((res) => {
        refData.value.error = false;
        refData.value.detail = res;
        onSuccess?.(res);
        resolve(res);
      })
      .catch((e) => {
        refData.value.error = true;
        refData.value.errorText = e.errorDesc;
        onError?.(e);
        reject(e);
      })
      .finally(() => {
        refData.value.loading = false;
      });
  });
}

// 处理分页数据
export function solvePagingData({
  refData,
  isFirstPage = true,
  pageSize,
  reqMethod,
  reqParams,
  onSuccess,
  solveRes = (res) => res,
  onError,
  // 第一页就没数据时是否显示：没有更多了
  isFirstPageShowNoMore = false,
  // 没有数据了是否显示：没有更多了
  showNomore = true,
  needLoading = true,
  newRecordDelRepeatKey = 'id' // 用于去重的新数据的key
}) {
  const page = isFirstPage ? 1 : refData.value.pageNum;
  if (isFirstPage) {
    if (needLoading) {
      refData.value.loading = true;
    }
    refData.value.pageNum = page;
  } else {
    // 如果没有下一页或者正在加载中，不调用数据
    if (
      !refData.value.hasNextPage ||
      refData.value.loadmoreStatus == 'loading' ||
      refData.value.loading
    )
      return;
    refData.value.loadmoreStatus = 'loading';
  }
  reqMethod(page, pageSize, reqParams)
    .then((res) => {
      res = solveRes(res);
      if (isFirstPage) {
        refData.value.error = false;
        refData.value.details = res.list;
        if (refData.value.details.length === 0 && !isFirstPageShowNoMore) {
          refData.value.loadmoreStatus = 'hide';
          onSuccess?.(res);
          return;
        }
      } else {
        // 合并，按照id去重
        const newDetails = res.list.filter(
          (item) =>
            !refData.value.details.some(
              (d) =>
                d[newRecordDelRepeatKey] === item[newRecordDelRepeatKey] &&
                item[newRecordDelRepeatKey] != null
            )
        );
        refData.value.details = refData.value.details.concat(newDetails);
      }
      onSuccess?.(res);
      // 分页处理
      refData.value.hasNextPage = res.hasNextPage;
      if (res.hasNextPage) {
        // 有下一页，页码+1
        refData.value.pageNum++;
        refData.value.loadmoreStatus = 'loadmore';
      } else {
        // 无下一页，加载更多变成无
        if (showNomore) {
          // 如果参数设置 showNomore 为 true时，显示“没有更多了
          refData.value.loadmoreStatus = 'nomore';
        } else {
          refData.value.loadmoreStatus = 'hide';
        }
      }
    })
    .catch((e) => {
      if (isFirstPage) {
        refData.value.error = true;
        refData.value.errorText = e.errorDesc;
      } else {
        refData.value.loadmoreStatus = 'loadmore';
      }
      onError?.(e);
    })
    .finally(() => {
      if (isFirstPage) {
        refData.value.loading = false;
      }
    });
}
