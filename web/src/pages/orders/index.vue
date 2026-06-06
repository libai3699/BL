<script setup>
import { ref, computed, onUnmounted } from 'vue';
import { onShow, onHide } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import dayjs from 'dayjs';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import { goBack, navigateTo } from '@/utils/navigate';
import {
  getUserAccount,
  sellStock,
  updateProfitTarget,
  getPositionList,
  getSubscribeAddList
} from '@/apis/order';
import { getSubscribeHistoryList } from '@/apis/ipo';
import { ensureWebSocketConnected } from '@/utils/websocket';
import ClosePositionModal from './components/ClosePositionModal.vue';
import ProfitStopModal from './components/ProfitStopModal.vue';
import PaymentModal from './components/PaymentModal.vue';

const { t } = useI18n();

const commonStore = useCommonStore();
const userStore = useUserStore();

// 账户类型列表
const accountTypes = ref([]);

// 当前账户类型
const currentAssetTab = ref('');

// 资产数据
const assetData = ref({});

// 当前选中的币种 - 根据账户类型动态显示
const currentCurrency = computed(() => currentAssetTab.value.toUpperCase());

// 切换资产显示
const toggleAssetVisibility = () => {
  commonStore.dataEye.orders = !commonStore.dataEye.orders;
};

// 快捷入口（原型 shortcuts）
const quickEntries = ref([
  { icon: 'stock-card', labelKey: 'order.fundDetail' },
  { icon: 'stock-clock', labelKey: 'order.holdHistory' },
  { icon: 'stock-document', labelKey: 'order.tradeHistory' }
]);

const formatBuildTime = (time) => {
  if (!time) return '--';
  const d = dayjs(time);
  return d.isValid() ? d.format('MM/DD') : String(time);
};

const pnlClass = (val) => {
  const n = Number(val);
  if (Number.isNaN(n) || n === 0) return '';
  return n > 0 ? 's-up' : 's-down';
};

const profitRatePillClass = (rate) => {
  if (!rate || rate === '--') return 's-flat';
  const n = parseFloat(String(rate).replace('%', ''));
  if (Number.isNaN(n) || n === 0) return 's-flat';
  return n > 0 ? 's-up' : 's-down';
};

const positionType = ref('holding');
const positionList = ref([]);
const positionPage = ref(1);
const positionPageSize = ref(10);
const positionHasMore = ref(true);
const positionLoading = ref(false);

// FPO相关状态
const fpoStatusFilter = ref(0); // 0全部, 2待审核, 3待付款, 4待转持仓, 5已转持仓
const fpoList = ref([]);
const fpoPage = ref(1);
const fpoPageSize = ref(10);
const fpoHasMore = ref(true);
const fpoLoading = ref(false);

// IPO相关状态
const ipoStatusFilter = ref(0); // 0全部, 1待审核, 3待付款, 4待转持仓, 5已转持仓
const ipoList = ref([]);
const ipoPage = ref(1);
const ipoPageSize = ref(10);
const ipoHasMore = ref(true);
const ipoLoading = ref(false);

// 获取用户账户信息
const fetchUserAccount = async (type = '') => {
  try {
    const res = await getUserAccount(type);
    if (res) {
      accountTypes.value = res.accountTypes || [];

      // 如果是首次加载且没有指定类型，默认选中第一个
      if (!type && accountTypes.value.length > 0) {
        currentAssetTab.value = accountTypes.value[0].type;
      }

      if (res.accounts && res.accounts.length > 0) {
        assetData.value = res.accounts[0];
      }
    }
  } catch (error) {
    console.error('获取账户信息失败', error);
  }
};

// 切换账户类型
const switchAssetTab = (type) => {
  if (currentAssetTab.value === type) return;
  currentAssetTab.value = type;
  // 切换时调用接口
  fetchUserAccount(type);
};

