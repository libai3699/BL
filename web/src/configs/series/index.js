import CONFIG from '../index.js';
import seriesDefault from './default.js';

// 公用配置
const commonConfig = {};

export const series = {
  // default配置
  default: Object.assign({}, commonConfig, seriesDefault)
};

// 最终应用的系列配置
let SERIES = series[CONFIG.series];
export default SERIES;
