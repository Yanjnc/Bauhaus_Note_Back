# 全局搜索与跳转功能开发文档

## 1. 文档概述

本文档旨在规范和协调前后端关于全局搜索与跳转功能的开发工作。文档包含功能需求说明、接口设计规范、数据模型定义、错误处理机制、开发流程、测试标准以及Trae工具相关的配置说明和最佳实践。

### 1.1 文档目的
- 明确搜索功能的需求和范围
- 定义前后端接口规范
- 规范数据模型和错误处理机制
- 指导开发流程和测试标准
- 提供Trae工具的使用指南
- 确保开发团队对功能实现达成共识

### 1.2 适用范围
- 前端开发人员
- 后端开发人员
- 测试人员
- 项目管理人员
- 参与项目维护的技术人员

### 1.3 项目概述
- **项目名称**：Bauhaus Note
- **项目类型**：笔记管理系统
- **技术栈**：
  - 前端：Vue.js 2.5.2、Element UI 2.13.0、Axios 0.19.2、mavon-editor 2.7.7、vue-router 3.0.1、vuex 3.1.3
  - 后端：待确定（需支持RESTful API）
  - 开发工具：Trae IDE

### 1.4 项目结构
```
d:/Bauhaus_Note/note_front/
├── build/           # 构建配置
├── config/          # 项目配置
├── docs/            # 文档目录
├── project_backup/  # 项目备份目录
├── src/             # 源代码
│   ├── assets/      # 静态资源
│   │   ├── css/     # 样式文件
│   │   └── logo.png # 项目logo
│   ├── components/  # Vue组件
│   │   ├── bookshelf/  # 书架相关组件
│   │   ├── common/      # 通用组件
│   │   ├── home/        # 首页相关组件
│   │   └── note/        # 笔记相关组件
│   ├── router/      # 路由配置
│   ├── store/       # Vuex状态管理
│   ├── utils/       # 工具函数
│   ├── App.vue      # 根组件
│   └── main.js      # 入口文件
├── static/          # 静态资源
├── test/            # 测试文件
│   ├── e2e/         # 端到端测试
│   └── unit/        # 单元测试
├── .babelrc         # Babel配置
├── .editorconfig    # 编辑器配置
├── .gitignore       # Git忽略配置
├── .postcssrc.js    # PostCSS配置
├── DESIGN_SYSTEM.md # 设计系统文档
├── README.md        # 项目说明文档
├── index.html       # HTML入口文件
└── package.json     # 项目依赖
```

## 2. 功能需求说明

### 2.1 核心功能
- **全局搜索**：用户可通过导航栏搜索框搜索笔记，支持按标题、内容进行全文搜索
- **搜索结果展示**：在书架页面以卡片形式展示搜索结果，包含笔记标题、摘要、创建时间等信息
- **快速跳转**：点击搜索结果可直接跳转到笔记详情页
- **搜索反馈**：显示搜索关键词和匹配的结果数量，无结果时显示友好提示
- **分页支持**：当搜索结果过多时，支持分页展示，每页显示12条记录

### 2.2 设计原则
- **包豪斯主义**：几何形态、清晰线条、功能性与简洁性，使用黑色、黄色、蓝色等包豪斯经典配色
- **剃刀原理**：避免冗余代码和不必要的视觉元素，保持功能核心直接明了
- **第一性原理**：确保核心功能（输入-搜索-结果反馈）直接、高效且无干扰

### 2.3 用户界面设计

#### 2.3.1 搜索栏设计
- **位置**：导航栏右侧
- **样式**：
  - 输入框：3px黑色边框，白色背景，大写文字
  - 搜索按钮：黑色背景，黄色图标，3px黑色边框
  - 点击搜索按钮悬停效果：黄色背景，黑色图标
  - 激活状态：输入框边框高亮，输入文字大写
- **交互**：
  - 支持键盘Enter键触发搜索
  - 点击搜索按钮触发搜索
  - 输入时提供实时搜索建议（可选扩展功能）

#### 2.3.2 搜索结果展示
- **位置**：书架页面
- **样式**：
  - 结果头部：显示搜索关键词（蓝色）和结果数量（黄色背景）
  - 结果卡片：白色背景，3px黑色边框，8px黑色阴影
  - 分页控件：黑色边框，白色背景，当前页码高亮显示
- **交互**：
  - 点击结果卡片跳转到笔记详情页
  - 支持分页导航

### 2.4 用户操作流程
1. 用户在导航栏搜索框输入关键词
2. 点击搜索按钮或按Enter键
3. 系统通过路由跳转至书架页面，并在URL中传递搜索参数
4. 书架页面通过watch监听路由参数变化，触发搜索API调用
5. 搜索结果以卡片形式展示，包含搜索关键词和结果数量
6. 用户查看搜索结果，点击感兴趣的笔记卡片
7. 系统跳转到笔记详情页，显示完整笔记内容

### 2.5 非功能性需求
- **响应时间**：搜索请求响应时间不超过2秒
- **兼容性**：支持Chrome、Firefox、Safari、Edge等主流浏览器
- **可访问性**：搜索功能符合WCAG 2.1可访问性标准，支持键盘导航和屏幕阅读器
- **性能**：支持至少1000条笔记的快速搜索
- **安全性**：搜索功能不泄露用户隐私数据，防止SQL注入等安全风险