const fetchPositionList = async (loadMore = false) => {
  if (positionLoading.value) return;

  try {
    positionLoading.value = true;
    const pageNum = loadMore ? positionPage.value + 1 : 1;
    const res = await getPositionList({
      pageNum,
      pageSize: positionPageSize.value,
      state: positionType.value === 'holding' ? 0 : 1
    });

    if (res && res.list) {
      const formattedList = res.list.map((item) => {
        const currentPrice = item.now_price || item.buyOrderPrice || 0;
        const buyPrice = item.buyOrderPrice || 0;
        const quantity = item.orderNum || 0;
        const orderDirection = item.orderDirection === 0 ? 'buy' : 'sell';

        // 计算当前市值
        const currentValue = currentPrice * quantity;
        const buyTotalPrice = item.buyTotalPrice || 0;

        // 直接使用后端返回的盈亏值
        const profitAndLose = item.profitAndLose || 0;

        // 计算盈亏率：盈亏 / (买入价 * 数量)
        // 先保留4位小数，再乘100，最后结果保留2位
        const profitRate =
          buyTotalPrice > 0
            ? (parseFloat((profitAndLose / buyTotalPrice).toFixed(4)) * 100).toFixed(2) +
              '%'
            : '--';

        return {
          ...item,
          id: item.id,
          positionSn: item.positionSn,
          stockCode: item.stockCode,
          stockName: item.stockName,
          stockSpell: item.stockSpell,
          stockPlate: item.stockPlate,
          stockType: orderDirection,
          displayCode: item.stockCode || item.stockName,
          displayName: item.stockSpell || item.stockName,
          availableNum: item.availableNum || 0,
          profitRate: profitRate,
          profitAndLose: profitAndLose,
          holdValue: buyTotalPrice,
          marketValue: currentValue,
          buyPrice: buyPrice,
          currentPrice: currentPrice,
          currentValue: currentValue,
          quantity: quantity,
          buyOrderTime: item.buyOrderTime || item.createTime || '',
          currency: item.currency || 'MXN',
          profitTarget: item.profitTargetPrice,
          stopTarget: item.stopTargetPrice,
          gid: item.gid || item.stockCode
        };
      });

      if (loadMore) {
        positionList.value = [...positionList.value, ...formattedList];
      } else {
        positionList.value = formattedList;
      }
      positionPage.value = pageNum;
      positionHasMore.value = res.hasNextPage;
    }
  } catch (error) {
    console.error('获取持仓列表失败', error);
  } finally {
    positionLoading.value = false;
  }
};

const switchPositionType = (type) => {
  if (positionType.value === type) return;
  positionType.value = type;

  if (type === 'fpo') {
    fpoPage.value = 1;
    fpoList.value = [];
    fetchFpoList();
  } else if (type === 'ipo') {
    ipoPage.value = 1;
    ipoList.value = [];
    fetchIpoList();
  } else if (type === 'pending') {
    // 挂单中：暂无数据占位
  } else {
    positionPage.value = 1;
    positionList.value = [];
    fetchPositionList();
  }
};

// 获取FPO列表
const fetchFpoList = async (loadMore = false) => {
  if (fpoLoading.value) return;

  try {
    fpoLoading.value = true;
    const pageNum = loadMore ? fpoPage.value + 1 : 1;

    const res = await getSubscribeAddList({
      pageNum,
      pageSize: fpoPageSize.value,
      status: fpoStatusFilter.value,
      type: 1
    });

    if (res && res.list) {
      if (loadMore) {
        fpoList.value = [...fpoList.value, ...res.list];
      } else {
        fpoList.value = res.list;
      }
      fpoPage.value = pageNum;
      fpoHasMore.value = res.hasNextPage;
    }
  } catch (error) {
    console.error('获取FPO列表失败', error);
    uni.showToast({
      title: error.errorDesc || error.msg || t('error.system'),
      icon: 'none'
    });
  } finally {
    fpoLoading.value = false;
  }
};

// 切换FPO状态筛选
const switchFpoStatus = (status) => {
  if (fpoStatusFilter.value === status) return;
  fpoStatusFilter.value = status;
  fpoPage.value = 1;
  fpoList.value = [];
  fetchFpoList();
};

// FPO项点击
const handleFpoClick = (item) => {
  navigateTo(
    `/pages/orders/fpo-detail?type=fpo&item=${encodeURIComponent(JSON.stringify(item))}`
  );
};

// 获取IPO列表
const fetchIpoList = async (loadMore = false) => {
  if (ipoLoading.value) return;

  try {
    ipoLoading.value = true;
    const pageNum = loadMore ? ipoPage.value + 1 : 1;

    const params = {
      pageNum,
      pageSize: ipoPageSize.value,
      status: ipoStatusFilter.value,
      type: 1 // IPO类型为1
    };

    const res = await getSubscribeHistoryList(params);
    const dataList = res?.data?.list || res?.list || [];

    if (loadMore) {
      ipoList.value = [...ipoList.value, ...dataList];
    } else {
      ipoList.value = dataList;
    }
    ipoPage.value = pageNum;
    ipoHasMore.value = res?.data?.hasNextPage || dataList.length >= ipoPageSize.value;
  } catch (error) {
    console.error('获取IPO列表失败', error);
    uni.showToast({
      title: error.errorDesc || error.msg || t('error.system'),
      icon: 'none'
    });
  } finally {
    ipoLoading.value = false;
  }
};

// 切换IPO状态筛选
const switchIpoStatus = (status) => {
  if (ipoStatusFilter.value === status) return;
  ipoStatusFilter.value = status;
  ipoPage.value = 1;
  ipoList.value = [];
  fetchIpoList();
};

// IPO项点击
const handleIpoClick = (item) => {
  // 使用公共的详情页面，添加type参数区分
  navigateTo(
    `/pages/orders/fpo-detail?type=ipo&item=${encodeURIComponent(JSON.stringify(item))}`
  );
};

// 下拉刷新
const onRefresh = () => {
  if (positionType.value === 'fpo') {
    fetchFpoList();
  } else if (positionType.value === 'ipo') {
    fetchIpoList();
  } else {
    fetchPositionList();
  }
};

