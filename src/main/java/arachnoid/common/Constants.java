package arachnoid.common;


public class Constants {
	
	public static class Defaults {
		public static final Integer maxDepth = 2;
		public static final Integer maxLinksperNode = 5;
		
		
	}
	
	public static class Patterns {
		public static final String STANDARD_URL = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		public static final String BINARY_URLS = ".*(.css|.js|.pdf|.ico|.gif|.png|.jpg|.jpeg|.tif|.tiff|.svg|.xml|php)";
		public static final String PAGE_TITLE = "<title>(.*?)</title>";
		public static final String PAGE_LINKS ="href=\"(.*?)\"";
	}

}
