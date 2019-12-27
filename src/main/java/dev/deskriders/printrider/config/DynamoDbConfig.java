package dev.deskriders.printrider.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.inject.Singleton;

@Singleton
public class DynamoDbConfig extends DbConfig {

    public DynamoDbConfig() {
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDBMapper = new DynamoDBMapper(this.amazonDynamoDB);
    }
}
