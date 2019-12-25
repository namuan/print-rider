package dev.deskriders.printrider.repository;

import dev.deskriders.printrider.config.DbConfig;
import dev.deskriders.printrider.model.PrintDocumentEntity;
import io.micronaut.context.annotation.Context;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.Optional;

@Slf4j
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
        System.out.println("Loading Entity: " + entity.getDocId());
        PrintDocumentEntity loadedEntity = dbConfig.dynamoDbMapper().load(entity);
        if (Objects.nonNull(loadedEntity)) {
            System.out.println("Loaded Entity: " + loadedEntity);
        }
        return Optional.ofNullable(loadedEntity);
    }
}
