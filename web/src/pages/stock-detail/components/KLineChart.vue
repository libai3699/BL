<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { init, dispose, utils } from 'klinecharts';

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  stockInfo: {
    type: Object,
    default: () => ({})
  },
  period: {
    type: String,
    default: '1D'
  },
  indicator: {
    type: String,
    default: 'VOL'
  },
  showVolume: {
    type: Boolean,
    default: true
  }
});

const emit = defineEmits(['zoom-in', 'zoom-out', 'settings']);

const chartContainer = ref(null);
const chartId = 'kline-chart-' + Date.now();
let chart = null;
let volumePaneId = null;
let volumePaneReady = false;
let indicatorPaneId = null;
let mainIndicatorName = null; // 记录主图上的指标名称

const INDICATOR_PANE_HEIGHT = 150;
const VOLUME_PANE_HEIGHT = 100;
const ZOOM_SCALE_STEP = 1;
const MIN_BAR_SPACE = 2;
const MAX_BAR_SPACE = 60;
const ZOOM_CENTER_Y = 0;
const MEXICO_TIMEZONE = 'America/Mexico_City';
const UTC_TIMEZONE = 'Etc/UTC';
const DAILY_OR_HIGHER_PERIODS = new Set(['P1D', 'P1W', 'P1M']);

const parseTimestamp = (value) => {
  if (!value) return Date.now();

  if (typeof value === 'string') {
    const numeric = Number(value);
    if (!Number.isNaN(numeric)) {
      return parseTimestamp(numeric);
    }

    const parsed = Date.parse(value);
    return Number.isNaN(parsed) ? Date.now() : parsed;
  }

  if (typeof value === 'number') {
    if (value < 1e11) {
      return value * 1000;
    }

    if (value > 1e15) {
      return Math.floor(value / 1000);
    }

    return value;
  }

  return Date.now();
};

const isDailyOrHigherPeriod = (period) => DAILY_OR_HIGHER_PERIODS.has(period);

const resolveTimezoneByPeriod = (period) =>
  isDailyOrHigherPeriod(period) ? UTC_TIMEZONE : MEXICO_TIMEZONE;

const toUtcDateStartTimestamp = (value) => {
  const timestamp = parseTimestamp(value);
  const date = new Date(timestamp);
  return Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate());
};

const normalizeTimestampByPeriod = (value, period) => {
  const timestamp = parseTimestamp(value);
  if (!isDailyOrHigherPeriod(period)) {
    return timestamp;
  }
  return toUtcDateStartTimestamp(timestamp);
};

