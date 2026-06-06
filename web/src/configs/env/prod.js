const config = {
  series: 'default',

  theme: 'default',

  treasureHouse: [
    'https://mmo.uittoy.com/bcb-prod/acator_4001.jpg',
    'https://cdn.hgovm.com/bcb-prod/acator_4001.jpg',
    'https://cdn.imtest2.com//bcb-prod/acator_4001.jpg',
    'https://mmo.poarb.com/bcb-prod/acator_4001.jpg'
  ],

  // 兜底
  baseURLs: 'https://front-service.artisantt.com',

  baseURL:
    process.env.IS_LOCAL_DEBUGGER || process.env.IS_APP
      ? 'https://front-service.artisantt.com/api'
      : '/fuckapi',

  // gp-verification 服务（文字点选验证码，独立域名）
  verificationURL: 'https://verification.artisantt.com',

  // 维护服务（独立域名）
  maintainURL: 'https://maintain.artisantt.com',

  base64Key: 'vby23b00wF04OK4O6K4sYQ==',

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
