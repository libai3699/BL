<script setup>
import { ref, watch } from 'vue';
import { onLaunch, onShow, onHide, onPageNotFound } from '@dcloudio/uni-app';
import { useI18n } from 'vue-i18n';
import 'nprogress/nprogress.css';

import { getUnreadMessageCount } from '@/apis/message';
import { useCommonStore } from '@/stores/common';
import { useUserStore } from '@/stores/user';
import CONFIG from '@/configs';
import { decodeFromFileData, getFileContent, setAppCanBack } from '@/utils';
import { getService } from '@/utils/biz';
import { checkCurrentRoute, goToErrorPage, setupH5RouteGuard } from '@/utils/errorPage';
import { ensureMaintainChecked, isMaintaining } from '@/utils/maintain';

const { t } = useI18n();
uni.$t = t;
ensureMaintainChecked();

const commonStore = useCommonStore();
const userStore = useUserStore();

let unreadTimer = null;
const isFetchingUnread = ref(false);
let previousUnread = null;
const treasureHouseConfig = {
  timer: null
};

const stopUnreadTimer = () => {
  if (unreadTimer) {
    clearInterval(unreadTimer);
    unreadTimer = null;
  }
};

const handleUnreadNotification = (currentCount) => {
  if (previousUnread !== null && currentCount > previousUnread) {
    uni.showToast({
      title: uni.$t('message.newUnreadMessage'),
      icon: 'none'
    });
  }
  previousUnread = currentCount;
};

const fetchUnreadCount = async () => {
  if (isFetchingUnread.value) {
    return;
  }

  if (
    commonStore.maintenance.needMaintenance === 'loading' ||
    isMaintaining(commonStore)
  ) {
    return;
  }

  if (!userStore.isLogin) {
    stopUnreadTimer();
    commonStore.setUnreadCount(0);
    previousUnread = 0;
    return;
  }

  try {
    isFetchingUnread.value = true;
    const res = await getUnreadMessageCount();
    const numericCount = Number(res ?? 0);
    const count =
      Number.isFinite(numericCount) && numericCount > 0 ? Math.floor(numericCount) : 0;
    handleUnreadNotification(count);
    commonStore.setUnreadCount(count);
  } catch (error) {
    console.error('获取未读消息数量失败', error);
  } finally {
    isFetchingUnread.value = false;
  }
};

const startUnreadTimer = () => {
  stopUnreadTimer();

  if (!userStore.isLogin) {
    return;
  }

  if (
    commonStore.maintenance.needMaintenance === 'loading' ||
    isMaintaining(commonStore)
  ) {
    return;
  }

  fetchUnreadCount();
  unreadTimer = setInterval(fetchUnreadCount, 5000);
};

function solveTreasureHouse() {
  const isApp = process.env.IS_APP;
  if (!isApp) return;
  // 如果之前有选择过线路，配置先初始化设置
  if (commonStore.storage_cur) {
    CONFIG.baseURL = commonStore.storage_cur.domain;
  }
  // 当前只有app才有线路切换的逻辑
  commonStore.getIp();
  onSolveFile();
}