// 触底加载
const onLoadMore = () => {
  if (positionType.value === 'fpo') {
    if (fpoHasMore.value && !fpoLoading.value) {
      fetchFpoList(true);
    }
  } else if (positionType.value === 'ipo') {
    if (ipoHasMore.value && !ipoLoading.value) {
      fetchIpoList(true);
    }
  } else {
    if (positionHasMore.value && !positionLoading.value) {
      fetchPositionList(true);
    }
  }
};

// 快捷入口点击
const handleQuickEntry = (item) => {
  if (item.labelKey === 'order.fundDetail') {
    navigateTo('/pages/fund-record/index');
  } else if (item.labelKey === 'order.holdHistory') {
    navigateTo('/pages/orders/position-history/index');
  } else if (item.labelKey === 'order.tradeHistory') {
    navigateTo('/pages/orders/transaction-records/index');
  }
};

// 持仓项点击
const handlePositionClick = (item) => {
  navigateTo(`/pages/orders/fpo-detail?type=position&positionSn=${item.positionSn}`);
};

const closeModalVisible = ref(false);
const profitStopModalVisible = ref(false);
const currentPosition = ref(null);

// IPO 付款弹窗
const paymentModalVisible = ref(false);
const currentIpoItem = ref(null);

const handleStopOrLimit = (item) => {
  currentPosition.value = item;
  profitStopModalVisible.value = true;
};

const handleClosePosition = (item) => {
  currentPosition.value = item;
  closeModalVisible.value = true;
};

// 打开付款弹窗
const handleIpoPayment = (item, event) => {
  if (event) {
    event.stopPropagation();
  }

  currentIpoItem.value = item;
  paymentModalVisible.value = true;
};

// 付款成功回调
const handlePaymentSuccess = () => {
  fetchIpoList();
};

const handleConfirmClose = async (position) => {
  const res = await sellStock(position.positionSn);

  if (res.status == 0) {
    uni.showToast({
      title: t('order.closeSuccess'),
      icon: 'success'
    });

    closeModalVisible.value = false;
    fetchUserAccount();
    fetchPositionList();
  } else {
    uni.showToast({
      title: res.msg || t('error.system'),
      icon: 'none'
    });
  }
};

const handleConfirmProfitStop = async (data) => {
  const res = await updateProfitTarget(data);

  if (res.status == 0) {
    uni.showToast({
      title: t('order.updateSuccess'),
      icon: 'success'
    });

    profitStopModalVisible.value = false;
    fetchUserAccount();
    fetchPositionList();
  } else {
    uni.showToast({
      title: res.msg || t('error.system'),
      icon: 'none'
    });
  }
};

// 获取 FPO 状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: t('order.fpoWaitWinning'),
    2: t('order.fpoNotWinning'),
    3: t('order.fpoNotWinning'),
    4: t('order.fpoWaitTransfer'),
    5: t('order.fpoTransferred'),
    6: t('common.cancel')
  };
  return statusMap[status] || '--';
};

// 获取 IPO 状态文本
const getStatusTextIpo = (status) => {
  const statusMap = {
    1: t('order.ipoPendingReview'),
    3: t('order.ipoPendingPayment'),
    4: t('order.ipoPendingTransfer'),
    5: t('order.ipoTransferred'),
    6: t('common.cancel')
  };
  return statusMap[status] || '--';
};

// WebSocket消息处理的节流控制
let updateTimer = null;
const pendingUpdates = new Map();

// WebSocket消息处理
const handleStockMessage = (data) => {
  // 如果不在持仓页面，不处理消息
  if (positionType.value !== 'holding' || positionList.value.length === 0) {
    return;
  }

  const stockId = data.pid || data.gid || data.code;
  if (!stockId) return;

  const newPrice = data.last_numeric;
  if (!newPrice) return;

  // 将更新缓存起来
  pendingUpdates.set(stockId, newPrice);

  // 使用节流，每100ms批量更新一次
  if (updateTimer) return;

  updateTimer = setTimeout(() => {
    updateTimer = null;

    // 批量处理所有待更新的股票
    let hasUpdate = false;

    pendingUpdates.forEach((price, stockId) => {
      const index = positionList.value.findIndex(
        (item) => item.stockCode === stockId || item.gid === stockId
      );

      if (index === -1) return;

      const item = positionList.value[index];

      // 如果价格没变化，跳过
      if (item.currentPrice === price) return;

      hasUpdate = true;

      // 计算新的市值
      const currentValue = price * item.quantity;

      // 根据买卖方向计算盈亏
      // 买: (现价 - 买入价) * 数量
      // 卖: (买入价 - 现价) * 数量
      let profitAndLose = 0;
      if (item.stockType === 'buy') {
        profitAndLose = (price - item.buyPrice) * item.quantity;
      } else {
        profitAndLose = (item.buyPrice - price) * item.quantity;
      }

      // 计算盈亏率：盈亏 / (买入价 * 数量)
      // 买卖都用同一个公式
      // 先保留4位小数，再乘100，最后结果保留2位
      let profitRate = '--';
      const buyTotal = item.buyPrice * item.quantity;
      profitRate =
        buyTotal > 0
          ? (parseFloat((profitAndLose / buyTotal).toFixed(4)) * 100).toFixed(2) + '%'
          : '--';

      // 只更新变化的项
      positionList.value[index] = {
        ...item,
        currentPrice: price,
        currentValue: currentValue,
        marketValue: currentValue,
        profitAndLose: profitAndLose,
        profitRate: profitRate
      };
    });

    // 清空待更新列表
    pendingUpdates.clear();

    // 如果有更新，重新计算浮动盈亏总额
    if (hasUpdate) {
      const totalFloating = positionList.value.reduce((sum, item) => {
        return sum + (item.profitAndLose || 0);
      }, 0);

      assetData.value.floating = totalFloating.toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    }
  }, 100);
};