## 3. 接口设计规范

### 3.1 接口概述
所有接口均遵循RESTful API设计规范，使用JSON格式进行数据交换。

### 3.2 认证机制
- **认证方式**：Cookie认证
- **认证流程**：用户登录后，服务器返回认证Cookie，后续请求自动携带该Cookie
- **权限控制**：所有接口（除登录、注册外）均需要认证，确保用户只能访问自己的数据

### 3.3 搜索接口

#### 3.3.1 请求信息
- **URL**：`GET /api/notes/search`
- **认证**：需要用户认证（Cookie）
- **请求头**：
  | 头部名称 | 类型 | 必填 | 描述 |
  |----------|------|------|------|
  | Content-Type | String | 否 | 内容类型，默认为application/json |
  | Cookie | String | 是 | 认证Cookie |

- **请求参数**：
  | 参数名 | 类型 | 必填 | 默认值 | 描述 |
  |--------|------|------|--------|------|
  | keyword | String | 是 | - | 搜索关键词，支持全文搜索 |
  | page | Integer | 否 | 1 | 页码，从1开始 |
  | size | Integer | 否 | 12 | 每页数量，最大不超过100 |
  | sortBy | String | 否 | createTime | 排序字段（createTime, updateTime, name） |
  | sortOrder | String | 否 | desc | 排序方式（asc, desc） |

#### 3.3.2 响应信息
- **响应头**：
  | 头部名称 | 类型 | 描述 |
  |----------|------|------|
  | Content-Type | String | 内容类型，application/json |
  | X-Total-Count | Integer | 总记录数 |
  | X-Total-Pages | Integer | 总页数 |
  | X-Current-Page | Integer | 当前页码 |
  | X-Page-Size | Integer | 每页数量 |

- **成功响应（200 OK）**：
  ```json
  {
    "status": 200,
    "message": "操作成功",
    "object": [
      {
        "id": 1,
        "name": "笔记标题",
        "abs": "笔记摘要内容，包含搜索关键词",
        "createTime": "2025-12-24T13:41:53",
        "updateTime": "2025-12-24T14:00:00",
        "categoryId": 1,
        "userId": 1
      }
    ],
    "total": 100,
    "page": 1,
    "size": 12,
    "totalPages": 9
  }
  ```

- **失败响应**：
  | 状态码 | 错误码 | 描述 | 示例响应 |
  |--------|--------|------|----------|
  | 400 | BAD_REQUEST | 参数错误 | `{"status": 400, "message": "搜索关键词不能为空"}` |
  | 401 | UNAUTHORIZED | 未认证 | `{"status": 401, "message": "请先登录"}` |
  | 403 | FORBIDDEN | 无权限 | `{"status": 403, "message": "无权限访问该资源"}` |
  | 500 | INTERNAL_SERVER_ERROR | 服务器内部错误 | `{"status": 500, "message": "服务器内部错误，请稍后重试"}` |

### 3.4 接口设计原则
- **RESTful设计**：使用合适的HTTP方法和URL资源命名
- **统一响应格式**：所有接口返回相同格式的JSON数据
- **明确错误处理**：使用标准HTTP状态码和详细的错误信息
- **支持分页查询**：所有列表接口均支持分页参数
- **安全性**：防止SQL注入、XSS攻击等安全问题
- **性能优化**：对搜索接口进行索引优化，确保响应快速

### 3.5 前端接口调用示例
```javascript
// 搜索接口调用示例 - 书架组件中实际实现
performSearch() {
  var _this = this
  this.axios.get('notes/search', {
    params: {
      keyword: this.searchQuery
    }
  })
  .then(function (response) {
    if(response.status === 200){
      _this.$refs.notes.showNotes(response.data, _this.searchQuery)
    }
  })
}

// 路由参数监听，处理搜索请求
watch: {
  '$route.query.search': {
    immediate: true,
    handler(newVal) {
      if (newVal) {
        this.searchQuery = newVal
        this.performSearch()
      } else if (!this.$route.query.cid) {
        // 如果没有搜索参数且没有分类参数，获取默认分类的笔记
        this.getNotes()
      }
    }
  }
}
```

## 4. 数据模型定义

### 4.1 数据模型概述
系统采用关系型数据库设计，主要包含用户、分类和笔记三个核心模型。

### 4.2 用户模型（User）

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | Integer | 主键，自增 | 用户ID |
| username | String(50) | 非空，唯一 | 用户名 |
| password | String(255) | 非空 | 加密后的密码 |
| email | String(100) | 非空，唯一 | 邮箱地址 |
| avatar | String(255) | 空 | 头像URL |
| createTime | DateTime | 非空 | 创建时间 |
| updateTime | DateTime | 非空 | 更新时间 |
| status | Integer | 非空，默认1 | 用户状态（1-正常，0-禁用） |

### 4.3 分类模型（Category）

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | Integer | 主键，自增 | 分类ID |
| name | String(255) | 非空 | 分类名称 |
| userId | Integer | 外键，非空 | 用户ID |
| createTime | DateTime | 非空 | 创建时间 |
| updateTime | DateTime | 非空 | 更新时间 |

