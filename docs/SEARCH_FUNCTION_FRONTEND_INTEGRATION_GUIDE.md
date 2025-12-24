# 全局搜索与跳转功能前端接入开发文档

## 1. 文档概述

本文档旨在规范前端团队接入后端全局搜索与跳转功能的开发流程，明确接口定义、数据格式、错误处理等规范，确保前后端协作顺畅，提高开发效率。

### 1.1 文档目的
- 明确搜索功能的前端接入要求
- 定义前后端接口调用规范
- 规范数据交互格式
- 提供接口调用示例代码
- 指导前端错误处理和异常情况处理
- 说明开发环境与生产环境的配置差异

### 1.2 适用范围
- 前端开发人员
- 前端测试人员
- 参与前后端协作的技术人员

## 2. 接口基础信息

### 2.1 基础URL
- **开发环境**：`http://localhost:8080/api`
- **生产环境**：`https://api.bauhausnote.com/api`

### 2.2 请求协议
- **HTTP/HTTPS**：支持HTTP和HTTPS（生产环境必须使用HTTPS）

### 2.3 认证方式
- **认证类型**：Cookie认证
- **认证流程**：用户登录后，服务器返回认证Cookie，后续请求自动携带该Cookie
- **认证要求**：所有接口（除登录、注册外）均需要认证
- **权限控制**：确保用户只能访问自己的数据

## 3. 完整接口列表

### 3.1 全局搜索接口

#### 3.1.1 接口概述
- **接口路径**：`/notes/search`
- **请求方法**：`GET`
- **功能描述**：根据关键词搜索用户笔记，支持按标题、内容进行全文搜索
- **响应时间**：< 2秒
- **支持数据量**：1000+笔记

#### 3.1.2 请求头
| 头部名称 | 类型 | 必填 | 描述 |
|----------|------|------|------|
| Content-Type | String | 否 | 内容类型，默认为application/json |
| Cookie | String | 是 | 认证Cookie |

#### 3.1.3 请求参数
| 参数名 | 类型 | 必填 | 默认值 | 描述 |
|--------|------|------|--------|------|
| keyword | String | 是 | - | 搜索关键词，支持全文搜索 |
| page | Integer | 否 | 1 | 页码，从1开始 |
| size | Integer | 否 | 12 | 每页数量，最大不超过100 |
| sortBy | String | 否 | createTime | 排序字段（createTime, updateTime, name） |
| sortOrder | String | 否 | desc | 排序方式（asc, desc） |

#### 3.1.4 响应数据格式

**成功响应（200 OK）**：
```json
{
  "status": 200,
  "message": "搜索成功",
  "object": {
    "notes": [
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
  },
  "error": null,
  "timestamp": "2025-12-24T14:30:00.000Z",
  "path": "/api/notes/search"
}
```

