package org.trsfrm.file.parser;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.trsfrm.file.attribute.IFileAttributeService;
import org.trsfrm.file.validator.IFileValidator;
import org.trsfrm.model.FileSettingsToSendDTO;

public abstract class FileParser {

	private static Logger LOGGER = Logger.getLogger(FileParser.class);
	private static final String DATE_FORMAT = "yyyy_MM_dd_HH_mm";
	private final static String FILE_SEPARATOR = File.separator;
	private final static String ERROR_DIRECTORY_NAME = "error";
	/**
	 * move file to staged area, and check if format is valid if not, move it to
	 * errors area start read file and return the iterator of block file
	 * 
	 * @return
	 */
	protected FileParser(IFileValidator jsonFileValidatorService, IFileAttributeService fileAttributeService2) {
		this.fileValidator = jsonFileValidatorService;
		this.fileAttributeService = fileAttributeService2;
	}

	protected IFileValidator fileValidator;
	protected IFileAttributeService fileAttributeService;

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

	public Iterator<String> loadFile(FileSettingsToSendDTO fileSetting) {

		if (!movFile("staged", fileSetting, false))
			return null;

		boolean isValidFormatFile = fileValidator.checkFileFormat(fileSetting);
		if (!isValidFormatFile) {
			movFile(ERROR_DIRECTORY_NAME, fileSetting, true);
			return null;
		}
		try {

			return readBlock(fileSetting);
		} catch (Exception e) {

			LOGGER.error(e.getMessage(), e);

			if (e instanceof NullPointerException)
				throw new NullPointerException();
		}
		return null;
	}

	public abstract Iterator<String> readBlock(FileSettingsToSendDTO fileSettingsToSend);

	/**
	 * move file to appropriate directory, or move it to error directory if not able
	 * for any reason
	 * 
	 * @param destPath
	 * @param fileSetting
	 * @return
	 */
	public boolean movFile(String destPath, FileSettingsToSendDTO fileSetting, boolean appender) {
		boolean moveResult;
		try {
			LocalDateTime date = LocalDateTime.now();
			String direstoryPath = fileSetting.getDiretoryPath();
			File file = fileSetting.getFile();
			String destName = direstoryPath + FILE_SEPARATOR + destPath + FILE_SEPARATOR + file.getName();
			if (appender)
				destName = destName + "_" + date.format(formatter);
			File destFile = new File(destName);

			moveResult = file.renameTo(destFile.getAbsoluteFile());
			if (!moveResult) {
				LOGGER.info("move is " + moveResult + " " + file.getAbsolutePath() + "=>" + destFile.getAbsoluteFile());

				file.renameTo(new File(direstoryPath + FILE_SEPARATOR+"error" +FILE_SEPARATOR+ file.getName() + "_" + date.format(formatter)));
				return false;
			}
			fileSetting.setFile(destFile);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}

		return moveResult;

	}

	public boolean movFile(String destPath, FileSettingsToSendDTO fileSetting) {
		boolean moveResult;
		try {
			String direstoryPath = fileSetting.getDiretoryPath();
			File file = fileSetting.getFile();
			String destName = direstoryPath +  FILE_SEPARATOR + destPath + FILE_SEPARATOR+ file.getName();
			File destFile = new File(destName);
			moveResult = file.renameTo(destFile.getAbsoluteFile());
			if (!moveResult) {
				LOGGER.info("move is " + moveResult + " " + file.getAbsolutePath() + "=>" + destFile.getAbsoluteFile());
				LocalDateTime date = LocalDateTime.now();
				file.renameTo(new File(direstoryPath + FILE_SEPARATOR+"error"+ FILE_SEPARATOR+ file.getName() + "_" + date.format(formatter)));
				return false;
			}
			fileSetting.setFile(destFile);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}

		return moveResult;

	}
}
