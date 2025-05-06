# 优惠券与订单API文档
## 优惠券API
### 获取用户有效优惠券
- URL : /api/coupons
- 方法 : GET
- 请求头 :
  - UserId : 用户ID
- 参数 :
  - merchantCategory : 可选，商家类别
- 响应 :
  - 返回用户所有有效的优惠券列表(数量>0且未过期)
  - 示例:
    json
    [
    {
    
    "id" : 1 ,
    
    "couponName" : "无门槛优惠券" ,
    
    "userId" : 123 ,
    
    "couponAmount" : 5 ,
    
    "category" : "餐饮" ,
    
    "shopId" : 456 ,
    
    "expireTime" : "2023-12-31T23:59:59"
    
    }
    
    ]
    

### 按选择发放优惠券
- URL : /api/coupons/issue-by-choice
- 方法 : POST
- 请求头 :
  - UserId : 用户ID
- 参数 :
  - choice : 优惠券类型(A/B/C/D)
    - A: 无门槛优惠券
    - B: 满30减8元
    - C: 0元免单券(10元以内)
    - D: 满100打8折券(最多可减30元)
- 响应 : 无返回内容
### 按名称发放优惠券
- URL : /api/coupons/issue-by-name
- 方法 : POST
- 请求头 :
  - UserId : 用户ID
- 请求体 :
  json
 [ 
  
  {
  
  "couponName" : "自定义优惠券" ,
  
  "couponAmount" : 10 ,
  
  "category" : "餐饮" ,
  
  "shopId" : 456 ,
  
  "expireTime" : "2023-12-31T23:59:59"
  
  }
 ]
- 响应 : 无返回内容
### 使用优惠券
- URL : /api/coupons/use/{couponId}
- 方法 : POST
- 请求头 :
  - UserId : 用户ID
- 响应 : 无返回内容
- 说明 :
  - 使用后优惠券数量减1
  - 当数量变为0时自动删除该优惠券
## 订单API
### 创建订单
- URL : /api/orders
- 方法 : POST
- 请求头 :
  - UserId : 用户ID
- 请求体 :
  json
  [
  {
  
  "packageId" : 1 ,
  
  "businessId" : 1
  
  }
  ]
- 响应 :
  - 成功: 返回创建的订单对象
  - 失败: 返回错误信息
  - 示例:
    json
    [
    
    {
    
    "id" : 1 ,
    
    "userId" : 123 ,
    
    "packageId" : 1 ,
    
    "status" : "CREATED"
    
    }

    ]
### 获取订单详情
- URL : /api/orders/{orderId}
- 方法 : GET
- 请求头 :
  - UserId : 用户ID
- 响应 :
  - 返回订单详情对象
  - 示例:
    [
    
    {
    
    "id" : 1 ,
    
    "userId" : 123 ,
    
    "packageId" : 1 ,
    
    "status" : "PAID" ,
    
    "createdAt" : "2023-01-01T00:00:00"
    
    }
    ]
    ### 获取用户订单列表
- URL : /api/orders/user
- 方法 : GET
- 请求头 :
  - UserId : 用户ID
- 响应 :
  - 返回用户所有订单列表
  - 示例:
    
    [
    
    {
    
    "id" : 1 ,
    
    "packageId" : 1 ,
    
    "status" : "PAID"
    
    }
    
    ]