package dev.deskriders.printrider.config;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@Singleton
@Replaces(DynamoDbConfig.class)
@Requires(env = {Environment.DEVELOPMENT, Environment.TEST})
public class LocalDynamoDbConfig extends DbConfig {

    public LocalDynamoDbConfig(AppConfig appConfig) {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_WEST_1)
                .endpointOverride(URI.create(appConfig.getDynamo()))
                .build();
    }
}
