import CONFIG from '@/configs';

// WebSocket 封装
class StockWebSocket {
  constructor(url, key, countryId, name) {
    this.ws = null;
    this.key = key;
    this.countryId = countryId;
    this.name = name;
    this.url = `${url}?key=${key}&countryId=${countryId}`;
    this.reconnectTimer = null;
    this.reconnectDelay = 3000; // 重连延迟
    this.listeners = new Map(); // 存储监听器
    this.isManualClose = false; // 是否手动关闭
    this.heartbeatTimer = null; // 心跳定时器
    this.heartbeatInterval = 30000; // 心跳间隔30秒
  }

  connect() {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      return false;
    }

    if (this.ws && this.ws.readyState === WebSocket.CONNECTING) {
      return false;
    }

    try {
      this.ws = new WebSocket(this.url);
      this.isManualClose = false;

      this.ws.onopen = () => {
        this.clearReconnectTimer();
        this.startHeartbeat();
      };

      this.ws.onmessage = (event) => {
        try {
          if (
            typeof event.data === 'string' &&
            !event.data.startsWith('{') &&
            !event.data.startsWith('[')
          ) {
            return;
          }
          const data = JSON.parse(event.data);
          requestAnimationFrame(() => {
            this.notifyListeners(data);
          });
        } catch (error) {
          console.error(`${this.name} WebSocket消息解析失败:`, error);
        }
      };

      this.ws.onclose = () => {
        if (!this.isManualClose) {
          this.reconnect();
        }
      };

      this.ws.onerror = () => {
        this.reconnect();
      };

      return true;
    } catch (error) {
      this.reconnect();
      return false;
    }
  }

  reconnect() {
    this.clearReconnectTimer();
    this.reconnectTimer = setTimeout(() => {
      this.connect();
    }, this.reconnectDelay);
  }

  // 清除重连定时器
  clearReconnectTimer() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
  }

  send(message) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(typeof message === 'string' ? message : JSON.stringify(message));
    }
  }

  // 添加监听器
  on(event, callback) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, []);
    }
    this.listeners.get(event).push(callback);
  }

  // 移除监听器
  off(event, callback) {
    if (this.listeners.has(event)) {
      const callbacks = this.listeners.get(event);
      const index = callbacks.indexOf(callback);
      if (index > -1) {
        callbacks.splice(index, 1);
      }
    }
  }

  notifyListeners(data) {
    const allowedPages = [
      '/pages/home/index',
      '/pages/market/index',
      '/pages/orders/index',
      '/pages/stock-detail/index'
    ];

    try {
      const pages = getCurrentPages();
      if (pages.length === 0) return;

      const currentPage = pages[pages.length - 1];
      const currentRoute = '/' + currentPage.route;

      if (!allowedPages.includes(currentRoute)) {
        return;
      }

      let eventName;
      if (this.name === '墨西哥') {
        eventName = 'ws:mexico';
      } else if (this.name === '秘鲁') {
        eventName = 'ws:peru';
      } else {
        eventName = 'ws:us';
      }
      uni.$emit(eventName, data);
    } catch (error) {
      console.error('WebSocket消息通知失败:', error);
    }
  }

  // 开始心跳
  startHeartbeat() {
    this.stopHeartbeat();
    this.heartbeatTimer = setInterval(() => {
      if (this.ws && this.ws.readyState === WebSocket.OPEN) {
        this.ws.send('heartbeat');
      }
    }, this.heartbeatInterval);
  }

  // 停止心跳
  stopHeartbeat() {
    if (this.heartbeatTimer) {
      clearInterval(this.heartbeatTimer);
      this.heartbeatTimer = null;
    }
  }

  // 关闭连接
  close() {
    this.isManualClose = true;
    this.clearReconnectTimer();
    this.stopHeartbeat();
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }

  // 订阅股票
  subscribeStock(pid, callback) {
    this.on(`stock:${pid}`, callback);
  }

  // 取消订阅股票
  unsubscribeStock(pid, callback) {
    this.off(`stock:${pid}`, callback);
  }
}

// WebSocket管理器
class StockWebSocketManager {
  constructor() {
    this.connections = new Map();
    this.initConnections();
  }

