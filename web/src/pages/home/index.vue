<script setup>
import { ref, computed, onUnmounted, onMounted } from 'vue';
import { onShow, onHide } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack } from '@/utils/navigate';
import {
  getBannerByPlat,
  getKeFu,
  getHotStockList,
  getNewsList,
  getPopup,
  getMarketStatus
} from '@/apis/home';
import { ensureWebSocketConnected } from '@/utils/websocket';
import StockItem from './components/stock-item.vue';
import { goService } from '@/utils/biz';
import { watch } from 'vue';
import { getNoticeSourceStyle, noticeCatInlineStyle } from '@/utils/notice-source';

const { t, locale } = useI18n();
const commonStore = useCommonStore();
const userStore = useUserStore();

// 判断是否为英语
const isEnglish = computed(() => locale.value === 'en' || locale.value === 'en-US');

// 轮播图数据
const bannerList = ref([]);
// 客服链接
const kefuUrl = ref('');
// 当前股票类型 tab
const currentStockTab = ref('peru'); // peru / us / mexico（默认与 tab 显示顺序一致）
// 滚动视图的滚动位置
const scrollLeft = ref(0);
const scrollViewId = 'stock-tabs-scroll';
// 墨西哥股票列表
const mexicoStockList = ref([]);
const mexicoStockPage = ref(1);
const mexicoLoading = ref(false);
// 美国股票列表
const usStockList = ref([]);
const usStockPage = ref(1);
const usLoading = ref(false);
// 秘鲁股票列表
const peruStockList = ref([]);
const peruStockPage = ref(1);
const peruLoading = ref(false);
// 新闻列表
const newsList = ref([]);
const newsPage = ref(1);
// 弹窗图片
const showPopupImage = ref(false);
const popupImageUrl = ref('');

// 快捷入口配置（对齐原型 quick-grid，图标见 static/svgs/stock-*.svg）
const quickEntries = ref([
  {
    icon: 'stock-block-trade',
    name: 'home.dazong',
    path: '/pages/bulk-trading/index',
    premium: false
  },
  {
    icon: 'stock-treasury',
    name: 'home.treasury',
    path: '/pages/issuance/additional/index?type=1',
    premium: false
  },
  {
    icon: 'stock-ipo',
    name: 'home.ipo',
    path: '/pages/ipo/index',
    badge: 'HOT',
    premium: false
  },
  {
    icon: 'stock-trend-up',
    name: 'home.FPO',
    path: '/pages/issuance/additional/index?type=2',
    premium: false
  },
  {
    icon: 'stock-crown',
    name: 'home.vip',
    path: '/pages/mine/vip/index',
    premium: true
  },
  {
    icon: 'stock-vip-loan',
    name: 'home.vipLoan',
    path: '/pages/vip-loan/index',
    premium: true
  },
  {
    icon: 'stock-card',
    name: 'home.creditLoan',
    path: '/pages/credit-loan/index',
    premium: false
  },
  {
    icon: 'stock-headphone',
    name: 'home.kefu',
    path: '',
    isKefu: true,
    premium: false
  }
]);

// 市场状态：后端返回 { key, isOpen, nextEventTime, nextEventType, pct }
// 显示数据由 computed marketStatus 基于 nowTick 实时计算（label/status/pct/dir）
const marketStatusRaw = ref([]);
// 每 30 秒刷新一次"now"用于倒计时显示
const nowTick = ref(Date.now());
let nowTickTimer = null;

const padNum = (n) => (n < 10 ? `0${n}` : `${n}`);
const formatDuration = (ms) => {
  if (!ms || ms <= 0) return '--';
  const totalSec = Math.floor(ms / 1000);
  const h = Math.floor(totalSec / 3600);
  const m = Math.floor((totalSec % 3600) / 60);
  if (h >= 24) {
    const d = Math.floor(h / 24);
    return `${d}d ${h % 24}h`;
  }
  return `${h}h ${padNum(m)}m`;
};

const marketStatus = computed(() => {
  return marketStatusRaw.value.map((m) => {
    const pctNum = Number(m.pct) || 0;
    const pctStr = `${pctNum >= 0 ? '+' : ''}${pctNum.toFixed(2)}%`;
    const dir = pctNum >= 0 ? 'up' : 'down';
    const label = t(`home.marketLabel.${m.key}`) || (m.key || '').toUpperCase();
    const stateKey = m.isOpen ? 'open' : 'closed';
    const tilKey = m.isOpen ? 'tilClose' : 'tilOpen';
    let status = t(`home.marketState.${stateKey}`);
    if (m.nextEventTime) {
      const remain = m.nextEventTime - nowTick.value;
      if (remain > 0) {
        status += ` · ${t(`home.marketState.${tilKey}`)} ${formatDuration(remain)}`;
      }
    }
    return { key: m.key, label, open: !!m.isOpen, status, pct: pctStr, dir };
  });
});

