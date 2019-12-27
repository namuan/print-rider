package dev.deskriders.printrider.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(value = "app")
public class AppConfig {
    private String domainName;
    private String dynamo;
}
