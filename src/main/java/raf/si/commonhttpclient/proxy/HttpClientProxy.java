package raf.si.commonhttpclient.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import raf.si.commonhttpclient.annotations.Body;
import raf.si.commonhttpclient.annotations.RemoteService;
import raf.si.commonhttpclient.annotations.RequestMapping;
import raf.si.commonhttpclient.metadata.MethodMetadata;
import raf.si.commonhttpclient.metadata.ParameterMetadata;
import raf.si.commonhttpclient.metadata.ParameterType;

public class HttpClientProxy implements InvocationHandler {
	
	public static Object newInstance(String apiGatewayAddress, Class<?> classOfInterface) {
		return Proxy.newProxyInstance(HttpClientProxy.class.getClassLoader(), new Class[]{ classOfInterface }, new HttpClientProxy(apiGatewayAddress, classOfInterface));
	}
	
	private static final String EMPTY_WORD = "";
	
	private RestTemplate restTemplate;
	
	private ObjectMapper objectMapper;
	
	private Map<Method, MethodMetadata> metadata;
	
	private String apiGatewayAddress, serviceName;
	
	public HttpClientProxy() {
		super();
	}
	
	public HttpClientProxy(String apiGatewayAddress, Class<?> classOfInterface) {
		this.apiGatewayAddress = apiGatewayAddress;
		this.restTemplate = new RestTemplate();
		this.objectMapper = new ObjectMapper();
		this.metadata = new HashMap();
		prepareMetadata(classOfInterface);
//		for(Method key: metadata.keySet()) {
//			System.out.println("========================");
//			System.out.println("KEY: "+key);
//			MethodMetadata methodMetadata = metadata.get(key);
//			System.out.println("PATH: "+methodMetadata.getPath());
//			System.out.println("RETURN TYPE: "+methodMetadata.getReturnType());
//			System.out.println("REQUEST METHOD: "+methodMetadata.getRequestMethod());
//			System.out.println("PARAMETERS:");
//			for(ParameterMetadata parameterMetadata: methodMetadata.getParameters()) {
//				System.out.println("name: "+parameterMetadata.getParameterName()+", possition: "+parameterMetadata.getPossition()+", type: "+parameterMetadata.getParameterType());
//			}
//			System.out.println("========================");
//		}
	}
	
	private void prepareMetadata(Class<?> classOfInterface) {
		RemoteService remoteService = classOfInterface.getAnnotation(RemoteService.class);
		serviceName = remoteService.name();
		for(Method method: classOfInterface.getDeclaredMethods()) {
			MethodMetadata metadata = new MethodMetadata(method.getReturnType());
			RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
			metadata.setPath(requestMapping.path());
			metadata.setRequestMethod(requestMapping.method());
			int parameterPossition = 0;
			for(Parameter parameter: method.getParameters()) {
				Annotation annotation = (parameter.getDeclaredAnnotations().length > 0) ? parameter.getDeclaredAnnotations()[0] : null;
				
				if(Objects.isNull(annotation)) {
					metadata.getParameters().add(new ParameterMetadata(parameterPossition, parameter.getName(), ParameterType.PARAMETER));
				} else if(annotation instanceof raf.si.commonhttpclient.annotations.Parameter) {
					raf.si.commonhttpclient.annotations.Parameter parameterAnnotation = (raf.si.commonhttpclient.annotations.Parameter) annotation;
					String name = (parameterAnnotation.name().equals(EMPTY_WORD)) ? parameter.getName() : parameterAnnotation.name();
					metadata.getParameters().add(new ParameterMetadata(parameterPossition, name, ParameterType.PARAMETER));
				} else if(annotation instanceof Body) {
					metadata.getParameters().add(new ParameterMetadata(parameterPossition, parameter.getName(), ParameterType.BODY));
				}
				parameterPossition++;
			}
			this.metadata.put(method, metadata);
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodMetadata methodMetadata = metadata.get(method);
		
		String url = apiGatewayAddress + "/" + serviceName + methodMetadata.getPath();
		System.out.println("URL: "+url);
		
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    
	    ParameterMetadata bodyParameter = null;
	    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
	    for(ParameterMetadata parameterMetadata: methodMetadata.getParameters()) {
	    	if(parameterMetadata.getParameterType() == ParameterType.BODY) {
	    		bodyParameter = parameterMetadata;
	    		continue;
	    	}
	    	builder.queryParam(parameterMetadata.getParameterName(), args[parameterMetadata.getPossition()]);
	    }

	    HttpEntity<?> entity = new HttpEntity(headers);
	    if(!Objects.isNull(bodyParameter)) {
	    	entity = new HttpEntity(args[bodyParameter.getPossition()], headers);
	    }
	    
	    HttpEntity<String> response = restTemplate.exchange(
	            builder.toUriString(), 
	            methodMetadata.getRequestMethod(), 
	            entity, 
	            String.class);
	    
		return (methodMetadata.getReturnType() == String.class) ? response.getBody() : objectMapper.readValue(response.getBody(), methodMetadata.getReturnType());
	}

}
