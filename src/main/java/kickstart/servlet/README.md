# graphql-java-servlet 集成  graphql calculator


#### 启动服务

执行`kickstart.servlet.HttpMain`中的main函数。


#### 执行查询

执行如下查询。
```graphql
query {
    hello @map(mapper:"'mockValue'")
}
```

请求链接为：http://localhost:8080/graphql?query=query%20{%20hello%20@map(mapper:%22%27mockValue%27%22)%20}
