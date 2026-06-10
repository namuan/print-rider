package dev.deskriders.printrider.config;

import lombok.Getter;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DbConfig {
    @Getter
    protected DynamoDbClient dynamoDbClient;
}
