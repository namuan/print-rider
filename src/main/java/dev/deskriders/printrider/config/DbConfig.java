package dev.deskriders.printrider.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public interface DbConfig {
    DynamoDBMapper dynamoDbMapper();
}
