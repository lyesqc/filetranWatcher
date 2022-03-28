package org.trsfrm;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.trsfrm.file.FileConfig;
import org.trsfrm.repository.FileWatcherLauncher;
import org.trsfrm.security.SecurityConfig;
import org.trsfrm.utils.LoggingOutputStream;

@SpringBootApplication
@EnableWebMvc
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

		Logger out = Logger.getLogger("");
		System.setOut(new PrintStream(new LoggingOutputStream(out, Level.INFO), true));
		/**
		 * start repository watcher
		 */
		fileWatcherLauncher.launchInspectRepositories();
	}

}
