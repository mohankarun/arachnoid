package arachnoid.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import arachnoid.to.Error;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class CrawlErrorController extends BaseController  implements ErrorController {

private static final Logger logger = Logger.getLogger(CrawlErrorController.class);	
	private static final String PATH = "/error";
	@Override
	public String getErrorPath() {
		return PATH;
	}
	 @Autowired
	    private ErrorAttributes errorAttributes;
		
	@RequestMapping(value = PATH)
	public String error(HttpServletRequest request, HttpServletResponse response)  throws JsonProcessingException{
		logger.error(getErrorAttributes(request, true));
		return map.writeValueAsString(new Error(Integer.toString(response.getStatus()), (String)getErrorAttributes(request, true).get("message")));
	}
	 
	 private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
	        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
	        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
	    }

}