const fetchMarketStatus = async () => {
  try {
    const res = await getMarketStatus();
    if (Array.isArray(res)) marketStatusRaw.value = res;
  } catch (e) {
    // 接口失败保持空列表，u-ms-list 占位即可
  }
};

const noticeIndex = ref(0);
const msIndex = ref(0);
let noticeTimer = null;
let msTimer = null;

// 公告悬浮弹窗（对齐原型 .nm-sheet，从底部弹出，不跳页）
const showNoticeModal = ref(false);
const currentNotice = ref(null);

const currentStockList = computed(() => {
  if (currentStockTab.value === 'mexico') return mexicoStockList.value;
  if (currentStockTab.value === 'peru') return peruStockList.value;
  return usStockList.value;
});

const startNoticeRoll = () => {
  if (noticeTimer) clearInterval(noticeTimer);
  if (!newsList.value.length) return;
  noticeTimer = setInterval(() => {
    noticeIndex.value = (noticeIndex.value + 1) % newsList.value.length;
  }, 3500);
};

const startMsRoll = () => {
  if (msTimer) clearInterval(msTimer);
  if (marketStatus.value.length <= 1) return;
  msTimer = setInterval(() => {
    msIndex.value = (msIndex.value + 1) % marketStatus.value.length;
  }, 5000);
};

const onNoticeTap = () => {
  const item = newsList.value[noticeIndex.value];
  if (!item) return;
  currentNotice.value = item;
  showNoticeModal.value = true;
  // 弹窗期间暂停自动滚动（对齐原型 openNoticeModal 内 clearInterval）
  if (noticeTimer) {
    clearInterval(noticeTimer);
    noticeTimer = null;
  }
};

const closeNoticeModal = () => {
  showNoticeModal.value = false;
  currentNotice.value = null;
  // 恢复滚动
  startNoticeRoll();
};

const currentNoticeStyle = computed(() =>
  getNoticeSourceStyle(currentNotice.value?.sourceName)
);

const handleNoticeAllMessages = () => {
  closeNoticeModal();
  handleGoMessage();
};

onMounted(() => {
  startNoticeRoll();
  startMsRoll();
});

const unreadCount = computed(() => commonStore.unreadCount);

const isApp = process.env.IS_APP;

// 监控语言发生变化
watch(
  () => commonStore.locale,
  (newV) => {
    fetchBanner();
  }
);

// 获取轮播图
const fetchBanner = async () => {
  const params = {
    platType: 'm'
  };
  const res = await getBannerByPlat(params);
  if (res && Array.isArray(res)) {
    bannerList.value = res;
  }
};

// 获取客服链接
const fetchKeFu = async () => {
  const res = await getKeFu();
  if (res) {
    kefuUrl.value = res.customerLink;
  }
};

// 获取墨西哥股票列表
const fetchMexicoStockList = async (loadMore = false) => {
  if (mexicoLoading.value) return;

  mexicoLoading.value = true;
  const pageNum = loadMore ? mexicoStockPage.value + 1 : 1;

  // 获取当前日期和5天前的日期
  const endDate = new Date();
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - 5);

  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const res = await getHotStockList({
    endTime: formatDate(endDate),
    pageNum,
    pageSize: 10,
    startTime: formatDate(startDate),
    stockType: 'mxg'
  });

  if (res && res.length !== 0) {
    if (loadMore) {
      mexicoStockList.value = [...mexicoStockList.value, ...res];
      mexicoStockPage.value = pageNum;
    } else {
      mexicoStockList.value = res;
      mexicoStockPage.value = 1;
    }
  }
  mexicoLoading.value = false;
};

// 获取美国股票列表
const fetchUsStockList = async (loadMore = false) => {
  if (usLoading.value) return;

  usLoading.value = true;
  const pageNum = loadMore ? usStockPage.value + 1 : 1;

  // 获取当前日期和5天前的日期
  const endDate = new Date();
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - 5);

  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const res = await getHotStockList({
    endTime: formatDate(endDate),
    pageNum,
    pageSize: 10,
    startTime: formatDate(startDate),
    stockType: 'us'
  });
  if (res && res.length !== 0) {
    if (loadMore) {
      usStockList.value = [...usStockList.value, ...res];
      usStockPage.value = pageNum;
    } else {
      usStockList.value = res;
      usStockPage.value = 1;
    }
  }
  usLoading.value = false;
};