**索引建议**：
- 联合唯一索引：`(userId, name)` - 确保用户内分类名称唯一
- 外键索引：`userId` - 优化关联查询

### 4.4 笔记模型（Note）

| 字段名 | 类型 | 约束 | 描述 |
|--------|------|------|------|
| id | Integer | 主键，自增 | 笔记ID |
| name | String(255) | 非空 | 笔记标题 |
| contentMd | Text | 非空 | Markdown内容 |
| contentHtml | Text | 非空 | HTML内容 |
| abs | String(500) | 非空 | 笔记摘要 |
| categoryId | Integer | 外键，空 | 分类ID |
| userId | Integer | 外键，非空 | 用户ID |
| createTime | DateTime | 非空 | 创建时间 |
| updateTime | DateTime | 非空 | 更新时间 |
| status | Integer | 非空，默认1 | 笔记状态（1-正常，0-删除） |
| viewCount | Integer | 非空，默认0 | 查看次数 |

**索引建议**：
- 全文索引：`(name, contentMd, abs)` - 优化搜索性能
- 外键索引：`userId`, `categoryId` - 优化关联查询
- 普通索引：`createTime`, `updateTime` - 优化排序查询
- 组合索引：`(userId, status)` - 优化用户笔记查询

### 4.5 模型关系

```
User (1) ──> (N) Category
User (1) ──> (N) Note
Category (1) ──> (N) Note
```

### 4.6 数据结构设计原则
- **范式化设计**：遵循数据库设计范式，减少数据冗余
- **性能优化**：为搜索字段添加全文索引，提高查询效率
- **安全性**：敏感数据（如密码）进行加密存储
- **可扩展性**：设计灵活的数据结构，支持未来功能扩展

### 4.7 数据访问层设计

#### 4.7.1 搜索相关数据访问方法

| 方法名 | 参数 | 返回值 | 描述 |
|--------|------|--------|------|
| searchNotes | keyword: String, userId: Integer, page: Integer, size: Integer | Page<Note> | 根据关键词搜索用户笔记 |
| countSearchResults | keyword: String, userId: Integer | Integer | 统计搜索结果数量 |
| getNoteById | id: Integer, userId: Integer | Note | 根据ID获取用户笔记 |
| getNotesByCategory | categoryId: Integer, userId: Integer, page: Integer, size: Integer | Page<Note> | 根据分类获取用户笔记 |

#### 4.7.2 数据访问层实现建议
- 使用ORM框架（如MyBatis、Hibernate）简化数据库操作
- 实现分页查询，提高大数据量查询性能
- 缓存热点数据，减少数据库压力
- 实现事务管理，确保数据一致性

## 5. 错误处理机制

### 5.1 错误分类

#### 5.1.1 客户端错误
- **网络错误**：网络连接失败、请求超时等
- **请求错误**：请求参数错误、请求格式错误等
- **认证错误**：未登录、认证过期等
- **授权错误**：无权限访问资源
- **业务逻辑错误**：如搜索关键词为空、笔记不存在等

#### 5.1.2 服务器错误
- **系统错误**：服务器内部错误、数据库连接失败等
- **业务错误**：业务逻辑处理失败、数据验证失败等
- **资源错误**：资源不存在、资源冲突等

### 5.2 错误响应格式

#### 5.2.1 统一响应格式
所有接口返回统一的JSON格式响应：

```json
{
  "status": 状态码,              // HTTP状态码或业务状态码
  "message": "错误信息",          // 用户友好的错误信息
  "object": null,                // 成功时返回的数据，失败时为null
  "error": {
    "code": "错误码",            // 业务错误码
    "details": "错误详细信息",     // 详细错误信息（开发调试用）
    "type": "错误类型"           // 错误类型（如VALIDATION_ERROR）
  },
  "timestamp": "2025-12-24T14:30:00Z", // 错误发生时间
  "path": "/api/notes/search"     // 请求路径
}
```

#### 5.2.2 错误码定义

| 错误码 | 描述 | HTTP状态码 |
|--------|------|------------|
| SUCCESS | 操作成功 | 200 |
| BAD_REQUEST | 请求参数错误 | 400 |
| UNAUTHORIZED | 未认证 | 401 |
| FORBIDDEN | 无权限 | 403 |
| NOT_FOUND | 资源不存在 | 404 |
| INTERNAL_SERVER_ERROR | 服务器内部错误 | 500 |
| VALIDATION_ERROR | 数据验证错误 | 400 |
| DATABASE_ERROR | 数据库错误 | 500 |
| NETWORK_ERROR | 网络错误 | 503 |

### 5.3 前端错误处理

#### 5.3.1 Axios拦截器配置

