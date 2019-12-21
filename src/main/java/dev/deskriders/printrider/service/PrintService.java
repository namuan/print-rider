package dev.deskriders.printrider.service;

import dev.deskriders.printrider.api.exception.BadRequestException;
import dev.deskriders.printrider.model.PrintDocumentEntity;
import dev.deskriders.printrider.repository.PrintDocumentRepository;
import io.micronaut.context.annotation.Context;

import java.util.UUID;

import static dev.deskriders.printrider.helper.HtmlHelper.sanitiseHtml;

@Context
public class PrintService {

    private PrintDocumentRepository printDocumentRepository;

    public PrintService(PrintDocumentRepository repository) {
        printDocumentRepository = repository;
    }

    public String savePrintDocument(String rawHtml) {
        PrintDocumentEntity printDocumentEntity = new PrintDocumentEntity();
        printDocumentEntity.setDocId(UUID.randomUUID().toString());
        printDocumentEntity.setDocumentCode(sanitiseHtml(rawHtml));

        return printDocumentRepository.savePrintDocument(printDocumentEntity);
    }

    public String renderMarkdown(String printDocumentId) {
        PrintDocumentEntity printDocumentEntity = new PrintDocumentEntity();
        printDocumentEntity.setDocId(printDocumentId);
        return printDocumentRepository.loadPrintDocument(printDocumentEntity)
                .map(PrintDocumentEntity::getDocumentCode)
                .orElseThrow(() -> new BadRequestException("Print " + printDocumentId + " not available"));
    }
}
