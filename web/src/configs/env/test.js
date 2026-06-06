const config = {
  series: 'default',

  theme: 'default',

  treasureHouse: [],

  // 兜底
  baseURLs: 'https://front-service.artisantt.com',

  baseURL: 'https://front-service.artisantt.com/api',

  // 维护服务（独立域名）
  maintainURL: 'https://maintain.artisantt.com',

  // gp-verification 服务（文字点选验证码，独立域名）
  verificationURL: 'https://verification.artisantt.com',

  base64Key: 'EKOLQBmkFARZj1m9iGZQYg==',

  // WebSocket配置
  wsURLs: {
    us: 'wss://ws-api2.stocktv.top/connect',
    mexico: 'wss://ws-api-mx.stocktv.top/connect',
    peru: 'wss://ws-api2.stocktv.top/connect'
  },
  wsKeys: {
    us: 'USfc355de5ae4a4cfb861824b154c465ec',
    mexico: 'MX0e56ceb8c3854a2ba5403b9506a59006',
    peru: 'PE04c3aa7f5d214265a193399bec656b0d'
  },
  wsCountryIds: {
    us: '5',
    mexico: '7',
    peru: '125'
  }
};

export default config;