```javascript
// src/main.js
import axios from 'axios'
import { Message, MessageBox } from 'element-ui'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.BASE_API, // API基础URL
  timeout: 10000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 可以在这里添加认证信息
    return config
  },
  error => {
    console.error('请求配置错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 统一处理业务状态码
    if (res.status !== 200) {
      Message({
        message: res.message || '操作失败',
        type: 'error',
        duration: 5 * 1000
      })
      // 处理特殊错误码
      if (res.status === 401) {
        // 未登录或认证过期，跳转登录页面
        MessageBox.confirm('登录已过期，请重新登录', '提示', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 清除本地存储的认证信息
          localStorage.removeItem('token')
          // 跳转登录页面
          router.push({ path: '/login' })
        })
      }
      return Promise.reject(new Error(res.message || '操作失败'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    let message = '网络错误，请稍后重试'
    if (error.response) {
      // 服务器返回错误
      const status = error.response.status
      switch (status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未登录，请重新登录'
          // 跳转登录页面
          router.push({ path: '/login' })
          break
        case 403:
          message = '无权限访问该资源'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 503:
          message = '服务器暂时不可用'
          break
        default:
          message = `请求失败 (${status})`
      }
    } else if (error.request) {
      // 请求已发送但未收到响应
      message = '网络连接失败，请检查网络'
    } else {
      // 请求配置错误
      message = '请求配置错误'
    }
    Message({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service
```

#### 5.3.2 组件内错误处理

```javascript
// 在组件内使用封装好的axios实例
import request from '@/utils/request'

export default {
  methods: {
    searchNotes(keyword) {
      this.loading = true
      request.get('/api/notes/search', {
        params: { keyword }
      })
      .then(response => {
        this.searchResults = response.object
        this.totalResults = response.total
      })
      .catch(error => {
        console.error('搜索失败:', error)
        // 错误已由拦截器处理，这里可以添加额外的处理逻辑
      })
      .finally(() => {
        this.loading = false
      })
    }
  }
}
```

#### 5.3.3 全局错误处理

```javascript
// src/main.js

// 全局错误处理
Vue.config.errorHandler = (err, vm, info) => {
  console.error('全局错误:', err)
  console.error('组件:', vm)
  console.error('错误信息:', info)
  // 可以将错误信息发送到日志服务器
}

// 全局未捕获的Promise错误处理
window.addEventListener('unhandledrejection', event => {
  console.error('未处理的Promise错误:', event.reason)
  // 可以将错误信息发送到日志服务器
})
```

### 5.4 后端错误处理

#### 5.4.1 统一异常处理

**Spring Boot示例：**

```java
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(e.getStatusCode());
        error.setMessage(e.getMessage());
        error.setError(new ErrorDetail(e.getErrorCode(), e.getDetails(), "BUSINESS_ERROR"));
        error.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(error, HttpStatus.valueOf(e.getStatusCode()));
    }

    // 处理参数验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(400);
        error.setMessage("参数验证失败");
        
        StringBuilder details = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            details.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
        }
        
        error.setError(new ErrorDetail("VALIDATION_ERROR", details.toString(), "VALIDATION_ERROR"));
        error.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 处理全局异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e) {
        ErrorResponse error = new ErrorResponse();
        error.setStatus(500);
        error.setMessage("服务器内部错误");
        error.setError(new ErrorDetail("INTERNAL_SERVER_ERROR", e.getMessage(), "SYSTEM_ERROR"));
        error.setTimestamp(LocalDateTime.now().toString());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

#### 5.4.2 业务异常定义

```java
public class BusinessException extends RuntimeException {
    private Integer statusCode;
    private String errorCode;
    private String details;

    public BusinessException(Integer statusCode, String errorCode, String message, String details) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.details = details;
    }

    // getter and setter
}
```

#### 5.4.3 错误日志记录

```java
@Service
public class NoteService {
    private static final Logger logger = LoggerFactory.getLogger(NoteService.class);

