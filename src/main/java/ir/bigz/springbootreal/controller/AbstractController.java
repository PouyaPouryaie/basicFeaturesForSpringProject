package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.commons.util.Utils;
import ir.bigz.springbootreal.dto.PagedQuery;
import ir.bigz.springbootreal.exception.ExceptionType;
import ir.bigz.springbootreal.exception.HttpExceptionModel;
import ir.bigz.springbootreal.messages.MessageContainer;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractController {

    protected HttpServletRequest request() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    protected PagedQuery getPagedQuery() {
        return new PagedQuery(request().getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()[0])));
    }

    protected PagedQuery getPagedQuery(Map<String, String> extraParams) {
        var params = request().getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()[0]));
        if (extraParams != null)
            params.putAll(extraParams);
        return new PagedQuery(params);
    }

    protected Map<String, String> getQueryString() {
        Map<String, String> result = new HashMap<>();
        result.putAll(request().getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()[0])));
        return result;
    }

    protected Map<String, String> getUnlimitedSizeParam() {
        return new HashMap() {
            {
                put("size", request().getParameter("size") == null ? "-1" : request().getParameter("size"));
            }
        };
    }

    protected ResponseEntity<?> getSuccessMessage(MessageSource messageSource, MessageContainer messageContainer, Locale locale) {
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaders -> httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8"))
                .body(messageSource.getMessage(messageContainer.getMessages().get(0).getMessageKey(),
                        messageContainer.getMessages().get(0).getMessageParams(), Objects.nonNull(locale) ? locale : Locale.getDefault()));
    }

    protected ResponseEntity<?> getErrorMessage(MessageSource messageSource, ExceptionType exceptionType, Object[] messageParams) {
        return ResponseEntity.status(exceptionType.getHttpStatus())
                .headers(httpHeaders -> httpHeaders.set("Content-Type", MediaType.APPLICATION_JSON_VALUE + "; charset=utf-8"))
                .body(getErrorModelResponse(exceptionType, messageSource, messageParams));
    }

    private HttpExceptionModel getErrorModelResponse(ExceptionType exceptionType, MessageSource messageSource, Object... messageParams) {
        return HttpExceptionModel.builder()
                .errorCode(exceptionType.getErrorCode())
                .message(messageSource.getMessage(exceptionType.getReasonMessage(), messageParams, Locale.getDefault()))
                .timestamp(Utils.getTimestampNow().toString())
                .build();
    }
}
