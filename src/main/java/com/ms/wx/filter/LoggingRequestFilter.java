package com.ms.wx.filter;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoggingRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request) && !logger.isInfoEnabled()) {
            filterChain.doFilter(request, response);
        } else {
            doFilter(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    private void doFilter(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        var stopWatch = new StopWatch();
        stopWatch.start();

        var method = request.getMethod();
        try {
            var uri = request.getRequestURI();
            var size = request.getContentLengthLong();
            logger.info(">> [" + method + "] " + uri + " request params size: " + (size == -1 ? "?" : size) + "B");
            filterChain.doFilter(request, response);
        } finally {
            stopWatch.stop();

            var status = response.getStatus();
            var size = response.getContentSize();
            var elapsed = stopWatch.getTotalTimeMillis();

            logger.info("<< " + status + " response data size: " + size + "B " + elapsed + " ms");

            response.copyBodyToResponse();
        }
    }

    private ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

}
