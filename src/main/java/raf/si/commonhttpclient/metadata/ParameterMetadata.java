package raf.si.commonhttpclient.metadata;

public class ParameterMetadata {
	
	private String parameterName;
	
	private int possition;
	
	private ParameterType parameterType;
	
	public ParameterMetadata(int possition, String parameterName, ParameterType parameterType) {
		this.possition = possition;
		this.parameterName = parameterName;
		this.parameterType = parameterType;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	
	public int getPossition() {
		return possition;
	}
	
	public void setPossition(int possition) {
		this.possition = possition;
	}
	
	public ParameterType getParameterType() {
		return parameterType;
	}
	
	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}

}
