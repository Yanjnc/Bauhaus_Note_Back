# 全局搜索功能前端接入开发文档

## 1. 文档概述

本文档旨在规范和协调前后端关于全局搜索与跳转功能的开发工作，提供详细的接口信息和集成指南，确保前端团队能够顺利接入后端API。

### 1.1 文档目的
- 明确全局搜索功能的接口规范
- 提供详细的接口调用示例
- 规范错误处理机制
- 确保前后端数据交互的一致性
- 指导前端开发人员快速接入API

### 1.2 适用范围
- 前端开发人员
- 测试人员
- 参与项目维护的技术人员

## 2. 接口基础信息

### 2.1 基础URL
- **开发环境**：`http://localhost:8080`
- **测试环境**：`http://test.note.example.com`
- **生产环境**：`https://note.example.com`

### 2.2 请求协议
- 开发环境：HTTP
- 测试环境：HTTP
- 生产环境：HTTPS（确保数据传输安全）

### 2.3 认证方式
- **认证类型**：Cookie认证
- **认证流程**：用户登录后，服务器返回认证Cookie，后续请求自动携带该Cookie
- **权限控制**：所有接口（除登录、注册外）均需要认证，确保用户只能访问自己的数据

## 3. 完整接口列表

### 3.1 全局搜索接口

#### 3.1.1 请求信息
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
  | keyword | String | 是 | - | 搜索关键词，支持全文搜索（标题、摘要、内容） |
  | page | Integer | 否 | 1 | 页码，从1开始 |
  | size | Integer | 否 | 12 | 每页数量，最大不超过100 |
  | sortBy | String | 否 | createdTime | 排序字段（createTime, updateTime, name） |
  | sortOrder | String | 否 | desc | 排序方式（asc, desc） |

#### 3.1.2 响应信息
- **响应头**：
  | 头部名称 | 类型 | 描述 |
  |----------|------|------|
  | Content-Type | String | 内容类型，application/json |

- **成功响应（200 OK）**：
  ```json
  {
    "status": 200,
    "message": "搜索成功",
    "object": {
      "notes": [
        {
          "id": 1,
          "name": "笔记标题",
          "abs": "笔记摘要内容",
          "contentMd": "Markdown格式的笔记内容",
          "contentHtml": "HTML格式的笔记内容",
          "categoryId": 1,
          "userId": 1,
          "createdTime": "2025-12-24T13:41:53",
          "updateTime": "2025-12-24T14:00:00",
          "status": 1,
          "viewCount": 0
        }
      ],
      "total": 100,
      "page": 1,
      "size": 12,
      "totalPages": 9
    },
    "error": null,
    "timestamp": "2025-12-24T14:30:00Z",
    "path": "/api/notes/search"
  }
  ```

- **失败响应（400 BAD_REQUEST）**：
  ```json
  {
    "status": 400,
    "message": "搜索关键词不能为空",
    "object": null,
    "error": {
      "code": "BAD_REQUEST",
      "details": "搜索关键词不能为空",
      "type": "VALIDATION_ERROR"
    },
    "timestamp": "2025-12-24T14:30:00Z",
    "path": "/api/notes/search"
  }
  ```

- **失败响应（401 UNAUTHORIZED）**：
  ```json
  {
    "status": 401,
    "message": "未认证，请先登录",
    "object": null,
    "error": {
      "code": "UNAUTHORIZED",
      "details": "用户未登录或认证已过期",
      "type": "AUTHENTICATION_ERROR"
    },
    "timestamp": "2025-12-24T14:30:00Z",
    "path": "/api/notes/search"
  }
  ```

- **失败响应（500 INTERNAL_SERVER_ERROR）**：
  ```json
  {
    "status": 500,
    "message": "搜索失败，请稍后重试",
    "object": null,
    "error": {
      "code": "INTERNAL_SERVER_ERROR",
      "details": "服务器内部错误",
      "type": "SYSTEM_ERROR"
    },
    "timestamp": "2025-12-24T14:30:00Z",
    "path": "/api/notes/search"
  }
  ```

