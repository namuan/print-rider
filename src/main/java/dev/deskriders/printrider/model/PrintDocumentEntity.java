package dev.deskriders.printrider.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "SEE_DYNAMO_DB_MAPPER_FACTORY")
public class PrintDocumentEntity {
    @DynamoDBHashKey(attributeName = "DocId")
    private String docId;

    @DynamoDBAttribute(attributeName = "DocumentCode")
    private String documentCode;
}
