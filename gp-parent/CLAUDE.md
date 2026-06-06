<!-- Reference: pom.xml, gp-common/*, each module's bootstrap.yml -->
<!-- Global rules are in the user-level CLAUDE.md -->

# gp-parent — Java Backend Standards

## Project Mode: `owner`

> 新搭建的项目骨架，架构由你自己设计。允许跨文件重构、删除废弃代码、调整目录结构。

## Pre-Task Checklist (Java backend specific)

- [ ] Touches `gp-common`? → Grep all callers across modules first; state impact scope to user
- [ ] New API endpoint? → new Controller file under `controller/`; never append to existing
- [ ] New Service file under `service/` — existing file gets one route line only
- [ ] New Feign client method? → update `gp-common-feign` and notify all callers
- [ ] MQ message structure changed? → update producer and consumer simultaneously
- [ ] Fund operation? → go through a fund manager (to be implemented), not direct UPDATE
- [ ] New DB table? → include SQL migration file in `/file/` directory

---

## Build Commands

```bash
mvn clean install -DskipTests                       # build all
mvn -pl gp-back-service clean install -DskipTests   # build one module
mvn clean install -DskipTests -Pgp-dev              # dev profile
mvn clean install -DskipTests -Pgp-prod             # prod profile
```

**Profiles:** `gp-local` (default), `gp-dev`, `gp-prod`. Nacos / Sentinel addresses are blank by default — fill them into the parent `pom.xml` `<profiles>` block.
**First build:** install `gp-common` first — all other modules depend on it.

---

## Module Architecture

| Module | Port | Purpose | Auth |
|--------|------|---------|------|
| `gp-back-service` | 13002 | 后台 Admin API | Spring Security + JWT (待实现) |
| `gp-control-back-service` | 13009 | 代理后台 API | Spring Security + JWT (待实现) |
| `gp-front-service` | 13004 | 前台 User API | Sa-Token + Redis |
| `gp-consumer` | 13003 | RabbitMQ 消费 | — |
| `gp-xxl` | 13005 | XXL-Job 调度 | — |
| `gp-maintain` | 13010 | 维护工具 | — |
| `gp-verification` | 13012 | 验证码 / KYC | — |

`gp-common` = 9 sub-modules providing shared models, utilities, Feign clients, and Spring Boot starters.

---

## Technology Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 2.3.2.RELEASE |
| Spring Cloud | Hoxton.SR9 |
| Spring Cloud Alibaba | 2.2.6.RELEASE |
| Nacos | service discovery + config |
| Auth (back/control-back) | Spring Security + JWT (待实现) |
| Auth (front) | Sa-Token + Redis |
| ORM | MyBatis Plus 3.4.2 + Druid (MySQL 8) |
| Cache | Redis (Jedis/Redisson) + Caffeine |
| Messaging | RabbitMQ (custom starter in `gp-common/rabbitmq-spring-boot-start`) |
| Scheduler | XXL-Job 2.3.1 |

---

## Configuration

- Per-service: `src/main/resources/bootstrap.yml` (Spring + Nacos wiring only — no secrets)
- Global Nacos: `application-${profile}.yml`
- Service Nacos: `${spring.application.name}-${profile}.yml`
- **All env-specific values (keys, URLs, thresholds) must live in Nacos — never hardcoded in bootstrap.yml**

---

## Infrastructure

Nacos (required) · MySQL 8 · Redis · RabbitMQ · Sentinel (optional)

SQL init files: place in `/file/` directory (Chinese filenames describing the change).

---

## Java-specific Development Rules

1. New Controller in a new file under `controller/` — never append to existing
2. New business logic in a new Service file — existing file gets one route line only
3. `gp-common` changes: must be backward-compatible (overload, don't modify signatures)
4. Feign interface change: update `gp-common-feign` + all callers in one go
5. All fund operations should go through a dedicated fund manager class — never direct SQL update
6. Payment callbacks: verify signature → check idempotency → process → respond ≤200ms

---

## Current Skeleton State

刚搭建完成，每个服务的 `src/main/java/com/gp/...` 下都是**空业务包**。你需要：
1. 在每个服务的 Java 根包下新建 `controller/`, `service/`, `mapper/`, `domain/` 包
2. 后台服务实现 Spring Security + JWT 认证（参考资料：`com.tg` 原项目的 `framework/security`）
3. 前台服务 Sa-Token 已配置在 bootstrap.yml，需在代码层补充 `SaTokenConfigure`
4. 在 Nacos 上创建 namespace、上传 `application-${profile}.yml` 全局配置 + 每个服务的 `${name}-${profile}.yml`
5. 填写父 pom.xml profiles 中的 Nacos / Sentinel 地址

## Lessons Learned (Java backend specific)

(None yet — cross-project lessons go in the user-level CLAUDE.md)
