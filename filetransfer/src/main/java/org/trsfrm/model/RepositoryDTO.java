package org.trsfrm.model;

import lombok.Data;

@Data
public class RepositoryDTO {
	private String path;
	private String fileType;
	private String parserType;
    private KafkaSettingsDTO kafkaSetting;
    public static RepositoryDTO Build(){
    	return new RepositoryDTO();
    }
    public RepositoryDTO withPath(String path){
    	this.path = path;
    	return this;
    }
    public RepositoryDTO withFileType(String fileType){
    	this.fileType = fileType;
    	return this;
    }
    public RepositoryDTO withParserType(String parserType){
    	this.parserType = parserType;
    	return this;
    }
    
    public RepositoryDTO withKaKaSetting(KafkaSettingsDTO kafkaSetting){
    	this.kafkaSetting = kafkaSetting;
    	return this;
    }
    
}
