package dev.deskriders.printrider.model;

import lombok.Data;

/**
 * Print document entity stored in DynamoDB.
 */
@Data
public class PrintDocumentEntity {
    private String docId;
    private String documentCode;
}