  // 初始化连接
  initConnections() {
    // 美股WebSocket
    this.connections.set(
      'us',
      new StockWebSocket(
        CONFIG.wsURLs.us,
        CONFIG.wsKeys.us,
        CONFIG.wsCountryIds.us,
        '美股'
      )
    );

    // 墨西哥股票WebSocket
    this.connections.set(
      'mexico',
      new StockWebSocket(
        CONFIG.wsURLs.mexico,
        CONFIG.wsKeys.mexico,
        CONFIG.wsCountryIds.mexico,
        '墨西哥'
      )
    );

    // 秘鲁股票WebSocket
    if (CONFIG.wsURLs.peru && CONFIG.wsKeys.peru) {
      this.connections.set(
        'peru',
        new StockWebSocket(
          CONFIG.wsURLs.peru,
          CONFIG.wsKeys.peru,
          CONFIG.wsCountryIds.peru,
          '秘鲁'
        )
      );
    }
  }

  // 获取指定类型的WebSocket
  getConnection(type) {
    return this.connections.get(type);
  }

  connectAll() {
    let hasConnected = false;
    this.connections.forEach((ws) => {
      // 只连接未连接或已关闭的WebSocket
      if (
        !ws.ws ||
        ws.ws.readyState === WebSocket.CLOSED ||
        ws.ws.readyState === WebSocket.CLOSING
      ) {
        const result = ws.connect();
        if (result) hasConnected = true;
      }
    });
    return hasConnected;
  }

  isConnected() {
    let allConnected = true;
    this.connections.forEach((ws) => {
      if (!ws.ws || ws.ws.readyState !== WebSocket.OPEN) {
        allConnected = false;
      }
    });
    return allConnected;
  }

  // 关闭所有WebSocket
  closeAll() {
    this.connections.forEach((ws) => {
      ws.close();
    });
  }

  // 根据股票类型订阅
  subscribeStock(stockType, pid, callback) {
    const ws = this.getConnection(stockType);
    if (ws) {
      ws.subscribeStock(pid, callback);
    }
  }

  // 根据股票类型取消订阅
  unsubscribeStock(stockType, pid, callback) {
    const ws = this.getConnection(stockType);
    if (ws) {
      ws.unsubscribeStock(pid, callback);
    }
  }
}

// 创建单例
let wsManagerInstance = null;

export function getStockWebSocketManager() {
  if (!wsManagerInstance) {
    wsManagerInstance = new StockWebSocketManager();
  }
  return wsManagerInstance;
}

// WebSocket 初始化状态管理
let isWebSocketInitialized = false;

/**
 * 确保 WebSocket 已连接
 * @param {Object} userStore - 用户状态 store
 * @param {number} delay - 延迟时间（毫秒），默认 300ms
 * @returns {boolean} 是否成功初始化
 */
export function ensureWebSocketConnected(userStore, delay = 300) {
  // 如果已经初始化过，直接返回
  if (isWebSocketInitialized) {
    const wsManager = getStockWebSocketManager();
    // 检查是否仍然连接，如果断开则重新连接
    if (!wsManager.isConnected()) {
      console.log('WebSocket 已断开，尝试重新连接...');
      wsManager.connectAll();
    }
    return true;
  }

  // 检查用户是否登录
  if (!userStore || !userStore.isLogin) {
    console.log('用户未登录，跳过 WebSocket 初始化');
    return false;
  }

  // 标记为已初始化
  isWebSocketInitialized = true;

  // 延迟初始化 WebSocket，优先保证页面响应
  setTimeout(() => {
    const wsManager = getStockWebSocketManager();
    if (!wsManager.isConnected()) {
      console.log('初始化 WebSocket 连接...');
      wsManager.connectAll();
    }
  }, delay);

  return true;
}

/**
 * 重置 WebSocket 初始化状态（用于登出等场景）
 */
export function resetWebSocketInitialization() {
  isWebSocketInitialized = false;
  console.log('WebSocket 初始化状态已重置');
}

/**
 * 检查并连接 WebSocket（用于页面级别的连接检查）
 * @param {Object} userStore - 用户状态 store
 * @param {Function} callback - 连接成功后的回调函数
 * @param {number} waitTime - 等待连接建立的时间（毫秒），默认 1000ms
 */
export function checkAndConnectWebSocket(userStore, callback, waitTime = 1000) {
  // 检查用户是否登录
  if (!userStore || !userStore.isLogin) {
    console.log('用户未登录，跳过 WebSocket 连接');
    return;
  }

  const wsManager = getStockWebSocketManager();

  // 如果已连接，直接执行回调
  if (wsManager.isConnected()) {
    callback?.();
    return;
  }

  // 未连接则尝试连接
  console.log('WebSocket 未连接，尝试连接...');
  wsManager.connectAll();

  // 等待连接建立后执行回调
  if (callback) {
    setTimeout(() => {
      callback();
    }, waitTime);
  }
}

export default StockWebSocketManager;
