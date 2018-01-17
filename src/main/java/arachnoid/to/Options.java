package arachnoid.to;

public class Options {

	private Integer depth;
	private Integer maxLinksperNode;
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	public Integer getMaxLinksperNode() {
		return maxLinksperNode;
	}
	public void setMaxLinksperNode(Integer maxLinksperNode) {
		this.maxLinksperNode = maxLinksperNode;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Options [ depth="+depth);
		sb.append(", maxLinksperNode="+maxLinksperNode+"]");		
		return sb.toString();
		
	}
	
}
