package com.cy.framework.mvc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.cy.framework.annotation.Controller;
import com.cy.framework.annotation.RequestMapping;
import com.cy.framework.mvc.util.ClassScanner;

public class MVCRegister {
	
	public static Map<String,RegisterInfo> register(String basePackage, String excludeFilter,String suffix) throws Exception{
		Map<String,RegisterInfo> mappingMap = new HashMap<String,RegisterInfo>();
		ClassScanner scanner = new ClassScanner(basePackage,excludeFilter);
		Set<Class<?>> classSet = scanner.scanning();
		for(Class<?> clazz:classSet){
			Controller controller = clazz.getAnnotation(Controller.class);
			if(controller == null){
				continue;
			}
			RequestMapping controllerMapping = clazz.getAnnotation(RequestMapping.class);
			String base = null;
			if(controllerMapping != null){
				base = controllerMapping.value();
			}else{
				base = "";
			}
			
			Method[] methods = clazz.getDeclaredMethods();
			RequestMapping methodMapping = null;
			for (Method method : methods) {
				methodMapping = method.getAnnotation(RequestMapping.class);
				if(methodMapping != null){
					String mapping = base + methodMapping.value();
					String requestMethod = methodMapping.method();
					mapping = mapping+suffix;
					if(mappingMap.containsKey(mapping)){
						throw new Exception("存在重复的映射["+mapping+"]");
					}else{
						RegisterInfo info = new RegisterInfo();
						info.setClazz(clazz);
						info.setMethod(method.getName());
						info.setRequestType(requestMethod);
						info.setParams(method.getParameterTypes());
						mappingMap.put(mapping, info);
						System.out.println("注册请求["+mapping+","+requestMethod+"]");
					}
				}
			}
		}
		return mappingMap;
	}

}
