import CONFIG, { mockApiBase } from '@/configs';
import SERIES from '@/configs/series';

/** 查询系统维护状态 */
export const reqCheckMaintain = () => {
  const params = `?productCode=${encodeURIComponent(SERIES.productCode)}`;
  return uni.$u.http.request({
    url: CONFIG.isUseStaticData
      ? `${mockApiBase}/maintain/checkMaintain.json`
      : `${CONFIG.maintainURL}/maintain/checkMaintain${params}`,
    method: 'get',
    custom: {
      hideLoadingToast: true,
      hideErrorToast: true,
      ignoreTokenInvalid: true,
      needToken: false,
      isMaintainApi: true
    }
  });
};
