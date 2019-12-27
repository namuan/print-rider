package dev.deskriders.printrider.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.Getter;

public class DbConfig {
    @Getter
    protected AmazonDynamoDB amazonDynamoDB;

    @Getter
    protected DynamoDBMapper dynamoDBMapper;

    public DynamoDBMapper dynamoDbMapper() {
        return this.dynamoDBMapper;
    }
}