**失败响应（400 BAD_REQUEST）**：
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
  "timestamp": "2025-12-24T14:30:00.000Z",
  "path": "/api/notes/search"
}
```

**失败响应（401 UNAUTHORIZED）**：
```json
{
  "status": 401,
  "message": "请先登录",
  "object": null,
  "error": {
    "code": "UNAUTHORIZED",
    "details": "用户未认证",
    "type": "AUTHENTICATION_ERROR"
  },
  "timestamp": "2025-12-24T14:30:00.000Z",
  "path": "/api/notes/search"
}
```

**失败响应（500 INTERNAL_SERVER_ERROR）**：
```json
{
  "status": 500,
  "message": "服务器内部错误，请稍后重试",
  "object": null,
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "details": "服务器内部错误",
    "type": "SYSTEM_ERROR"
  },
  "timestamp": "2025-12-24T14:30:00.000Z",
  "path": "/api/notes/search"
}
```

#### 3.1.5 响应字段含义
| 字段名 | 类型 | 描述 |
|--------|------|------|
| status | Integer | HTTP状态码 |
| message | String | 响应消息 |
| object | Object | 响应数据，成功时包含搜索结果 |
| object.notes | Array | 笔记列表，包含笔记的基本信息 |
| object.total | Integer | 总记录数 |
| object.page | Integer | 当前页码 |
| object.size | Integer | 每页数量 |
| object.totalPages | Integer | 总页数 |
| error | Object | 错误信息，失败时包含 |
| error.code | String | 错误码 |
| error.details | String | 错误详细信息 |
| error.type | String | 错误类型 |
| timestamp | String | 响应时间戳 |
| path | String | 请求路径 |

## 4. 错误码规范及处理建议

### 4.1 错误码定义

| 错误码 | HTTP状态码 | 错误类型 | 描述 |
|--------|------------|----------|------|
| SUCCESS | 200 | SUCCESS | 操作成功 |
| BAD_REQUEST | 400 | VALIDATION_ERROR | 请求参数错误 |
| UNAUTHORIZED | 401 | AUTHENTICATION_ERROR | 未认证 |
| FORBIDDEN | 403 | AUTHORIZATION_ERROR | 无权限 |
| NOT_FOUND | 404 | RESOURCE_ERROR | 资源不存在 |
| INTERNAL_SERVER_ERROR | 500 | SYSTEM_ERROR | 服务器内部错误 |
| DATABASE_ERROR | 500 | SYSTEM_ERROR | 数据库错误 |
| NETWORK_ERROR | 503 | SYSTEM_ERROR | 网络错误 |

### 4.2 错误处理建议

#### 4.2.1 客户端错误处理

1. **网络错误**
   - 显示"网络连接失败，请检查网络"提示
   - 提供重试按钮

2. **请求错误（400）**
   - 根据错误信息提示用户修正参数
   - 例如："搜索关键词不能为空"

3. **认证错误（401）**
   - 跳转到登录页面
   - 提示用户"登录已过期，请重新登录"

4. **授权错误（403）**
   - 显示"无权限访问该资源"提示

5. **服务器错误（500+）**
   - 显示"服务器内部错误，请稍后重试"提示
   - 记录错误日志

#### 4.2.2 前端错误处理示例

```javascript
// 搜索接口错误处理示例
performSearch() {
  this.loading = true;
  this.axios.get('/notes/search', {
    params: {
      keyword: this.searchQuery
    }
  })
  .then(response => {
    if (response.status === 200) {
      this.searchResults = response.data.object.notes;
      this.totalResults = response.data.object.total;
    }
  })
  .catch(error => {
    if (error.response) {
      // 服务器返回错误
      const status = error.response.status;
      switch (status) {
        case 400:
          this.$message.error(error.response.data.message || '参数错误');
          break;
        case 401:
          this.$message.error('登录已过期，请重新登录');
          this.$router.push('/login');
          break;
        case 403:
          this.$message.error('无权限访问该资源');
          break;
        case 404:
          this.$message.error('请求的资源不存在');
          break;
        case 500:
          this.$message.error('服务器内部错误，请稍后重试');
          break;
        default:
          this.$message.error(`请求失败 (${status})`);
      }
    } else if (error.request) {
      // 请求已发送但未收到响应
      this.$message.error('网络连接失败，请检查网络');
    } else {
      // 请求配置错误
      this.$message.error('请求配置错误');
    }
    console.error('搜索失败:', error);
  })
  .finally(() => {
    this.loading = false;
  });
}
```

## 5. 接口调用示例代码

### 5.1 Axios调用示例

```javascript
// 搜索接口调用示例
import axios from 'axios';
import { Message } from 'element-ui';

