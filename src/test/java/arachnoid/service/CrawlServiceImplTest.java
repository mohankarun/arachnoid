package arachnoid.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import arachnoid.to.Options;
import arachnoid.to.Response;

@RunWith(MockitoJUnitRunner.class) 
public class CrawlServiceImplTest {
 
	@Mock
    private RestTemplate http;
    
    @InjectMocks
    @Spy
    private CrawlServiceImpl service;
    
    @Mock
    ResponseEntity mockResponse;
    
    @Before
    public void setUp() throws Exception{
    	//mockResponse = new ResponseEntity<String>(HttpStatus.OK);
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.TEXT_HTML);
    	Mockito.when(mockResponse.getHeaders()).thenReturn(headers);
		Mockito.when(mockResponse.getBody())
				.thenReturn(
						"<head><title>Zero-d IN</title></head><a href=\"http://i.me.myself\"</a><a href=\"ftp://i.me.myself\"</a><a href=\"//i.agnostic.myself/\"</a><a href=\"/i.relative.myself\"</a>");
	//Mockito.when(service.getResponse(anyString())).thenReturn("<head><title>Zero-d IN</title></head><a href=\"http://i.me.myself\"</a><a href=\"ftp://i.me.myself\"</a><a href=\"//i.agnostic.myself/\"</a><a href=\"/i.relative.myself\"</a>");
    Mockito.when(http.getForEntity(anyString(), Mockito.any())).thenReturn(mockResponse);
    service.updateHttp(http);
    	}
 
    @Test
    public void crawlTest() throws Exception {
    	ArrayList<Response> nodes = new ArrayList<Response>();
    	nodes.add(new Response("http://a.com/","A"));
    	nodes.add(new Response("//b.com","B"));
    	nodes.add(new Response("/c","C"));
    	Mockito.when(service.processChildNodes(Matchers.anyList(), Matchers.anyObject())).thenReturn(nodes);
    	Options pref  = new Options();
    	pref.setDepth(1);
    	pref.setMaxLinksperNode(2);
        Response response = service.crawl("http://blah.com", pref);
        
        Mockito.verify(service).validURL("http://blah.com");
        Mockito.verify(service).getResponse("http://blah.com");
        Mockito.verify(service,times(1)).getHttp();
        
        assertEquals("Zero-d IN",response.getTitle());
        assertEquals(3,response.getNodes().size());
    }
    @Test
    public void crawlTestNegative() throws InterruptedException, ExecutionException {
    	Options pref  = new Options();
    	pref.setDepth(1);
        Response response = service.crawl("blah", pref);
        
        Mockito.verify(service).validURL("blah");
        assertNull(response);
    }
    
    
    
    @Test
    public void crawlTestError() throws Exception {
    	Mockito.when(service.getResponse(anyString())).thenReturn(null);
    	Options pref  = new Options();
    	pref.setDepth(1);
    	pref.setMaxLinksperNode(1);
        Response response = service.crawl("//blah.com", pref);
        assertNull(response);
    }
    
    @Test(expected = Exception.class)
    public void crawlTestnonHTML() throws Exception {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	Mockito.when(mockResponse.getHeaders()).thenReturn(headers);
        Mockito.when(http.getForEntity(anyString(), Mockito.any())).thenReturn(mockResponse);
        service.updateHttp(http);
        service.getResponse("http://json.com");
    }
    
    @Test
    public void crawlTestnullResponse() throws Exception {
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	Mockito.when(mockResponse.getHeaders()).thenReturn(headers);
    	Mockito.when(mockResponse.getBody()).thenReturn(null);
        Mockito.when(http.getForEntity(anyString(), Mockito.any())).thenReturn(mockResponse);
        service.updateHttp(http);
        Options pref  = new Options();
    	pref.setDepth(1);
    	pref.setMaxLinksperNode(1);
        service.crawl("https://blah.com", pref);
    }
    
    
    
    @Test 
    public void crawlTestProcessor() throws Exception {
    	Options pref = new Options();
    	pref.setDepth(5);
    	pref.setMaxLinksperNode(10);
    	Response a = new Response();
    	a.setUri("http://a1.com");
    	a.setDepth(2);
    	Response b = new Response();
    	b.setUri("http://b1.com");
    	a.setDepth(2);
    	ArrayList<Response> childNodes = new ArrayList<Response>();
    	childNodes.add(a);
    	childNodes.add(b);
    	ArrayList<Response> processed = service.processChildNodes(childNodes, pref);
    	assertEquals(2,processed.size());
    	
    }
    @Test
    public void crawlTestInjection() throws InterruptedException, ExecutionException {
    	service.updateHttp(null);
    	Options pref  = new Options();
    	pref.setDepth(1);
        Response response = service.crawl("blah", pref);
        
        Mockito.verify(service).validURL("blah");
        assertNull(response);
    }
    
   
}
