package dev.deskriders.printrider.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;

@Factory
public class DynamoDbMapperFactory {
    @Bean
    public DynamoDBMapperConfig dynamoDBMapperConfig(AppConfig appConfig) {
        return DynamoDBMapperConfig
                .builder()
                .withTableNameOverride(
                        new DynamoDBMapperConfig.TableNameOverride(
                                appConfig.getPrintDocumentsDbTable()
                        )
                )
                .build();
    }
}