// 获取秘鲁股票列表
const fetchPeruStockList = async (loadMore = false) => {
  if (peruLoading.value) return;

  peruLoading.value = true;
  const pageNum = loadMore ? peruStockPage.value + 1 : 1;

  const endDate = new Date();
  const startDate = new Date();
  startDate.setDate(startDate.getDate() - 5);

  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const res = await getHotStockList({
    endTime: formatDate(endDate),
    pageNum,
    pageSize: 10,
    startTime: formatDate(startDate),
    stockType: 'pe'
  });
  if (res && res.length !== 0) {
    if (loadMore) {
      peruStockList.value = [...peruStockList.value, ...res];
      peruStockPage.value = pageNum;
    } else {
      peruStockList.value = res;
      peruStockPage.value = 1;
    }
  }
  peruLoading.value = false;
};

// 切换股票类型并加载数据
const switchStockTab = (type) => {
  if (currentStockTab.value === type) return;
  currentStockTab.value = type;

  // 切换时按需加载对应的股票数据（懒加载，避免一上来 3 个市场都打）
  if (type === 'us' && usStockList.value.length === 0) {
    fetchUsStockList();
  }
  if (type === 'peru' && peruStockList.value.length === 0) {
    fetchPeruStockList();
  }
  if (type === 'mexico' && mexicoStockList.value.length === 0) {
    fetchMexicoStockList();
  }

  // 切换时重新监听对应的WebSocket
  listenCurrentTab();
};

// 左箭头点击 - 滚动到最左边
const handleScrollLeft = () => {
  scrollLeft.value = 0;
};

// 右箭头点击 - 滚动到最右边
const handleScrollRight = () => {
  scrollLeft.value = 9999; // 设置一个足够大的值，让它滚动到最右边
};

// 获取新闻列表
const fetchNewsList = async (loadMore = false) => {
  const pageNum = loadMore ? newsPage.value + 1 : 1;
  const res = await getNewsList({
    pageNum,
    pageSize: 10,
    type: 0,
    sort: 'time1'
  });
  if (res && res.list) {
    if (loadMore) {
      newsList.value = [...newsList.value, ...res.list];
      newsPage.value = pageNum;
    } else {
      newsList.value = res.list;
      newsPage.value = 1;
    }
    startNoticeRoll();
  }
};

// 快捷入口点击
const handleQuickEntry = (item) => {
  if (item.isKefu && kefuUrl.value) {
    // 跳转客服
    goService(kefuUrl.value);
  } else if (item.path) {
    uni.navigateTo({
      url: item.path
    });
  }
};

// 股票项点击
const handleStockClick = (item) => {
  // 只传gid到股票详情页
  uni.navigateTo({
    url: `/pages/stock-detail/index?gid=${item.gid}`
  });
};

// 新闻项点击
const handleNewsClick = (item) => {
  // 保存新闻数据到本地存储
  uni.setStorageSync('currentNews', JSON.stringify(item));
  // 跳转新闻详情
  uni.navigateTo({
    url: `/pages/news/detail?id=${item.id}`
  });
};

// 查看更多股票
const handleMoreStock = () => {
  // 跳转到市场页面
  uni.switchTab({
    url: '/pages/market/index'
  });
};

// 点击搜索框跳转到搜索页
const handleSearchClick = () => {
  uni.navigateTo({
    url: '/pages/search/index'
  });
};

// 查看更多新闻
const handleMoreNews = () => {
  fetchNewsList(true);
};

// 跳转消息页面
const handleGoMessage = () => {
  uni.navigateTo({
    url: '/pages/message/index'
  });
};

// 获取弹窗信息
const fetchPopup = async () => {
  const res = await getPopup();
  console.log('弹窗图接口返回数据:', res);
  if (res && res.popupImg) {
    popupImageUrl.value = res.popupImg;
    showPopupImage.value = true;
  }
};

// 关闭弹窗
const closePopup = () => {
  showPopupImage.value = false;
};

// 页面初始化回调
const onPageInit = async () => {
  // 默认加载秘鲁股票（与 tab 顺序一致），切换时再懒加载美/墨
  await Promise.all([
    fetchBanner(),
    fetchKeFu(),
    fetchPeruStockList(),
    /*     fetchNewsList(), */
    fetchPopup(),
    fetchMarketStatus()
  ]);
  // 拉到大盘数据后启动轮播 + 倒计时秒针
  startMsRoll();
  if (!nowTickTimer) {
    nowTickTimer = setInterval(() => {
      nowTick.value = Date.now();
    }, 30000);
  }
};

