package raf.si.commonhttpclient.metadata;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpMethod;

public class MethodMetadata {
	
	private HttpMethod requestMethod;
	
	private Class<?> returnType;
	
	private String path;
	
	private List<ParameterMetadata> parameters = new ArrayList<ParameterMetadata>();
	
	public MethodMetadata(Class<?> returnType) {
		this.returnType = returnType;
	}
	
	public HttpMethod getRequestMethod() {
		return requestMethod;
	}
	
	public void setRequestMethod(HttpMethod requestMethod) {
		this.requestMethod = requestMethod;
	}
	
	public Class<?> getReturnType() {
		return returnType;
	}
	
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public List<ParameterMetadata> getParameters() {
		return parameters;
	}
	
	public void setParameters(List<ParameterMetadata> parameters) {
		this.parameters = parameters;
	}

}
