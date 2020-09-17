spring-boot-project目录是在Spring Boot 2.0版本发布后新增的目录层级，并将原来在Spring Boot 1.5.x版本中的一级模块作为spring-boot-project的子模块。该模块包含了Spring Boot所有的核心功能。

·spring-boot：Spring Boot核心代码，也是入口类SpringApplication类所在项目，是本书重点介绍的内容。
·spring-boot-actuator：提供应用程序的监控、统计、管理及自定义等相关功能。
·spring-boot-actuator-autoconfigure：针对actuator提供的自动配置功能。
·spring-boot-autoconfigure：Spring Boot自动配置核心功能，默认集成了多种常见框架的自动配置类等。
·spring-boot-cli：命令工具，提供快速搭建项目原型、启动服务、执行Groovy脚本等功能。
·spring-boot-dependencies：依赖和插件的版本信息。
·spring-boot-devtools：开发者工具，提供热部署、实时加载、禁用缓存等提升开发效率的功能。
·spring-boot-docs：参考文档相关内容。
·spring-boot-parent：spring-boot-dependencies的子模块，是其他项目的父模块。
·spring-boot-properties-migrator：Spring Boot 2.0版本新增的模块，支持升级版本配置属性的迁移。
·spring-boot-starters：Spring Boot以预定义的方式集成了其他应用的starter集合。
·spring-boot-test：测试功能相关代码。
·spring-boot-test-autoconfigure：测试功能自动配置相关代码。
·spring-boot-tools：Spring Boot工具支持模块，包含Ant、Maven、Gradle等构建工具