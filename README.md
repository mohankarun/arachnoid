# Arachnoid 
### Overview 
This is a Simple rest service build on Java/Spring which takes a uri as a parameter, crawls the uri to identify links, checks if they are accessible and provides a JSON response in a tree structure  with identified links. There are a few customizable options for depth and linkswithin a node the JSON response.
###Tech Stack 
- Java 1.8
- Spring Boot Web 1.5.9
- Junit 4.12
- Gradle 

### Requirements
 - Java  [install](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)
 - Gradle [install](https://gradle.org/install)

## Installation
#### Developement
The gradle build has a wrapper task which installs required libraries and starts the server on port 8080 on local server.

```
$ gradle start
```
Alternatively we can build an executable jar and deploy to a specific port 

```
$gradle build
$java -Dserver.port=9999 -jar build/libs/arachnoid-1.0.0.jar
```
the above command starts the server on port 9999 . 
#### Run TestCases 
```
$gradle test
```

#### Generate Coverage Report
```
$gradle test jacocoTestReport
```
For more tasks run `$gradle tasks`

### Usage
Once the server is started , we can use a browser or commandline tools to make a HTTP GET request with uri as query parameter e.g. http://localhost:8090/crawl?uri=http://google.com

Below table describes the possible ways to use the crawler

QueryParameter |Required| Description
--- | --- | --- | --- 
uri | Yes | The parent url to crawl and generate the crawl report |

Paths |Parameters| Description
--- | --- | --- |
 /crawl | **name**: uri ,**method**: QueryString|Reads the provided url parameter and generate the crawl report *with Defaults* **Depth** : 3 , **maxLinksperNode** : 25 |
/crawl/depth/{maxDepth}| **name**: uri **method**: QueryString,**name**: maxDepth ,**method**: PathParam | Reads the provided url parameter and generate the crawl report with defined *maxDepth* and **maxLinksperNode** : 25 |
/crawl/nodeLinks/{maxNodeLinksperNode}|**name**: uri ,**method**: QueryString | Reads the provided url parameter and generate the crawl report *with* **Depth** : 3 , and defined maxLinksperNode|
/crawl/depth/{maxDepth}/nodeLinks/{maxNodeLinksperNode}| **name**: uri **method**: QueryString,**name**: maxDepth **method**: PathParam, **name**: maxNodeLinksperNode **method**: PathParam | Reads the provided url query parameter and generate the crawl report with user defined depth and nodelinks |

### Response format 
It renders a JSON file in the below format 


```

{
  "uri": "https://www.google.com",
  "title": "Google",
  "nodes": [
    {
      "uri": "https://www.google.com.au/imghp?hl=en&tab=wi",
      "title": "Google Images",
      "nodes": [
        {
          "uri": "https://www.google.com.au/webhp?tab=iw",
          "title": "Google",
          "nodes": []
        },
        {
          "uri": "https://maps.google.com.au/maps?hl=en&tab=il",
          "title": "Google Maps",
          "nodes": []
        }
      ]
    },
    {
      "uri": "https://maps.google.com.au/maps?hl=en&tab=wl",
      "title": "Google Maps",
      "nodes": []
    }
  ]
}
```

### Further References
- 	[Java Docs](./doc/index.html)
- [Spring Boot](https://projects.spring.io/spring-boot/)
- [Gradle](https://gradle.org/)
- [Junit](http://junit.org/junit4/)



