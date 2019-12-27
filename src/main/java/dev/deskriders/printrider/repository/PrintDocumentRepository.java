package dev.deskriders.printrider.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import dev.deskriders.printrider.config.DbConfig;
import dev.deskriders.printrider.model.PrintDocumentEntity;
import io.micronaut.context.annotation.Context;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;
import java.util.Optional;

@Log4j2
@Context
public class PrintDocumentRepository {

    private DbConfig dbConfig;

    private DynamoDBMapperConfig dynamoDBMapperConfig;

    public PrintDocumentRepository(DbConfig aDbConfig, DynamoDBMapperConfig dynamoDBMapperConfig) {
        this.dbConfig = aDbConfig;
        this.dynamoDBMapperConfig = dynamoDBMapperConfig;
    }

    public String savePrintDocument(PrintDocumentEntity entity) {
        dbConfig.dynamoDbMapper().save(entity, dynamoDBMapperConfig);
        return entity.getDocId();
    }

    public Optional<PrintDocumentEntity> loadPrintDocument(PrintDocumentEntity entity) {
        log.info("Loading Entity: " + entity.getDocId());
        PrintDocumentEntity loadedEntity = dbConfig.dynamoDbMapper().load(entity, dynamoDBMapperConfig);
        if (Objects.nonNull(loadedEntity)) {
            log.info("Loaded Entity: " + loadedEntity);
        }
        return Optional.ofNullable(loadedEntity);
    }
}
