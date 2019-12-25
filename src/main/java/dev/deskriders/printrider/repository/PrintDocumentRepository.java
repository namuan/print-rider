package dev.deskriders.printrider.repository;

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

    public PrintDocumentRepository(DbConfig aDbConfig) {
        this.dbConfig = aDbConfig;
    }

    public String savePrintDocument(PrintDocumentEntity entity) {
        dbConfig.dynamoDbMapper().save(entity);
        return entity.getDocId();
    }

    public Optional<PrintDocumentEntity> loadPrintDocument(PrintDocumentEntity entity) {
        log.info("Loading Entity: " + entity.getDocId());
        PrintDocumentEntity loadedEntity = dbConfig.dynamoDbMapper().load(entity);
        if (Objects.nonNull(loadedEntity)) {
            log.info("Loaded Entity: " + loadedEntity);
        }
        return Optional.ofNullable(loadedEntity);
    }
}
