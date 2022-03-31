package org.trsfrm.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.trsfrm.file.RepositoryConfig;
import org.trsfrm.kafka.KafkaFileProducer;
import org.trsfrm.model.KafkaSettingsDTO;
import org.trsfrm.model.RepositoryDTO;

@Service
public class FileWatcherLauncher implements FileWatcher {

	final static Logger LOGGER = LogManager.getLogger(FileWatcherLauncher.class.getName());

	@Autowired
	FileWatcherLauncher fileWatcherLauncher;

	@Value("${repository.count:0}")
	private int repositoryCount = 1;

	@Autowired
	ApplicationContext ctx;

	@Autowired
	private RepositoryConfig repositoriesConfig;

	public FileWatcherLauncher() {
		super();
	}

	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(repositoryCount);

	List<RepositoryDTO> listRepository = new ArrayList();
	private Map<String, Future> repositoriesThread = new HashMap<>();

	/**
	 * entry point for our application
	 */
	public void launchInspectRepositories() {
		listRepository.forEach(repository -> {
			try {
				launchInspector(repository);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		});
	}

	public void launchInspector(RepositoryDTO repository) throws Exception {
		RepositoryThreadInpector threadDirectory = (RepositoryThreadInpector) ctx
				.getBean(RepositoryThreadInpector.class, repository, new KafkaFileProducer());

		repositoriesThread.put(repository.getPath(), executor.submit(threadDirectory));
	}

	/**
	 * load repository setting from properties file and put it in list of repository
	 */
	@PostConstruct
	public void loadRepository() {
		String path;
		String parserType;
		String fileType;
		String broker;
		String topic;
		KafkaSettingsDTO kafkaSetting;
		for (int i = 1; i <= repositoryCount; i++) {

			path = repositoriesConfig.getDirectories().get(i).getPath();
			fileType = repositoriesConfig.getDirectories().get(i).getFormat();
			parserType = repositoriesConfig.getDirectories().get(i).getParser();
			topic = repositoriesConfig.getDirectories().get(i).getTopic();
			broker = repositoriesConfig.getDirectories().get(i).getBroker();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Parser is " + parserType + ", " + broker);
			}

			kafkaSetting = KafkaSettingsDTO.bluid().withBrokers(broker).withTopic(topic);
			RepositoryDTO repository = RepositoryDTO.Build().withFileType(fileType).withParserType(parserType)
					.withPath(path).withKaKaSetting(kafkaSetting);
			listRepository.add(repository);
		}

	}

	public Map<String, Future> getRepositoriesThread() {
		return repositoriesThread;
	}

}
