<script setup>
import { computed, onMounted, reactive, ref } from 'vue';

import DateRangePicker from '@/components/date-range-picker/index.vue';
import {
  mapOptionsAccount,
  mapOptionsAmount,
  mapOptionsOrder
} from '@/configs/mapOptions';
import { reqUserGetAmountChangeType } from '@/apis/user/amoutChange';

const usesDefaultValue = {
  amount: {
    name: 'amount',
    paramName: 'amountType',
    defaultValue: 2
  },
  order: {
    name: 'order',
    paramName: 'orderType',
    defaultValue: ''
  },
  account: {
    name: 'account',
    paramName: 'accountType',
    defaultValue: ''
  },
  transaction: {
    name: 'transaction',
    paramName: 'type',
    defaultValue: ''
  },
  daterange: {
    name: 'daterange',
    params: [
      {
        paramName: 'startTime',
        defaultValue: ''
      },
      {
        paramName: 'endTime',
        defaultValue: ''
      }
    ]
  }
};

const emits = defineEmits(['init', 'select']);
const props = defineProps({
  // 使用哪些筛选，数组控制显示和顺序
  // 可传入以下字符串参数，也可传入对象参数，设置扩展参数
  // amount-币种
  // account-收支
  // transaction-账变类型
  // order-订单类型
  // daterange-时间范围
  uses: {
    type: Array,
    default: () => []
  }
});

// 筛选条件
const filters = reactive({});

const refPopupSelectOptions = ref(null);
const showDatePicker = ref(false);
// 是否已选择过时间
const hasSelectedTime = ref(false);
const refFilters = ref({});
const refOptionsTransaction = ref(null);

// 使用哪些筛选的键值
const cpdUsesKeys = computed(() =>
  props.uses.map((t) => {
    return t?.name || t;
  })
);
// 使用哪些筛选的对象数组，如果传的是字符串，则使用默认值
const cpdUses = computed(() =>
  props.uses.map((t) => {
    if (t.constructor === Object) return t;
    return usesDefaultValue[t];
  })
);

// 当前选中的币种文本
const currentCurrencyText = computed(() => {
  const item = mapOptionsAmount.find((t) => t.value === filters.amountType);
  return item && item?.value !== '' ? uni.$t(item.label) : uni.$t('commission.currency');
});

// 当前选中的方向文本
const currentDirectionText = computed(() => {
  const item = mapOptionsAccount.find((t) => t.value === filters.accountType);
  return item && item?.value !== '' ? uni.$t(item?.label) : uni.$t('filters.direction');
});

// 当前选中的订单类型文本
const currentOrderText = computed(() => {
  const item = mapOptionsOrder.find((t) => t.value === filters.orderType);
  return item && item?.value !== '' ? uni.$t(item.label) : uni.$t('commission.type');
});

// 当前选中的账变类型文本
const currentTransactionText = computed(() => {
  const item = refOptionsTransaction.value?.find((t) => t.value === filters.type);
  return item && item?.value !== '' ? item.label : uni.$t('filters.source');
});

// 显示日期范围（格式：MM-DD至MM-DD）
const displayDateRange = computed(() => {
  // 如果从未选择过时间，显示"时间"
  if (!hasSelectedTime.value) {
    return uni.$t('commission.time');
  }

  if (
    !filters[refFilters.value.daterange.params[0].paramName] ||
    !filters[refFilters.value.daterange.params[1].paramName]
  ) {
    return uni.$t('commission.time');
  }
  // 只取日期部分：YYYY-MM-DD
  const startDate = filters[refFilters.value.daterange.params[0].paramName].split(' ')[0];
  const endDate = filters[refFilters.value.daterange.params[1].paramName].split(' ')[0];

  // 拆成 MM-DD
  const [startYear, startMonth, startDay] = startDate.split('-');
  const [endYear, endMonth, endDay] = endDate.split('-');

  return `${startMonth}-${startDay}${uni.$t('commission.to')}${endMonth}-${endDay}`;
});

// 选择订单类型
const showPopupOrder = () => {
  refPopupSelectOptions.value.open({
    title: uni.$t('commission.type'),
    options: mapOptionsOrder,
    value: filters[refFilters.value.order.paramName],
    success(value) {
      filters[refFilters.value.order.paramName] = value.value;
      emits('select', filters);
    }
  });
};

// 选择币种
function showPopupAmount() {
  refPopupSelectOptions.value.open({
    title: uni.$t('commission.currency'),
    options: mapOptionsAmount,
    value: filters[refFilters.value.amount.paramName],
    success(value) {
      filters[refFilters.value.amount.paramName] = value.value;
      emits('select', filters);
    }
  });
}

// 选择收支
function showPopupAccount() {
  refPopupSelectOptions.value.open({
    title: uni.$t('filters.direction'),
    options: mapOptionsAccount,
    value: filters[refFilters.value.account.paramName],
    success(value) {
      filters[refFilters.value.account.paramName] = value.value;
      emits('select', filters);
    }
  });
}

