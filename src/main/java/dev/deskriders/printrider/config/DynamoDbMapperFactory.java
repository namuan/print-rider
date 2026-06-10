package dev.deskriders.printrider.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;

/**
 * Factory that provides the configured DynamoDB table name.
 */
@Factory
public class DynamoDbMapperFactory {

    @Bean
    @Named("printDocumentsTableName")
    public String tableName(AppConfig appConfig) {
        return appConfig.getPrintDocumentsDbTable();
    }
}