    public Page<Note> searchNotes(String keyword, Integer userId, Integer page, Integer size) {
        try {
            // 业务逻辑处理
            return noteRepository.searchNotes(keyword, userId, PageRequest.of(page - 1, size));
        } catch (Exception e) {
            logger.error("搜索笔记失败: keyword={}, userId={}, page={}, size={}", 
                        keyword, userId, page, size, e);
            throw new BusinessException(500, "SEARCH_FAILED", "搜索失败", e.getMessage());
        }
    }
}
```

### 5.5 错误处理最佳实践

#### 5.5.1 前端最佳实践
- **使用统一的请求工具**：封装Axios，统一处理请求和响应
- **友好的用户提示**：使用Element UI的Message组件显示用户友好的错误信息
- **加载状态管理**：在请求过程中显示加载状态，请求结束后隐藏
- **日志记录**：记录前端错误，便于调试和分析
- **优雅降级**：在网络错误时提供离线模式或缓存数据

#### 5.5.2 后端最佳实践
- **统一异常处理**：使用@ControllerAdvice等机制实现全局异常处理
- **明确的错误信息**：提供清晰、具体的错误信息，便于前端处理
- **日志记录**：详细记录错误信息，包括请求参数、用户信息等
- **安全考虑**：不在生产环境返回详细的错误堆栈信息
- **监控和告警**：对系统错误进行监控，及时发现和处理问题

### 5.6 常见错误场景处理

#### 5.6.1 搜索功能错误处理

| 错误场景 | 错误信息 | 处理方式 |
|----------|----------|----------|
| 搜索关键词为空 | 请输入搜索关键词 | 前端验证，提示用户输入关键词 |
| 网络连接失败 | 网络连接失败，请检查网络 | 显示网络错误提示，建议用户检查网络 |
| 服务器内部错误 | 搜索服务暂时不可用，请稍后重试 | 显示服务器错误提示，记录错误日志 |
| 搜索结果过多 | 搜索结果过多，请缩小搜索范围 | 建议用户使用更精确的关键词 |
| 无搜索结果 | 未找到匹配的笔记 | 显示无结果提示，建议用户尝试其他关键词 |

## 6. 开发流程

### 6.1 开发环境配置

#### 6.1.1 前端开发环境
- **操作系统**：Windows 10/11、macOS、Linux
- **Node.js**：v6.0.0 或更高版本（建议 v8.9.0+）
- **npm**：v3.0.0 或更高版本（建议 v5.6.0+）
- **开发框架**：Vue.js 2.5.2、Element UI 2.13.0、Axios 0.19.2、mavon-editor 2.7.7
- **开发工具**：Trae IDE

**环境配置步骤**：
1. 安装Node.js和npm
2. 克隆项目仓库：`git clone <repository-url>`
3. 进入项目目录：`cd d:/Bauhaus_Note/note_front`
4. 安装依赖：`npm install`
5. 启动开发服务器：`npm run dev` 或 `npm start`
6. 访问项目：http://localhost:8080

**可用npm命令**：
- `npm run dev`：启动开发服务器
- `npm run unit`：运行单元测试
- `npm run e2e`：运行端到端测试
- `npm run test`：运行所有测试
- `npm run build`：构建生产版本

#### 6.1.2 后端开发环境
- **操作系统**：Windows 10/11、macOS、Linux
- **JDK**：v8 或更高版本（如果使用Spring Boot）
- **Node.js**：v12.0.0 或更高版本（如果使用Node.js）
- **数据库**：MySQL 5.7 或更高版本
- **开发框架**：Spring Boot 2.x 或 Express.js 4.x
- **开发工具**：Trae IDE

**环境配置步骤**：
1. 安装JDK或Node.js
2. 安装MySQL数据库
3. 克隆项目仓库
4. 配置数据库连接
5. 安装依赖
6. 启动开发服务器

### 6.2 分支管理策略

#### 6.2.1 分支类型
- **master**：主分支，用于发布生产版本
- **develop**：开发分支，用于集成所有功能开发
- **feature/xxx**：功能分支，用于开发新功能
- **bugfix/xxx**：bug修复分支，用于修复生产环境或开发环境的bug
- **release/xxx**：发布分支，用于准备发布新版本
- **hotfix/xxx**：紧急修复分支，用于修复生产环境的紧急bug

#### 6.2.2 分支流程
1. 从`develop`分支创建功能分支：`git checkout -b feature/search develop`
2. 在功能分支上进行开发
3. 开发完成后，提交代码并推送到远程仓库
4. 创建PR（Pull Request）到`develop`分支
5. 经过代码审查和测试后，合并到`develop`分支
6. 定期从`develop`分支创建`release`分支进行发布准备
7. 发布完成后，将`release`分支合并到`master`和`develop`分支

### 6.3 开发步骤

#### 6.3.1 前端开发流程

1. **需求分析**：理解搜索功能的需求和接口规范
2. **环境准备**：配置Trae IDE和开发环境
3. **组件设计**：
   - 导航栏搜索框组件（NavMenu.vue）
   - 搜索结果展示组件（Notes.vue）
   - 搜索状态管理
4. **UI实现**：
   - 实现搜索栏的包豪斯风格设计
   - 实现搜索结果的卡片布局和样式
   - 实现分页控件
5. **交互实现**：
   - 搜索触发逻辑（点击按钮和Enter键）
   - 路由跳转和参数传递
   - 加载状态和错误提示
6. **API集成**：
   - 调用搜索API获取结果
   - 处理API响应和错误
   - 实现分页加载
7. **测试**：
   - 单元测试：使用Jest测试组件逻辑
   - 集成测试：测试组件间的交互
   - 端到端测试：使用Nightwatch测试完整流程
8. **代码提交**：遵循Git提交规范
9. **代码审查**：创建PR，接受团队审查
10. **合并部署**：合并到develop分支，部署到测试环境

#### 6.3.2 后端开发流程

1. **需求分析**：理解搜索功能的需求和数据模型
2. **环境准备**：配置Trae IDE和开发环境
3. **接口设计**：
   - 设计搜索API接口
   - 定义请求参数和响应格式
4. **业务逻辑实现**：
   - 实现搜索关键词的处理
   - 实现数据库查询和过滤
   - 实现分页逻辑
5. **数据访问层实现**：
   - 实现数据模型和数据库映射
   - 实现搜索查询的优化
   - 添加全文索引
6. **错误处理和日志**：
   - 实现统一异常处理
   - 添加详细的日志记录
   - 实现监控和告警
7. **测试**：
   - 单元测试：测试业务逻辑和数据访问层
   - 集成测试：测试API接口和数据库交互
   - 性能测试：测试搜索功能的性能
8. **代码提交**：遵循Git提交规范
9. **代码审查**：创建PR，接受团队审查
10. **部署**：部署到测试环境，与前端进行联调

### 6.4 代码提交规范

#### 6.4.1 Git提交信息规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）**：
- **feat**：新功能（feature）
- **fix**：修复bug
- **docs**：文档更新
- **style**：代码样式调整（不影响功能）
- **refactor**：代码重构（不新增功能或修复bug）
- **test**：测试代码
- **chore**：构建过程或辅助工具的变动
- **perf**：性能优化
- **ci**：CI配置文件或脚本的更新
- **revert**：回滚到之前的提交

**范围（scope）**：
- 可以是组件名、模块名或功能名
- 例如：`nav`, `search`, `notes`, `api`

**主题（subject）**：
- 简短描述提交的内容（不超过50个字符）
- 以动词开头，使用第一人称现在时
- 首字母小写，结尾不加句号

**正文（body）**：
- 详细描述提交的内容（可选）
- 每行不超过72个字符
- 解释为什么做这个修改，而不是怎么做

**页脚（footer）**：
- 引用相关的issue或PR（可选）
- 例如：`Closes #123`, `Fixes #456`
- 如果是破坏性变更，需要在页脚中说明

