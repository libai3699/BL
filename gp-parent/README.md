# gp-parent

Spring Cloud microservices skeleton (Spring Boot 2.3.2 / Spring Cloud Hoxton.SR9 / Nacos).

## Modules

| Module | Port | Purpose |
|--------|------|---------|
| `gp-back-service` | 13002 | 后台服务 (Admin API) |
| `gp-control-back-service` | 13009 | 代理后台服务 (Agent Admin API) |
| `gp-front-service` | 13004 | 前台服务 (User API, Sa-Token) |
| `gp-consumer` | 13003 | MQ 消费服务 (RabbitMQ) |
| `gp-xxl` | 13005 | XXL-Job 处理服务 |
| `gp-maintain` | 13010 | 维护工具服务 |
| `gp-verification` | 13012 | 验证码 / KYC 服务 |
| `gp-common` | — | 共享模块组 (9 sub-modules) |

`gp-common` sub-modules:
- `gp-common-base` · `gp-common-feign` · `gp-common-game` · `gp-common-mybatisplus`
- `core-spring-boot-start` · `datasource-spring-boot-starter` · `mybatisplus-spring-boot-start` · `rabbitmq-spring-boot-start`

## Build

```bash
mvn clean install -DskipTests                       # build all
mvn -pl gp-back-service clean install -DskipTests   # build one module
mvn clean install -DskipTests -Pgp-dev              # dev profile
mvn clean install -DskipTests -Pgp-prod             # prod profile
```

Profiles: `gp-local` (default), `gp-dev`, `gp-prod`. Fill in Nacos / Sentinel addresses in the parent `pom.xml` `<profiles>` section.

## Infrastructure

Nacos (required) · MySQL 8 · Redis · RabbitMQ · Sentinel (optional)

## Status

Scaffolding only — all 7 services start with empty business packages. Add your `controller/`, `service/`, `mapper/`, `domain/` packages under each service's `src/main/java/com/gp/...` tree.
