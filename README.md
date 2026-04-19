# RamiAgent

RamiAgent 是一个基于 Spring Boot 和 Spring AI 的智能对话应用，集成了 Deepseek 大模型，为 10 岁女孩打造个人 AI 助手。

## 🚀 项目特性

- **Spring Boot 3.5.0** - 现代化的 Java 框架
- **Spring AI 1.0.0** - 集成 AI 能力
- **Deepseek Chat** - 使用 Deepseek 大模型提供智能对话
- **流式响应** - 实时流式返回 AI 对话内容
- **Java 25** - 使用最新的 Java 版本
- **UTF-8 支持** - 完整支持中文交互

## 📋 系统要求

- Java 25 或更高版本
- Maven 3.8.0 或更高版本
- Deepseek API 密钥

## 🔧 环境配置

### 1. 获取 Deepseek API 密钥

访问 [Deepseek](https://www.deepseek.com/) 获取 API 密钥。

### 2. 配置环境变量

```bash
export DEEPSEEK_API_KEY="your-api-key-here"
```

或在 `deepseek.properties` 文件中配置：

```properties
spring.ai.deepseek.api-key=your-api-key-here
```

## 📦 安装与编译

### 克隆项目

```bash
git clone https://github.com/ramiran216/RamiAgent.git
cd RamiAgent
```

### 编译项目

```bash
mvn clean compile
```

### 运行项目

```bash
mvn spring-boot:run
```

或直接打包运行：

```bash
mvn clean package
java -jar target/rami-agent-0.0.1-SNAPSHOT.jar
```

## 🌐 API 端点

### 1. 健康检查

```
GET http://localhost:8080/
```

返回：
```
Hello, Spring Boot!
```

### 2. Deepseek 智能对话

```
GET http://localhost:8080/deepseek?message=你好
```

**参数：**
- `message` (可选): 用户提问内容，默认值为 "你好，请介绍一下你自己"

**返回：** 流式返回 AI 对话内容

**示例：**

```bash
curl "http://localhost:8080/deepseek?message=你好，你叫什么名字？"
```

## 📁 项目结构

```
RamiAgent/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── RamiAgentApplication.java      # 主应用类
│   │   │       └── HelloController.java           # 控制器
│   │   └── resources/
│   │       ├── application.properties             # 应用配置
│   │       └── deepseek.properties               # Deepseek 配置
│   └── test/
├── pom.xml                                        # Maven 依赖配置
└── README.md                                      # 项目说明文档
```

## 🔌 依赖库

主要依赖：

- `spring-boot-starter-web` - Web 框架
- `spring-boot-starter-actuator` - 应用监控
- `spring-ai-starter-model-deepseek` - Deepseek AI 集成

查看完整依赖列表：`pom.xml`

## 🏗️ 构建与部署

### 本地构建

```bash
mvn clean package
```

### Docker 部署

创建 `Dockerfile`：

```dockerfile
FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
COPY target/rami-agent-0.0.1-SNAPSHOT.jar app.jar
ENV DEEPSEEK_API_KEY=${DEEPSEEK_API_KEY}
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：

```bash
docker build -t rami-agent .
docker run -e DEEPSEEK_API_KEY=your-api-key -p 8080:8080 rami-agent
```

## ⚙️ 配置说明

### application.properties

```properties
# 服务器配置
server.port=8080

# Spring AI Deepseek 配置
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat

# UTF-8 编码
server.servlet.encoding.force=true
server.servlet.encoding.charset=UTF-8
```

## 🐛 常见问题

### 1. API 密钥错误

**问题**: 运行时显示 API 密钥错误
**解决**: 检查 `DEEPSEEK_API_KEY` 环境变量是否正确设置

### 2. 连接超时

**问题**: 请求超时
**解决**: 检查网络连接和 Deepseek 服务是否可访问

### 3. 编译失败

**问题**: Maven 编译报错
**解决**: 确保使用 Java 25 以上版本
```bash
java -version
```

## 📝 开发指南

### 添加新的 API 端点

在 `HelloController.java` 中添加新方法：

```java
@GetMapping("/new-endpoint")
public String newEndpoint() {
    return "Your response here";
}
```

### 自定义 ChatClient 配置

在 `RamiAgentApplication.java` 中配置 Bean。

## 🔐 安全建议

- **不要在代码中硬编码 API 密钥**
- 使用环境变量或配置管理服务
- 定期轮换 API 密钥
- 不要提交包含秘密信息的文件到版本控制

## 📄 许可证

本项目采用 [LICENSE](LICENSE) 文件中指定的许可证。

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📚 相关资源

- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Spring AI 文档](https://spring.io/projects/spring-ai)
- [Deepseek API 文档](https://www.deepseek.com/)
- [Java 25 发行说明](https://www.oracle.com/java/technologies/)

## 📧 联系方式

如有问题或建议，欢迎联系项目维护者。

---

**最后更新**: 2026-04-19
