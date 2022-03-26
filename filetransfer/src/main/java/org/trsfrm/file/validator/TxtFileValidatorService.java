package org.trsfrm.file.validator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.trsfrm.model.FileSettingsToSendDTO;

public class TxtFileValidatorService extends FileValidator {

	private static Logger LOGGER = Logger.getLogger(TxtFileValidatorService.class);

	@Override
	public boolean checkFileFormat(FileSettingsToSendDTO fileSetting) {

		File fileToValidate = fileSetting.getFile();
		File validatorFormat = listSchema.get(fileSetting.getDiretoryPath());

		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("validate Txt File");
			}
			String[] formatAndDelimitorColumn = Files.lines(Paths.get(validatorFormat.getPath()))
					.toArray(String[]::new);

			if (formatAndDelimitorColumn == null || formatAndDelimitorColumn.length < 2)
				return false;

			String delimitor = formatAndDelimitorColumn[1];
			int attributeNumber = formatAndDelimitorColumn[0].split(delimitor).length;

			List<String> fileLine = Files.lines(Paths.get(fileToValidate.getPath())).collect(Collectors.toList());
			for (String line : fileLine) {
				if (line == null || (line.split(delimitor) != null && line.split(delimitor).length == attributeNumber))
					continue;
				else
					return false;
			}

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return true;
	}

}