export default {
  data() {
    return {
      searchQuery: '',
      searchResults: [],
      totalResults: 0,
      page: 1,
      size: 12,
      loading: false
    };
  },
  methods: {
    // 执行搜索
    performSearch() {
      if (!this.searchQuery.trim()) {
        Message.warning('请输入搜索关键词');
        return;
      }
      
      this.loading = true;
      
      // 构造请求参数
      const params = {
        keyword: this.searchQuery,
        page: this.page,
        size: this.size,
        sortBy: 'createdTime',
        sortOrder: 'desc'
      };
      
      // 发送请求
      axios.get('/api/notes/search', { params })
        .then(response => {
          if (response.status === 200) {
            this.searchResults = response.data.object.notes;
            this.totalResults = response.data.object.total;
            this.page = response.data.object.page;
            this.size = response.data.object.size;
            this.totalPages = response.data.object.totalPages;
          }
        })
        .catch(error => {
          // 错误处理逻辑
          if (error.response) {
            Message.error(error.response.data.message || '搜索失败');
          } else {
            Message.error('网络连接失败，请检查网络');
          }
          console.error('搜索失败:', error);
        })
        .finally(() => {
          this.loading = false;
        });
    },
    
    // 分页处理
    handlePageChange(newPage) {
      this.page = newPage;
      this.performSearch();
    },
    
    // 页面大小变化处理
    handleSizeChange(newSize) {
      this.size = newSize;
      this.page = 1;
      this.performSearch();
    }
  },
  
  // 路由参数监听，处理搜索请求
  watch: {
    '$route.query.search': {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.searchQuery = newVal;
          this.performSearch();
        }
      }
    }
  }
};
```

### 5.2 搜索组件使用示例

```vue
<template>
  <div class="search-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="搜索笔记..."
        @keyup.enter="performSearch"
      >
      <button @click="performSearch">搜索</button>
    </div>
    
    <!-- 搜索结果 -->
    <div v-if="searchQuery" class="search-results">
      <div class="results-header">
        <h3>搜索结果：<span>{{ searchQuery }}</span>（共 {{ totalResults }} 条）</h3>
      </div>
      
      <div v-if="loading" class="loading">
        <el-loading-spinner></el-loading-spinner>
        <span>搜索中...</span>
      </div>
      
      <div v-else-if="searchResults.length > 0" class="results-list">
        <div 
          v-for="note in searchResults" 
          :key="note.id" 
          class="note-card"
          @click="goToNoteDetail(note.id)"
        >
          <h4>{{ note.name }}</h4>
          <p class="note-summary">{{ note.abs }}</p>
          <p class="note-time">{{ formatTime(note.createTime) }}</p>
        </div>
      </div>
      
      <div v-else class="no-results">
        <p>没有找到匹配的笔记</p>
      </div>
      
      <!-- 分页 -->
      <div v-if="searchResults.length > 0" class="pagination">
        <el-pagination
          :current-page="page"
          :page-sizes="[12, 24, 48]"
          :page-size="size"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalResults"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>
```

## 6. 前后端数据交互格式约定

### 6.1 请求数据格式
- **格式**：JSON
- **编码**：UTF-8

### 6.2 响应数据格式
- **格式**：JSON
- **编码**：UTF-8
- **统一格式**：所有接口返回统一的JSON格式，包含status、message、object、error等字段

### 6.3 时间格式
- **格式**：ISO 8601标准格式
- **示例**：`2025-12-24T14:30:00.000Z`

### 6.4 空值处理
- **字符串**：空字符串 `""`
- **数字**：`null` 或 `0`（根据业务需求）
- **数组**：空数组 `[]`
- **对象**：空对象 `{}` 或 `null`（根据业务需求）

## 7. 接口变更管理流程

### 7.1 接口变更类型

1. **向后兼容变更**
   - 添加新接口
   - 在请求中添加可选参数
   - 在响应中添加新字段
   - 修改错误信息（不影响错误码和状态码）

2. **非向后兼容变更**
   - 删除接口
   - 修改接口路径
   - 修改请求方法
   - 移除或重命名请求参数
   - 移除或重命名响应字段
   - 修改错误码或状态码

### 7.2 接口变更流程

1. **变更申请**：提出接口变更需求，说明变更原因和影响
2. **评估**：前后端团队评估变更影响和可行性
3. **实现**：后端团队实现接口变更
4. **文档更新**：更新接口文档
5. **通知**：通知前端团队接口变更
6. **前端适配**：前端团队适配接口变更
7. **测试**：进行联调测试
8. **发布**：部署变更后的接口

### 7.3 版本控制
- **接口版本**：使用URL路径版本控制，例如：`/api/v1/notes/search`
- **版本升级**：当接口发生非向后兼容变更时，升级接口版本

## 8. 开发环境与生产环境的配置差异说明

### 8.1 基础URL配置

| 环境 | 基础URL | 协议 |
|------|---------|------|
| 开发环境 | `http://localhost:8080/api` | HTTP |
| 生产环境 | `https://api.bauhausnote.com/api` | HTTPS |

