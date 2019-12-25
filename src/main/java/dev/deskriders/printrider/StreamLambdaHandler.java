package dev.deskriders.printrider;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.ApplicationContextBuilder;
import io.micronaut.context.env.Environment;
import io.micronaut.function.aws.proxy.MicronautLambdaContainerHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamLambdaHandler implements RequestStreamHandler {
    private static MicronautLambdaContainerHandler handler;

    static {
        try {
            ApplicationContextBuilder contextBuilder = ApplicationContext.build();
            String aws_sam_local = System.getenv("AWS_SAM_LOCAL");
            if (Boolean.parseBoolean(aws_sam_local)) {
                contextBuilder.environments(Environment.DEVELOPMENT);
            }
            handler = new MicronautLambdaContainerHandler(contextBuilder);
        } catch (ContainerInitializationException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialise Micronaut", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        handler.proxyStream(input, output, context);
    }
}
