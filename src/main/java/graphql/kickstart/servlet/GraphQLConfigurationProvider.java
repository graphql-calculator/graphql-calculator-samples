package graphql.kickstart.servlet;


import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import calculator.config.Config;
import calculator.config.DefaultConfig;
import calculator.graphql.CalculatorDocumentCachedProvider;
import calculator.graphql.DefaultGraphQLSourceBuilder;
import calculator.graphql.GraphQLSource;
import graphql.ExecutionInput;
import graphql.execution.preparsed.PreparsedDocumentEntry;
import graphql.kickstart.execution.GraphQLQueryInvoker;
import graphql.kickstart.execution.config.DefaultExecutionStrategyProvider;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class GraphQLConfigurationProvider {

    private static GraphQLConfigurationProvider instance;

    private final GraphQLConfiguration configuration;

    private GraphQLConfigurationProvider() {

        /**
         * step 1: 参考
         */
        Config wrapperConfig = DefaultConfig.newConfig().build();
        DefaultGraphQLSourceBuilder graphqlSourceBuilder = new DefaultGraphQLSourceBuilder();
        GraphQLSource source = graphqlSourceBuilder
                .wrapperConfig(wrapperConfig)
                .originalSchema(createSchema())
                .preparsedDocumentProvider(new DocumentParseAndValidationCache())
                .build();

        GraphQLQueryInvoker queryInvoker = GraphQLQueryInvoker.newBuilder()
                .withExecutionStrategyProvider(
                        new DefaultExecutionStrategyProvider(
                                source.getGraphQL().getQueryStrategy(),
                                source.getGraphQL().getMutationStrategy(),
                                source.getGraphQL().getSubscriptionStrategy()))
                // kp
                .withInstrumentation(source.getGraphQL().getInstrumentation())
                // kp
                .withPreparsedDocumentProvider(source.getGraphQL().getPreparsedDocumentProvider())
                .build();

        configuration = GraphQLConfiguration.
                // kp 3
                        with(source.getWrappedSchema())
                .with(queryInvoker)
                .build();
    }

    static class DocumentParseAndValidationCache extends CalculatorDocumentCachedProvider {

        private final Map<String, PreparsedDocumentEntry> cache = new ConcurrentHashMap<>();

        @Override
        public PreparsedDocumentEntry getDocumentFromCache(ExecutionInput executionInput,
                                                           Function<ExecutionInput, PreparsedDocumentEntry> parseAndValidateFunction) {
            return cache.get(executionInput.getQuery());
        }

        @Override
        public void setDocumentCache(ExecutionInput executionInput,
                                     PreparsedDocumentEntry cachedValue) {
            cache.put(executionInput.getQuery(), cachedValue);
        }
    }

    static GraphQLConfigurationProvider getInstance() {
        if (instance == null) {
            instance = new GraphQLConfigurationProvider();
        }
        return instance;
    }

    GraphQLConfiguration getConfiguration() {
        return configuration;
    }

    private GraphQLSchema createSchema() {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(loadSchemaFile());

        RuntimeWiring runtimeWiring =
                newRuntimeWiring()
                        .type(newTypeWiring("Query").dataFetcher("hello", new StaticDataFetcher("world")))
                        .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private Reader loadSchemaFile() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("schema.graphqls");
        return new InputStreamReader(stream);
    }
}

