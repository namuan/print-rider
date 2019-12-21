package dev.deskriders.printrider.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import lombok.Getter;

import javax.inject.Singleton;

@Singleton
@Requires(notEnv = Environment.AMAZON_EC2)
public class LocalDynamoDbConfig implements DbConfig {

    @Getter
    private AmazonDynamoDB amazonDynamoDB;

    @Getter
    private DynamoDBMapper dynamoDBMapper;

    public LocalDynamoDbConfig() {
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                "http://localhost:9000", "eu-west-1"
        );
        this.amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
        this.dynamoDBMapper = new DynamoDBMapper(this.amazonDynamoDB);
    }

    @Override
    public DynamoDBMapper dynamoDbMapper() {
        return this.dynamoDBMapper;
    }
}
