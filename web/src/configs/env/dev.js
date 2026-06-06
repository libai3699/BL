const config = {
  series: 'default',

  theme: 'default',

  treasureHouse: [],

  // 兜底
  baseURLs: 'http://localhost:13004',

  // 本地后端 gp-front-service 端口 13004
  baseURL: 'http://localhost:13004/api',

  // 维护服务（独立域名）
  maintainURL: 'https://maintain.artisantt.com',

  // 本地后端 gp-verification 端口 13012
  verificationURL: 'http://localhost:13012',

  base64Key: 'EKOLQBmkFARZj1m9iGZQYg==',

  // WebSocket配置
  // https://documenter.getpostman.com/view/10940044/2sAYHxnPns#f49e8f21-81ed-45a9-9ac7-711d20238622   对接文档
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
