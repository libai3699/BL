// 引入配置
import CONFIG from '@/configs';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { decryptAes, getValidParamObject } from '@/utils';
import { compileEnv } from '@/configs';
import { showModal } from '@/utils/uniUtils';

let timer = null;

const LOGIN_PAGE = '/pages/account/login';

function isOnLoginPage() {
  return uni.$u.page?.() === LOGIN_PAGE;
}

// 获取通用请求头
export const getRequestHeader = () => {
  // 可以在此使用pinia获取用户state，将token放入header中
  const token = useUserStore().token.value;
  // 国际化
  const locale = useCommonStore().locale;
  const result = {};
  if (token) {
    result['token-user'] = token;
  }
  if (locale) {
    result['Accept-Language'] = locale;
  }
  return result;
};

/**
 * 请求拦截
 * @param {Object} http
 */
const requestInterceptors = (vm) => {
  uni.$u.http.interceptors.request.use(
    (config) => {
      // 可使用async await 做异步操作
      const {
        hideLoadingToast,
        // 是否需要传token
        needToken = true,
        loadingText = uni.$t('common.loadingText'),
        loadingMask = true,
        loadingDelay = 300
      } = config?.custom;

      // 需要登录但未登录时拦截（登录页不再提示）
      if (needToken && !useUserStore().isLogin) {
        if (!hideLoadingToast) {
          clearTimeout(timer);
          timer = setTimeout(uni.hideLoading, loadingDelay);
        }
        if (!isOnLoginPage()) {
          uni.$u.toast(uni.$t('common.pleaseLogin'));
        }
        return Promise.reject({
          errorType: 'login',
          errorDesc: uni.$t('common.pleaseLogin'),
          errorData: null
        });
      }

      const requestHeader = getRequestHeader();
      for (let i in requestHeader) {
        config.header[i] = requestHeader[i];
      }
      // 如果不需要传token，删除token
      if (!needToken) {
        delete config.header['token'];
      }
      // 如果hideLoadingToast不设置或者为false，使用toast等待
      if (!hideLoadingToast) {
        // 设置等待延迟，防止一加载接口就出现导致的闪动效果
        clearTimeout(timer);
        timer = setTimeout(() => {
          uni.showLoading({
            title: loadingText,
            mask: loadingMask
          });
        }, loadingDelay);
      }
      return config;
    },
    (
      config // 可使用async await 做异步操作
    ) => Promise.reject(config)
  );
};

/**
 * 响应拦截
 * @param {Object} http
 */
const responseInterceptors = (vm) => {
  uni.$u.http.interceptors.response.use(
    (response) => {
      // 对响应成功做点什么 可使用async await 做异步操作
      const {
        hideErrorToast,
        hideLoadingToast,
        loadingDelay = 300,
        ignoreTokenInvalid
      } = response?.config?.custom || {};
      // 清除加载动画
      if (!hideLoadingToast) {
        clearTimeout(timer);
        timer = setTimeout(uni.hideLoading, loadingDelay);
      }
      // 如果后端没有返回数据
      if (response.data === '' || response.data === null || response.data === undefined) {
        const error = {
          errorType: 'back-empty',
          errorData: '',
          errorDesc: uni.$t('error.back')
        };
        return Promise.reject(error);
      }
      const data = decryptAes(response.data);

      // 维护服务独立响应：{ code: 200, data: { status: 0|1, ... } }，明文 JSON
      const { isMaintainApi } = response?.config?.custom || {};
      if (
        isMaintainApi ||
        String(response?.config?.url || '').includes('/maintain/checkMaintain')
      ) {
        if (data.code === 200) {
          return data.data || {};
        }
        const error = {
          errorType: 'back',
          errorData: data,
          errorDesc: data?.msg || uni.$t('error.back')
        };
        if (!hideErrorToast) {
          uni.$u.toast(error.errorDesc);
        }
        return Promise.reject(error);
      }

      // 在开发环境 or 本地开发环境
      // 显示解密后数据
      if (compileEnv.endsWith('dev') || process.env.NODE_ENV === 'development') {
        console.log(
          `url(${response?.config?.method}): ${response?.config?.url}\n`,
          '参数',
          response?.config?.data ? getValidParamObject(response?.config?.data) : '无',
          '\n',
          '返回数据',
          data
        );
      }
      // 服务端返回的状态码等于200，返回结果
      if (data.status === 0) {
        if (data.data?.constructor === Object) {
          data.data.msg = data.msg;
        }
        return (
          data.data || {
            msg: data.msg,
            status: data.status
          }
        );
      }

      // 后端接口错误返回
      const error = {
        errorType: 'back',
        errorData: data,
        errorDesc: data?.msg || uni.$t('error.back')
      };
      // token过期或者无效token，重置用户信息和token
      // 如果 ignoreTokenInvalid 为 true，则忽略token失效的情况
      if ([-1, -2, -3, -4, -5].includes(data.status) && !ignoreTokenInvalid) {
        error.errorType = 'token';
        if (isOnLoginPage()) {
          useUserStore().setTokenInitial();
          useUserStore().setUserInfoInitial();
          return Promise.reject(error);
        }
        showModal(
          {
            content: error.errorDesc,
            showCancel: false,
            confirmText: uni.$t('common.refresh'),
            success(res) {
              if (res.confirm) {
                useUserStore().setTokenInitial();
                useUserStore().setUserInfoInitial();
                // 直接刷新页面
                location.reload();
              }
            }
          },
          true
        );
        return Promise.reject(error);
      }

      // 如果hideErrorToast不设置或者为false，使用toast提示
      if (!hideErrorToast) {
        uni.$u.toast(error.errorDesc);
      }
      return Promise.reject(error);
    },
    (response) => {
      console.log('http状态码非200错误', response);
      /*  对响应错误做点什么 （statusCode !== 200）*/
      const { errMsg, config, data, statusCode } = response;
      const {
        hideErrorToast,
        hideLoadingToast,
        loadingDelay = 300
      } = config?.custom || {};
      // 清除加载动画
      if (!hideLoadingToast) {
        clearTimeout(timer);
        timer = setTimeout(uni.hideLoading, loadingDelay);
      }
      // 错误描述
      let errorDesc = `${statusCode}: ${JSON.stringify(data)}`;
      if (errMsg === 'request:fail') {
        errorDesc = uni.$t('error.network');
      } else if (errMsg === 'request:fail timeout') {
        errorDesc = uni.$t('error.timeout');
      }
      const error = {
        errorType: 'other',
        errorData: response,
        errorDesc
      };
      // 如果hideErrorToast不设置或者为false，使用toast提示
      if (!hideErrorToast) {
        uni.$u.toast(error.errorDesc);
      }
      return Promise.reject(error);
    }
  );
};

// 初始化请求配置
const initRequest = (vm) => {
  uni.$u.http.setConfig((defaultConfig) => {
    /* defaultConfig 为默认全局配置 */
    // 根域名
    defaultConfig.baseURL = CONFIG.baseURL;
    // 延迟时间
    defaultConfig.timeout = 20000;
    return defaultConfig;
  });
  requestInterceptors(vm);
  responseInterceptors(vm);
};

export { initRequest };
