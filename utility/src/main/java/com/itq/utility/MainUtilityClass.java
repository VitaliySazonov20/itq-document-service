package com.itq.utility;


import com.itq.utility.dto.DocumentCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class MainUtilityClass {

    private static final String BASE_API_URL = "http://localhost:8080/api/documents";
    private static final String CONFIG_FILE = "generator-config.txt";
    public static void main(String[] args) {
        try {
            log.info("Utility started - reading configuration file");
            int count = readConfigFile();
            log.info("Will generate {} documents", count);

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            long startTime =System.currentTimeMillis();
            int failedToGenerate = 0;
            for(int i = 0; i< count; i++){
                DocumentCreateRequest request = new DocumentCreateRequest();
                request.setAuthor("Generator");
                request.setTitle("Document-"+ (i+1));
                request.setInitiator("Generator");
                try{
                    restTemplate.postForObject(BASE_API_URL,request, String.class);
                    if((i+1-failedToGenerate)%10==0) {
                        log.info("Progress: {}/{} documents created ({}%)",
                                (i + 1 - failedToGenerate), count, (i - failedToGenerate + 1.0) / count * 100);
                    }
                } catch (Exception e){
                    log.error("Failed to create document {}: {}",(i+1),e.getMessage());
                    failedToGenerate++;
                }
            }
            long duration = System.currentTimeMillis() - startTime;
            log.info("Utility completed - {} documents in {} ms",count-failedToGenerate, duration );

        } catch (Exception e) {
            log.error("Generator failed: {}",e.getMessage());
        }
    }

    private static int readConfigFile() throws Exception{
        Path path = Paths.get(CONFIG_FILE);
        if(!Files.exists(path)){
            //Create default config file
            Files.writeString(path,"100");
            log.info("Created default config file with value: 100");
            return 100;
        }
        String contents = Files.readString(path).trim();
        return Integer.parseInt(contents);
    }
}
