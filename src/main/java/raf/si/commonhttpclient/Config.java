package raf.si.commonhttpclient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import raf.si.commonhttpclient.domain.ApplicationMetadata;
import raf.si.commonhttpclient.proxy.HttpClientProxy;

public class Config {
	
private static final String API_GATEWAY_SINGUP_PATH = "/api-gateway/newApp";
	
	private int port;
	
	private String name;
	
	private String apiGatewayHost;
	
	private String apiGatewayPort;
	
	public Config(int port, String name, String apiGatewayHost, String apiGatewayPort) {
		this.port = port;
		this.name = name;
		this.apiGatewayHost = apiGatewayHost;
		this.apiGatewayPort = apiGatewayPort;
	}
	
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	public static <T> T createProxt(Class<T> interfaceClass) {
		return (T) HttpClientProxy.newInstance("http://localhost:9000", interfaceClass);
	}
	
	public String getApiGatewayAddress() {
		return "http://" + apiGatewayHost+":"+apiGatewayPort;
	}
	
	public void singUp() {
		RestTemplate restTemplate = getRestTemplate();
		String address = getApiGatewayAddress() + API_GATEWAY_SINGUP_PATH;
		ApplicationMetadata applicationMetadata = new ApplicationMetadata("http", name, "localhost", port);
		ResponseEntity<Boolean> response = restTemplate.postForEntity(address, applicationMetadata, Boolean.class);
	}

}