const formatUtcDateOnly = (timestamp) => {
  const date = new Date(timestamp);
  const year = date.getUTCFullYear();
  const month = String(date.getUTCMonth() + 1).padStart(2, '0');
  const day = String(date.getUTCDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

const formatTimestampByPeriod = (dateTimeFormat, timestamp, format) => {
  if (isDailyOrHigherPeriod(props.period)) {
    return formatUtcDateOnly(timestamp);
  }
  return utils.formatDate(dateTimeFormat, timestamp, format);
};

const removeVolumePane = () => {
  if (!chart || !volumePaneId) return;
  chart.removeIndicator(volumePaneId);
  volumePaneId = null;
  volumePaneReady = false;
};

const ensureVolumePane = () => {
  if (!chart || !props.showVolume) {
    removeVolumePane();
    return;
  }

  if (volumePaneReady && volumePaneId) return;

  const options = { height: VOLUME_PANE_HEIGHT };
  if (volumePaneId) {
    options.id = volumePaneId;
  }

  const paneId = chart.createIndicator('VOL', true, options);
  if (paneId) {
    volumePaneId = paneId;
    volumePaneReady = true;
  }
};

const applyIndicator = (indicatorName) => {
  if (!chart) {
    return;
  }

  ensureVolumePane();

  // 移除之前的指标面板
  if (indicatorPaneId) {
    chart.removeIndicator(indicatorPaneId);
    indicatorPaneId = null;
  }

  // 移除主图上的指标（除了MA）
  if (mainIndicatorName && mainIndicatorName !== 'MA') {
    chart.removeIndicator('candle_pane', mainIndicatorName);
    mainIndicatorName = null;
  }

  if (!indicatorName || indicatorName === 'VOL') {
    return;
  }

  // BOLL指标显示在主图上，其他指标显示在独立面板
  if (indicatorName === 'BOLL') {
    const result = chart.createIndicator('BOLL', false, { id: 'candle_pane' });
    mainIndicatorName = 'BOLL';
  } else {
    // 其他指标创建独立面板
    const paneOptions = {
      id: `indicator-${indicatorName.toLowerCase()}`,
      height: INDICATOR_PANE_HEIGHT
    };

    indicatorPaneId = chart.createIndicator(indicatorName, true, paneOptions);
  }
};

// 初始化图表
const initChart = () => {
  if (!chartContainer.value) return;

  try {
    // 创建图表实例
    const timezone = resolveTimezoneByPeriod(props.period);
    chart = init(chartId, {
      timezone,
      customApi: {
        formatDate: (dateTimeFormat, timestamp, format) =>
          formatTimestampByPeriod(dateTimeFormat, timestamp, format)
      }
    });

    chart?.setTimezone?.(timezone);

    // 设置样式
    chart.setStyles({
      grid: {
        show: true,
        horizontal: {
          show: true,
          size: 1,
          color: '#f5f5f5',
          style: 'solid'
        },
        vertical: {
          show: false
        }
      },
      candle: {
        type: 'candle_solid',
        bar: {
          upColor: '#00C087',
          downColor: '#FF4D4F',
          upBorderColor: '#00C087',
          downBorderColor: '#FF4D4F',
          upWickColor: '#00C087',
          downWickColor: '#FF4D4F'
        },
        tooltip: {
          showRule: 'always',
          showType: 'standard',
          labels: ['O: ', 'C: ', 'H: ', 'L: ', 'V: '],
          text: {
            size: 12,
            color: '#666'
          }
        }
      },
      indicator: {
        tooltip: {
          showRule: 'always',
          showType: 'standard',
          text: {
            size: 12,
            color: '#666'
          }
        },
        bars: [{ upColor: 'rgba(0, 192, 135, 0.5)', downColor: 'rgba(255, 77, 79, 0.5)' }]
      },
      xAxis: {
        show: true,
        axisLine: {
          show: true,
          color: '#e0e0e0'
        },
        tickLine: {
          show: true,
          length: 4,
          color: '#e0e0e0'
        },
        tickText: {
          show: true,
          color: '#999',
          size: 11
        }
      },
      yAxis: {
        show: true,
        position: 'right',
        type: 'normal',
        inside: false,
        axisLine: {
          show: true,
          color: '#e0e0e0'
        },
        tickLine: {
          show: true,
          length: 4,
          color: '#e0e0e0'
        },
        tickText: {
          show: true,
          color: '#999',
          size: 11
        }
      },
      crosshair: {
        show: true,
        horizontal: {
          show: true,
          line: {
            show: true,
            style: 'dashed',
            dashValue: [4, 2],
            size: 1,
            color: '#9B7DFF'
          },
          text: {
            show: true,
            color: '#fff',
            size: 11,
            backgroundColor: '#9B7DFF'
          }
        },
        vertical: {
          show: true,
          line: {
            show: true,
            style: 'dashed',
            dashValue: [4, 2],
            size: 1,
            color: '#9B7DFF'
          },
          text: {
            show: true,
            color: '#fff',
            size: 11,
            backgroundColor: '#9B7DFF'
          }
        }
      }
    });

    chart.setZoomEnabled(true);
    chart.setScrollEnabled(true);
    chart.setLeftMinVisibleBarCount(1);
    chart.setRightMinVisibleBarCount(1);

    // 先设置数据
    updateChartData();

    // 创建主图均线指标
    const mainIndicator = chart.createIndicator('MA', false, { id: 'candle_pane' });

    // 延迟创建指标，确保数据已加载
    setTimeout(() => {
      ensureVolumePane();
      applyIndicator(props.indicator);
    }, 100);
  } catch (error) {
    console.error('初始化图表失败:', error);
    console.error('错误详情:', error.stack);
  }
};

// 格式化数据
const formatData = (data) => {
  if (!data || data.length === 0) return [];

  return data
    .map((item) => ({
      timestamp: normalizeTimestampByPeriod(
        item.time || item.timestamp || item.t,
        props.period
      ),
      open: parseFloat(item.open || item.o || 0),
      high: parseFloat(item.high || item.h || 0),
      low: parseFloat(item.low || item.l || 0),
      close: parseFloat(item.close || item.c || 0),
      volume: parseFloat(item.volume || item.v || 0)
    }))
    .filter((item) => item.open > 0 && item.high > 0 && item.low > 0 && item.close > 0);
};

// 更新图表数据
const updateChartData = () => {
  if (!chart || !props.data || props.data.length === 0) {
    return;
  }

  try {
    const formattedData = formatData(props.data);

    if (formattedData.length > 0) {
      chart.applyNewData(formattedData);
    }
  } catch (error) {
    console.error('更新图表数据失败:', error);
    console.error('错误详情:', error.stack);
  }
};

// 切换指标
const switchIndicator = (indicatorName) => {
  if (!chart) {
    console.error('图表未初始化');
    return;
  }

  try {
    applyIndicator(indicatorName);
  } catch (error) {
    console.error('切换指标失败:', error);
    console.error('错误详情:', error.stack);
  }
};

const performZoom = (scale) => {
  if (!chart) {
    console.error('图表未初始化');
    return;
  }

  try {
    const barSpaceInfo = chart.getBarSpace?.();
    const currentBarSpace = barSpaceInfo?.bar ?? 0;

    if (!currentBarSpace) return;

    if (scale > 0 && currentBarSpace >= MAX_BAR_SPACE) {
      return;
    }

    if (scale < 0 && currentBarSpace <= MIN_BAR_SPACE) {
      return;
    }

    const container = chartContainer.value;
    const centerX = container ? container.clientWidth / 2 : 0;
    chart.zoomAtCoordinate(scale, { x: centerX, y: ZOOM_CENTER_Y });
  } catch (error) {
    console.error('缩放失败:', error);
  }
};

// 缩放
const zoomIn = () => {
  performZoom(ZOOM_SCALE_STEP);
  emit('zoom-in');
};

const zoomOut = () => {
  performZoom(-ZOOM_SCALE_STEP);
  emit('zoom-out');
};

// 监听数据变化
watch(
  () => props.data,
  () => {
    updateChartData();
    nextTick(() => {
      ensureVolumePane();
      applyIndicator(props.indicator);
    });
  },
  { deep: true }
);

// 监听周期变化
watch(
  () => props.period,
  () => {
    const timezone = resolveTimezoneByPeriod(props.period);
    chart?.setTimezone?.(timezone);
    updateChartData();
  }
);

watch(
  () => props.indicator,
  (val) => {
    applyIndicator(val);
  }
);

watch(
  () => props.showVolume,
  (val) => {
    if (val) {
      ensureVolumePane();
    } else {
      removeVolumePane();
      if (props.indicator !== 'VOL' && indicatorPaneId) {
        applyIndicator(props.indicator);
      }
    }
  }
);

// 获取K线时间周期（毫秒）
const getPeriodMilliseconds = (period) => {
  const periodMap = {
    PT5M: 5 * 60 * 1000, // 5分钟
    PT15M: 15 * 60 * 1000, // 15分钟
    P1D: 24 * 60 * 60 * 1000, // 1天
    P1W: 7 * 24 * 60 * 60 * 1000, // 1周
    P1M: 30 * 24 * 60 * 60 * 1000 // 1月（近似）
  };
  return periodMap[period] || 24 * 60 * 60 * 1000;
};

// 更新实时数据
const updateRealtimeData = (realtimeData) => {
  if (!chart || !realtimeData || !props.data || props.data.length === 0) return;

  try {
    const newPrice = parseFloat(realtimeData.close);
    const newTimestamp = normalizeTimestampByPeriod(
      realtimeData.timestamp || Date.now(),
      props.period
    );

    // 获取最后一根K线
    const lastCandle = props.data[props.data.length - 1];
    if (!lastCandle) return;

    const lastTimestamp = normalizeTimestampByPeriod(
      lastCandle.time || lastCandle.timestamp || lastCandle.t,
      props.period
    );
    const periodMs = getPeriodMilliseconds(props.period);

    // 判断是否在同一个时间周期内
    const timeDiff = newTimestamp - lastTimestamp;
    const isSamePeriod = timeDiff < periodMs;

    if (isSamePeriod) {
      // 更新当前K线
      const updatedCandle = {
        timestamp: lastTimestamp,
        open: parseFloat(lastCandle.open || lastCandle.o || 0),
        high: Math.max(parseFloat(lastCandle.high || lastCandle.h || 0), newPrice),
        low: Math.min(parseFloat(lastCandle.low || lastCandle.l || 0), newPrice),
        close: newPrice,
        volume: parseFloat(lastCandle.volume || lastCandle.v || 0)
      };

      chart.updateData(updatedCandle);
    } else {
      // 创建新的K线
      const newCandle = {
        timestamp: newTimestamp,
        open: newPrice,
        high: newPrice,
        low: newPrice,
        close: newPrice,
        volume: 0
      };

      chart.updateData(newCandle);
    }
  } catch (error) {
    console.error('更新实时数据失败:', error);
  }
};

// 暴露方法给父组件
defineExpose({
  switchIndicator,
  updateIndicator: applyIndicator,
  updateRealtimeData,
  zoomIn,
  zoomOut
});

onMounted(() => {
  nextTick(() => {
    setTimeout(() => {
      initChart();
    }, 100);
  });
});

onUnmounted(() => {
  if (chart) {
    try {
      dispose(chartId);
    } catch (error) {
      console.error('销毁图表失败:', error);
    }
    chart = null;
  }
  volumePaneId = null;
  volumePaneReady = false;
  indicatorPaneId = null;
  mainIndicatorName = null;
});
</script>

<template>
  <view class="kline-chart-wrapper">
    <!-- 图表信息栏 -->
    <view class="chart-info">
      <text class="stock-name">{{ stockInfo.name || '' }}</text>
      <text class="period">{{ period }}</text>
    </view>

    <!-- 图表容器 -->
    <view :id="chartId" ref="chartContainer" class="chart-container"></view>
  </view>
</template>

<style lang="scss" scoped>
.kline-chart-wrapper {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 16rpx 0;
}

.stock-name {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}

.period {
  font-size: 24rpx;
  color: #666;
}

.chart-container {
  flex: 1;
  width: 100%;
  height: 800rpx;
}
</style>
