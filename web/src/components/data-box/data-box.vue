<script setup>
import { onMounted, watch, ref, onBeforeUnmount } from 'vue';

import sLoadmore from './s-loadmore.vue';

const emits = defineEmits(['onErrorRetry', 'loadmore', 'scrolltolower']);
const props = defineProps({
  // 是否定位在外部容器水平垂直居中
  isPosition: {
    type: Boolean,
    default: false
  },
  // 状态显示类型：block：显示为块状（带对应图标） noImg 无图标块状 onlyText 只有文本
  statusShowType: {
    type: String,
    default: 'block'
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: true
  },
  // 加载图类型
  loadingType: {
    type: String,
    default: undefined
  },
  // 加载文本
  loadingText: {
    type: String,
    default: undefined
  },
  // 加载状态延迟显示，默认300ms
  loadingDelay: {
    type: Number,
    default: 300
  },
  // 错误组件单独显示模式
  errorShowType: {
    type: String,
    default: undefined
  },
  // 是否错误状态
  isError: {
    type: Boolean,
    default: false
  },
  // 报错类型
  errorType: {
    type: String,
    default: ''
  },
  // 报错文本
  errorText: {
    type: String,
    default: undefined
  },
  // 报错按钮文本
  errorBtnText: {
    type: String,
    default: undefined
  },
  // 是否空状态
  isEmpty: {
    type: Boolean,
    default: false
  },
  // 空状态类型
  emptyType: {
    type: String,
    default: undefined
  },
  // 空状态文本
  emptyText: {
    type: String,
    default: undefined
  },
  // 自定义空状态图片路径，当emptyType为custom时使用
  customSrc: {
    type: String,
    default: ''
  },
  // 记载更多状态，如果没传则是空字符串不出现
  loadmoreStatus: {
    type: String,
    default: ''
  },
  // 距离底部多少像素触发加载
  loadmoreBottomDistance: {
    type: Number,
    default: 200
  },
  // 容器是否使用scroll-view
  isScrollView: {
    type: Boolean,
    default: false
  }
});

let timer = null;
// 是初始化状态
const isInit = ref(true);
// 组件内加载状态
const curLoading = ref(false);

watch(() => props.loading, solveCurLoading);

function onErrorRetry() {
  emits('onErrorRetry');
}
function solveCurLoading(loading) {
  clearTimeout(timer);
  if (loading) {
    timer = setTimeout(() => {
      curLoading.value = loading;
    }, props.loadingDelay);
  } else {
    curLoading.value = loading;
    isInit.value = false;
  }
}

const onScroll = () => {
  uni.$u.throttle(
    () => {
      // 如果滚动到底部，触发组件内的滚动到底部事件
      const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
      const scrollHeight =
        document.documentElement.scrollHeight || document.body.scrollHeight;
      const windowHeight =
        document.documentElement.clientHeight || document.body.clientHeight;
      if (
        scrollTop > 2 &&
        Math.ceil(scrollTop + windowHeight) >= scrollHeight - props.loadmoreBottomDistance
      ) {
        emits('scrolltolower');
      }
    },
    20,
    true
  );
};

onMounted(addEventScroll);
onBeforeUnmount(removeEventScroll);

function addEventScroll() {
  solveCurLoading(props.loading);
  // 如果使用滚动容器，不处理
  if (props.isScrollView) return;
  // 如果没有加载更多的设置，不处理
  if (props.loadmoreStatus === '') return;
  // 添加事件
  document.addEventListener('scroll', onScroll, true);
}

function removeEventScroll() {
  document.removeEventListener('scroll', onScroll, true);
}

defineExpose({
  addEventScroll,
  removeEventScroll
});
</script>

<template>
  <view class="m-data-box">
    <!-- loading状态，但没有触发组件内等待时，不显示内容。 -->
    <!-- 必须是初始化状态，如果已经加载过数据了，这个view不会出现，避免出现闪动的情况 -->
    <!-- 如果是报错重试，需要显示这个view，因为怕用户感觉点击重试按钮没有效果 -->
    <view v-if="(isInit || isError) && loading && !curLoading" />
    <!-- 组件内等待 -->
    <loading-box
      v-else-if="curLoading"
      :isPosition="isPosition"
      :showType="statusShowType"
      :text="loadingText"
      :type="loadingType"
    />
    <!-- 报错机制 -->
    <error-box
      v-else-if="isError"
      :isPosition="isPosition"
      :showType="errorShowType || statusShowType"
      :text="errorText"
      :btnText="errorBtnText"
      @onErrorRetry="onErrorRetry"
      :type="errorType"
    />
    <!-- 空状态 -->
    <empty-box
      v-else-if="isEmpty"
      :isPosition="isPosition"
      :showType="statusShowType"
      :text="emptyText"
      :type="emptyType"
      :customSrc="customSrc"
    />
    <!-- 没设置加载更多时，直接显示页面元素 -->
    <slot v-else-if="loadmoreStatus === ''"></slot>
    <block v-else>
      <!-- 使用滚动容器 -->
      <scroll-view
        v-if="isScrollView"
        scroll-y
        class="u-scroll-view"
        @scrolltolower="emits('scrolltolower')"
      >
        <slot></slot>
        <s-loadmore :loadmoreStatus="loadmoreStatus" @loadmore="emits('loadmore')" />
      </scroll-view>
      <!-- 不使用滚动容器 -->
      <block v-else>
        <slot></slot>
        <s-loadmore :loadmoreStatus="loadmoreStatus" @loadmore="emits('loadmore')" />
      </block>
    </block>
  </view>
</template>

<style lang="scss">
.m-data-box {
  position: relative;
  flex: 1;
  width: 100%;
  box-sizing: border-box;
  .u-scroll-view {
    height: 100%;
  }
  .u-loadmore-wrap {
    padding: 20rpx 0;
    .u-line-1 {
      overflow: auto;
    }
  }
}
</style>
