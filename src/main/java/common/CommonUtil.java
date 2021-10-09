package common;

import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.InputStream;
import java.io.InputStreamReader;

public class CommonUtil {

    public static GraphQLSchema schemaByInputFile(String inputPath, RuntimeWiring runtimeWiring) {
        InputStream inputStream = CommonUtil.class.getClassLoader().getResourceAsStream(inputPath);
        InputStreamReader inputReader = new InputStreamReader(inputStream);
        TypeDefinitionRegistry registry = new SchemaParser().parse(inputReader);
        return new SchemaGenerator().makeExecutableSchema(registry, runtimeWiring);
    }

}
