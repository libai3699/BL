<script setup>
import { onMounted, useAttrs, ref } from 'vue';

import CONFIG from '@/configs';
import { getRequestHeader } from '@/apis/request';
import ZINDEX from '@/configs/zindex';

import './css/tac.css'; // 验证码css（tianai-captcha 1.5.5）
import './js/tac.min.js'; // 验证码js（tianai-captcha 1.5.5，已不依赖 jQuery）

// 透传的属性和事件对象
const attrs = useAttrs();
const emits = defineEmits(['success']);
const props = defineProps({
  // 请求数据
  requestData: {
    type: Function,
    default: () => {}
  }
});

const refIsTacShow = ref(false);
const refTac = ref(null);

onMounted(() => {});

// 添加第三个箭头元素
const addThirdArrow = (retryCount = 0, maxRetries = 10) => {
  const sliderBtn = document.querySelector('.slider-move-btn');

  // 检查滑块按钮是否存在且未添加第三个箭头
  if (sliderBtn && !sliderBtn.querySelector('.third-arrow')) {
    const thirdArrow = document.createElement('div');
    thirdArrow.className = 'third-arrow';
    sliderBtn.appendChild(thirdArrow);
    return true;
  }

  // 达到最大重试次数时停止
  if (retryCount >= maxRetries) {
    return false;
  }

  // 滑块按钮未加载完成时延迟重试
  if (!sliderBtn) {
    setTimeout(() => {
      addThirdArrow(retryCount + 1, maxRetries);
    }, 200);
  }

  return false;
};
// 替换默认图标为自定义SVG
const replaceTacIcons = () => {
  // 关闭按钮SVG
  const closeSvg = `<svg class="tac-close-svg" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9" stroke="var(--tac-icon-color)" stroke-width="2"/>
    <path d="M16 8.63538L15.3646 8L12 11.3646L8.63538 8L8 8.63538L11.3646 12L8 15.3646L8.63538 16L12 12.6354L15.3646 16L16 15.3646L12.6354 12L16 8.63538Z" fill="var(--tac-icon-color)" stroke="var(--tac-icon-color)"/>
  </svg>`;

  // 刷新按钮SVG
  const refreshSvg = `<svg class="tac-refresh-svg" width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M21.375 7.125L18.375 10.125C17.625 6.875 14.625 4.375 11.125 4.375C7 4.375 3.625 7.75 3.625 11.875C3.625 16 7 19.375 11.125 19.375C15.125 19.375 18.5 16.25 18.625 12.25L20.875 10C21 10.625 21.125 11.25 21.125 12C21.125 17.5 16.625 22 11.125 22C5.625 22 1.125 17.5 1.125 12C1.125 6.5 5.625 2 11.125 2C14.375 2 17.375 3.625 19.125 6L19.375 5.625H22.875L21.375 7.125Z" fill="var(--tac-icon-color)"/>
  </svg>`;

  // 批量替换图标
  document
    .querySelectorAll('.close-btn, #tianai-captcha-slider-close-btn')
    .forEach((btn) => (btn.innerHTML = closeSvg));
  document
    .querySelectorAll('.refresh-btn, #tianai-captcha-slider-refresh-btn')
    .forEach((btn) => (btn.innerHTML = refreshSvg));
};

// 事件处理函数引用数组
let refreshBtnHandlers = [];

// 绑定刷新按钮点击事件
const bindRefreshBtnEvent = () => {
  // 清除之前的事件监听器避免重复绑定
  unbindRefreshBtnEvent();

  const refreshBtns = document.querySelectorAll(
    '.refresh-btn, #tianai-captcha-slider-refresh-btn'
  );
  refreshBtns.forEach((btn) => {
    const handler = () => {
      // 等待验证码重新加载完成后执行
      setTimeout(() => {
        addThirdArrow();
        replaceTacIcons();
        bindRefreshBtnEvent(); // 重新绑定事件监听器
      }, 1000);
    };

    btn.addEventListener('click', handler);
    // 存储按钮和处理函数的映射关系
    refreshBtnHandlers.push({ btn, handler });
  });
};

// 移除刷新按钮事件监听器
const unbindRefreshBtnEvent = () => {
  refreshBtnHandlers.forEach(({ btn, handler }) => {
    btn.removeEventListener('click', handler);
  });
  // 清空处理函数数组
  refreshBtnHandlers = [];
};

