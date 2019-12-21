package dev.deskriders.printrider.api;

import dev.deskriders.printrider.api.exception.BadRequestException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;
import java.util.Map;


@Singleton
@Produces
@Requires(classes = {BadRequestException.class})
public class GlobalExceptionHandler implements ExceptionHandler<BadRequestException, HttpResponse> {
    @Override
    public HttpResponse<Map> handle(HttpRequest request, BadRequestException exception) {
        Map error = CollectionUtils.mapOf(
                "error", exception.getMessage()
        );
        return HttpResponse.badRequest(error);
    }
}
