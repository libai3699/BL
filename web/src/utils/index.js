import CryptoJS from 'crypto-js';
import { useCommonStore } from '@/stores/common';

// 判断能否使用JSON.parse方法
export function isCanParse(str) {
  if (typeof str !== 'string') return false;
  try {
    JSON.parse(str);
    return true;
  } catch (e) {
    return false;
  }
}

// 获取JSON.parse后的值
// needRecursion 是否需要递归获取数据最原始类型，默认不需要
export const getJSONParse = (param, needRecursion = false) => {
  // 不是字符串类型，返回空字符串
  if (typeof param !== 'string') return '';
  // 清除JSON字符串中的换行符
  param = param.replace(/\n+/gi, '');
  // 如果引号都是\\"这样的格式才转换成"
  if (param.match(/\\"/g)?.length === param.match(/"/g)?.length) {
    param = param.replace(/\\"/gi, '"');
  }
  // 处理多级嵌套JSON字符串
  if (/\\\\"/.test(param)) {
    param = param.replace(/\\\\"/gi, '\\"');
  }
  try {
    let res = JSON.parse(param);
    // 需要递归获取最原始数据，则继续对返回的字符串进行parse
    if (typeof res === 'string' && needRecursion) {
      return getJSONParse(res);
    } else {
      return res;
    }
  } catch (e) {
    // console.log('e', e);
    return param;
  }
};

// 是否为uniapp中使用webview嵌套h5的app端
window.isAppFromUniApp = isAppFromUniApp;
export function isAppFromUniApp() {
  return window.navigator.userAgent.indexOf('uni-app') > -1;
}

export function setAppCanBack() {
  const curUrl = window.location.href;
  const isHome = curUrl.endsWith('/#/') || curUrl.endsWith('/pages/home/index');
  const setBackData = {
    data: isHome,
    type: 'setCanBack'
  };
  if (isAppFromUniApp()) {
    window.uniSdk.postMessage({
      data: setBackData
    });
  }
}

export function decryptAes(hexString) {
  // 如果传入的是对象，则直接返回
  if (hexString.constructor === Object) return hexString;

  const base64Key = CONFIG.base64Key; // 替换为你的Base64编码密钥
  // 将Base64密钥转换为CryptoJS词语对象
  const key = CryptoJS.enc.Utf8.parse(base64Key);

  // 将十六进制的数据转换为CryptoJS的词语对象
  const encryptedHexStr = CryptoJS.enc.Hex.parse(hexString);
  const encryptedBase64Str = CryptoJS.enc.Base64.stringify(encryptedHexStr);

  // 解密数据
  const decryptedData = CryptoJS.AES.decrypt(encryptedBase64Str, key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  });

  // 将解密后的数据转换为utf8字符串
  const decryptedText = decryptedData.toString(CryptoJS.enc.Utf8);

  return JSON.parse(decryptedText);
}

// 传入一个对象，将参数值未定义的过滤并返回
export const getValidParamObject = (obj) => {
  const result = {};
  for (let i in obj) {
    if (obj[i] !== undefined) {
      result[i] = obj[i];
    }
  }
  return JSON.parse(JSON.stringify(result));
};

// 处理swiper水平滚动时候禁用页面垂直滚动
// selector 选择器，如：'#id'、'.class'
export function solveSwiperVerticalScroll(selector) {
  const swiperContainer = document.querySelector(selector);

  if (!swiperContainer) {
    return;
  }

  // 拖动回调
  function handleTouchMove(e) {
    const moveX = e.touches[0].clientX;
    const moveY = e.touches[0].clientY;

    const diffX = moveX - startX;
    const diffY = moveY - startY;

    // 判断是否为水平滑动
    if (Math.abs(diffX) > Math.abs(diffY)) {
      // 水平滑动，禁用垂直滚动
      e.preventDefault();
    }
    // 如果是垂直滑动，不阻止默认行为
  }

  // 解除拖动回调
  function handleDetouchMove() {
    // 移除 touchmove 监听器
    swiperContainer.removeEventListener('touchmove', handleTouchMove);
  }

  let startX = 0;
  let startY = 0;

  swiperContainer.addEventListener('touchstart', (e) => {
    startX = e.touches[0].clientX;
    startY = e.touches[0].clientY;

    // 添加 touchmove 监听器
    swiperContainer.addEventListener('touchmove', handleTouchMove, { passive: false });
  });

  swiperContainer.addEventListener('touchend', handleDetouchMove);
  swiperContainer.addEventListener('touchcancel', handleDetouchMove);
}

// name=参数名称，val=参数值，isRefresh=是否刷新（0不刷新，1刷新）
// 使用方法：replaceParamVal("id", "888", 0)
export function updateUrlParam(name, val, isRefresh = 0) {
  var url = window.location.href.toString();
  var pattern = '[?]' + name + '=([^&]*)';
  var pattern2 = '[&]' + name + '=([^&]*)';
  var replaceText = name + '=' + val;
  var replaceText1 = '?' + replaceText;
  var replaceText2 = '&' + replaceText;
  if (url.match(pattern)) {
    var tmp = '/\\?(' + name + '=)([^&]*)/gi';
    var nUrl = url.replace(eval(tmp), replaceText1);
  } else if (url.match(pattern2)) {
    var tmp = '/&(' + name + '=)([^&]*)/gi';
    var nUrl = url.replace(eval(tmp), replaceText2);
  } else {
    if (url.match('[?]')) {
      var nUrl = url + '&' + replaceText;
    } else {
      var nUrl = url + '?' + replaceText;
    }
  }
  if (isRefresh) {
    window.location.href = nUrl;
  }
  var stateObject = { id: '' };
  var title = '';
  history.replaceState(stateObject, title, nUrl);
}

/**
 * 检测所有线路的响应状态
 * @param {Object} options 配置项
 * @param {Array} options.lines 线路数组
 * @param {Function} options.onFirstSuccess 第一个成功线路的回调（只触发一次）
 * @param {Function} options.onEachComplete 每一个线路检测完成时回调
 * @param {Function} options.onAllComplete 所有线路检测完成后回调
 * @param {Function} options.onAllFail 所有线路检测失败后回调
 */
export async function testApi(options = {}) {
  const TIMEOUT_MS = 10000;
  const lines = options.lines || [];
  let hasFirstSuccessCalled = false;

  // 初始化每条线路状态
  lines.forEach((line) => {
    line.status = 'loading'; // 正在检测
    line.timeout = ''; // 响应时间（待填）
  });

  // 封装 fetch 为带超时的 Promise
  const fetchWithTimeout = (url, timeout = TIMEOUT_MS) => {
    return new Promise((resolve, reject) => {
      const controller = new AbortController(); // 创建一个 AbortController 实例
      const signal = controller.signal;

      const timer = setTimeout(() => {
        controller.abort(); // 超时后中断请求
        reject(new Error('timeout'));
      }, timeout);

      fetch(url, { signal })
        .then((response) => {
          clearTimeout(timer);
          if (!response.ok) {
            reject(new Error('Request failed'));
          } else {
            resolve(response);
          }
        })
        .catch((err) => {
          clearTimeout(timer);
          reject(err);
        });
    });
  };

  // 遍历所有线路并检测
  const tasks = lines.map(async (line) => {
    const start = Date.now();
    const url = `${line.domain}/index/aws/elb/health`;

    try {
      const res = await fetchWithTimeout(url, TIMEOUT_MS);
      const duration = Date.now() - start;
      const text = await res.text(); // 获取返回的文本

      if (res.status === 200 && text.trim() === 'ok') {
        line.timeout = `${duration}ms`;
        line.status = duration < 1000 ? 'good' : 'warning';

        if (!hasFirstSuccessCalled) {
          hasFirstSuccessCalled = true;
          options.onFirstSuccess?.(line);
        }
      } else {
        line.timeout = uni.$t('line.timeout');
        line.status = 'bad';
      }
    } catch (err) {
      line.timeout = uni.$t('line.timeout');
      line.status = 'bad';
    }

    // 更新当前线路状态
    const commonStore = useCommonStore();
    if (commonStore.storage_cur?.domain === line.domain) {
      commonStore.storage_cur.timeout = line.timeout;
      commonStore.storage_cur.status = line.status;
    }
    // 单个线路检测完成回调
    options.onEachComplete?.(line);
  });

  // 所有线路检测完成后回调
  await Promise.all(tasks);
  options.onAllComplete?.(lines);

  // 所有线路检测失败后回调
  if (lines.every((line) => line.status === 'bad')) {
    options.onAllFail?.(lines);
  }
}

function arrayBufferToString(buffer) {
  const uint8Array = new Uint8Array(buffer);
  let str = '';
  for (let i = 0; i < uint8Array.length; i++) {
    str += String.fromCharCode(uint8Array[i]);
  }
  return str;
}

// 获取文件内容
export function getFileContent(urls, success, error) {
  let hasFirstSuccessCalled = false;

  // 使用 fetch 代替 uni.request
  const request = (url) => {
    url += '?t=' + Date.now();
    return new Promise((resolve, reject) => {
      const controller = new AbortController(); // 创建一个 AbortController 实例
      const signal = controller.signal;

      fetch(url, { method: 'GET', signal })
        .then((response) => {
          if (!response.ok) {
            reject(new Error(`Request failed with status: ${response.status}`));
          } else {
            return response.arrayBuffer(); // 获取响应数据并转为 arraybuffer
          }
        })
        .then((data) => {
          const encrypted = arrayBufferToString(data); // 将 arrayBuffer 转为字符串
          resolve(encrypted);
        })
        .catch(reject);

      // 可选的超时控制（如果需要）
      const timeout = setTimeout(() => {
        controller.abort(); // 请求超时，调用 abort
        reject(new Error('Request timed out'));
      }, 10000); // 设置超时时间为 10 秒

      // 清理超时定时器
      signal.addEventListener('abort', () => {
        clearTimeout(timeout);
      });
    });
  };

  let errorCount = 0;
  urls.forEach((url) => {
    request(url)
      .then((data) => {
        if (!hasFirstSuccessCalled) {
          hasFirstSuccessCalled = true;
          success(data);
        }
      })
      .catch((err) => {
        errorCount++;
        // 全部都报错时，抛出
        if (errorCount >= urls.length) {
          error(err);
        }
      });
  });
}

// 解密文件加密
export function decodeFromFileData(encrypted) {
  const BASE64_CHARS =
    'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
  const COMPLEX_KEY = 'hJFOcaQn386MUd5Z';

  function getKeyOffsets(key) {
    const offsets = [];
    for (let i = 0; i < key.length; i++) {
      offsets.push(key.charCodeAt(i) % 7);
    }
    return offsets;
  }

  function decodeDomain(encoded) {
    const keyOffsets = getKeyOffsets(COMPLEX_KEY);
    const keyLen = keyOffsets.length;

    const base64 = encoded
      .split('')
      .reverse()
      .map((char, i) => {
        const index = BASE64_CHARS.indexOf(char);
        if (index === -1) throw new Error('非法字符: ' + char);

        const shift = keyOffsets[i % keyLen];
        const originalIndex = (index - shift + BASE64_CHARS.length) % BASE64_CHARS.length;
        return BASE64_CHARS.charAt(originalIndex);
      })
      .join('');

    return atob(base64);
  }

  // 示例用法：
  const decrypted = decodeDomain(encrypted);

  return decrypted;
}