// 是否正在加载
const isLoadingData = ref(false);

// 加载页面数据
const loadPageData = async () => {
  // 防止重复加载
  if (isLoadingData.value) {
    return;
  }

  isLoadingData.value = true;

  try {
    // 先快速加载账户信息（通常较快）
    await fetchUserAccount();

    // 然后加载列表数据
    if (positionType.value === 'fpo') {
      await fetchFpoList();
    } else if (positionType.value === 'ipo') {
      await fetchIpoList();
    } else {
      await fetchPositionList();
    }
  } catch (error) {
    console.error('加载页面数据失败', error);
  } finally {
    isLoadingData.value = false;
  }
};

onShow(() => {
  commonStore.fnBack = goBack;

  // 监听WebSocket消息（每次都需要重新监听）
  uni.$on('ws:mexico', handleStockMessage);
  uni.$on('ws:us', handleStockMessage);

  // 确保 WebSocket 已连接（首次会初始化，后续会检查连接状态）
  ensureWebSocketConnected(userStore, 300);

  // 延迟加载数据，确保页面先渲染
  setTimeout(() => {
    loadPageData();
  }, 50);
});

// 页面隐藏时移除监听
onHide(() => {
  uni.$off('ws:mexico', handleStockMessage);
  uni.$off('ws:us', handleStockMessage);

  // 清理定时器和待更新数据
  if (updateTimer) {
    clearTimeout(updateTimer);
    updateTimer = null;
  }
  pendingUpdates.clear();
});

// 组件卸载时也清理
onUnmounted(() => {
  uni.$off('ws:mexico', handleStockMessage);
  uni.$off('ws:us', handleStockMessage);

  // 清理定时器和待更新数据
  if (updateTimer) {
    clearTimeout(updateTimer);
    updateTimer = null;
  }
  pendingUpdates.clear();
});
</script>

