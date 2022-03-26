package org.trsfrm.file.validator;

import org.trsfrm.model.FileSettingsToSendDTO;

public interface IFileValidator {
	
	boolean checkFileFormat(FileSettingsToSendDTO fileSetting);

}