const handleMexicoStockMessage = (data) => {
  const stockId = data.pid || data.gid;
  if (!stockId) return;

  const index = mexicoStockList.value.findIndex((item) => item.code === stockId);
  if (index !== -1) {
    const stock = mexicoStockList.value[index];
    const newPrice = data.last_numeric || stock.nowPrice;
    const newRate = data.pcp ? parseFloat(data.pcp) : stock.hcrate;
    if (newPrice !== stock.nowPrice || newRate !== stock.hcrate) {
      mexicoStockList.value[index] = {
        ...stock,
        nowPrice: newPrice,
        hcrate: newRate
      };
    }
  }
};

const handlePeruStockMessage = (data) => {
  const stockId = data.pid || data.gid;
  if (!stockId) return;

  const index = peruStockList.value.findIndex((item) => item.code === stockId);
  if (index !== -1) {
    const stock = peruStockList.value[index];
    const newPrice = data.last_numeric || stock.nowPrice;
    const newRate = data.pcp ? parseFloat(data.pcp) : stock.hcrate;
    if (newPrice !== stock.nowPrice || newRate !== stock.hcrate) {
      peruStockList.value[index] = {
        ...stock,
        nowPrice: newPrice,
        hcrate: newRate
      };
    }
  }
};

const handleUsStockMessage = (data) => {
  const stockId = data.pid || data.gid;
  if (!stockId) return;

  const index = usStockList.value.findIndex((item) => item.code === stockId);
  if (index !== -1) {
    const stock = usStockList.value[index];
    const newPrice = data.last_numeric || stock.nowPrice;
    const newRate = data.pcp ? parseFloat(data.pcp) : stock.hcrate;
    if (newPrice !== stock.nowPrice || newRate !== stock.hcrate) {
      usStockList.value[index] = {
        ...stock,
        nowPrice: newPrice,
        hcrate: newRate
      };
    }
  }
};

const listenCurrentTab = () => {
  uni.$off('ws:mexico', handleMexicoStockMessage);
  uni.$off('ws:us', handleUsStockMessage);
  uni.$off('ws:peru', handlePeruStockMessage);

  if (currentStockTab.value === 'mexico') {
    uni.$on('ws:mexico', handleMexicoStockMessage);
  } else if (currentStockTab.value === 'peru') {
    uni.$on('ws:peru', handlePeruStockMessage);
  } else {
    uni.$on('ws:us', handleUsStockMessage);
  }
};

onShow(() => {
  commonStore.fnBack = goBack;

  // 确保 WebSocket 已连接
  ensureWebSocketConnected(userStore, 300);

  listenCurrentTab();
  fetchNewsList();
});

// 页面隐藏时移除监听
onHide(() => {
  uni.$off('ws:mexico', handleMexicoStockMessage);
  uni.$off('ws:us', handleUsStockMessage);
  uni.$off('ws:peru', handlePeruStockMessage);
});

// 组件卸载时也清理
onUnmounted(() => {
  uni.$off('ws:mexico', handleMexicoStockMessage);
  uni.$off('ws:us', handleUsStockMessage);
  uni.$off('ws:peru', handlePeruStockMessage);
  if (noticeTimer) clearInterval(noticeTimer);
  if (msTimer) clearInterval(msTimer);
  if (nowTickTimer) {
    clearInterval(nowTickTimer);
    nowTickTimer = null;
  }
});
</script>

