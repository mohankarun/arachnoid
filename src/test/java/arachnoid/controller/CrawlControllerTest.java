package arachnoid.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import arachnoid.server.App;
import arachnoid.service.CrawlService;
import arachnoid.to.Options;
import arachnoid.to.Response;
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes=App.class)
public class CrawlControllerTest {
	@Autowired
    private MockMvc mvc;
	
	@MockBean
    private CrawlService service;
	
	@Before
	public void setUp(){
		Response response = new Response();
		response.setUri("http://blah.com");
		response.setTitle("Test");
		response.setDepth(1);
		
		Mockito.when(service.crawl(org.mockito.Matchers.anyString(), org.mockito.Matchers.any(Options.class))).thenReturn(response);
	}
	
	
	@Test
	public void testCrawlwithNoURI() throws Exception {
		mvc.perform(get("/crawl")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isBadRequest());

	}
	
	@Test
	public void testCrawlwithURI() throws Exception {
		mvc.perform(get("/crawl?uri=test")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}


	
	@Test
	public void testErrorController() throws Exception {
		mvc.perform(get("/error")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}

	@Test
	public void testCrawlUserDefined() throws Exception {
		mvc.perform(get("/crawl/depth/1/nodeLinks/20?uri=test")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$.uri", is("http://blah.com")));
	}

	@Test
	public void testCrawlLimitDepth() throws Exception{
		mvc.perform(get("/crawl/depth/1?uri=test")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$.uri", is("http://blah.com")))
			      .andExpect(jsonPath("$.title", is("Test")));
	}
	
	@Test
	public void testCrawlLimitNodeLinks() throws Exception{
		mvc.perform(get("/crawl/nodeLinks/1?uri=test")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk())
			      .andExpect(jsonPath("$.uri", is("http://blah.com")));
	}

	@Test
	public void testCrawlwithNonExistent() throws Exception{
		mvc.perform(get("/crawl/failme")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isNotFound());
	}
	
	@Test public void testCrawlerforException() throws Exception {
		Mockito.when(service.crawl(org.mockito.Matchers.anyString(), org.mockito.Matchers.any(Options.class))).thenReturn(null);
		mvc.perform(get("/crawl?uri=http://www.test.com")
			      .contentType(MediaType.APPLICATION_JSON))
			      .andExpect(status().isOk());
	}
	
	
	
	    

}
