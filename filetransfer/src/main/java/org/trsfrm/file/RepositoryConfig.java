package org.trsfrm.file;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("repository")
public class RepositoryConfig {

	private List<Config> directories =new ArrayList();


	public List<Config> getDirectories() {
		return directories;
	}


	public void setDirectories(List<Config> directories) {
		this.directories = directories;
	}


	public static class Config{
	String format;
	String parser;
	String path;
	String delimitor;
	String topic;
	String broker;
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getParser() {
		return parser;
	}
	public void setParser(String parser) {
		this.parser = parser;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDelimitor() {
		return delimitor;
	}
	public void setDelimitor(String delimitor) {
		this.delimitor = delimitor;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getBroker() {
		return broker;
	}
	public void setBroker(String broker) {
		this.broker = broker;
	}
	}
}
