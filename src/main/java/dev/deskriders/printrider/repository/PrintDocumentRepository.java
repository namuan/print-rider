package dev.deskriders.printrider.repository;

import dev.deskriders.printrider.model.PrintDocumentEntity;

import java.util.Optional;

/**
 * Repository interface for print document persistence.
 */
public interface PrintDocumentRepository {
    String savePrintDocument(PrintDocumentEntity entity);
    Optional<PrintDocumentEntity> loadPrintDocument(PrintDocumentEntity entity);
}
