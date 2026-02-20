package com.itq.utility;


import com.itq.utility.dto.DocumentCreateRequest;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainUtilityClass {

    private static final String BASE_API_URL = "http://localhost:8080/api/documents";
    private static final String CONFIG_FILE = "generator-config.txt";
    public static void main(String[] args) {
        try {
            int count = readConfigFile();
            System.out.println("Starting generation of " + count + " documents");

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            for(int i = 0; i< count; i++){
                DocumentCreateRequest request = new DocumentCreateRequest();
                request.setAuthor("Generator");
                request.setTitle("Document-"+ (i+1));
                request.setInitiator("Generator");
                try{
                    restTemplate.postForObject(BASE_API_URL,request, String.class);
                } catch (Exception e){
                    System.err.println("Failed to create document " +(i+1)+ ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Generator failed: " + e.getMessage());
        }
    }

    private static int readConfigFile() throws Exception{
        Path path = Paths.get(CONFIG_FILE);
        if(!Files.exists(path)){
            //Create default config file
            Files.writeString(path,"100");
            System.out.println("Created default config file with value: 100");
            return 100;
        }
        String contents = Files.readString(path).trim();
        return Integer.parseInt(contents);
    }
}
