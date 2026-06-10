package dev.deskriders.printrider.config;

import jakarta.inject.Singleton;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Singleton
public class DynamoDbConfig extends DbConfig {

    public DynamoDbConfig() {
        this.dynamoDbClient = DynamoDbClient.builder().build();
    }
}