## 4. 错误码规范及处理建议

### 4.1 统一错误响应格式

所有接口返回统一的错误响应格式：

```json
{
  "status": 状态码,              // HTTP状态码
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

### 4.2 错误码列表

| 错误码 | 描述 | HTTP状态码 | 类型 |
|--------|------|------------|------|
| SUCCESS | 操作成功 | 200 | - |
| BAD_REQUEST | 请求参数错误 | 400 | VALIDATION_ERROR |
| UNAUTHORIZED | 未认证 | 401 | AUTHENTICATION_ERROR |
| FORBIDDEN | 无权限 | 403 | AUTHORIZATION_ERROR |
| NOT_FOUND | 资源不存在 | 404 | RESOURCE_ERROR |
| INTERNAL_SERVER_ERROR | 服务器内部错误 | 500 | SYSTEM_ERROR |
| DATABASE_ERROR | 数据库错误 | 500 | SYSTEM_ERROR |
| NETWORK_ERROR | 网络错误 | 503 | SYSTEM_ERROR |

### 4.3 错误处理建议

1. **请求拦截器**：在请求发送前统一处理认证信息
2. **响应拦截器**：统一处理响应，对错误码进行分类处理
3. **组件内错误处理**：在组件内处理特定的业务逻辑错误
4. **全局错误处理**：处理未捕获的错误，提升用户体验

## 5. 接口调用示例代码

### 5.1 Axios实例配置

```javascript
// src/utils/request.js
import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.BASE_API, // 配置在环境变量中
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
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

### 5.2 搜索接口调用示例

```javascript
// src/components/bookshelf/bookshelf.vue
import request from '@/utils/request'

export default {
  data() {
    return {
      searchQuery: '',
      searchResults: [],
      totalResults: 0,
      page: 1,
      size: 12,
      loading: false
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalResults / this.size)
    }
  },
  methods: {
    // 执行搜索
    performSearch() {
      this.loading = true
      request.get('/api/notes/search', {
        params: {
          keyword: this.searchQuery,
          page: this.page,
          size: this.size,
          sortBy: 'createdTime',
          sortOrder: 'desc'
        }
      })
      .then(response => {
        this.searchResults = response.object.notes
        this.totalResults = response.object.total
        this.page = response.object.page
        this.size = response.object.size
      })
      .catch(error => {
        console.error('搜索失败:', error)
        // 错误已由拦截器处理，这里可以添加额外的处理逻辑
      })
      .finally(() => {
        this.loading = false
      })
    },
    // 分页处理
    handlePageChange(newPage) {
      this.page = newPage
      this.performSearch()
    }
  },
  // 路由参数监听，处理搜索请求
  watch: {
    '$route.query.search': {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.searchQuery = newVal
          this.page = 1 // 重置页码
          this.performSearch()
        } else if (!this.$route.query.cid) {
          // 如果没有搜索参数且没有分类参数，获取默认分类的笔记
          this.getNotes()
        }
      }
    }
  }
}
```

### 5.3 搜索组件示例

```vue
<!-- src/components/common/search-nav.vue -->
<template>
  <div class="search-nav">
    <el-input
      v-model="searchQuery"
      placeholder="搜索笔记..."
      prefix-icon="el-icon-search"
      class="search-input"
      @keyup.enter.native="handleSearch"
    ></el-input>
    <el-button
      type="primary"
      class="search-button"
      @click="handleSearch"
    >
      搜索
    </el-button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      searchQuery: ''
    }
  },
  methods: {
    handleSearch() {
      if (this.searchQuery.trim()) {
        // 跳转到书架页面并携带搜索参数
        this.$router.push({
          path: '/bookshelf',
          query: {
            search: this.searchQuery.trim()
          }
        })
      }
    }
  },
  // 监听路由变化，同步搜索参数
  watch: {
    '$route.query.search': {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.searchQuery = newVal
        }
      }
    }
  }
}
</script>

<style scoped>
.search-nav {
  display: flex;
  align-items: center;
}

.search-input {
  width: 200px;
  margin-right: 10px;
}

.search-button {
  background-color: #000;
  color: #ffd700;
  border-color: #000;
}

.search-button:hover {
  background-color: #ffd700;
  color: #000;
  border-color: #000;
}
</style>
```

