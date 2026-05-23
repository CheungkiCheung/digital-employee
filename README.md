# Digital Employee

数字员工项目，基于 Java 17、Spring Boot 3.4、Maven 多模块和 DDD 六边形架构搭建。

## Quick Start

```bash
./init.sh
```

`./init.sh` 是本仓库的标准启动和验证入口，会检查 harness 状态、DDD 架构边界，并执行 Maven 构建。

## Module Layout

```text
digital-employee-api             API contracts and response DTOs
digital-employee-app             Spring Boot application assembly
digital-employee-case            Use-case orchestration
digital-employee-domain          Domain model, services, ports, repositories
digital-employee-infrastructure  Adapters, DAO, Redis, gateways, config
digital-employee-trigger         HTTP/job/listener entrypoints
digital-employee-types           Shared enums, exceptions, constants
docs/harness                     Agent harness rules and verification docs
```

## Agent Harness

Future agent sessions must start from [AGENTS.md](AGENTS.md), then run:

```bash
./init.sh
```

The current feature state is tracked in [feature_list.json](feature_list.json). Work-in-progress is limited to one feature at a time.

## Original Scaffold Links

- docker 使用文档：[https://bugstack.cn/md/road-map/docker.html](https://bugstack.cn/md/road-map/docker.html)
- DDD 教程；
  - [DDD 概念理论](https://bugstack.cn/md/road-map/ddd-guide-01.html)
  - [DDD 建模方法](https://bugstack.cn/md/road-map/ddd-guide-02.html)
  - [DDD 工程模型](https://bugstack.cn/md/road-map/ddd-guide-03.html)
  - [DDD 架构设计](https://bugstack.cn/md/road-map/ddd.html)
  - [DDD 建模案例](https://bugstack.cn/md/road-map/ddd-model.html)