// 选择来源
function showPopupTransaction() {
  if (refOptionsTransaction.value) {
    cb();
  } else {
    reqUserGetAmountChangeType().then((res) => {
      let options = [
        {
          label: uni.$t('common.all'),
          value: ''
        }
      ];
      options = options.concat(
        res.map((item) => {
          return {
            label: item.typeName,
            value: item.type
          };
        })
      );
      refOptionsTransaction.value = options;
      cb();
    });
  }

  function cb() {
    refPopupSelectOptions.value.open({
      title: uni.$t('filters.source'),
      options: refOptionsTransaction.value,
      optionsNeedLang: false,
      value: filters[refFilters.value.transaction.paramName],
      success(value) {
        filters[refFilters.value.transaction.paramName] = value.value;
        emits('select', filters);
      }
    });
  }
}

// 日期选择确认
const handleDateConfirm = (e) => {
  console.log('页面 - 日期选择器返回的数据:', e);

  // DateRangePicker返回的是已格式化的数组 [startTime, endTime]
  const values = e.value;
  console.log('页面 - 提取的values:', values);

  if (Array.isArray(values) && values.length === 2) {
    filters[refFilters.value.daterange.params[0].paramName] = formatDate(values[0]);
    filters[refFilters.value.daterange.params[1].paramName] = formatDate(values[1]);
    hasSelectedTime.value = true;

    // 重新加载数据
    emits('select', filters);
  } else {
    console.log('页面 - 日期格式不正确，不调用接口');
  }
};

// 格式化日期
const formatDate = (date) => {
  // 如果是字符串，将横杠替换为斜杠，确保 iOS Safari 能正确解析
  const dateStr = typeof date === 'string' ? date.replace(/-/g, '/') : date;
  const d = new Date(dateStr);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 初始化日期范围（默认最近一周）
const initDateRange = () => {
  const end = new Date();
  const start = new Date();
  start.setDate(start.getDate() - 7);

  filters[refFilters.value.daterange.params[0].paramName] = formatDate(start);
  filters[refFilters.value.daterange.params[1].paramName] = formatDate(end);

  // 初始化时不标记为已选择，保持显示"时间"
  hasSelectedTime.value = false;
};

onMounted(() => {
  // 初始化筛选条件
  cpdUses.value.forEach((t) => {
    if (t.name !== 'daterange') {
      filters[t.paramName] = t.defaultValue;
    }
    // 将对象转载到ref
    refFilters.value[t.name] = t;
  });

  // 时间段特别处理
  if (cpdUsesKeys.value.includes('daterange')) {
    initDateRange();
  }

  emits('init', filters);
});
</script>

<template>
  <view class="filter-bar" v-if="cpdUses.length" v-bind="$attrs">
    <template v-for="item in cpdUses" :key="item.name">
      <view class="filter-item" v-if="item.name === 'order'" @click="showPopupOrder">
        <text class="filter-text">{{ currentOrderText }}</text>
        <image class="arrow-icon" src="/static/svgs/arrow-down.svg" mode="aspectFit" />
      </view>

      <view
        class="filter-item"
        v-if="item.name === 'daterange'"
        @click="showDatePicker = true"
      >
        <text class="filter-text">{{ displayDateRange }}</text>
        <image class="arrow-icon" src="/static/svgs/arrow-down.svg" mode="aspectFit" />
      </view>

      <view class="filter-item" v-if="item.name === 'amount'" @click="showPopupAmount">
        <text class="filter-text">{{ currentCurrencyText }}</text>
        <image class="arrow-icon" src="/static/svgs/arrow-down.svg" mode="aspectFit" />
      </view>

      <view class="filter-item" v-if="item.name === 'account'" @click="showPopupAccount">
        <text class="filter-text">{{ currentDirectionText }}</text>
        <image class="arrow-icon" src="/static/svgs/arrow-down.svg" mode="aspectFit" />
      </view>

      <view
        class="filter-item"
        v-if="item.name === 'transaction'"
        @click="showPopupTransaction"
      >
        <text class="filter-text">{{ currentTransactionText }}</text>
        <image class="arrow-icon" src="/static/svgs/arrow-down.svg" mode="aspectFit" />
      </view>
    </template>
  </view>

  <!-- 日期范围选择器 -->
  <DateRangePicker
    v-if="refFilters.daterange"
    :show="showDatePicker"
    :startDate="filters[refFilters.daterange.params[0].paramName]"
    :endDate="filters[refFilters.daterange.params[1].paramName]"
    @confirm="handleDateConfirm"
    @close="showDatePicker = false"
  />

  <!-- 类型筛选弹窗 -->
  <c-popup-select-options ref="refPopupSelectOptions" />
</template>

<style lang="scss" scoped>
// 筛选栏
.filter-bar {
  display: flex;
  background: #fff;
  gap: 16rpx 36rpx;
  .filter-item {
    display: flex;
    align-items: center;
    gap: 8rpx;
    padding: 12rpx 0;
    border-radius: 32rpx;
    white-space: nowrap;
    word-break: break-all;
    .filter-text {
      font-size: 26rpx;
      color: #666;
    }
    .arrow-icon {
      width: 24rpx;
      height: 24rpx;
    }
  }
}

.u-popup {
  flex: 0 0 !important;
}
</style>