<template>
  <page-wrapper>
    <view class="m-content">
      <view class="orders-page">
        <!-- 资产卡片（原型 orders-asset-card） -->
        <view class="orders-header">
          <view class="orders-asset-card">
            <view class="acc-tabs">
              <view
                v-for="accountType in accountTypes"
                :key="accountType.type"
                class="acc-tab"
                :class="{ active: currentAssetTab === accountType.type }"
                @click="switchAssetTab(accountType.type)"
              >
                {{ accountType.title }}
              </view>
            </view>

            <view class="total-row">
              <view class="total-block">
                <view class="total-label">
                  <text>{{ $t('order.totalAsset') }}</text>
                  <svg-icon
                    :name="commonStore.dataEye.orders ? 'eye-show' : 'eye-close'"
                    :size="26"
                    class="u-eye"
                    @tap.stop="toggleAssetVisibility"
                  />
                </view>
                <view class="total-value" translate="no">
                  <template v-if="commonStore.dataEye.orders">
                    <c-amount :value="assetData.totalAsset" />
                  </template>
                  <text v-else>****</text>
                  <text class="total-currency">{{ currentCurrency }}</text>
                </view>
              </view>
              <view class="swap-btn" @tap="navigateTo('/pages/transfer/index')">
                <svg-icon name="transfer" :size="28" color="#D4A24C" />
                <text>{{ $t('common.transfer') }}</text>
              </view>
            </view>

            <view class="metric-grid">
              <view class="metric">
                <text class="metric-k"
                  >{{ $t('order.available') }}({{ currentCurrency }})</text
                >
                <view class="metric-v" v-if="commonStore.dataEye.orders">
                  <c-amount :value="assetData.availableAmount" />
                </view>
                <text class="metric-v" v-else>****</text>
              </view>
              <view class="metric">
                <text class="metric-k"
                  >{{ $t('order.frozen') }}({{ currentCurrency }})</text
                >
                <view class="metric-v" v-if="commonStore.dataEye.orders">
                  <c-amount :value="assetData.positionValue" />
                </view>
                <text class="metric-v" v-else>****</text>
              </view>
              <view class="metric">
                <text class="metric-k"
                  >{{ $t('order.floating') }}({{ currentCurrency }})</text
                >
                <view class="metric-v" v-if="commonStore.dataEye.orders">
                  <c-amount :value="assetData.frozenAmount" />
                </view>
                <text class="metric-v" v-else>****</text>
              </view>
              <view class="metric">
                <text class="metric-k"
                  >{{ $t('order.unrealized') }}({{ currentCurrency }})</text
                >
                <view
                  class="metric-v"
                  :class="pnlClass(assetData.unrealizedPnl)"
                  v-if="commonStore.dataEye.orders"
                >
                  <text v-if="Number(assetData.unrealizedPnl) > 0">+</text>
                  <c-amount :value="assetData.unrealizedPnl" />
                </view>
                <text class="metric-v" v-else>****</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 快捷入口 -->
        <view class="orders-shortcuts">
          <view
            v-for="(item, index) in quickEntries"
            :key="index"
            class="shortcut"
            @click="handleQuickEntry(item)"
          >
            <view class="shortcut-ico">
              <svg-icon :name="item.icon" :size="44" color="#0B1E47" />
            </view>
            <text>{{ $t(item.labelKey) }}</text>
          </view>
        </view>

        <!-- 持仓 Tab（原型 pos-tabs / seg-tab） -->
        <view class="orders-pos-tabs">
          <view
            class="seg-tab"
            :class="{ active: positionType === 'holding' }"
            @click="switchPositionType('holding')"
          >
            {{ $t('order.holding') }}
          </view>
          <view
            class="seg-tab"
            :class="{ active: positionType === 'fpo' || positionType === 'ipo' }"
            @click="switchPositionType('fpo')"
          >
            {{ $t('order.fpoIpo') }}
          </view>
          <view
            class="seg-tab"
            :class="{ active: positionType === 'pending' }"
            @click="switchPositionType('pending')"
          >
            {{ $t('order.pending') }}
          </view>
        </view>

        <!-- FPO / IPO 子切换 -->
        <view
          class="orders-sub-tabs"
          v-if="positionType === 'fpo' || positionType === 'ipo'"
        >
          <view
            class="sub-tab"
            :class="{ active: positionType === 'fpo' }"
            @click="switchPositionType('fpo')"
          >
            FPO
          </view>
          <view
            class="sub-tab"
            :class="{ active: positionType === 'ipo' }"
            @click="switchPositionType('ipo')"
          >
            IPO
          </view>
        </view>

        <!-- FPO状态筛选 -->
        <scroll-view
          scroll-x
          class="orders-status-scroll"
          v-if="positionType === 'fpo'"
          :show-scrollbar="false"
        >
          <view class="orders-status-filters">
            <view
              class="status-pill"
              :class="{ active: fpoStatusFilter === 0 }"
              @click="switchFpoStatus(0)"
            >
              {{ $t('order.fpoAll') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: fpoStatusFilter === 2 }"
              @click="switchFpoStatus(2)"
            >
              {{ $t('order.fpoWaitWinning') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: fpoStatusFilter === 3 }"
              @click="switchFpoStatus(3)"
            >
              {{ $t('order.fpoNotWinning') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: fpoStatusFilter === 4 }"
              @click="switchFpoStatus(4)"
            >
              {{ $t('order.fpoWaitTransfer') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: fpoStatusFilter === 5 }"
              @click="switchFpoStatus(5)"
            >
              {{ $t('order.fpoTransferred') }}
            </view>
          </view>
        </scroll-view>

        <!-- IPO状态筛选 -->
        <scroll-view
          scroll-x
          class="orders-status-scroll"
          v-if="positionType === 'ipo'"
          :show-scrollbar="false"
        >
          <view class="orders-status-filters">
            <view
              class="status-pill"
              :class="{ active: ipoStatusFilter === 0 }"
              @click="switchIpoStatus(0)"
            >
              {{ $t('order.ipoAll') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: ipoStatusFilter === 1 }"
              @click="switchIpoStatus(1)"
            >
              {{ $t('order.ipoPendingReview') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: ipoStatusFilter === 3 }"
              @click="switchIpoStatus(3)"
            >
              {{ $t('order.ipoPendingPayment') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: ipoStatusFilter === 4 }"
              @click="switchIpoStatus(4)"
            >
              {{ $t('order.ipoPendingTransfer') }}
            </view>
            <view
              class="status-pill"
              :class="{ active: ipoStatusFilter === 5 }"
              @click="switchIpoStatus(5)"
            >
              {{ $t('order.ipoTransferred') }}
            </view>
          </view>
        </scroll-view>

        <view class="orders-pos-list">
          <!-- 挂单中占位 -->
          <view class="orders-empty" v-if="positionType === 'pending'">
            <text>{{ $t('order.pendingEmpty') }}</text>
          </view>

          <!-- Loading -->
          <view
            class="orders-loading"
            v-else-if="
              (positionLoading && positionList.length === 0) ||
              (fpoLoading && fpoList.length === 0) ||
              (ipoLoading && ipoList.length === 0)
            "
          >
            <u-loading-icon mode="circle" size="40"></u-loading-icon>
          </view>

          <!-- FPO列表 -->
          <scroll-view
            v-else-if="positionType === 'fpo'"
            class="orders-scroll"
            scroll-y
            @scrolltolower="onLoadMore"
            @refresherrefresh="onRefresh"
            refresher-enabled
            :refresher-triggered="fpoLoading"
          >
            <view
              v-for="item in fpoList"
              :key="item.id"
              class="pos-card"
              @click="handleFpoClick(item)"
            >
              <view class="pos-top">
                <view>
                  <text class="pos-name">{{ item.newName }}</text>
                  <text class="pos-code">{{ item.newCode || '--' }}</text>
                </view>
                <text class="pos-pill s-tag">{{ getStatusText(item.status) }}</text>
              </view>
              <view class="pos-tags">
                <text class="pill tag">FPO</text>
              </view>
              <view class="pos-grid">
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoIssuePrice') }}</text>
                  <view class="pos-v s-muted"><c-amount :value="item.buyPrice" /></view>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoApplyNum') }}</text>
                  <text class="pos-v s-muted"
                    >{{ item.applyNums }} {{ $t('ipo.shareUnit') }}</text
                  >
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoSubscribeDate') }}</text>
                  <text class="pos-v s-muted">{{ item.subscriptionTime || '--' }}</text>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoWinningDate') }}</text>
                  <text class="pos-v s-muted">{{ item.endTime || '--' }}</text>
                </view>
              </view>
            </view>
            <view class="orders-empty" v-if="!fpoLoading && fpoList.length === 0">
              <text>{{ $t('common.noData') }}</text>
            </view>
          </scroll-view>

          <!-- IPO列表 -->
          <scroll-view
            v-else-if="positionType === 'ipo'"
            class="orders-scroll"
            scroll-y
            @scrolltolower="onLoadMore"
            @refresherrefresh="onRefresh"
            refresher-enabled
            :refresher-triggered="ipoLoading"
          >
            <view
              v-for="item in ipoList"
              :key="item.id"
              class="pos-card"
              @click="handleIpoClick(item)"
            >
              <view class="pos-top">
                <view>
                  <text class="pos-name">{{ item.newName }}</text>
                  <text class="pos-code">{{ item.newCode || '--' }}</text>
                </view>
                <text class="pos-pill s-tag">{{ getStatusTextIpo(item.status) }}</text>
              </view>
              <view class="pos-tags">
                <text class="pill tag">IPO</text>
              </view>
              <view class="pos-grid">
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoIssuePrice') }}</text>
                  <view class="pos-v s-muted"><c-amount :value="item.buyPrice" /></view>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoApplyNum') }}</text>
                  <text class="pos-v s-muted"
                    >{{ item.applyNums }} {{ $t('ipo.shareUnit') }}</text
                  >
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoSubscribeDate') }}</text>
                  <text class="pos-v s-muted">{{ item.subscriptionTime || '--' }}</text>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.fpoWinningNum') }}</text>
                  <text class="pos-v s-muted"
                    >{{ item.applyNumber || 0 }} {{ $t('ipo.shareUnit') }}</text
                  >
                </view>
              </view>
              <view class="pos-actions" v-if="item.status === 3">
                <view class="btn primary" @click.stop="handleIpoPayment(item, $event)">
                  {{ $t('order.payment') }}
                </view>
              </view>
            </view>
            <view class="orders-empty" v-if="!ipoLoading && ipoList.length === 0">
              <text>{{ $t('common.noData') }}</text>
            </view>
          </scroll-view>

          <!-- 股票持仓列表 -->
          <scroll-view
            v-else-if="positionType === 'holding'"
            class="orders-scroll"
            scroll-y
            @scrolltolower="onLoadMore"
            @refresherrefresh="onRefresh"
            refresher-enabled
            :refresher-triggered="positionLoading"
          >
            <view
              v-for="item in positionList"
              :key="item.id"
              class="pos-card"
              @click="handlePositionClick(item)"
            >
              <view class="pos-top">
                <view>
                  <text class="pos-name">{{ item.displayCode }}</text>
                  <text class="pos-code">{{ item.displayName }}</text>
                </view>
                <text class="pos-pill" :class="profitRatePillClass(item.profitRate)">{{
                  item.profitRate
                }}</text>
              </view>
              <view class="pos-tags">
                <text v-if="item.stockPlate" class="pill tag">{{ item.stockPlate }}</text>
                <text
                  class="pill tag"
                  :class="item.stockType === 'buy' ? 'success' : 'warn'"
                  >{{ $t(`order.${item.stockType}`) }}</text
                >
              </view>
              <view class="pos-grid">
                <view class="pos-cell">
                  <text class="pos-k"
                    >{{ $t('order.profitLoss') }}({{ item.currency }})</text
                  >
                  <view class="pos-v" :class="pnlClass(item.profitAndLose)">
                    <text v-if="Number(item.profitAndLose) > 0">+</text>
                    <c-amount :value="item.profitAndLose" />
                  </view>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.holdValue') }}</text>
                  <view class="pos-v s-muted">
                    <c-amount :value="item.marketValue || item.holdValue" />
                  </view>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.currentPrice') }}</text>
                  <view class="pos-v s-muted"
                    ><c-amount :value="item.currentPrice"
                  /></view>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.buyPrice') }}</text>
                  <view class="pos-v s-muted"><c-amount :value="item.buyPrice" /></view>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.quantity') }}</text>
                  <text class="pos-v s-muted">{{ item.quantity }}</text>
                </view>
                <view class="pos-cell">
                  <text class="pos-k">{{ $t('order.createTime') }}</text>
                  <text class="pos-v s-muted">{{
                    formatBuildTime(item.buyOrderTime)
                  }}</text>
                </view>
              </view>
              <view class="pos-actions">
                <view class="btn outline" @click.stop="handleStopOrLimit(item)">
                  {{ $t('order.stopOrLimit') }}
                </view>
                <view class="btn primary" @click.stop="handleClosePosition(item)">
                  {{ $t('order.closePosition') }}
                </view>
              </view>
            </view>
            <view
              class="orders-empty"
              v-if="!positionLoading && positionList.length === 0"
            >
              <text>{{ $t('common.noData') }}</text>
            </view>
          </scroll-view>
        </view>

        <view class="orders-bottom-spacer" />
      </view>

      <!-- 底部导航 -->
      <tab-bar :page="$page" />
    </view>

    <!-- 平仓弹框 -->
    <close-position-modal
      v-if="closeModalVisible"
      v-model:visible="closeModalVisible"
      :position="currentPosition"
      @confirm="handleConfirmClose"
    />

    <!-- 止盈止损弹框 -->
    <profit-stop-modal
      v-if="profitStopModalVisible"
      v-model:visible="profitStopModalVisible"
      :position="currentPosition"
      @confirm="handleConfirmProfitStop"
    />

    <!-- IPO 付款弹窗 -->
    <payment-modal
      v-if="paymentModalVisible"
      v-model:visible="paymentModalVisible"
      :ipo-item="currentIpoItem"
      @success="handlePaymentSuccess"
    />
  </page-wrapper>
</template>

<style lang="scss" scoped>
@use '@/styles/mixin.scss' as *;

.m-content {
  @include hasBottomNav();
  background: var(--stock-page-bg, #e9edf5);
}

.orders-page {
  min-height: 100%;
  padding-bottom: 16rpx;
}

// —— 资产卡片 ——
.orders-header {
  padding: 28rpx 24rpx 0;
}

.orders-asset-card {
  background: #fff;
  border-radius: 32rpx;
  padding: 36rpx;
  box-shadow:
    0 8rpx 28rpx rgba(15, 30, 71, 0.07),
    0 2rpx 6rpx rgba(15, 30, 71, 0.04);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.acc-tabs {
  display: flex;
  gap: 0;
  margin-bottom: 28rpx;
  width: fit-content;
}

.acc-tab {
  padding: 14rpx 36rpx;
  border-radius: 36rpx;
  font-size: 26rpx;
  font-weight: 600;
  color: var(--stock-ink-2, #475569);

  &.active {
    background: #0f172a;
    color: #fff;
  }
}

.total-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.total-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  font-size: 24rpx;
  color: var(--stock-ink-3, #64748b);
}

.u-eye {
  flex-shrink: 0;
}

.total-value {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
  margin-top: 12rpx;
  font-size: 56rpx;
  font-weight: 800;
  color: var(--stock-ink, #0f172a);
  letter-spacing: 0.6rpx;
  font-feature-settings: 'tnum';
}

.total-currency {
  font-size: 28rpx;
  font-weight: 600;
  color: var(--stock-ink-2, #475569);
}

.swap-btn {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding-top: 36rpx;
  font-size: 26rpx;
  font-weight: 600;
  color: #d4a24c;
}

.metric-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32rpx 48rpx;
  margin-top: 36rpx;
  padding-top: 32rpx;
  border-top: 1rpx dashed #e5e9f2;
}

.metric-k {
  font-size: 23rpx;
  color: var(--stock-ink-3, #64748b);
}

.metric-v {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
  margin-top: 8rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: var(--stock-ink, #0f172a);
  font-feature-settings: 'tnum';

  &.s-up {
    color: var(--stock-green, #10b981);
  }

  &.s-down {
    color: var(--stock-red, #ef4444);
  }
}

// —— 快捷入口 ——
.orders-shortcuts {
  display: flex;
  justify-content: space-around;
  margin: 28rpx 24rpx 0;
  padding: 40rpx 16rpx 36rpx;
  background: #fff;
  border-radius: var(--stock-radius, 24rpx);
  box-shadow:
    0 8rpx 28rpx rgba(15, 30, 71, 0.07),
    0 2rpx 6rpx rgba(15, 30, 71, 0.04);
}

.shortcut {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: var(--stock-ink, #0f172a);
}

.shortcut-ico {
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 28rpx;
  background: #f4f6fb;
  border: 1rpx solid #ebeff7;

  &:active {
    transform: scale(0.96);
  }
}

// —— Tab ——
.orders-pos-tabs {
  display: flex;
  gap: 48rpx;
  padding: 16rpx 32rpx 0;
  margin-top: 28rpx;
}

.seg-tab {
  position: relative;
  padding: 12rpx 0 20rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: var(--stock-ink-3, #64748b);

  &.active {
    color: var(--stock-ink, #0f172a);

    &::after {
      content: '';
      position: absolute;
      left: 50%;
      bottom: 4rpx;
      transform: translateX(-50%);
      width: 48rpx;
      height: 6rpx;
      background: var(--stock-gold, #d4a24c);
      border-radius: 4rpx;
    }
  }
}

.orders-sub-tabs {
  display: flex;
  gap: 16rpx;
  margin: 16rpx 24rpx 0;
  padding: 6rpx;
  background: #f1f5fb;
  border-radius: 22rpx;

  .sub-tab {
    flex: 1;
    text-align: center;
    padding: 16rpx 0;
    font-size: 24rpx;
    font-weight: 600;
    color: var(--stock-ink-2, #475569);
    border-radius: 18rpx;

    &.active {
      background: var(--stock-navy, #0b1e47);
      color: #fff;
    }
  }
}

.orders-status-scroll {
  margin: 20rpx 0 0;
  white-space: nowrap;
}

.orders-status-filters {
  display: inline-flex;
  gap: 16rpx;
  padding: 0 24rpx 8rpx;
}

.status-pill {
  flex-shrink: 0;
  padding: 12rpx 28rpx;
  border-radius: 16rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: var(--stock-ink-2, #475569);
  background: #fff;
  border: 1rpx solid var(--line-color, #e5e9f2);

  &.active {
    background: #0f172a;
    color: #fff;
    border-color: #0f172a;
  }
}

// —— 持仓列表 ——
.orders-pos-list {
  padding: 0 24rpx;
}

.orders-scroll {
  max-height: none;
}

.orders-bottom-spacer {
  height: 40rpx;
}

.pos-card {
  margin-top: 24rpx;
  padding: 28rpx;
  border-radius: 28rpx;
  background: #fff;
  border: 1rpx solid rgba(255, 255, 255, 0.8);
  box-shadow:
    0 8rpx 28rpx rgba(15, 30, 71, 0.07),
    0 2rpx 6rpx rgba(15, 30, 71, 0.04);
}

.pos-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.pos-name {
  display: block;
  font-size: 32rpx;
  font-weight: 800;
  color: var(--stock-ink, #0f172a);
}

.pos-code {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  color: var(--stock-ink-3, #64748b);
}

.pos-pill {
  flex-shrink: 0;
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
  font-weight: 600;
  line-height: 1.2;

  &.s-up {
    background: rgba(16, 185, 129, 0.12);
    color: var(--stock-green, #10b981);
  }

  &.s-down {
    background: rgba(239, 68, 68, 0.12);
    color: var(--stock-red, #ef4444);
  }

  &.s-flat {
    background: #f1f5f9;
    color: var(--stock-ink-2, #475569);
  }

  &.s-tag {
    background: rgba(16, 185, 129, 0.12);
    color: var(--stock-green, #10b981);
  }
}

.pos-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 16rpx;
}

.pill {
  display: inline-flex;
  align-items: center;
  padding: 6rpx 16rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
  font-weight: 500;
  line-height: 1.2;

  &.tag {
    background: #eef2ff;
    color: #4f46e5;
  }

  &.warn {
    background: #fef3c7;
    color: #a16207;
  }

  &.success {
    background: rgba(16, 185, 129, 0.12);
    color: var(--stock-green, #10b981);
  }
}

.pos-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28rpx 20rpx;
  margin-top: 28rpx;
}

.pos-k {
  font-size: 22rpx;
  color: var(--stock-ink-3, #64748b);
  line-height: 1.4;
}

.pos-v {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
  margin-top: 8rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: var(--stock-ink, #0f172a);
  font-feature-settings: 'tnum';
  line-height: 1.3;
  white-space: nowrap;

  &.s-up {
    color: var(--stock-green, #10b981);
  }

  &.s-down {
    color: var(--stock-red, #ef4444);
  }

  &.s-muted {
    color: var(--stock-ink-2, #475569);
    font-weight: 600;
  }
}

.pos-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 24rpx;
  padding-top: 24rpx;
  border-top: 1rpx dashed var(--line-color, #e5e9f2);
}

.btn {
  box-sizing: border-box;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 64rpx;
  padding: 12rpx 28rpx;
  border-radius: 16rpx;
  font-size: 24rpx;
  font-weight: 600;
  line-height: 1.2;

  &.outline {
    border: 1rpx solid var(--stock-ink-3, #64748b);
    color: var(--stock-ink-2, #475569);
    background: #fff;
  }

  &.primary {
    color: #fff;
    background: linear-gradient(
      135deg,
      var(--stock-navy, #0b1e47),
      var(--stock-blue, #2563eb)
    );
    box-shadow: 0 12rpx 28rpx rgba(37, 99, 235, 0.22);
  }
}

.orders-loading,
.orders-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 120rpx 0;
  font-size: 28rpx;
  color: var(--stock-ink-3, #64748b);
}
</style>