## 6. 前后端数据交互格式约定

### 6.1 数据类型约定

| 前端类型 | 后端类型 | 描述 |
|----------|----------|------|
| String | String | 字符串类型 |
| Number | Integer/Long | 数字类型 |
| Boolean | Boolean | 布尔类型 |
| Date | LocalDateTime | 日期时间类型，格式：yyyy-MM-dd'T'HH:mm:ss |

### 6.2 分页数据格式

```json
{
  "notes": [/* 笔记列表 */],
  "total": 100,     // 总记录数
  "page": 1,        // 当前页码
  "size": 12,       // 每页数量
  "totalPages": 9   // 总页数
}
```

### 6.3 笔记数据格式

```json
{
  "id": 1,
  "name": "笔记标题",
  "abs": "笔记摘要内容",
  "contentMd": "Markdown格式的笔记内容",
  "contentHtml": "HTML格式的笔记内容",
  "categoryId": 1,
  "userId": 1,
  "createdTime": "2025-12-24T13:41:53",
  "updateTime": "2025-12-24T14:00:00",
  "status": 1,
  "viewCount": 0
}
```

## 7. 接口变更管理流程

### 7.1 变更通知
- 后端接口变更需提前3个工作日通知前端团队
- 变更通知需包含：变更内容、影响范围、迁移指南

### 7.2 版本管理
- 接口版本通过URL路径进行管理，如：`/api/v1/notes/search`
- 旧版本接口需保留至少一个月，确保前端有足够时间进行迁移

### 7.3 文档更新
- 接口变更后，需及时更新相关文档
- 文档更新后需通知所有相关团队成员

## 8. 开发环境与生产环境的配置差异

| 配置项 | 开发环境 | 生产环境 |
|--------|----------|----------|
| 基础URL | `http://localhost:8080` | `https://note.example.com` |
| 请求协议 | HTTP | HTTPS |
| 超时时间 | 10秒 | 15秒 |
| 日志级别 | DEBUG | INFO |
| 错误信息 | 详细错误信息 | 用户友好的错误信息 |

## 9. 性能优化建议

1. **分页查询**：使用分页减少单次请求的数据量
2. **缓存机制**：对热门搜索结果进行缓存，减少数据库压力
3. **索引优化**：确保搜索字段（标题、摘要、内容）已添加索引
4. **请求合并**：避免不必要的重复请求
5. **延迟加载**：对非关键数据采用延迟加载策略

## 10. 测试建议

1. **功能测试**：测试搜索功能的基本功能
2. **边界测试**：测试空关键词、特殊字符、长关键词等边界情况
3. **性能测试**：测试在大数据量下的搜索性能
4. **兼容性测试**：测试在不同浏览器下的兼容性
5. **安全性测试**：测试认证、授权等安全机制

## 11. 附录

### 11.1 术语表

| 术语 | 解释 |
|------|------|
| 全局搜索 | 支持按标题、内容进行全文搜索的功能 |
| 快速跳转 | 点击搜索结果可直接跳转到笔记详情页 |
| 分页支持 | 当搜索结果过多时，支持分页展示 |
| Cookie认证 | 通过Cookie进行用户认证的方式 |
| RESTful API | 符合REST设计风格的API |

### 11.2 参考文档

- [Vue.js官方文档](https://vuejs.org/)
- [Element UI官方文档](https://element.eleme.io/)
- [Axios官方文档](https://axios-http.com/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)