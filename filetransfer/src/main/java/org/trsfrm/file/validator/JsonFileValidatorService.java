package org.trsfrm.file.validator;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;
import org.trsfrm.model.FileSettingsToSendDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonFileValidatorService extends FileValidator {

	private static Logger LOGGER = Logger.getLogger(JsonFileValidatorService.class);
	@Override
	public boolean checkFileFormat(FileSettingsToSendDTO fileSetting) {
		JSONObject jsonSchema = null;
		ObjectMapper mapper = new ObjectMapper();
				
		if(LOGGER.isDebugEnabled()) {
			LOGGER.info("Validate Json File Type");
		}

		try (InputStream in = new FileInputStream(FileValidator.listSchema.get(fileSetting.getDiretoryPath()))) {

			jsonSchema = new JSONObject(new JSONTokener(in));
			Schema schema = SchemaLoader.load(jsonSchema);
			JsonNode node = mapper.readTree(fileSetting.getFile());
			
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("node in validate " + node.toString());
			}
			schema.validate(new JSONObject(node.toString()));

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		} finally {

		}
		return true;
	}

}
