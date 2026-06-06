const setting = {
  name: 'default',

  /** 维护接口 productCode */
  productCode: 'ART',

  showName: 'ARTISAN PARTNERS',

  theme: '',

  // 离线页
  offlinePage: {
    logoSrc: 'static/images/series/default/logo/logo.png',
    skin: 'aurora'
  },

  // 底部导航
  tabbar: {
    list: [
      {
        name: () => uni.$t('nav.home'),
        route: '/pages/home/index',
        key: 'home',
        icon: 'tab-nav-home'
      },
      {
        name: () => uni.$t('nav.market'),
        route: '/pages/market/index',
        key: 'market',
        icon: 'tab-nav-market'
      },
      {
        name: () => uni.$t('nav.orders'),
        route: '/pages/orders/index',
        key: 'orders',
        icon: 'tab-nav-orders'
      },
      {
        name: () => uni.$t('nav.mine'),
        route: '/pages/mine/index',
        key: 'mine',
        icon: 'tab-nav-mine'
      }
    ]
  }
};

export default setting;
