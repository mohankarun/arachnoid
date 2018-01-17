package arachnoid.service;

import arachnoid.to.Options;
import arachnoid.to.Response;

public interface CrawlService {

public Response crawl(String uri,Options options) ;

public boolean cacheClear();

}
