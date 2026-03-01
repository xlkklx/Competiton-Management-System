# Competiton-Management-System
# 高校竞赛管理系统

## 项目介绍

高校竞赛管理系统是一个基于Java的桌面应用程序，用于管理高校内的各类竞赛活动。该系统支持管理员、学生和竞赛管理者三种角色，提供竞赛发布、报名、成绩管理等功能。

## 技术栈

- **开发语言**：Java 17
- **构建工具**：Maven
- **数据库**：MySQL
- **数据库驱动**：mysql-connector-java-5.1.48
- **界面技术**：Java Swing

## 项目结构

```
UniversityCompetitions/
├── src/
│   └── main/
│       └── java/
│           ├── Dao/          # 数据访问层
│           ├── Entity/        # 实体类
│           ├── Service/       # 服务层
│           ├── Util/          # 工具类
│           └── View/          # 视图层
├── .gitignore
├── mysql-connector-java-5.1.48.jar
└── pom.xml
```

## 主要功能

### 管理员角色
- 系统管理
- 用户管理
- 竞赛管理
- 通知管理

### 学生角色
- 查看竞赛信息
- 报名参加竞赛
- 查看个人成绩
- 查看通知

### 竞赛管理者角色
- 发布竞赛
- 管理竞赛报名
- 录入和管理成绩
- 发布通知

## 数据库配置

系统使用MySQL数据库，数据库配置信息位于 `Util/DBUtil.java` 文件中：

```java
private static final String URL = "jdbc:mysql://localhost:3306/高校竞赛管理系统?serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "080620";
```

## 安装与运行

### 前置条件

- JDK 17 或更高版本
- Maven 3.6 或更高版本
- MySQL 5.7 或更高版本

### 步骤

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd UniversityCompetitions
   ```

2. **创建数据库**
   - 在MySQL中创建名为 `高校竞赛管理系统` 的数据库
   - 导入相应的表结构（请参考数据库设计文档）

3. **修改数据库配置**
   - 编辑 `Util/DBUtil.java` 文件，修改数据库连接信息

4. **构建项目**
   ```bash
   mvn clean package
   ```

5. **运行项目**
   - 执行生成的JAR文件，或在IDE中运行 `View/LoginFrame.java`

## 系统登录

- **管理员**：用户名和密码（默认值请参考系统初始化数据）
- **学生**：使用学号和密码登录
- **竞赛管理者**：使用工号和密码登录

## 项目特点

1. **多角色管理**：支持管理员、学生和竞赛管理者三种角色
2. **功能完整**：涵盖竞赛发布、报名、成绩管理等全流程
3. **界面友好**：基于Java Swing的桌面应用，操作简单直观
4. **数据安全**：使用数据库连接池和参数化查询，确保数据安全

## 未来规划

1. 增加更多竞赛类型的支持
2. 优化系统性能和用户体验
3. 增加数据分析和报表功能
4. 开发Web版本，支持跨平台访问

## 联系方式

如有问题或建议，请联系：

- 项目维护者：[您的姓名]
- 邮箱：[您的邮箱]
- 电话：[您的电话]

## 许可证

本项目采用 [MIT 许可证](LICENSE)。
