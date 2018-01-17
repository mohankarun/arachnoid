package arachnoid.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import arachnoid.common.Constants;
import arachnoid.common.Constants.Defaults;
import arachnoid.service.CrawlService;
import arachnoid.to.Error;
import arachnoid.to.Options;
import arachnoid.to.Response;

import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
public class CrawlController extends BaseController{

	private static final Logger logger = Logger.getLogger(CrawlController.class);	

	@Autowired
	CrawlService service;
	
	@RequestMapping(value = "/crawl/depth/{maxDepth}", method = RequestMethod.GET, produces = "application/json")
	public String crawltoDepth(@RequestParam(value = "uri", required = true) String uri,
			@PathVariable Integer maxDepth) throws JsonProcessingException{
		return crawl(uri,maxDepth, Constants.Defaults.maxLinksperNode);
	}
	
	@RequestMapping(value = "/crawl/depth/{maxDepth}/nodeLinks/{maxNodeLinksperNode}", method = RequestMethod.GET, produces = "application/json")
	public String crawlUserDefined(@RequestParam(value = "uri", required = true) String uri,
			@PathVariable Integer maxDepth,
			@PathVariable Integer maxNodeLinksperNode) throws JsonProcessingException{
		return crawl(uri,maxDepth, maxNodeLinksperNode);
	}
	
	@RequestMapping(value = "/crawl/nodeLinks/{maxNodeLinksperNode}", method = RequestMethod.GET, produces = "application/json")
	public String crawlLimited(@RequestParam(value = "uri", required = true) String uri,
			@PathVariable Integer maxNodeLinksperNode) throws JsonProcessingException{
		return crawl(uri,Defaults.maxDepth, maxNodeLinksperNode);
	}
	
	

	@RequestMapping(value = "/crawl", method = RequestMethod.GET, produces = "application/json")
	public String crawl(
			@RequestParam(value = "uri", required = true) String uri,
			Integer maxDepth, Integer maxLinksperNode) throws JsonProcessingException {
		
		try {
			long start = System.currentTimeMillis();
			if(null== maxLinksperNode) maxLinksperNode = Constants.Defaults.maxLinksperNode;
			if(null== maxDepth) maxDepth = Constants.Defaults.maxDepth;
			Options options = new Options();
			options.setDepth(maxDepth);
			options.setMaxLinksperNode(maxLinksperNode);
			
			Response nodes = service.crawl(uri,options);
			logger.info("Returning the response to User in "+(System.currentTimeMillis() - start )+"ms");
			if (nodes != null)
				return map.writeValueAsString(nodes);
			else
				throw new Exception("Error Retreiving the mentioned uri");

		} catch (Exception e) {
			logger.error(e);
			Error error = new Error();
			error.setStatus("Error");
			error.setMessage(e.getLocalizedMessage());
			return map.writeValueAsString(error);
		}

	}
	@RequestMapping(value = "/crawl/cache/clear", method = RequestMethod.GET, produces = "application/json")
	public String cacheClear() throws JsonProcessingException{
		Error cacheResponse = new Error();
		if (service.cacheClear())
		{
			cacheResponse.setStatus("Success");
			cacheResponse.setMessage("Cache Cleared");
		}
		else{
			cacheResponse.setStatus("Failed");
			cacheResponse.setMessage("Cache Clear Failed, Try again");
		}
		return map.writeValueAsString(cacheResponse);
	}

}
