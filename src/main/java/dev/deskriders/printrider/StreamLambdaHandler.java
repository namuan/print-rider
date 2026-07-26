package dev.deskriders.printrider;

import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.env.Environment;
import io.micronaut.function.aws.proxy.payload1.ApiGatewayProxyRequestEventFunction;

/**
 * AWS Lambda handler for API Gateway v1 (REST API) proxy events.
 * Uses {@link ApiGatewayProxyRequestEventFunction} which is the non-deprecated
 * replacement for {@code MicronautLambdaHandler}.
 * <p>
 * When running under AWS SAM local ({@code AWS_SAM_LOCAL=true}), the
 * {@code DEVELOPMENT} environment is activated so that
 * {@link dev.deskriders.printrider.config.LocalDynamoDbConfig} replaces
 * the production DynamoDB config.
 */
public class StreamLambdaHandler extends ApiGatewayProxyRequestEventFunction {

    @Override
    protected ApplicationContextBuilder newApplicationContextBuilder() {
        ApplicationContextBuilder builder = super.newApplicationContextBuilder();
        String awsSamLocal = System.getenv("AWS_SAM_LOCAL");
        if (Boolean.parseBoolean(awsSamLocal)) {
            builder.environments(Environment.DEVELOPMENT);
        }
        return builder;
    }
}