**示例**：

```
feat(search): 实现全局搜索功能的前端UI

- 导航栏添加搜索框组件
- 实现搜索结果展示卡片
- 集成搜索API调用
- 添加搜索状态管理

Closes #123
```

```
fix(search): 修复搜索结果分页错误

- 修复分页参数传递错误
- 调整分页组件样式
- 优化分页加载性能

Fixes #456
```

#### 6.4.2 代码风格规范

**前端代码风格**：
- 使用ES6+语法
- 缩进：2个空格
- 引号：单引号
- 语句末尾：加分号
- 变量命名：小驼峰命名法
- 组件命名：大驼峰命名法
- 注释：使用JSDoc规范

**后端代码风格**：
- 使用Java 8+或ES6+语法
- 缩进：4个空格（Java）或2个空格（Node.js）
- 命名：
  - 类名：大驼峰命名法
  - 方法名和变量名：小驼峰命名法
  - 常量：全大写，下划线分隔
- 注释：使用Javadoc或JSDoc规范

### 6.5 代码审查流程

1. **创建PR**：开发完成后，创建PR到目标分支
2. **PR描述**：
   - 清晰描述PR的目的和内容
   - 引用相关的issue
   - 说明测试情况
3. **代码审查**：
   - 团队成员进行代码审查
   - 检查代码风格、逻辑正确性和性能
   - 提出修改建议
4. **修改代码**：根据审查建议修改代码
5. **重新审查**：修改完成后，请求重新审查
6. **合并代码**：审查通过后，合并到目标分支
7. **删除分支**：合并完成后，删除功能分支

### 6.6 发布流程

1. **创建release分支**：从develop分支创建release分支
2. **版本更新**：更新项目版本号
3. **测试验证**：进行全面的测试
4. **修复问题**：如果发现问题，在release分支上修复
5. **合并到master**：测试通过后，合并到master分支
6. **打标签**：在master分支上打版本标签
7. **合并到develop**：将release分支合并回develop分支
8. **部署**：部署master分支到生产环境
9. **发布公告**：发布新版本公告

### 6.7 开发协作工具

- **项目管理**：Jira或Trello
- **代码仓库**：Git
- **CI/CD**：Jenkins或GitHub Actions
- **文档管理**：Confluence或Markdown文件
- **沟通工具**：Slack或微信

## 7. 测试标准

### 7.1 前端测试

#### 7.1.1 功能测试
- 搜索框可以正常输入
- 点击搜索按钮可以触发搜索
- 按Enter键可以触发搜索
- 搜索结果正确展示
- 点击搜索结果可以跳转到笔记详情页
- 搜索无结果时显示正确提示

#### 7.1.2 性能测试
- 搜索请求响应时间小于2秒
- 页面加载时间小于3秒
- 支持至少1000条笔记的搜索

#### 7.1.3 兼容性测试
- 支持Chrome、Firefox、Safari、Edge等主流浏览器
- 支持移动端和桌面端

### 7.2 后端测试

#### 7.2.1 功能测试
- 搜索接口可以正常响应
- 支持关键词搜索
- 支持分页查询
- 只返回当前用户的笔记
- 正确处理边界条件

#### 7.2.2 性能测试
- 单接口响应时间小于500毫秒
- 支持每秒至少100个并发请求
- 数据库查询性能优化

#### 7.2.3 安全测试
- 防止SQL注入
- 防止XSS攻击
- 正确处理认证和授权

## 8. Trae工具使用指南

### 8.1 工具简介
Trae是一款集成开发环境（IDE），专为现代化Web应用开发设计，提供了代码编辑、调试、版本控制、项目管理等全方位功能，支持前后端开发协作。

### 8.2 配置说明

#### 8.2.1 项目配置
1. 打开Trae IDE
2. 选择"File" > "Open Folder"，打开项目根目录 `d:/Bauhaus_Note/note_front`
3. 等待项目加载完成，Trae会自动识别Vue.js项目并配置相关插件
4. 在欢迎界面可以查看项目概览、最近打开的文件和Git状态

#### 8.2.2 代码编辑配置
1. 打开设置（"File" > "Settings"或使用快捷键Ctrl+,）
2. **编辑器配置**：
   - 字体：建议使用Consolas或SF Mono，大小14px
   - 主题：推荐使用Bauhaus风格主题（高对比度，黑色/黄色/蓝色配色）
   - 缩进：2个空格（与Vue.js项目规范一致）
   - 行号：显示行号
   - 滚动条：显示垂直滚动条
