# webagentadmin (代理后台) — pure-admin-thin 子项目规范

> 子项目级 CLAUDE.md — 仅含本子项目特定内容。
> 根级规范见 `../CLAUDE.md`，全局 AI 行为规范见 `~/.claude/CLAUDE.md`。
> 技术栈与 `../webadmin/CLAUDE.md` 完全相同（同一模板），差异仅在 Sa-Token account-type 和业务面向对象。

## Project Mode: `conservative`

## 技术栈

与 `webadmin/` 完全一致（pure-admin-thin / Vue 3.5 / TS 5.9 / Vite 7 / Element Plus / Tailwind 4 / Pinia 3 / pnpm）

## Sa-Token account-type

调用后端用 `agent` 账号体系。

## 角色定位

代理（分销）后台 — 服务 `agent_user` 表的用户。常见功能：
- 查看下级用户与佣金（`commission_task`, `load_earn`）
- 拉新邀请码 / 邀请链接管理
- 下级业绩报表
- 代理审核（agent_user 申请）

## File Modification Restrictions

与 webadmin 一致：禁止改 `build/` `plugins/` `types/` `layout/` `vite.config.ts` `tsconfig.json` 与现有路由。

## 与 webadmin 共享代码

⚠️ 两个后台基于同一模板，相同业务（如菜单管理、登录页）逻辑结构相同。
- 若需要在两个后台都加同一个功能 → 分别在两边实现，**不**共享代码（两个 git repo 独立）
- 若发现 bug → 两边同步修复

## Pre-Task Checklist

与 webadmin 一致（详见 `../webadmin/CLAUDE.md`），增加：
- [ ] 接口调用是否走 `agent` Sa-Token 体系？
- [ ] 数据范围是否限定为"当前代理及其下级"？（避免越权看全站数据）

## Gotchas

(开发中积累)
