package Castles.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

import bonzai.AI;
import bonzai.Agent;

public class AIJar implements bonzai.Jar {
	private Class<?> aiClass;
	
	private String name;
	private File file;
	
	public AIJar(File file) throws Exception {
		URLClassLoader loader = URLClassLoader.newInstance(new URL[] { file.toURI().toURL() });
		
		try{
			aiClass = loader.loadClass("CompetitorAI");
		} catch (Exception e){
			aiClass = loader.loadClass("competitor.CompetitorAI");
		}
		
		// if they are not extending the AI class, throw and exception 
		if(!AI.class.equals(aiClass.getSuperclass())){
			throw new ClassNotFoundException("CompetitorAI is not extending the AI super class from Package snowbound.api");
		}
		if(aiClass.getAnnotation(Agent.class) == null){
			throw new NullPointerException("CompetitorAI is not using the Agent Annotation on their Class");
		}
		
		this.name = aiClass.getAnnotation(Agent.class).name();
		this.file = file;
	}
	
	public String name() {
		return name;
	}

	public File file() {
		return file;
	}
	
	@Override
	public int compareTo(bonzai.Jar jar) {
		return name.compareTo(jar.name());
	}
	
	public AI instantiate() throws Exception, InstantiationException {
		Constructor<?> aiConstructor = aiClass.getConstructor();
		return (AI)aiConstructor.newInstance();
	}
}
