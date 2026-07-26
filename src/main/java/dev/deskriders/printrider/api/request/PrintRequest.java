package dev.deskriders.printrider.api.request;

import io.micronaut.core.annotation.Introspected;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Introspected
public class PrintRequest {
    @NotNull
    String document;
}
