<script setup>
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';
import CustomCalendar from '@/components/u-calendar-plus/u-calendar.vue';

const { t } = useI18n();

const props = defineProps({
  show: {
    type: Boolean,
    default: false
  },
  startDate: {
    type: String,
    default: ''
  },
  endDate: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['confirm', 'close']);

// 格式化日期为字符串
const formatDate = (date) => {
  // 如果是字符串，将横杠替换为斜杠，确保 iOS Safari 能正确解析
  const dateStr = typeof date === 'string' ? date.replace(/-/g, '/') : date;
  const d = new Date(dateStr);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 最小日期：三个月前
const minDate = computed(() => {
  const date = new Date();
  date.setMonth(date.getMonth() - 1);
  return formatDate(date);
});

// 最大日期：今天
const maxDate = computed(() => {
  return formatDate(new Date());
});

// 默认日期：如果有值则显示该值的范围
const defaultDate = computed(() => {
  if (props.startDate && props.endDate) {
    return [props.startDate, props.endDate];
  }
  return [];
});

// 确认选择
const handleConfirm = (e) => {
  if (Array.isArray(e) && e.length >= 2) {
    const startDate = formatDate(e[0]) + ' 00:00:00';
    const endDate = formatDate(e[e.length - 1]) + ' 23:59:59';

    // 触发confirm事件
    emit('confirm', {
      value: [startDate, endDate]
    });

    // 确认后也要关闭弹窗
    emit('close');
  }
};

// 关闭
const handleClose = () => {
  console.log('DateRangePicker - 关闭');
  emit('close');
};
</script>

<template>
  <CustomCalendar :show="show" :title="t('common.selectDate')" mode="range" :monthNum="2"
    :startText="t('common.startTime')" :endText="t('common.endTime')" :confirmText="t('common.confirm')"
    :defaultDate="defaultDate" :minDate="minDate" :maxDate="maxDate" @confirm="handleConfirm" @close="handleClose">
  </CustomCalendar>
</template>
