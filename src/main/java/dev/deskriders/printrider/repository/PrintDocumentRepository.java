package dev.deskriders.printrider.repository;

import dev.deskriders.printrider.config.DbConfig;
import dev.deskriders.printrider.model.PrintDocumentEntity;
import io.micronaut.context.annotation.Context;

import javax.inject.Inject;
import java.util.Optional;

@Context
public class PrintDocumentRepository {

    @Inject
    private DbConfig dbConfig;

    public String savePrintDocument(PrintDocumentEntity entity) {
        dbConfig.dynamoDbMapper().save(entity);
        return entity.getDocId();
    }

    public Optional<PrintDocumentEntity> loadPrintDocument(PrintDocumentEntity entity) {
        PrintDocumentEntity loadedEntity = dbConfig.dynamoDbMapper().load(entity);
        return Optional.ofNullable(loadedEntity);
    }
}