### 8.2 配置管理建议

```javascript
// 环境配置示例
const envConfig = {
  development: {
    baseURL: 'http://localhost:8080/api',
    timeout: 10000
  },
  production: {
    baseURL: 'https://api.bauhausnote.com/api',
    timeout: 10000
  }
};

// 创建axios实例
const service = axios.create({
  baseURL: envConfig[process.env.NODE_ENV].baseURL,
  timeout: envConfig[process.env.NODE_ENV].timeout
});
```

### 8.3 其他配置差异

| 配置项 | 开发环境 | 生产环境 |
|--------|----------|----------|
| 日志级别 | 详细 | 简洁 |
| 错误提示 | 详细错误信息 | 用户友好提示 |
| 性能监控 | 关闭 | 开启 |
| 缓存策略 | 简单 | 复杂 |

## 9. 前端性能优化建议

### 9.1 搜索性能优化

1. **防抖处理**
   - 搜索输入时添加防抖处理，延迟发送请求
   - 避免频繁发送请求

2. **结果缓存**
   - 缓存最近搜索结果
   - 减少重复请求

3. **分页加载**
   - 使用分页加载搜索结果
   - 避免一次性加载大量数据

4. **预加载**
   - 预加载热门搜索结果
   - 提高用户体验

### 9.2 代码优化建议

1. **组件拆分**
   - 将搜索功能拆分为独立组件
   - 提高代码复用性

2. **状态管理**
   - 使用Vuex管理搜索状态
   - 确保状态一致性

3. **懒加载**
   - 懒加载搜索结果组件
   - 提高页面加载速度

## 10. 测试建议

### 10.1 功能测试

1. **搜索功能测试**
   - 验证关键词搜索准确性
   - 测试分页功能
   - 测试排序功能

2. **边界条件测试**
   - 空关键词
   - 不存在的关键词
   - 大量搜索结果

### 10.2 性能测试

1. **响应时间测试**
   - 验证搜索响应时间<2秒
   - 测试大量数据下的性能

2. **并发测试**
   - 测试多用户同时搜索的性能

### 10.3 兼容性测试

1. **浏览器兼容性**
   - Chrome
   - Firefox
   - Safari
   - Edge

2. **设备兼容性**
   - 桌面端
   - 移动端

## 11. 附录

### 11.1 术语定义

| 术语 | 描述 |
|------|------|
| 全局搜索 | 在整个系统范围内搜索笔记 |
| 全文搜索 | 搜索笔记的标题、摘要、内容等所有字段 |
| 分页 | 将搜索结果分成多页显示 |
| 排序 | 按照指定字段对搜索结果进行排序 |
| RESTful API | 基于REST原则的API设计风格 |
| JWT | JSON Web Token，一种认证机制 |

### 11.2 参考文档

- [后端搜索功能开发文档](SEARCH_FUNCTION_DEVELOPMENT_GUIDE.md)
- [Vue.js官方文档](https://vuejs.org/)
- [Axios官方文档](https://axios-http.com/)
- [Element UI官方文档](https://element.eleme.io/)

### 11.3 联系方式

- 后端接口负责人：XXX
- 前端接口负责人：XXX
- 技术支持邮箱：tech-support@bauhausnote.com
