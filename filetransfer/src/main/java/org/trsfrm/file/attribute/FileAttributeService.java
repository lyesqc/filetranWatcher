package org.trsfrm.file.attribute;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trsfrm.file.RepositoryConfig;

@Service
public class FileAttributeService implements IFileAttributeService {

	private final static String CONFIG_PARAM = "conf";
	private final static String FILE_SEPARATOR = File.separator;
	private final static String ATTRIBUTE_FILE_NAME = "attributes.conf";
	private final static String DELEMITOR_FILE_NAME = "delimitor.conf";
	
	@Autowired
	private RepositoryConfig repositoriesConfig;
	
	private Map<String, List<String>> attributesName = new HashMap<>();
	private Map<String, String[]> blocDelimitors = new HashMap<>();
	@Value("${repository.count:0}")
	private int repositoryCount;

	@PostConstruct
	void init() {
		loadDelimitorRepository();
		loadAttributesRepositories();
	}

	private void loadDelimitorRepository() {
		String path;
		for (int i = 0; i < repositoryCount; i++) {
			
			path = repositoriesConfig.getDirectories().get(i).getPath();
			Path delimitorFile = Paths.get(path + FILE_SEPARATOR + CONFIG_PARAM + FILE_SEPARATOR + DELEMITOR_FILE_NAME);
			
			try (Stream<String> line = Files.lines(delimitorFile).limit(1)) {
				
				String lineFirst = line.findFirst().get();
				String[] delimitors = lineFirst.split(";");
				if (delimitors == null || delimitors.length == 0)
					return;
				blocDelimitors.put(path, delimitors);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void loadAttributesRepositories() {
		String path;
		List<String> attributes = new ArrayList<>();
		for (int i = 1; i <= repositoryCount; i++) {
			path = repositoriesConfig.getDirectories().get(i).getPath();
			Path delimitorFile = Paths.get(path + FILE_SEPARATOR + CONFIG_PARAM + FILE_SEPARATOR + ATTRIBUTE_FILE_NAME);
			try (Stream<String> stream = Files.lines(delimitorFile)) {

				stream.forEach(e -> attributes.add(e));

			} catch (IOException e) {
				e.printStackTrace();
			}
			attributesName.put(path, attributes);
		}
	}

	public List<String> getAttributes(String path) {

		return attributesName.get(path);
	}

	public String[] getDelimitorRepositpry(String path) {
		return blocDelimitors.get(path);
	}

}
