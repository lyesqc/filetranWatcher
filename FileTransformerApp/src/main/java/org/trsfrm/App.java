package org.trsfrm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.trsfrm.file.FileConfig;
import org.trsfrm.repository.FileWatcherLauncher;
import org.trsfrm.security.SecurityConfig;

@SpringBootApplication
@ComponentScan(basePackageClasses = { FileConfig.class, SecurityConfig.class })


public class App implements CommandLineRunner {

	@Autowired
	FileWatcherLauncher fileWatcherLauncher;

	@Value("${repository.count}")
	private int repositoryCount;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

				/**
		 * start repository watcher
		 */
		fileWatcherLauncher.launchInspectRepositories();
	}

}
