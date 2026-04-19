schema: spec-driven

context: |

# 项目总体描述
RamiAgent是一个AI助手程序，为了我10岁的闺女设计。通过不断地和我闺女对话，可以自主进化，更加了解我闺女的性格，兴趣，爱好等。

# 整体技术栈
 - 基于Spring Boot的Fat-jar
 - 使用Spring AI、Deepseek的API接口进行交互
 - 使用HSQLDB来记录会话历史

# 工程红线（绝对不可以违反）
 - 不允许改动Java、Maven、Spring Boot、Spring AI的依赖版本

rules: |

  proposal:
   - 方案设计必须从Total Solution出发，避免补丁式修复

  design:
   - 代码风格遵循Spring Boot友好

  task:
   - 每个Task必须遵循功能分支开发标准流程，禁止在主干上直接修改代码
   - 每个change archive后，必须提炼并总结该change中需要纳入config.yaml的内容，比如新增架构模式、技术栈变更，工程红线补充等。经用户确认后更新至config.yaml
