package arachnoid.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import arachnoid.common.Constants;
import arachnoid.to.Options;
import arachnoid.to.Response;

@Service
public class CrawlServiceImpl implements CrawlService {

	private RestTemplate http;
	private Map<String, String> cache = new HashMap<String, String>();

	private static Logger logger = Logger.getLogger(CrawlServiceImpl.class);

	private Response processNode(Response node, Options pref) {
		Integer linkCounter = 0;

		long start = System.currentTimeMillis();

		linkCounter++;
		String uri = node.getUri();
		node.setUri(uri);
		node.incrementDepth();

		logger.debug("[" + node.getDepth() + "] Procesing URI : "
				+ node.getUri());

		if (!validURL(uri)) {
			return null;
		}

		try {
			String responseBody = (cache.get(uri) != null) ? cache.get(uri)
					: null;
			if (null != responseBody) {
					logger.info("Loaded from Cache... "+uri);
			
			}
			if (null == responseBody) {
				responseBody = getResponse(uri);
				logger.info("Loaded from Server... "+uri);
			}
			if (null != responseBody) {
				cache.put(uri, responseBody);
			} else {
				throw new Exception("Null Response from URI " + uri);
			}

			extractLinks(node, pref, responseBody);
			ArrayList<Response> processed = processChildNodes(node.getNodes(), pref);
			node.setNodes(processed);
		} catch (Exception e) {
			e.printStackTrace();
			// silent catch
			logger.error("URI " + uri);
			logger.error("Arachnoid is not able to process further , "
					+ e.getLocalizedMessage());
			return null;
		}

		logger.debug("[" + node.getDepth() + "]" + "Time Taken : "
				+ (System.currentTimeMillis() - start) + "ms");
		return node;
	}

	private void extractLinks(Response node, Options pref,
			String responseBody) throws Exception {
		logger.debug(pref.toString());
		long start = System.currentTimeMillis();
		String uri = node.getUri();
		URL me = new URL(uri);
		Pattern title = Pattern.compile(Constants.Patterns.PAGE_TITLE);
		Matcher getTitle = title.matcher(responseBody);

		while (getTitle.find()) {
			node.setTitle(getTitle.group(1));
		}
		Pattern links = Pattern.compile(Constants.Patterns.PAGE_LINKS);
		Matcher checkLinks = links.matcher(responseBody);
		int linkCounter=0;

		while (checkLinks.find()) {

			String child_uri = checkLinks.group(1);

			child_uri = handleStructure(child_uri, me);
			if (!validURL(child_uri)) {
				continue;
			}
			linkCounter++;

			if (!child_uri.equals(uri)) {
				Response childNode = new Response();
				childNode.setUri(child_uri);
				childNode.setDepth(node.getDepth());
				if (node.getDepth() <= pref.getDepth()
						&& linkCounter <= pref.getMaxLinksperNode()) {
					node.getNodes().add(childNode);
				}
			}
		}
		logger.debug("[" + node.getDepth() + "]" + "Time Taken : "
				+ (System.currentTimeMillis() - start) + "ms");
	}

	public String getResponse(String uri) throws Exception {
		ResponseEntity<String> response = getHttp().getForEntity(uri,
				String.class);
		if (response.getHeaders().getContentType()
				.includes(MediaType.TEXT_HTML)) {
			String responseBody = response.getBody();
			return responseBody;
		} else
			throw new Exception("Not a HTML Response for " + uri);
	}

	public boolean validURL(String url) {
		return url.matches(Constants.Patterns.STANDARD_URL)
				&& !url.matches(Constants.Patterns.BINARY_URLS);
	}

	private String handleStructure(String url, URL parentNode) {
		// Relative-URLS and protocol agnostic ones
		if (url.startsWith("//")) {
			url = parentNode.getProtocol() + ":" + url;
		} else if (url.startsWith("/")) {
			url = parentNode.getProtocol() + "://" + parentNode.getHost() + url;

		}

		// Tweaks
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}

		return url;

	}

	@Override
	public Response crawl(String uri, Options options) {
		Response mainNode = new Response();
		mainNode.setUri(uri);
		return processNode(mainNode, options);
	}

	public ArrayList<Response> processChildNodes(List<Response> inputs,
			Options options) throws InterruptedException, ExecutionException {
		long start = System.currentTimeMillis();
		ArrayList<Response> outputs = new ArrayList<Response>();
		Response processed;
		for(Response input:inputs){
			processed = processNode(input, options);
			if(processed!=null){
			outputs.add(processNode(input, options));
			}
		}
		
		
//         long start = System.currentTimeMillis();
//		int threads = Runtime.getRuntime().availableProcessors();
//		ExecutorService service = Executors.newFixedThreadPool(threads);
//		int linkCounter = 0;
//
//		List<Future<Response>> futures = new ArrayList<Future<Response>>();
//		for (final Response input : inputs) {
//			if (linkCounter < options.getMaxLinksperNode()) {
//				Callable<Response> callable = new Callable<Response>() {
//					public Response call() throws Exception {
//						return processNode(input, options);
//					}
//				};
//				futures.add(service.submit(callable));
//			}
//			linkCounter++;
//		}
//
//		service.shutdown();
//
//		ArrayList<Response> outputs = new ArrayList<Response>();
//		for (Future<Response> future : futures) {
//			if (null != future.get())
//				outputs.add(future.get());
//		}
		
		logger.debug("Time Taken on Nodes:: " + (System.currentTimeMillis() - start)+"ms");
		return outputs;
	}

	@Override
	public boolean cacheClear() {
		try {
			cache.clear();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public RestTemplate getHttp() {
		if (null != http)
			return http;
		else {
			http = new RestTemplate();
			return http;

		}
	}

	public void updateHttp(RestTemplate http) {
		this.http = http;
	}

}
