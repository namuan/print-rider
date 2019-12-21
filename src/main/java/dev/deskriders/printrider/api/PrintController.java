package dev.deskriders.printrider.api;

import dev.deskriders.printrider.api.request.PrintRequest;
import dev.deskriders.printrider.service.PrintService;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.util.Map;

@Controller(value = "/prints")
@Slf4j
public class PrintController {

    @Value("${hostname}")
    private String hostname;

    @Inject
    private PrintService printService;

    @Post(consumes = {MediaType.APPLICATION_JSON})
    public HttpResponse savePrint(@Valid PrintRequest printRequest) {
        String printId = printService.savePrintDocument(printRequest.getDocument());
        return HttpResponse.created(URI.create(hostname + "/prints/" + printId));
    }

    @View(value = "print")
    @Get(value = "/{printDocumentId}", produces = MediaType.TEXT_HTML)
    public HttpResponse<Map> getPrint(String printDocumentId) {
        String renderedMarkdown = printService.renderMarkdown(printDocumentId);
        return HttpResponse.ok(CollectionUtils.mapOf("code", renderedMarkdown));
    }
}
