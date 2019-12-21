package dev.deskriders.printrider.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.Getter;

public interface DbConfig {
    public DynamoDBMapper dynamoDbMapper();
}
