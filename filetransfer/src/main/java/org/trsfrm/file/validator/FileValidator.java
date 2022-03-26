package org.trsfrm.file.validator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.trsfrm.file.RepositoryConfig;
import org.trsfrm.model.FileSettingsToSendDTO;

public abstract class FileValidator implements IFileValidator {

	private static Logger LOGGER = Logger.getLogger(FileValidator.class);
	private static final String FILE_SEPARATOR = File.separator;

	@Autowired
	private RepositoryConfig repositoriesConfig;

	@Value("${repository.count}")
	private int repositoryCounts;

	protected static Map<String, File> listSchema = new HashMap();

	public abstract boolean checkFileFormat(FileSettingsToSendDTO fileSetting);

	@PostConstruct
	protected void loadSchemas() {

		String fileType;

		for (int i = 0; i < repositoryCounts; i++) {
			fileType = repositoriesConfig.getDirectories().get(i).getFormat();
			String path = repositoriesConfig.getDirectories().get(i).getPath();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("inside loadSchemas File Validator " + fileType + "||" + path);
			}
			File schema = new File(path + FILE_SEPARATOR + "conf" + FILE_SEPARATOR + "schema." + fileType);
			listSchema.put(path, schema);
		}
	}
}
