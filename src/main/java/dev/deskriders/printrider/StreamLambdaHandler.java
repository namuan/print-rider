package dev.deskriders.printrider;

import io.micronaut.function.aws.proxy.MicronautLambdaHandler;

/**
 * AWS Lambda handler that bridges API Gateway proxy events to the Micronaut application.
 * Environment (e.g. DEVELOPMENT) is controlled via the {@code MICRONAUT_ENVIRONMENTS} env var.
 */
public class StreamLambdaHandler extends MicronautLambdaHandler {
}
