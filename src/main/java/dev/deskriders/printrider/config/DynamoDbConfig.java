package dev.deskriders.printrider.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import lombok.Getter;

import javax.inject.Singleton;

@Singleton
@Requires(env = Environment.AMAZON_EC2)
public class DynamoDbConfig implements DbConfig {
    @Getter
    private AmazonDynamoDB amazonDynamoDB;

    @Getter
    private DynamoDBMapper dynamoDBMapper;

    public DynamoDbConfig() {
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDBMapper = new DynamoDBMapper(this.amazonDynamoDB);
    }

    @Override
    public DynamoDBMapper dynamoDbMapper() {
        return this.dynamoDBMapper;
    }
}
