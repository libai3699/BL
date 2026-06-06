# webadmin (后台管理端) — pure-admin-thin 子项目规范

> 子项目级 CLAUDE.md — 仅含本子项目特定内容。
> 根级规范见 `../CLAUDE.md`，全局 AI 行为规范见 `~/.claude/CLAUDE.md`。

## Project Mode: `conservative`

> 基于 pure-admin-thin 模板二开。禁止跨文件重构、删除既有页面、调整目录结构。
> 所有新功能：新建 `views/xxx/index.vue` + `api/xxx.ts`，路由在 `router/modules/` 注册一行。

## 技术栈

- Vue 3.5 (Composition API + `<script setup>`) + TypeScript 5.9
- Vite 7 + Element Plus 2.11 + Tailwind CSS 4
- Pinia 3 + vue-router 4
- @pureadmin/* 组件（descriptions / table / utils）
- axios + qs
- echarts 6
- pnpm 包管理

## Sa-Token account-type

调用后端用 `admin` 账号体系。

## File Modification Restrictions

| 路径 | 禁止行为 |
|---|---|
| `build/` | 构建配置模板，禁止改 |
| `plugins/` | 框架插件，禁止改 |
| `types/` | 模板类型定义，禁止改 |
| `mock/` | 仅 dev 用，生产无效，改前确认 |
| `vite.config.ts` `tsconfig.json` | 构建配置，改前请确认 |
| `layout/` 主框架 | 禁止改（菜单 / sidebar / 主题切换） |
| 现有 `router/modules/*.ts` 路由 | 不删除/改路径，只追加 |

## 开发约定

- 新页面 → `views/<模块>/<页面名>/index.vue` + 新建 `router/modules/<模块>.ts` 路由文件
- 新接口 → `api/<模块>.ts`（一个业务一个文件，用 TypeScript 类型）
- 全局状态 → `store/modules/` 新建 store
- 表格分页 → 用 `@pureadmin/table` PureTable + ProTable 模式
- 表单 → Element Plus + 自带校验，弹窗 + Drawer 都加 `key={record.id}` 强制重建
- 多语言 → 后端 i18n code，前端用 vue-i18n 映射；不硬编码中文
- 时间显示 → 前端按浏览器时区，不用后端拼好的字符串
- 金额展示 → 必须带币种符号（USD/MXN）

## Pre-Task Checklist

- [ ] 新页面是否加到 `router/modules/`？
- [ ] 新 API 是否有 TypeScript 类型定义？
- [ ] 表格 / Modal / Drawer 编辑场景 → refetch 详情而非复用列表行？
- [ ] 列表四态（loading/empty/error/success）？
- [ ] 删除/批量操作有二次确认 `Modal.confirm`？
- [ ] 提交按钮防双击 loading + disabled？
- [ ] 改完跑 `pnpm typecheck`（强制运行，编译不通过不交付）
- [ ] 改完跑 `pnpm build:staging` 验证打包

## Gotchas

- pure-admin-thin 模板的 Tab 缓存机制：路由 name 必须与组件 name 一致，否则无法缓存
- ProTable 与原 PureTable 的 API 区别：写新页面前先看现有 `views/` 范例

## 项目特有 Lessons Learned

(开发中积累)