3. **扩展配置**：
   - 启用Eslint扩展，配置自动修复选项
   - 启用Prettier扩展，配置保存时自动格式化
   - 安装Vue.js扩展，支持Vue组件语法高亮和智能提示
   - 安装Element UI扩展，提供Element组件的代码补全

#### 8.2.3 调试配置
1. **前端调试配置**：
   - 点击左侧边栏的"Run and Debug"图标（或按Ctrl+Shift+D）
   - 点击"create a launch.json file"，选择"Chrome"或"Edge"
   - 修改launch.json配置文件，设置正确的url（http://localhost:8080）
   - 保存配置，点击绿色三角形启动调试
2. **断点设置**：
   - 在代码行号前点击设置断点（红色圆点）
   - 支持条件断点、日志断点和函数断点
   - 可以在调试面板中管理所有断点

#### 8.2.4 Git配置
1. 在左侧边栏的"Source Control"面板（或按Ctrl+Shift+G）
2. 配置Git用户名和邮箱：
   ```bash
   git config --global user.name "Your Name"
   git config --global user.email "your.email@example.com"
   ```
3. 启用自动fetch：在设置中搜索"git.autofetch"并启用
4. 配置提交模板：可以设置统一的提交信息模板

### 8.3 工具使用指南

#### 8.3.1 代码编辑
- 使用智能补全功能：输入代码时Trae会提供智能补全建议，按Enter或Tab接受
- 语法高亮：支持Vue、JavaScript、CSS等多种语言的语法高亮
- 代码片段：使用预设的代码片段快速插入常用代码结构（如v-if、v-for、方法定义等）
- 代码折叠：点击代码行号左侧的箭头折叠/展开代码块
- 跳转定义：按住Ctrl键并点击变量/函数名，跳转到定义位置
- 查找引用：右键点击变量/函数名，选择"Find All References"

#### 8.3.2 文件管理
- 使用"Explorer"面板（左侧边栏，或按Ctrl+Shift+E）管理项目文件
- 右键点击文件或文件夹可进行创建、删除、重命名、复制等操作
- 使用"New File"按钮快速创建新文件
- 使用"Open Editors"面板查看所有打开的文件

#### 8.3.3 搜索功能
- **文件搜索**：按Ctrl+P，输入文件名快速打开文件
- **全局搜索**：按Ctrl+Shift+F，在所有文件中搜索文本
- **替换功能**：按Ctrl+H，进行文本替换
- **正则表达式搜索**：在搜索框中点击".*"图标启用正则表达式模式

#### 8.3.4 版本控制
- **查看更改**：在"Source Control"面板中查看已更改的文件
- **暂存更改**：点击文件旁边的"+"图标暂存更改
- **提交更改**：输入提交信息，点击"✓"图标提交
- **创建分支**：点击分支名称，选择"Create Branch"
- **切换分支**：点击分支名称，选择要切换的分支
- **拉取/推送**：点击"..."图标，选择"Pull"或"Push"
- **查看历史**：右键点击文件，选择"View History"查看文件的Git历史

#### 8.3.5 调试功能
- **启动调试**：按F5或点击"Run and Debug"面板中的绿色三角形
- **暂停/继续**：按F6暂停或继续执行
- **单步执行**：
  - F10：单步跳过（不进入函数）
  - F11：单步进入（进入函数）
  - Shift+F11：单步退出（退出当前函数）
- **查看变量**：在"Variables"面板中查看当前作用域的变量值
- **查看调用栈**：在"Call Stack"面板中查看函数调用栈
- **控制台**：在"Console"面板中查看日志输出和执行JavaScript代码

