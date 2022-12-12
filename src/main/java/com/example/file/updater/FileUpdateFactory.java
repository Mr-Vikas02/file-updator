package com.example.file.updater;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class FileUpdateFactory {
  public static FileUpdater getFileUpdater(String extension){
    FileUpdater fileUpdater = null;
    switch (extension) {
      case "properties" -> {
        fileUpdater = new PropertiesFileUpdater();
      }
      case "yml", "yaml" -> {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        fileUpdater = new JsonOrYamlFileUpdater(objectMapper);
      }
      case "json" -> {
        ObjectMapper objectMapper = new ObjectMapper();
        fileUpdater = new JsonOrYamlFileUpdater(objectMapper);
      }
    }
    return fileUpdater;
  }
}
