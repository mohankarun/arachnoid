package arachnoid.to;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Response {
	
	private String uri;
	private String title;
	@JsonIgnore
	private Integer depth =0;
	private ArrayList<Response> nodes = new ArrayList<Response>();
	
	
	public Response(String uri, String title){
		this.uri = uri;
		this.title = title;
	}
	public Response(){
		
	}
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	public ArrayList<Response> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<Response> nodes) {
		this.nodes = nodes;
	}
	
	public void incrementDepth(){
		this.depth ++;
	}


}
