package com.cy.framework.mvc.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

	private String basePath;
	
	private String basePackage;
	private String excludeFilter;
	private String excludeDirFilter;
	ScannerFilter scannerFilter;
	private Set<Class<?>> classSet;
	private String FILE_SEPARATOR;

	public ClassScanner(String basePackage, String excludeFilter) {
		this.basePackage = basePackage;
		this.excludeFilter = excludeFilter;
		classSet = new HashSet<Class<?>>();
		scannerFilter = new ScannerFilter();
	}

	public static void main(String[] args) {
		ClassScanner scanner = new ClassScanner("com.cy",
				"com.cy.framework.mvc.util");
		scanner.scanning();
	}

	public Set<Class<?>> scanning() {
		basePath = basePackage.replaceAll("\\.", "/");
		excludeDirFilter = excludeFilter.replaceAll("\\.", "/");
		String osName = System.getProperty("os.name");
		if(osName.toLowerCase().indexOf("window") != -1){
			FILE_SEPARATOR = "\\\\";
		}else{
			FILE_SEPARATOR = "/";
		}
		
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader()
					.getResources(basePath);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				if ("file".equals(url.getProtocol())) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					scanFromFile(filePath);
				} else if ("jar".equals(url.getProtocol())) {
					JarFile jar;
					jar = ((JarURLConnection) url.openConnection())
							.getJarFile();
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						scanFromJAR(entries.nextElement());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classSet;
	}

	private void scanFromFile(String dirPath) throws ClassNotFoundException {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			return;
		}
		File[] files = dir.listFiles(scannerFilter);
		for (File f : files) {
			if (f.isDirectory()) {
				scanFromFile(f.getPath());
			} else {
				String className = f.getPath().substring(f.getPath().indexOf("classes")+8,
						f.getPath().lastIndexOf("."));
				className = className.replaceAll(FILE_SEPARATOR, "\\.");
				addClass(className);
			}
		}
	}
	
	private void scanFromJAR(JarEntry entry) throws ClassNotFoundException {
		String name = entry.getName();
		if (entry.isDirectory()) {
			return;
		}else{
			if(name.startsWith(basePath) && !name.startsWith(excludeDirFilter)){
				name = name.substring(0,name.indexOf(".class"));
				String className = name.replaceAll("/", "\\.");
				addClass(className);
			}
		}
	}
	
	private void addClass(String className) throws ClassNotFoundException{
		classSet.add(Class.forName(className));
	}

	class ScannerFilter implements java.io.FileFilter {

		@Override
		public boolean accept(File file) {
			if(file.isDirectory()){
				return true;
			}else{
				return file.getPath().endsWith(".class")
						&& !file.getPath().startsWith(excludeDirFilter) && file.getPath().indexOf("$") == -1;
			}
		}

	}

}