<template>
  <page-wrapper @onPageInit="onPageInit">
    <view class="p-stock-home m-content">
      <!-- Hero 顶栏 + 轮播 -->
      <view class="m-hero">
        <view class="u-top">
          <image
            class="u-logo-img"
            src="/static/images/series/default/logo/logo.png"
            mode="aspectFit"
          />
          <line-switching v-if="isApp" class="u-line-switch" />
          <view class="u-search" @click="handleSearchClick">
            <svg-icon name="search" :size="28" color="rgba(255,255,255,0.75)" />
            <text>{{ $t('common.search') }}</text>
          </view>
          <view class="u-bell" @click="handleGoMessage">
            <svg-icon name="home_notify" :size="36" color="#fff" />
            <view v-if="unreadCount > 0" class="u-dot"></view>
          </view>
        </view>

        <view class="m-banner" v-if="bannerList.length > 0">
          <swiper class="u-swiper" circular autoplay :interval="4000">
            <swiper-item v-for="(item, index) in bannerList" :key="index">
              <image class="u-banner-img" :src="item.bannerUrl" mode="aspectFill" />
            </swiper-item>
          </swiper>
        </view>
      </view>

      <!-- 市场状态（5 秒垂直轮播一条） -->
      <view class="m-market-status">
        <view class="u-ms-list">
          <view
            v-for="(m, idx) in marketStatus"
            :key="m.key"
            class="u-ms-item"
            :style="{
              transform: `translateY(${(idx - msIndex) * 100}%)`,
              opacity: idx === msIndex ? 1 : 0
            }"
          >
            <view class="u-ms-dot" :class="{ closed: !m.open }"></view>
            <text class="u-ms-text">
              <text class="u-ms-bold">{{ m.label }}</text> {{ m.status }}
            </text>
            <text class="u-ms-pct" :class="m.dir">{{ m.pct }}</text>
          </view>
        </view>
      </view>

      <!-- 公告滚动 -->
      <view class="m-notice-ticker" v-if="newsList.length" @click="onNoticeTap">
        <view class="u-tag"
          ><text class="u-tag-icon">📢</text>{{ $t('home.announcement') }}</view
        >
        <view class="u-list">
          <view
            v-for="(n, idx) in newsList"
            :key="n.id"
            class="u-item"
            :style="{
              transform: `translateY(${(idx - noticeIndex) * 64}rpx)`,
              opacity: idx === noticeIndex ? 1 : 0
            }"
          >
            <text
              v-if="n.sourceName"
              class="u-cat"
              :style="noticeCatInlineStyle(n.sourceName)"
              >{{ n.sourceName }}</text
            >
            <text class="u-text">{{ n.title || n.name }}</text>
          </view>
        </view>
        <text class="u-arrow">›</text>
      </view>

      <!-- 快捷入口 -->
      <view class="m-quick-grid">
        <view
          v-for="(item, index) in quickEntries"
          :key="index"
          class="u-quick-item"
          :class="{ premium: item.premium }"
          @click="handleQuickEntry(item)"
        >
          <text v-if="item.badge" class="u-badge">{{ item.badge }}</text>
          <view class="u-quick-ico">
            <svg-icon
              :name="item.icon"
              :size="44"
              :color="item.premium ? '#8B6914' : '#0B1E47'"
            />
          </view>
          <text class="u-quick-name" :class="{ 's-en': isEnglish }">{{
            $t(item.name)
          }}</text>
        </view>
      </view>

      <!-- 热门股票 -->
      <view class="m-section-title">
        <text class="u-title">{{ $t('home.hotStocks') }}</text>
        <text class="u-more" @click="handleMoreStock">{{ $t('home.viewAll') }} →</text>
      </view>

      <view class="m-tab-pills">
        <view
          class="u-pill"
          :class="{ active: currentStockTab === 'peru' }"
          @click="switchStockTab('peru')"
        >
          {{ $t('home.peruStock') }}
        </view>
        <view
          class="u-pill"
          :class="{ active: currentStockTab === 'us' }"
          @click="switchStockTab('us')"
        >
          {{ $t('home.usStock') }}
        </view>
        <view
          class="u-pill"
          :class="{ active: currentStockTab === 'mexico' }"
          @click="switchStockTab('mexico')"
        >
          {{ $t('home.mexicoStock') }}
        </view>
        <text class="u-pill-more" @click="handleMoreStock">›</text>
      </view>

      <view class="m-stock-list">
        <view
          v-if="
            (currentStockTab === 'mexico' && mexicoLoading) ||
            (currentStockTab === 'us' && usLoading) ||
            (currentStockTab === 'peru' && peruLoading)
          "
          class="stock-loading"
        >
          <u-loading-icon mode="circle" size="40"></u-loading-icon>
        </view>
        <template v-else>
          <stock-item
            v-for="item in currentStockList"
            :key="item.gid"
            :item="item"
            :stock-type="currentStockTab"
            @click="handleStockClick(item)"
          />
        </template>
      </view>

      <tab-bar :page="$page" />
    </view>

    <u-popup
      :show="showPopupImage"
      v-if="showPopupImage"
      mode="center"
      :round="16"
      @close="closePopup"
      :customStyle="{ background: 'transparent' }"
    >
      <view class="popup-image-container" @click="closePopup">
        <image class="popup-image" :src="popupImageUrl" mode="widthFix" />
      </view>
    </u-popup>

    <!-- 公告详情弹窗（点击公告条触发，对齐原型 .nm-sheet） -->
    <u-popup
      :show="showNoticeModal"
      v-if="showNoticeModal"
      mode="bottom"
      :round="22"
      @close="closeNoticeModal"
    >
      <view class="m-notice-sheet">
        <view class="u-nm-handle"></view>
        <view class="u-nm-top">
          <view class="u-nm-tag-row">
            <text
              v-if="currentNotice?.sourceName"
              class="u-nm-cat"
              :style="noticeCatInlineStyle(currentNotice.sourceName)"
              >{{ currentNotice.sourceName }}</text
            >
            <text class="u-nm-time" v-if="currentNotice?.addTime">{{
              currentNotice.addTime
            }}</text>
          </view>
          <view class="u-nm-close" @click="closeNoticeModal">
            <text>×</text>
          </view>
        </view>
        <view class="u-nm-title">{{ currentNotice?.title }}</view>
        <view class="u-nm-body" v-if="currentNotice?.content">
          <rich-text :nodes="currentNotice.content"></rich-text>
        </view>
        <view class="u-nm-cta-row">
          <view class="u-nm-btn outline" @click="handleNoticeAllMessages">{{
            $t('home.noticeAllMessages')
          }}</view>
          <view
            class="u-nm-btn"
            :class="currentNoticeStyle.closeBtn"
            @click="closeNoticeModal"
            >{{ $t('common.close') }}</view
          >
        </view>
      </view>
    </u-popup>
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-content {
  @include hasBottomNav();
}