// 处理文件
function onSolveFile() {
  const treasureHouse = CONFIG.treasureHouse;
  if (!treasureHouse || treasureHouse.length === 0) return;

  clearTimeout(treasureHouseConfig.timer);

  treasureHouseConfig.isSolving = true;
  treasureHouseConfig.isError = false;
  // 设置一个超时，超时后如果还在加载，则认为加载失败
  treasureHouseConfig.timer = setTimeout(() => {
    if (treasureHouseConfig.isSolving) {
      solveImageErrorDefault('加载超时，请重试');
    }
  }, 10000);

  // 获取文件内容
  getFileContent(
    treasureHouse,
    (data) => solveFileData(data),
    (e) => {
      clearTimeout(treasureHouseConfig.timer);
      solveImageErrorDefault('加载失败，点击重试');
    }
  );
}
function solveFileData(data) {
  clearTimeout(treasureHouseConfig.timer);

  treasureHouseConfig.isSolving = true;
  treasureHouseConfig.isError = false;

  let decrypted = decodeFromFileData(data);

  // 如果图片的网关地址是空，认为加载失败
  if (decrypted == null || decrypted === '') {
    solveImageErrorDefault('获取资源失败，请重试');
    return;
  }

  decrypted = decrypted.split(',');
  CONFIG.baseURLs = decrypted;
  if (process.env.NODE_ENV === 'development') {
    console.log('baseURLs', CONFIG.baseURLs);
  }

  // 开始执行跳转逻辑
  checkBaseURLs();
}
// 文件处理失败或超时的兜底措施
function solveImageErrorDefault(errorText) {
  // treasureHouseConfig.errorText = errorText;
  // treasureHouseConfig.isSolving = false;
  // treasureHouseConfig.isError = true;

  if (process.env.NODE_ENV === 'development') {
    console.log('加载失败，使用兜底策略');
    CONFIG.baseURLs = CONFIG.baseURLs.split(',');
    console.log('baseURLs', CONFIG.baseURLs);
  }

  // 开始执行跳转逻辑
  checkBaseURLs();
}
// 统一处理域名
async function checkBaseURLs() {
  commonStore.setApiConfig(
    CONFIG.baseURLs.join(','),
    () => {},
    (lines) => {
      // 弹出提示
      commonStore.openNormalPopup({
        title: uni.$t('line.error'),
        onEnsure() {
          checkBaseURLs();
        }
      });
    }
  );
}

watch(
  () => userStore.token.value,
  (token) => {
    if (token) {
      startUnreadTimer();
    } else {
      stopUnreadTimer();
      commonStore.setUnreadCount(0);
      previousUnread = 0;
    }
  },
  { immediate: true }
);

watch(
  () => commonStore.maintenance.needMaintenance,
  (val) => {
    if (val === 'loading' || val === '1') {
      stopUnreadTimer();
      return;
    }
    if (userStore.isLogin) {
      startUnreadTimer();
    }
  }
);

// app处调用，设置版本号给本项目
window.setVersion = (version) => {
  console.log('页面调用设置版本号', version);
  commonStore.version = version;
};

// app版本检查
window.checkVersion = (version) => {
  console.log('页面调用版本检查', version);
  // 热更新不做，先不管了
  // api.getVersion(version).then((res) => {
  //   console.log('页面调用版本检查结果', res);
  //   window.uniSdk.postMessage({
  //     data: {
  //       type: 'checkAndUpgrade',
  //       data: res.data
  //     }
  //   });
  // });
};

onPageNotFound(() => {
  goToErrorPage('404');
});

onLaunch(() => {
  setupH5RouteGuard();

  if (userStore.isLogin) {
    startUnreadTimer();
  }
  // 给uniapp的webview用的
  window.myHistory = () => {
    // 获取有后退按钮dom元素
    const $backIcons = document.querySelectorAll('.j-back-icon');
    // 获取有移动端登录弹层
    const $loginMobile = document.querySelector('.j-skip-login-mobile');
    // 获取显示状态下的后退按钮dom元素
    const $visibleBackIcons = [];
    $backIcons.forEach((item) => {
      if (item.offsetParent) {
        $visibleBackIcons.push(item);
      }
    });
    // 有显示状态下的后退按钮元素时,取最后一个(层级最高的)
    if ($visibleBackIcons.length) {
      const $aimBackIcon = $visibleBackIcons.slice(-1)[0];
      $aimBackIcon.click();
    } else if ($loginMobile) {
      // 有移动端登录弹层，关闭
      $loginMobile.click();
    } else {
      history.back();
    }
    setTimeout(() => {
      // 处理返回后，是否可以返回上一页
      setAppCanBack();
    }, 100);
  };
  // 初始化时判断是否可以返回上一页
  setAppCanBack();

  // 处理宝藏
  solveTreasureHouse();

  // 预拉取客服链接写入 localStorage
  getService();
});

onShow(() => {
  checkCurrentRoute();

  if (userStore.isLogin) {
    startUnreadTimer();
  }
});

onHide(() => {
  stopUnreadTimer();
});
</script>

<style lang="scss">
/*每个页面公共css */
@use '@/uni.scss' as uni;
</style>
