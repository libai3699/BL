const config = {
  /** 正式环境通过 nginx 反向代理；本地调试正式环境时直连 */
  baseURL:
    process.env.IS_LOCAL_DEBUGGER === "true"
      ? "https://www.k1hdgy.com/api"
      : "/fuckapi"
};

export default config;
