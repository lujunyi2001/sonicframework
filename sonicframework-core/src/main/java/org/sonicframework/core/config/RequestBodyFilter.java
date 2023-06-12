package org.sonicframework.core.config;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.ContentCachingRequestWrapper;

/**
* @author lujunyi
*/
public class RequestBodyFilter implements Filter {

	@Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

    	if(Objects.equals("application/json", request.getContentType())) {
    		request = new ContentCachingRequestWrapper((HttpServletRequest) request);
    	}
    	chain.doFilter(request, response);

    }
}