// 给父组件调用的方法
defineExpose({
  start() {
    // 样式配置
    const config = {
      requestCaptchaDataUrl: `${CONFIG.verificationURL}/verification/getVerificationCode`,
      validCaptchaUrl: `${CONFIG.verificationURL}/verification/verificationCode`,
      bindEl: '#captcha-div',
      requestHeaders: getRequestHeader(),
      requestData: props.requestData(),
      // 验证成功回调函数
      validSuccess: (res, _c, tac) => {
        tac.destroyWindow();
        emits('success', res);
      },
      onCreate() {
        // 显示验证码遮罩层
        refIsTacShow.value = true;
        // 防止页面滚动
        document.body.style.overflow = 'hidden';
        document.body.style.touchAction = 'none';
        // 延迟执行确保DOM渲染完成
        setTimeout(() => {
          addThirdArrow();
          replaceTacIcons();
          bindRefreshBtnEvent(); // 绑定刷新按钮事件
        }, 500);
      },
      onDestroy() {
        // 隐藏验证码遮罩层
        refIsTacShow.value = false;
        // 恢复页面滚动
        document.body.style.overflow = '';
        document.body.style.touchAction = '';
        unbindRefreshBtnEvent(); // 清理刷新按钮事件监听器
      },
      textGroup: {
        sliderTip: uni.$t('component.tacVerification.textGroup.sliderTip'),
        validFail: uni.$t('component.tacVerification.textGroup.validFail'),
        validFailNot4001: uni.$t('component.tacVerification.textGroup.validFailNot4001'),
        validSuccess: uni.$t('component.tacVerification.textGroup.validSuccess')
      },
      // 验证失败的回调函数(可忽略，如果不自定义 validFail 方法时，会使用默认的)
      validFail: (_res, _c, tac) => {
        // 验证失败后重新拉取验证码
        tac.reloadCaptcha();
        // 重新加载后需要重新添加第三个箭头和替换SVG图标
        setTimeout(() => {
          addThirdArrow();
          replaceTacIcons();
          bindRefreshBtnEvent(); // 重新绑定刷新按钮事件
        }, 600);
      }
    };
    const tac = new window.TAC(config);
    refTac.value = tac;
    tac.init();
  }
});
</script>

<template>
  <view v-bind="attrs">
    <div
      class="u-captcha"
      id="captcha-div"
      :style="{ zIndex: ZINDEX.tacVertification }"
    ></div>
    <div
      v-if="refIsTacShow"
      class="u-captcha-overlay"
      :style="{ zIndex: ZINDEX.tacVertificationOverlay }"
    ></div>
  </view>
</template>

<style lang="scss" scoped>
.u-captcha {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

// 验证码遮罩层
.u-captcha-overlay {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  background: var(--tac-overlay-bg); // 毛玻璃背景色
  backdrop-filter: var(--tac-overlay-backdrop-filter); // 背景模糊效果

  // 只防止遮罩层本身的触摸滚动
  touch-action: none;
  -webkit-user-select: none;
  user-select: none;
}

// ---------- TAC验证组件样式 ----------
// 验证码背景图片容器
:deep(#tianai-captcha-bg-img) {
  background-color: var(--tac-bg-img-bg) !important;
}

// 滑动时的阴影效果
:deep(.slider-move-shadow) {
  background-color: var(--tac-shadow-bg) !important;
  box-shadow: var(--tac-shadow-box-shadow) !important;
}

// 旋转验证码背景图片
:deep(#tianai-captcha.tianai-captcha-rotate2 #tianai-captcha-bg-img) {
  background-color: var(--tac-bg-img-bg) !important;
}

// ---------- 滑块验证码样式 ----------
:deep(#tianai-captcha.tianai-captcha-slider) {
  // 滑块轨道样式
  .slider-move-track {
    background: var(--tac-slider-track-bg) !important;
    border: var(--tac-slider-track-border) !important;
    color: var(--tac-slider-track-color) !important;
  }

  // 滑块按钮样式
  .slider-move-btn {
    background-color: var(--tac-slider-btn-bg) !important;
    box-shadow: var(--tac-slider-btn-box-shadow) !important;

    // 前两个箭头通用样式
    &::before,
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      width: 6px;
      height: 6px;
      border-right: 2px solid var(--tac-slider-btn-arrow-color);
      border-bottom: 2px solid var(--tac-slider-btn-arrow-color);
      transform: translateY(-50%) rotate(-45deg);
      animation: slider-arrow-blink 1.8s infinite ease-in-out;
    }

    // 第一个箭头位置
    &::before {
      left: 25%;
      animation-delay: 0s;
    }

    // 第二个箭头位置
    &::after {
      left: 40%;
      animation-delay: 0.3s;
    }
  }

  // 第三个箭头样式
  .third-arrow {
    position: absolute;
    top: 50%;
    left: 55%;
    width: 6px;
    height: 6px;
    border-right: 2px solid var(--tac-slider-btn-arrow-color);
    border-bottom: 2px solid var(--tac-slider-btn-arrow-color);
    transform: translateY(-50%) rotate(-45deg);
    animation: slider-arrow-blink 1.8s infinite ease-in-out;
    animation-delay: 0.6s;
    pointer-events: none;
  }

  // 滑块提示文字样式
  .slider-tip {
    color: var(--tac-slider-tip-color) !important;
  }

  // 滑动轨迹遮罩样式
  #tianai-captcha-slider-move-track-mask {
    background-color: var(--tac-slider-mask-bg) !important;
    border-color: var(--tac-slider-mask-border) !important;
    opacity: var(--tac-slider-mask-opacity) !important;
  }
}
</style>
