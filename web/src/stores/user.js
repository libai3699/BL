import { defineStore } from 'pinia';

import { reqUserGetUserInfo } from '@/apis/user';
import { getUniStorage } from '@/utils/uniUtils';

export const useUserStore = defineStore({
  id: 'user',
  state: () => ({
    // 用户令牌
    token: {
      isLoading: false,
      isError: false,
      errorText: '',
      errorType: '',
      value: ''
    },
    // 用户信息
    userInfo: {
      isLoading: false,
      isError: false,
      errorText: '',
      errorType: '',
      value: null
    },
    // 需要更新用户信息 - 适用于某些操作使此值变化后，更新用户信息的情况
    needGetUserInfo: true,
    // 前端保存的用户名密码
    savedAccount: {
      open: true,
      value: {}
    }
  }),
  // 持久化
  persist: {
    enabled: true,
    strategies: [
      {
        storage: getUniStorage(),
        paths: ['token', 'userInfo', 'savedAccount']
      }
    ]
  },
  getters: {
    // 是否登录
    isLogin(state) {
      return !!state.token.value;
    },
    // 是否获取登录和用户信息接口完成
    isGetInfoFinish(state) {
      return !state.token.isLoading && !state.userInfo.isLoading;
    }
  },
  actions: {
    // 设置方法
    // 设置正在读取token
    setTokenLoading() {
      this.token.isLoading = true;
      this.token.isError = false;
      this.token.errorText = '';
      this.token.errorType = '';
      this.token.value = '';
    },
    // 设置token读取完成
    setTokenFinish(value) {
      this.token.isLoading = false;
      this.token.isError = false;
      this.token.errorText = '';
      this.token.errorType = '';
      this.token.value = value;
    },
    // 设置token读取失败
    setTokenError(err) {
      this.token.isLoading = false;
      this.token.isError = true;
      this.token.errorText = err.errorText;
      this.token.errorType = err.errorType;
      this.token.value = '';
    },
    // 设置token初始化
    setTokenInitial() {
      this.token.isLoading = false;
      this.token.isError = false;
      this.token.errorText = '';
      this.token.errorType = '';
      this.token.value = '';
    },
    // 设置正在读取用户信息
    setUserInfoLoading() {
      this.userInfo.isLoading = true;
      this.userInfo.isError = false;
      this.userInfo.errorText = '';
      this.userInfo.errorType = '';
      this.userInfo.value = null;
    },
    // 设置用户信息读取完成
    setUserInfoFinish(value) {
      this.userInfo.isLoading = false;
      this.userInfo.isError = false;
      this.userInfo.errorText = '';
      this.userInfo.errorType = '';
      this.userInfo.value = value;
    },
    // 设置用户信息读取失败
    setUserInfoError(err) {
      this.userInfo.isLoading = false;
      this.userInfo.isError = true;
      this.userInfo.errorText = err.errorText;
      this.userInfo.errorType = err.errorType;
      this.userInfo.value = null;
    },
    // 设置用户信息初始化
    setUserInfoInitial() {
      this.userInfo.isLoading = false;
      this.userInfo.isError = false;
      this.userInfo.errorText = '';
      this.userInfo.errorType = '';
      this.userInfo.value = null;
    },
    // 获取用户信息 needLoading 代表是否需要等待加载状态
    getUserInfo(needLoading = false) {
      if (needLoading) {
        return new Promise((resolve, reject) => {
          this.setUserInfoLoading();
          reqUserGetUserInfo()
            .then((res) => {
              this.setUserInfoFinish(res);
              resolve(res);
            })
            .catch((e) => {
              this.setUserInfoError(e);
              reject(e);
            });
        });
      } else {
        // 不需要等待，后台静默执行
        return new Promise((resolve, reject) => {
          reqUserGetUserInfo()
            .then((res) => {
              this.setUserInfoFinish(res);
              resolve(res);
            })
            .catch((e) => {
              reject(e);
            });
        });
      }
    },
    // 退出登录
    logout() {
      this.setTokenInitial();
      this.setUserInfoInitial();
    }
  }
});
