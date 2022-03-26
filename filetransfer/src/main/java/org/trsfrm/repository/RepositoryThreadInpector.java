package org.trsfrm.repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.trsfrm.file.parser.FileParser;
import org.trsfrm.kafka.KafkaFileProducer;
import org.trsfrm.model.FileSettingsToSendDTO;
import org.trsfrm.model.KafkaSettingsDTO;
import org.trsfrm.model.RepositoryDTO;

@Component
@Scope("prototype")
public class RepositoryThreadInpector implements Runnable {

	private static Logger LOGGER = Logger.getLogger(RepositoryThreadInpector.class);
	private RepositoryDTO repository;
	private Path directory;
	public KafkaProducer<Long, String> producer;
	List<Future> fileHandler = new ArrayList<>();
	private String topic;

	@Autowired
	Map<String, FileParser> parserList;
	private static Vector<FileSettingsToSendDTO> inspectedFiles = new Vector<>();

	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

	FileParser eligeableFileParser;
	private KafkaFileProducer kafkaFileProducer;

	public RepositoryDTO getRepository() {
		return repository;

	}

	public RepositoryThreadInpector(RepositoryDTO repository, KafkaFileProducer kafkaFileProducer) {
		this.repository = repository;
		this.directory = Paths.get(repository.getPath());
		this.topic = repository.getKafkaSetting().getTopic();
		this.kafkaFileProducer = kafkaFileProducer;
	}

	/**
	 * survey the directory and for each new file launch new producer kafka Handler
	 */
	public void run() {

		while (true) {
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Start inspect " + directory);
				}
				Thread.sleep(1000);
				while (Files.list(directory).count() == 4)
					Thread.sleep(1000);

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Files numbers is great than 4");
				}

				Files.list(directory).filter(e -> !Files.isDirectory(e)).forEach(e -> {
					try {
						FileSettingsToSendDTO fileSetting = new FileSettingsToSendDTO(repository.getPath().toString(),
								e.toFile(), eligeableFileParser);
						if (fileAlreadyInInspect(fileSetting))
							return;

						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("new File add to " + directory + " : " + e);
						}
						Future<Integer> result = (Future<Integer>) executor
								.submit(this.kafkaFileProducer.init(fileSetting, producer, topic));
						fileHandler.add(result);

						if (LOGGER.isDebugEnabled()) {

							LOGGER.debug("adding file to list " + directory.toString() + File.separator
									+ e.getFileName().toString());
						}
						trackInsoectedFile(fileSetting);
					} catch (Exception e1) {
						LOGGER.error(e1.getMessage(), e1);
					}
				});

			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

	}

	/**
	 * create a kafka producer for each repository
	 */
	@PostConstruct
	private void createProducer() {

		KafkaSettingsDTO kafkaSetting = repository.getKafkaSetting();
		this.eligeableFileParser = selectFileParserOfRepository(repository);
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaSetting.getBrokers());
		props.put(ProducerConfig.CLIENT_ID_CONFIG, "1");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		this.producer = new KafkaProducer<>(props);
	}

	private FileParser selectFileParserOfRepository(RepositoryDTO repository) {
		return parserList.get(repository.getParserType());

	}

	public static void untrackInpectedFile(FileSettingsToSendDTO fileSetting, int patternDateLength) {
		if (fileSetting == null || fileSetting.getFile() == null || fileSetting.getDiretoryPath() == null)
			return;
		String originFileName = fileSetting.getFile().getName();
		FileSettingsToSendDTO newFileSetting = new FileSettingsToSendDTO(fileSetting.getDiretoryPath(),
				new File(originFileName), null);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("remove file is " + RepositoryThreadInpector.inspectedFiles.remove(newFileSetting));
		}
	}

	public void trackInsoectedFile(FileSettingsToSendDTO fileSetting) {
		inspectedFiles.add(fileSetting);
	}

	private boolean fileAlreadyInInspect(FileSettingsToSendDTO fileSetting) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("check if file existe " + directory.toString() + File.separator
					+ fileSetting.getFile().getName().toString() + "  -->" + inspectedFiles.contains(
							directory.toString() + File.separator + fileSetting.getFile().getName().toString()));

			LOGGER.debug("Existing files are " + inspectedFiles.toString());
		}
		
		return inspectedFiles.contains(fileSetting);
	}

}