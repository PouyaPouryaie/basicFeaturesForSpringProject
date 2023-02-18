package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.exception.SampleExceptionType;
import ir.bigz.springbootreal.messages.MessageContainer;
import ir.bigz.springbootreal.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@CrossOrigin
@RequestMapping("/home")
public class SampleController extends AbstractController {

    final MessageService messageService;

    final MessageSource source;

    @Autowired
    @Qualifier("loadErrorMessageSource")
    ReloadableResourceBundleMessageSource loadMessageSource;

    public SampleController(MessageService messageService, MessageSource source) {
        this.messageService = messageService;
        this.source = source;
    }

    @GetMapping("/v1/geterror")
    public ResponseEntity<?> getErrorMessage(
            @RequestHeader(name = "Accept-Language", required = false) final Locale locale) {
        MessageContainer messageContainer = messageService.getErrorMessage("internal_error");
        return getErrorMessage(loadMessageSource,
                SampleExceptionType.of(messageContainer.getErrorMessages().get(0).getMessageKey()),
                locale, messageContainer.getErrorMessages().get(0).getMessageParams());
    }

    @GetMapping("/v1/welcome")
    public ResponseEntity<?> getLocaleMessage(
            @RequestHeader(name = "Accept-Language", required = false) final Locale locale,
            @RequestParam(name = "username", defaultValue = "Java Geek", required = false) final String username) {
        MessageContainer messageContainer = messageService.getNormalMessage("welcome.message", username);
        return getSuccessMessage(source, messageContainer, locale);
    }

}
