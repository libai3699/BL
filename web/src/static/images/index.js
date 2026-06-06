import SERIES from '@/configs/series';

const imageModules = import.meta.glob(
  '/src/static/images/series/**/*.{png,jpg,jpeg,webp,svg}',
  {
    eager: true,
    import: 'default'
  }
);

const imageModulesCommon = import.meta.glob(
  '/src/static/images/common/**/*.{png,jpg,jpeg,webp,svg}',
  {
    eager: true,
    import: 'default'
  }
);

const imageMap = {};
const imageMapCommon = {};

for (const fullPath in imageModules) {
  // 例如: /src/static/images/series/theme1/home/kanban/xgsg.png
  const relativePath = fullPath.replace('/src/static/images/series/', '');
  const [theme, ...restPathParts] = relativePath.split('/');

  // 去掉文件扩展名，例如 xgsg.png -> xgsg
  const fileName = restPathParts[restPathParts.length - 1];
  const nameWithoutExt = fileName.replace(/\.(png|jpg|jpeg|webp|svg)$/, '');

  // 构建嵌套结构
  let current = imageMap;
  if (!current[theme]) current[theme] = {};
  current = current[theme];

  // 遍历除最后一项的中间路径（如 home/kanban）
  for (let i = 0; i < restPathParts.length - 1; i++) {
    const part = restPathParts[i];
    if (!current[part]) current[part] = {};
    current = current[part];
  }

  // 设置最终的图片路径
  current[nameWithoutExt] = imageModules[fullPath];
}

for (const fullPath in imageModulesCommon) {
  const relativePath = fullPath.replace('/src/static/images/common/', '');
  const [theme, ...restPathParts] = relativePath.split('/');
  const fileName = restPathParts[restPathParts.length - 1] || theme;
  const nameWithoutExt = fileName.replace(/\.(png|jpg|jpeg|webp|svg)$/, '');
  let current = imageMapCommon;
  if (restPathParts.length === 0) {
    current[nameWithoutExt] = imageModulesCommon[fullPath];
    continue;
  }
  if (!current[theme]) current[theme] = {};
  current = current[theme];
  for (let i = 0; i < restPathParts.length - 1; i++) {
    const part = restPathParts[i];
    if (!current[part]) current[part] = {};
    current = current[part];
  }
  current[nameWithoutExt] = imageModulesCommon[fullPath];
}

export { imageMapCommon };

export default imageMap[SERIES.name];
