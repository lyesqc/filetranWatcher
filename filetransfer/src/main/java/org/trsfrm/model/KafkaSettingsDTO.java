package org.trsfrm.model;

import lombok.Data;

@Data
public class KafkaSettingsDTO {
	
	private String brokers;
	private String topic;
	private String key;
	private String keySerializer;
	private String valueSerializer;
	private String[] partitions;
	private String cleintId;
	public static KafkaSettingsDTO bluid(){
		return new KafkaSettingsDTO();
	}
	public KafkaSettingsDTO withBrokers(String brokers){
		this.brokers = brokers;
		return this;
	}
	
	public KafkaSettingsDTO withTopic(String topic){
		this.topic = topic;
		return this;
	}
	
	public KafkaSettingsDTO withKeySerializer(String keySerializer){
		this.keySerializer = keySerializer;
		return this;
	}
	
	public KafkaSettingsDTO withKey(String key){
		this.key = key;
		return this;
	}
	
	public KafkaSettingsDTO withValueSerializer(String valueSerializer){
		this.valueSerializer = valueSerializer;
		return this;
	}
	
	public KafkaSettingsDTO withPartitions(String[] partitions){
		this.partitions = partitions;
		return this;
	}
	
	public KafkaSettingsDTO withCleintId(String cleintId){
		this.cleintId = cleintId;
		return this;
	}
}
