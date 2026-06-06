/**
 * 账户余额管理工具
 * 根据stockType区分US和MXG账户
 */

import { reqUserGetUserInfo } from '@/apis/user';

// 货币单位映射
export const CURRENCY_MAP = {
  us: 'USD',
  mxg: 'MXN',
  mx: 'MXN'
};

// 股票类型映射
export const STOCK_TYPE_MAP = {
  us: 'us',
  usa: 'us',
  mxg: 'mxg',
  mx: 'mxg',
  mexico: 'mxg'
};

/**
 * 标准化股票类型
 * @param {string} stockType - 股票类型
 * @returns {string} 标准化后的类型 'us' 或 'mxg'
 */
export function normalizeStockType(stockType) {
  if (!stockType) return 'mxg';
  const type = String(stockType).toLowerCase();
  return STOCK_TYPE_MAP[type] || 'mxg';
}

/**
 * 获取货币单位
 * @param {string} stockType - 股票类型
 * @returns {string} 货币单位 'USD' 或 'MXN'
 */
export function getCurrency(stockType) {
  const normalizedType = normalizeStockType(stockType);
  return CURRENCY_MAP[normalizedType] || 'MXN';
}

/**
 * 获取账户余额信息
 * @param {string} stockType - 股票类型 'us' 或 'mxg'
 * @returns {Promise<Object>} 账户信息
 */
export async function getAccountBalance(stockType) {
  try {
    const res = await reqUserGetUserInfo();
    const userInfo = res?.data || res || {};
    
    const normalizedType = normalizeStockType(stockType);
    
    // 根据股票类型返回对应的账户余额
    if (normalizedType === 'us') {
      return {
        // 美股账户 - 使用 usdAmt
        availableBalance: Number(userInfo.usdAmt || 0),
        totalBalance: Number(userInfo.usdTotalAmt || userInfo.usdAmt || 0),
        frozenBalance: Number(userInfo.usdFrozenAmt || 0),
        positionValue: Number(userInfo.usdPositionAmt || 0),
        currency: 'USD',
        stockType: 'us'
      };
    } else {
      return {
        // 墨西哥股账户 - 使用 mxAmt
        availableBalance: Number(userInfo.mxAmt || 0),
        totalBalance: Number(userInfo.mxTotalAmt || userInfo.mxAmt || 0),
        frozenBalance: Number(userInfo.mxFrozenAmt || 0),
        positionValue: Number(userInfo.mxPositionAmt || 0),
        currency: 'MXN',
        stockType: 'mxg'
      };
    }
  } catch (error) {
    console.error('获取账户余额失败', error);
    return {
      availableBalance: 0,
      totalBalance: 0,
      frozenBalance: 0,
      positionValue: 0,
      currency: getCurrency(stockType),
      stockType: normalizeStockType(stockType)
    };
  }
}

/**
 * 格式化金额显示
 * @param {number} amount - 金额
 * @param {string} stockType - 股票类型
 * @param {number} decimals - 小数位数，默认2位
 * @returns {string} 格式化后的金额字符串
 */
export function formatAmount(amount, stockType, decimals = 2) {
  const currency = getCurrency(stockType);
  const value = Number(amount || 0).toFixed(decimals);
  return `${value} ${currency}`;
}

/**
 * 格式化价格显示（带货币单位）
 * @param {number} price - 价格
 * @param {string} stockType - 股票类型
 * @returns {string} 格式化后的价格字符串
 */
export function formatPrice(price, stockType) {
  const currency = getCurrency(stockType);
  return `${Number(price || 0).toFixed(2)}/${currency}`;
}
