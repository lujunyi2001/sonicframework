package org.sonicframework.core.encrypt;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonicframework.core.webapi.service.WebApiResultApplyService;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import org.sonicframework.context.exception.DevelopeCodeException;
import org.sonicframework.utils.JsonUtil;
import org.sonicframework.utils.encrypt.RSAUtil;

/**
* @author lujunyi
*/
public class DecryptFilter<T> implements Filter {

	private EncryptConfig config;
	private RsakeyProviderService rsakeyProvider;
	@Autowired
	private WebApiResultApplyService<T> resultApplyService;
	
	private static Logger logger = LoggerFactory.getLogger(DecryptFilter.class);

	public DecryptFilter(EncryptConfig config, RsakeyProviderService rsakeyProvider){
		this.config = config;
		this.rsakeyProvider = rsakeyProvider;
	}
	
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	logger.info("DecryptFilter has been inited");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(!config.isEnable()) {
        	chain.doFilter(request, response);
        	return;
        }
    	HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        if(StringUtils.isNotBlank(contextPath) && requestURI.startsWith(contextPath)) {
        	requestURI = requestURI.substring(contextPath.length());
        }
        if(CollectionUtils.isEmpty(config.getExclude()) && config.getExclude().contains(requestURI)) {
        	chain.doFilter(request, response);
        	return;
        }
        
        req.setCharacterEncoding("utf-8");
        Set<String> set = null;
        try {
        	set = parseRequestEncryptKey(req.getParameter(this.config.getEncryptKey()));
		} catch (Exception e) {
			writeResponceError("获取加密项失败", request, response);
    		return;
		}
        if(config.getInclude().contains(requestURI) && set.isEmpty()) {
        	writeResponceError("缺少加密项", request, response);
    		return;
        }
        if(set.isEmpty()) {
        	chain.doFilter(request, response);
        	return;
        }
        if(logger.isDebugEnabled()) {
        	logger.debug("decrypy requestURI:[{}], keys:[{}]", requestURI, set);
        }
        Map<String, String[]> paramMap = req.getParameterMap();
        Map<String, String[]> paramMap2 = Maps.newHashMap();

        
        for(String item:paramMap.keySet()){
            String str = paramMap.get(item)[0];
            try {
				str = convertEncryptVal(set, str, item);
			} catch (Exception e) {
				writeResponceError("解密失败", request, response);
	    		return;
			}
            String[] strArr = new String[1];
            //特殊字符处理
            strArr[0]=str;
            paramMap2.put(item,strArr);
        }

        ParameterRequestWrapper wrapRequest = new ParameterRequestWrapper(req, paramMap2);
        request = wrapRequest;

        chain.doFilter(request, response);
    }
    
    private void writeResponceError(String message, ServletRequest request, ServletResponse response) throws IOException {
    	T dto = resultApplyService.applyByResult(DevelopeCodeException.CODE, message);
    	request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=utf-8");
		response.getWriter().write(JsonUtil.toJson(dto));
    }
    
    private String convertEncryptVal(Set<String> set, String oldVal, String key) throws Exception {
    	if(StringUtils.isNotBlank(oldVal) && set.contains(key)) {
    		if(logger.isTraceEnabled()) {
    			logger.trace("decrypt key:[{}]", key);
    		}
			return RSAUtil.decrypt(oldVal.replace(" ", "+"), rsakeyProvider.generatePrivateKey());
    	}
    	return oldVal;
    }
    
    private Set<String> parseRequestEncryptKey(String str){
    	if(StringUtils.isBlank(str)) {
    		return new HashSet<>();
    	}
    	try {
			str = RSAUtil.decrypt(str.replace(" ", "+"), rsakeyProvider.generatePrivateKey());
		} catch (Exception e) {
			e.printStackTrace();
			
		}
    	if(StringUtils.isBlank(str)) {
    		return new HashSet<>();
    	}
    	Set<String> set = new HashSet<>();
    	String[] split = str.split(",");
    	for (int i = 0; i < split.length; i++) {
    		if(StringUtils.isNotBlank(split[i])) {
    			set.add(split[i].trim());
    		}
		}
    	return set;
    }

    @Override
    public void destroy() {
    	logger.info("DecryptFilter has been destroyed");
    }

	public void setResultApplyService(WebApiResultApplyService<T> resultApplyService) {
		this.resultApplyService = resultApplyService;
	}

}