#### 8.3.6 终端操作
- 打开终端：按Ctrl+`或点击"Terminal" > "New Terminal"
- 切换终端类型：可以在终端下拉菜单中选择PowerShell、Command Prompt或WSL
- 分屏终端：点击终端右上角的分屏图标，支持左右或上下分屏
- 命令历史：按向上/向下箭头键查看历史命令
- 快捷键：
  - Ctrl+C：中断当前命令
  - Ctrl+L：清屏
  - Ctrl+D：关闭终端

### 8.4 最佳实践

#### 8.4.1 项目开发流程
1. **创建功能分支**：使用Trae的Git面板创建功能分支（如`feature/search`）
2. **编写代码**：使用智能补全和代码片段提高编码效率
3. **代码检查**：利用Eslint和Prettier确保代码风格一致
4. **本地测试**：运行`npm run unit`和`npm run e2e`进行测试
5. **提交代码**：使用规范的提交信息格式提交代码
6. **创建PR**：在Trae中直接创建Pull Request
7. **代码审查**：在Trae中查看和评论PR，进行代码审查
8. **合并分支**：审查通过后合并分支到develop

#### 8.4.2 调试技巧
- **条件断点**：在复杂循环中使用条件断点，只在特定条件下暂停
- **日志断点**：使用日志断点代替console.log，避免污染代码
- **监视表达式**：在调试面板中添加监视表达式，实时查看变量值
- **Vue DevTools集成**：结合Vue DevTools浏览器扩展，调试Vue组件状态

#### 8.4.3 团队协作
- **共享配置**：将Trae配置（.vscode目录）提交到Git仓库，确保团队成员使用相同的配置
- **代码片段共享**：创建团队共享的代码片段，提高代码一致性
- **扩展推荐**：推荐团队成员安装相同的扩展，确保开发环境一致
- **使用Live Share**：利用Live Share功能进行实时协作编码

#### 8.4.4 性能优化
- **使用代码分析工具**：在Trae中使用集成的代码分析工具，识别性能瓶颈
- **优化导入**：使用Trae的"Organize Imports"功能（Shift+Alt+O）优化导入语句
- **重构工具**：利用Trae的重构功能（如重命名变量、提取函数）提高代码质量
- **查找未使用的代码**：使用Eslint的no-unused-vars规则查找并删除未使用的代码

#### 8.4.5 快捷键推荐
- **文件操作**：
  - Ctrl+P：快速打开文件
  - Ctrl+Shift+N：新建文件
  - Ctrl+S：保存文件
  - Ctrl+Shift+S：另存为
- **编辑操作**：
  - Ctrl+D：选择下一个相同的单词
  - Ctrl+Shift+L：选择所有相同的单词
  - Ctrl+/：注释/取消注释
  - Ctrl+Shift+A：代码格式化
- **导航操作**：
  - Ctrl+G：跳转到行号
  - Ctrl+Shift+O：跳转到函数/方法
  - Ctrl+F：查找
  - Ctrl+H：替换
- **调试操作**：
  - F5：开始/继续调试
  - F6：暂停调试
  - F10：单步跳过
  - F11：单步进入

### 8.5 常见问题解决

#### 8.5.1 项目加载缓慢
- 关闭不必要的扩展
- 清理Trae缓存（在设置中搜索"clearEditorHistory"）
- 增加Trae的内存限制（编辑settings.json，添加"files.maxMemoryForLargeFiles": 4096）

#### 8.5.2 Eslint/Prettier不工作
- 确保已安装必要的依赖：`npm install --save-dev eslint prettier`
- 检查.eslintrc和.prettierrc配置文件
- 重启Trae IDE

#### 8.5.3 调试无法启动
- 确保开发服务器已启动（`npm run dev`）
- 检查launch.json配置文件中的url是否正确
- 确保浏览器已安装并可访问

#### 8.5.4 Git操作失败
- 检查Git配置（用户名、邮箱）
- 确保有足够的权限访问仓库
- 检查网络连接是否正常
- 尝试在终端中执行相同的Git命令，查看详细错误信息

## 9. 开发规范

### 9.1 前端开发规范

#### 9.1.1 组件命名规范
- 使用PascalCase命名组件
- 组件名称应反映其功能
- 避免使用缩写和模糊命名

#### 9.1.2 代码风格
- 使用2个空格缩进
- 使用单引号
- 语句末尾添加分号
- 保持适当的空行和代码块

#### 9.1.3 API调用规范
- 使用Axios进行API调用
- 统一处理错误和响应
- 避免在组件中直接调用API，使用Vuex或Service层

### 9.2 后端开发规范

#### 9.2.1 API命名规范
- 遵循RESTful API命名规范
- 使用小写字母和连字符
- 明确资源和操作

#### 9.2.2 代码风格
- 遵循Java或Node.js的代码风格规范
- 使用一致的命名规范
- 保持代码简洁和可读性

#### 9.2.3 数据库操作规范
- 使用ORM框架进行数据库操作
- 优化查询性能
- 实现事务管理

## 10. 验收标准

### 10.1 功能验收
- 搜索功能正常工作
- 搜索结果准确
- 跳转到笔记详情页正常
- 错误处理和用户反馈正常

### 10.2 性能验收
- 搜索请求响应时间小于2秒
- 页面加载时间小于3秒
- 支持至少1000条笔记的搜索

### 10.3 安全验收
- 防止SQL注入攻击
- 防止XSS攻击
- 正确处理认证和授权

### 10.4 兼容性验收
- 支持主流浏览器
- 支持移动端和桌面端

## 11. 文档维护

### 11.1 文档更新
- 当需求或接口发生变化时，及时更新文档
- 保持文档的准确性和完整性
- 记录文档更新历史

### 11.2 文档版本

| 版本 | 更新日期 | 更新内容 | 更新人 |
|------|----------|----------|--------|
| 1.0 | 2025-12-24 | 初始版本 | Trae AI |

## 12. 附录

### 12.1 术语表

| 术语 | 解释 |
|------|------|
| RESTful API | 一种软件架构风格，用于设计网络应用程序接口 |
| Vue.js | 一套用于构建用户界面的渐进式JavaScript框架 |
| Axios | 一个基于Promise的HTTP客户端，用于浏览器和node.js |
| Trae | 一款集成开发环境（IDE） |

### 12.2 参考文档

- [Vue.js官方文档](https://vuejs.org/v2/guide/)
- [Element UI官方文档](https://element.eleme.io/)
- [Axios官方文档](https://axios-http.com/docs/intro)
- [RESTful API设计指南](https://restfulapi.net/)
- [Trae工具使用手册](https://trae.ai/docs/)

---

**文档审核：**
- [ ] 前端开发负责人
- [ ] 后端开发负责人
- [ ] 项目管理人员

**文档批准：**
- [ ] 项目经理

**生效日期：** 2025-12-24
