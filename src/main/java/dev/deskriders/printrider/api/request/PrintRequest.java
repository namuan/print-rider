package dev.deskriders.printrider.api.request;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
@Introspected
public class PrintRequest {
    @NotNull
    String document;
}
