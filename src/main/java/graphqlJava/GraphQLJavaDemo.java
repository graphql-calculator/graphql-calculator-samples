package graphqlJava;

import calculator.config.DefaultConfig;
import calculator.graphql.DefaultGraphQLSourceBuilder;
import calculator.graphql.GraphQLSource;
import common.CommonUtil;
import graphql.ExecutionResult;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;

import java.util.Collections;

/**
 * More details in
 * https://github.com/graphql-calculator/graphql-calculator/blob/main/src/test/java/calculator/example/Example.java
 */
public class GraphQLJavaDemo {

    public static void main(String[] args) {


        /**
         * step 1: create Schema like you always do
         */
        TypeRuntimeWiring.Builder queryBuilder = TypeRuntimeWiring
                .newTypeWiring("Query")
                .dataFetchers(Collections.singletonMap("userInfo", env -> Collections.singletonMap("nickName", "dugenkui")));

        TypeRuntimeWiring.Builder userBuilder = TypeRuntimeWiring
                .newTypeWiring("UserInfo")
                .dataFetchers(Collections.singletonMap("nickName", env -> null));

        RuntimeWiring.Builder runtimeWiring = RuntimeWiring.newRuntimeWiring().type(queryBuilder).type(userBuilder);
        GraphQLSchema originalSchema = CommonUtil.schemaByInputFile("schema.graphqls", runtimeWiring.build());


        /**
         * step 2: create GraphQLSource
         */
        DefaultGraphQLSourceBuilder graphqlSourceBuilder = new DefaultGraphQLSourceBuilder();
        GraphQLSource graphqlSource = graphqlSourceBuilder
                // TODO you can replace the script engine by calculator.config.Config
                //      and the default script engine is AviatorScript(https://github.com/killme2008/aviatorscript)
                .wrapperConfig(DefaultConfig.newConfig().build())
                .originalSchema(originalSchema)
                //.preparsedDocumentProvider(new DocumentParseAndValidationCache())
                .build();

        /**
         * execute the query by GraphQLSource
         */
        String query = " " +
                "query { \n" +
                "    userInfo{ \n" +
                "        nickName @map(mapper:\"string.substring(nickName,0,4)\")\n" +
                "    } \n" +
                "} ";
        ExecutionResult result = graphqlSource.getGraphQL().execute(query);
    }
}
