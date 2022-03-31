package org.trsfrm.model;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.trsfrm.file.parser.FileParser;

import lombok.Data;

@Data
public class FileSettingsToSendDTO {

	private static Logger LOGGER = LogManager.getLogger(FileSettingsToSendDTO.class);

	private String diretoryPath;
	private File file;
	private FileParser fileParser;

	public FileSettingsToSendDTO(String diretoryPath, File file, FileParser fileParser) {
		this.diretoryPath = diretoryPath;
		this.file = file;
		this.fileParser = fileParser;
	}

	@Override
	public boolean equals(Object setting) {
		if (!(setting instanceof FileSettingsToSendDTO)) {
			return false;
		}

		FileSettingsToSendDTO fileSetting = (FileSettingsToSendDTO) setting;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Compare File " + file.getName() + " with : " + fileSetting.getFile().getName() + "--> "
					+ file.getName().equals(fileSetting.getFile().getName()));
			LOGGER.debug("Compare Directory " + fileSetting.getDiretoryPath() + " with : " + diretoryPath + " -->"
					+ fileSetting.getDiretoryPath().equals(diretoryPath));
		}
		
		return (fileSetting.getDiretoryPath().equals(diretoryPath)
				&& file.getName().equals(fileSetting.getFile().getName()));
	}
}
