# feat: Merchant表和DTO说明

## Merchant表
属性：
```
long id,
String merchantName,
String type,
float score,
String location,
float averageConsumption,
long telephoneNumber,
Date startTime,   //营业时间开始
Date endTime,   //营业时间结束
String tags,   //标签，最多500字
String photoUrl //照片的在项目里面的相对路径，照片应放在总项目的MerchantPhoto内
```

## DTO
按要求，只提供了Response方法

