package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.dto.PagedQuery;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
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
}
