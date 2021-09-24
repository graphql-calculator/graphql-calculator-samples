# graphql-java-servlet 集成  graphql calculator


## 集成步骤

任何基于`GraphQL Java`的框架与`GraphQL Calculator`集成时，基本思路是先按照[graphql calculator#快速开始](https://github.com/graphql-calculator/graphql-calculator#%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B)生成具有计算能力的执行引擎对象`GraphQL`和具有指令定义的`GraphQLSchema`（包含在`GraphQLSource`对象中）。

然后在创建其他框架时使用的graphql相关对象都使用`GraphQLSource`中的数据即可，主要有 queryExecutionStrategy、Instrumentation、GraphQLSchema 和 PreparsedDocumentProvider，**queryExecutionStrategy、Instrumentation是创建新的框架执行引擎对象时必须设置的字段。** 


####  创建 `GraphQLSource`

```
        /**
         * step 1: 使用 graphql calculator 生成GraphQL计算执行引擎
         */
        DefaultGraphQLSourceBuilder graphqlSourceBuilder = new DefaultGraphQLSourceBuilder();
        GraphQLSource source = graphqlSourceBuilder
                .wrapperConfig(DefaultConfig.newConfig().build())
                // 原始 Schema
                .originalSchema(createSchema())
                // 建议使用 CalculatorDocumentCachedProvider 实现类
                .preparsedDocumentProvider(new DocumentParseAndValidationCache())
                .build();
```


#### 创建 `graphql-java-kickstart` 执行引擎
创建graphql-java-kickstart `GraphQLConfiguration`对象时所用的`GraphQL`引擎相关对象都使用`GraphQLSource` 对象中数据即可。

```java
        /**
         * step 2:
         *      创建 GraphQLConfiguration 用的 graphql相关对象，使用 'GraphQLSource source' 中的数据；
         *      主要有 queryExecutionStrategy、Instrumentation、GraphQLSchema 和 PreparsedDocumentProvider
         *
         */
        GraphQLQueryInvoker queryInvoker = GraphQLQueryInvoker.newBuilder()
                .withExecutionStrategyProvider(
                        new DefaultExecutionStrategyProvider(
                                source.getGraphQL().getQueryStrategy(),
                                source.getGraphQL().getMutationStrategy(),
                                source.getGraphQL().getSubscriptionStrategy()))
                // calculator source
                .withInstrumentation(source.getGraphQL().getInstrumentation())
                // calculator source
                .withPreparsedDocumentProvider(source.getGraphQL().getPreparsedDocumentProvider())
                .build();

        GraphQLConfiguration configuration = GraphQLConfiguration.
                        // calculator source
                        with(source.getWrappedSchema())
                .with(queryInvoker)
                .build();
```


## 请求/测试

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
