package org.trsfrm.kafka;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.trsfrm.file.parser.FileParser;
import org.trsfrm.model.FileSettingsToSendDTO;
import org.trsfrm.repository.RepositoryThreadInpector;

public class KafkaFileProducer implements Callable<Integer> {

	private static Logger LOGGER = LogManager.getLogger(KafkaFileProducer.class);
	private final String ERROR_PATH = "error";
	private KafkaProducer<Long, String> producer;
	private String topic;
	private FileSettingsToSendDTO fileSetting;
	private FileParser fileParser;
	private String valueToSend = null;
	int resultOfSend = 0;
	private static String DATE_PATTERN = "yyyy_MM_dd_HH_mm";
	private String destPath = "success";

	public Callable<Integer> init(FileSettingsToSendDTO fileSettingsToSend, KafkaProducer<Long, String> producer,
			String topic) {
		this.fileSetting = fileSettingsToSend;
		this.producer = producer;
		this.topic = topic;
		this.fileParser = fileSettingsToSend.getFileParser();
		return this;
	}

	/**
	 * load file, and start read, it cancel sending if at least one message cannot
	 * be sent
	 */
	@Override
	public Integer call() {

		Iterator<String> iterator = null;

		try {
			iterator = fileParser.loadFile(fileSetting);
			if (iterator == null)
				resultOfSend = -1;
			while (resultOfSend != -1 && iterator.hasNext()) {
				valueToSend = iterator.next();
				if (valueToSend != null && valueToSend.length() > 0)
					/**
					 * call onCompleteKafkaSend method to put resultOfSend =-1 if send not done
					 */
					producer.send(new ProducerRecord<Long, String>(topic, valueToSend),
							(meta, excep) -> onCompleteKafkaSend(meta, excep));
			}
		} catch (Exception e) {
			resultOfSend = -1;
			LOGGER.error(e.getMessage(), e);
		}

		finally {
			if (resultOfSend == -1)
				destPath = "error";
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("in finally result send is " + resultOfSend);
			}

			if (resultOfSend == 0)
				producer.flush();
			RepositoryThreadInpector.untrackInpectedFile(fileSetting, DATE_PATTERN.length());
			fileParser.movFile(destPath, fileSetting, true);

		}
		return resultOfSend;
	}

	/**
	 * method called on finish of produccer.send, to track the data send status put
	 * resultOfSend = -1 if error is occured when sending data
	 * 
	 * @param meta
	 * @param e
	 * @param fileSetting
	 */
	public void onCompleteKafkaSend(RecordMetadata meta, Exception e) {
		if (meta == null) {
			resultOfSend = -1;
			LOGGER.warn("Execption occured when try to send to kafka " + resultOfSend);
			destPath = ERROR_PATH;
		}
	}

}
