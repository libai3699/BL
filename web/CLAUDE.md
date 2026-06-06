# web (用户端 App) — uni-app 子项目规范

> 子项目级 CLAUDE.md — 仅含本子项目特定内容。
> 根级规范见 `../CLAUDE.md`，全局 AI 行为规范见 `~/.claude/CLAUDE.md`。

## Project Mode: `conservative`

> 基于 uni-app + uview-plus 模板二开。禁止跨文件重构、删除既有页面、调整目录结构。
> 所有新功能：新建 `pages/xxx/yyy.vue` + `apis/xxx.js`，路由在 `pages.json` 注册一行。

## 技术栈

- uni-app 3.x (Vue 3 Composition API) — 多端：H5 / 微信 / Alipay / App / Harmony
- Pinia 2.0 + pinia-plugin-persist (持久化)
- uview-plus 3 (UI 组件库)
- klinecharts + lightweight-charts (K 线图)
- technicalindicators (技术指标)
- vue-i18n 9 (中/英/西)
- crypto-js, dayjs

## Sa-Token account-type

调用后端用 `user` 账号体系，Header `Authorization: <token>`。

## File Modification Restrictions

| 路径 | 禁止行为 |
|---|---|
| `_codeTemplate/` | 模板代码，禁止改 |
| `_docs/` `_reference/` | 文档目录，禁止改 |
| `vite.config.js` `manifest.json` | 构建配置，改前请确认 |
| `pages.json` 现有路由 | 不删除/改路径，只追加 |

## 开发约定

- 新页面 → 新建 `pages/<模块>/<页面名>.vue` + 在 `pages.json` 加一行
- 新接口 → 在 `apis/` 下新建 `*.js` 模块（一个业务一个文件）
- 全局状态 → 在 `stores/` 新建 store，不要往现有 store 里堆
- 多语言文案 → `locales/zh.js` / `en.js` / `es.js` 三处同步加
- 金额展示 → 始终带币种前缀（USD/MXN），不要只显示数字
- K 线图组件 → 复用现有 klinecharts 封装，禁止重写

## Pre-Task Checklist

- [ ] 新页面是否加到 `pages.json`？
- [ ] 三语 (zh/en/es) 文案是否同步？
- [ ] 接口调用是否用了正确的 `apis/` 封装？
- [ ] 金额显示是否带 USD/MXN 标识？
- [ ] 列表四态（loading/empty/error/success）？
- [ ] 提交按钮防双击 loading？
- [ ] 改完跑 `yarn build:test` 验证编译

## Gotchas

(开发中积累)