.p-stock-home {
  min-height: 100vh;
  background: var(--stock-page-bg, #e9edf5);
  padding-bottom: 200rpx;

  .m-hero {
    position: relative;
    background: linear-gradient(160deg, #0b1e47 0%, #13306e 55%, #1e407f 100%);
    color: #fff;
    padding: 24rpx 32rpx 44rpx;
    border-radius: 0 0 48rpx 48rpx;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      width: 600rpx;
      height: 600rpx;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(212, 162, 76, 0.18) 0%, transparent 70%);
      top: -200rpx;
      right: -160rpx;
    }

    .u-top {
      position: relative;
      z-index: 2;
      display: flex;
      align-items: center;
      gap: 16rpx;

      .u-logo-img {
        width: 68rpx;
        height: 68rpx;
        border-radius: 20rpx;
        flex-shrink: 0;
      }

      .u-search {
        flex: 1;
        background: rgba(255, 255, 255, 0.14);
        border: 2rpx solid rgba(255, 255, 255, 0.18);
        border-radius: 44rpx;
        height: 76rpx;
        padding: 0 28rpx;
        display: flex;
        align-items: center;
        gap: 16rpx;
        color: rgba(255, 255, 255, 0.75);
        font-size: 26rpx;
      }

      .u-bell {
        width: 72rpx;
        height: 72rpx;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.1);
        display: flex;
        align-items: center;
        justify-content: center;
        position: relative;
        flex-shrink: 0;

        .u-dot {
          position: absolute;
          top: 14rpx;
          right: 14rpx;
          width: 12rpx;
          height: 12rpx;
          border-radius: 50%;
          background: var(--stock-gold, #d4a24c);
        }
      }
    }

    .m-banner {
      position: relative;
      z-index: 2;
      margin-top: 28rpx;
      border-radius: 36rpx;
      overflow: hidden;
      box-shadow: 0 20rpx 56rpx rgba(11, 30, 71, 0.22);

      .u-swiper {
        height: 320rpx;
      }

      .u-banner-img {
        width: 100%;
        height: 100%;
      }
    }
  }

  .m-market-status {
    margin: 28rpx 24rpx 0;
    padding: 20rpx 28rpx;
    background: #fff;
    border-radius: 24rpx;
    box-shadow:
      0 8rpx 28rpx rgba(15, 30, 71, 0.07),
      0 2rpx 6rpx rgba(15, 30, 71, 0.04);
    height: 68rpx;
    overflow: hidden;
    box-sizing: border-box;

    .u-ms-list {
      position: relative;
      height: 100%;
      mask-image: linear-gradient(
        to bottom,
        transparent 0,
        #000 15%,
        #000 85%,
        transparent 100%
      );
    }

    .u-ms-item {
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      height: 100%;
      display: flex;
      align-items: center;
      gap: 16rpx;
      font-size: 23rpx;
      color: var(--stock-ink-2, #475569);
      font-weight: 500;
      transition:
        transform 0.55s cubic-bezier(0.4, 0, 0.2, 1),
        opacity 0.35s;
    }

    .u-ms-text {
      flex: 1;
      line-height: 1.4;
      @include ellipsis;
    }

    .u-ms-bold {
      color: var(--stock-ink, #0f172a);
      font-weight: 700;
      letter-spacing: 0.4rpx;
    }

    .u-ms-dot {
      width: 14rpx;
      height: 14rpx;
      border-radius: 50%;
      background: var(--stock-up, #10b981);
      box-shadow: 0 0 0 6rpx rgba(16, 185, 129, 0.22);
      position: relative;
      flex-shrink: 0;

      &::after {
        content: '';
        position: absolute;
        inset: -6rpx;
        border-radius: 50%;
        background: var(--stock-up, #10b981);
        opacity: 0.35;
        animation: ms-pulse 2.2s ease-in-out infinite;
      }

      &.closed {
        background: #94a3b8;
        box-shadow: none;

        &::after {
          display: none;
        }
      }
    }

    .u-ms-pct {
      margin-left: 4rpx;
      padding: 2rpx 12rpx;
      border-radius: 8rpx;
      font-size: 20rpx;
      font-weight: 700;
      line-height: 1.4;
      flex-shrink: 0;

      &.up {
        color: var(--stock-up, #10b981);
        background: var(--stock-up-bg, #e6f8f1);
      }

      &.down {
        color: var(--stock-down, #ef4444);
        background: var(--stock-down-bg, #fdecec);
      }
    }

    .u-ms-divider {
      width: 2rpx;
      height: 28rpx;
      background: var(--stock-line, #e5e9f2);
      flex-shrink: 0;
    }
  }

  @keyframes ms-pulse {
    0% {
      transform: scale(0.6);
      opacity: 0.5;
    }
    100% {
      transform: scale(1.8);
      opacity: 0;
    }
  }

  .m-notice-ticker {
    margin: 28rpx 24rpx 0;
    background: #fff;
    border-radius: 28rpx;
    box-shadow: 0 8rpx 28rpx rgba(15, 30, 71, 0.07);
    display: flex;
    align-items: center;
    height: 92rpx;
    padding: 0 24rpx;
    gap: 20rpx;

    .u-tag {
      display: flex;
      align-items: center;
      gap: 8rpx;
      padding: 8rpx 20rpx;
      background: linear-gradient(135deg, #f59e0b, #ef4444);
      color: #fff;
      font-size: 21rpx;
      font-weight: 800;
      border-radius: 12rpx;
      flex-shrink: 0;
      letter-spacing: 0.6rpx;

      .u-tag-icon {
        font-size: 22rpx;
      }
    }

    .u-list {
      flex: 1;
      height: 64rpx;
      overflow: hidden;
      position: relative;
    }

    .u-item {
      position: absolute;
      left: 0;
      right: 0;
      height: 64rpx;
      display: flex;
      align-items: center;
      gap: 12rpx;
      transition:
        transform 0.55s cubic-bezier(0.4, 0, 0.2, 1),
        opacity 0.35s;

      .u-cat {
        padding: 2rpx 14rpx;
        font-size: 20rpx;
        border-radius: 8rpx;
        font-weight: 700;
        flex-shrink: 0;
        letter-spacing: 0.6rpx;
        line-height: 1.4;
      }

      .u-text {
        flex: 1;
        min-width: 0;
        font-size: 25rpx;
        color: var(--stock-ink, #0f172a);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-weight: 500;
      }
    }

    .u-arrow {
      color: var(--stock-ink-3, #64748b);
      font-size: 36rpx;
    }
  }

  .m-quick-grid {
    @include stock-card;
    margin: 28rpx 24rpx 0;
    padding: 36rpx 16rpx 28rpx;
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 8rpx 0;

    .u-quick-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 14rpx;
      padding: 16rpx 8rpx;
      font-size: 23rpx;
      color: var(--stock-ink, #0f172a);
      font-weight: 500;
      position: relative;

      .u-badge {
        position: absolute;
        top: 4rpx;
        right: 28rpx;
        background: linear-gradient(135deg, #f59e0b, #ef4444);
        color: #fff;
        font-size: 18rpx;
        font-weight: 700;
        padding: 2rpx 10rpx;
        border-radius: 12rpx;
      }

      .u-quick-ico {
        @include stock-soft-icon-box(96rpx, 28rpx);
        width: 96rpx;
        height: 96rpx;
        color: var(--stock-navy, #0b1e47);
      }

      &.premium .u-quick-ico {
        background: linear-gradient(135deg, #fcf1d6 0%, #f1e0bd 100%);
        border-color: #ead89a;
      }

      .u-quick-name.s-en {
        white-space: pre-line;
        text-align: center;
        line-height: 1.3;
      }
    }
  }

  .m-section-title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin: 36rpx 32rpx 20rpx;

    .u-title {
      font-size: 32rpx;
      font-weight: 700;
      color: var(--stock-ink, #0f172a);
    }

    .u-more {
      font-size: 24rpx;
      color: var(--stock-ink-3, #64748b);
    }
  }

  .m-tab-pills {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 0 32rpx;
    margin-bottom: 20rpx;

    .u-pill {
      padding: 14rpx 28rpx;
      border-radius: 40rpx;
      background: #fff;
      border: 2rpx solid var(--stock-line, #e5e9f2);
      font-size: 26rpx;
      color: var(--stock-ink-2, #475569);
      font-weight: 500;
      flex-shrink: 0;

      &.active {
        background: var(--stock-navy, #0b1e47);
        color: #fff;
        border-color: var(--stock-navy, #0b1e47);
        font-weight: 600;
      }
    }

    .u-pill-more {
      margin-left: auto;
      font-size: 36rpx;
      color: var(--stock-ink-3, #64748b);
      line-height: 1;
      padding: 0 8rpx;
    }
  }

  .m-stock-list {
    @include stock-card;
    margin: 0 24rpx;
    overflow: hidden;
  }

  .stock-loading,
  .no-data {
    text-align: center;
    padding: 48rpx 0;
    color: var(--stock-ink-3, #64748b);
    font-size: 28rpx;
  }
}

.popup-image-container {
  max-width: 600rpx;
  background: transparent;
}

.popup-image {
  border-radius: 16rpx;
}

/* 公告悬浮弹窗（对齐原型 .nm-sheet 样式：原型 1px → 2rpx） */
.m-notice-sheet {
  height: 72vh;
  max-height: 72vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  padding: 20rpx 36rpx calc(48rpx + env(safe-area-inset-bottom));
  background: #fff;

  .u-nm-handle {
    width: 84rpx;
    height: 8rpx;
    background: #cbd5e1;
    border-radius: 4rpx;
    margin: 0 auto 32rpx;
    flex-shrink: 0;
  }

  .u-nm-top {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 20rpx;
    margin-bottom: 28rpx;
    flex-shrink: 0;
  }

  .u-nm-tag-row {
    display: flex;
    align-items: center;
    gap: 16rpx;
    flex-wrap: wrap;
    flex: 1;
    min-width: 0;
  }

  .u-nm-cat {
    padding: 6rpx 20rpx;
    font-size: 22rpx;
    border-radius: 12rpx;
    font-weight: 800;
    letter-spacing: 1rpx;
    line-height: 1.4;
  }

  .u-nm-time {
    font-size: 22rpx;
    color: var(--stock-ink-3, #64748b);
  }

  .u-nm-close {
    width: 64rpx;
    height: 64rpx;
    background: #f1f5fb;
    border-radius: 18rpx;
    color: var(--stock-ink-2, #475569);
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    font-size: 40rpx;
    line-height: 1;
    font-weight: 600;
  }

  .u-nm-title {
    font-size: 36rpx;
    font-weight: 800;
    color: var(--stock-ink, #0f172a);
    line-height: 1.35;
    margin-bottom: 24rpx;
    letter-spacing: 0.4rpx;
    flex-shrink: 0;
  }

  .u-nm-body {
    flex: 1;
    min-height: 0;
    font-size: 27rpx;
    color: var(--stock-ink-2, #475569);
    line-height: 1.65;
    white-space: pre-wrap;
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    margin-bottom: 36rpx;
  }

  .u-nm-cta-row {
    display: flex;
    gap: 20rpx;
    flex-shrink: 0;
  }

  .u-nm-btn {
    flex: 1;
    height: 84rpx;
    border-radius: 22rpx;
    border: none;
    font-size: 27rpx;
    font-weight: 800;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;

    &.outline {
      background: #fff;
      border: 3rpx solid var(--stock-line, #e5e9f2);
      color: var(--stock-ink-2, #475569);
    }

    &.primary {
      background: linear-gradient(
        135deg,
        var(--stock-navy, #0b1e47),
        var(--stock-blue, #2563eb)
      );
      color: #fff;
      box-shadow: 0 8rpx 24rpx rgba(37, 99, 235, 0.25);
    }

    &.success {
      background: linear-gradient(135deg, var(--stock-green, #10b981), #059669);
      color: #fff;
      box-shadow: 0 8rpx 24rpx rgba(16, 185, 129, 0.25);
    }
  }
}
</style>
