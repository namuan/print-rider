package dev.deskriders.printrider.api;

import dev.deskriders.printrider.api.request.PrintRequest;
import dev.deskriders.printrider.config.AppConfig;
import dev.deskriders.printrider.service.PrintService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.views.View;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.Map;

@Controller("/prints")
public class PrintController {

    private final AppConfig appConfig;
    private final PrintService printService;

    public PrintController(PrintService aPrintService, AppConfig anAppConfig) {
        this.appConfig = anAppConfig;
        this.printService = aPrintService;
    }

    @Post(consumes = {MediaType.APPLICATION_JSON})
    public HttpResponse<?> savePrint(@Body @Valid PrintRequest printRequest) {
        String printId = printService.savePrintDocument(printRequest.getDocument());
        return HttpResponse.created(URI.create(appConfig.getDomainName() + "/prints/" + printId));
    }

    @View("print")
    @Get(value = "/{printDocumentId}", produces = MediaType.TEXT_HTML)
    public HttpResponse<Map<String, Object>> getPrint(String printDocumentId) {
        String renderedMarkdown = printService.renderMarkdown(printDocumentId);
        return HttpResponse.ok(Map.of("code", renderedMarkdown));
    }
}
