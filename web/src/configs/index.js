// 配置项 只能是js，因为要给不同编译环境使用
import envDev from './env/dev.js';
import envTest from './env/test.js';
import envProd from './env/prod.js';

export const compileEnv = process.env.APP_ENV || 'prod';

// 公共配置
const configCommon = {
  defaultLocale: 'zh_CN',
  // 默认国家地区 因为美国和加拿大都是1，所以还是用对象比较好
  defaultArea: {
    name: 'China',
    code: '86'
  },
  // 默认分页大小
  pageSize: 10,

  // 是否使用静态数据 - 防止后台发版、无法访问
  isUseStaticData: false,

  // 静态资源地址
  staticPath: typeof location === 'object' ? `${location.origin}/static` : ''
};

// 环境配置字典
const config = {
  // 开发环境配置
  dev: Object.assign({}, configCommon, envDev),
  // 测试环境配置
  test: Object.assign({}, configCommon, envTest),
  // 生产环境配置
  prod: Object.assign({}, configCommon, envProd)
};

// 最终应用的环境 - 编译环境
let CONFIG = config[compileEnv];

if (CONFIG.isUseStaticData) {
  console.error(`请注意：当前部分接口使用本地数据`);
}

// mock接口地址
export const mockApiBase = `${CONFIG.staticPath}/_mocks`;

export default CONFIG;
