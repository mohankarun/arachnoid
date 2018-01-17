package arachnoid.to;

public class Error {
 private String status;
 private String message;
 
 public Error(String i,String message){
	 this.status= i;
	 this.message = message;
 }
 public Error(){
	 
 }
 
public String getStatus() {
	return status;
}
public void setStatus(String status) {
	this.status = status;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
 
 
}
