package dev.deskriders.printrider.repository;

import dev.deskriders.printrider.config.DbConfig;
import dev.deskriders.printrider.model.PrintDocumentEntity;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Singleton
public class DynamoDbPrintDocumentRepository implements PrintDocumentRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbPrintDocumentRepository(DbConfig dbConfig,
                                           @Named("printDocumentsTableName") String tableName) {
        this.dynamoDbClient = dbConfig.getDynamoDbClient();
        this.tableName = tableName;
    }

    @Override
    public String savePrintDocument(PrintDocumentEntity entity) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("DocId", AttributeValue.builder().s(entity.getDocId()).build());
        item.put("DocumentCode", AttributeValue.builder().s(entity.getDocumentCode()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
        return entity.getDocId();
    }

    @Override
    public Optional<PrintDocumentEntity> loadPrintDocument(PrintDocumentEntity entity) {
        log.info("Loading Entity: {}", entity.getDocId());

        Map<String, AttributeValue> key = new HashMap<>();
        key.put("DocId", AttributeValue.builder().s(entity.getDocId()).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        Map<String, AttributeValue> result = dynamoDbClient.getItem(request).item();

        if (result != null && !result.isEmpty()) {
            PrintDocumentEntity loaded = new PrintDocumentEntity();
            loaded.setDocId(result.get("DocId").s());
            loaded.setDocumentCode(result.get("DocumentCode").s());
            log.info("Loaded Entity: {}", loaded);
            return Optional.of(loaded);
        }
        return Optional.empty();
    }
}
