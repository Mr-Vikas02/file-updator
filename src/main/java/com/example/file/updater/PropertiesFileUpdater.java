package com.example.file.updater;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class PropertiesFileUpdater implements FileUpdater{

  @Override
  @SuppressWarnings("unchecked")
  public boolean UpdateFile(String oldPath, String newPath) {
    if (isEmpty(oldPath) || isEmpty(newPath)) {
      System.out.println("Old and New Path of file is required for updating file content. Old File = " + oldPath + ", New File = " + newPath);
      return false;
    }
    try {
      //** Read Properties **//
      Properties properties = readProps(oldPath);
      Properties newProperties = readProps(newPath);

      //** 2. Validate arguments **//
      List<String> oldKeys = properties.keySet().stream().map(String::valueOf).toList();
      List<String> newKeys = newProperties.keySet().stream().map(String::valueOf).toList();

      //** List of all keys (Removed, Existing, Newly) **//
      List<String> removeKeys = oldKeys.stream().filter(k -> !newKeys.contains(k)).toList();
      List<String> existingKeys = newKeys.stream().filter(oldKeys::contains).toList();
      List<String> newAddedKeys = newKeys.stream().filter(k -> !oldKeys.contains(k)).toList();

      // removing keys
      removeKeys.forEach(properties::remove);

      // adding new properties
      newAddedKeys.forEach(key -> {
        properties.put(key, newProperties.get(key));
      });

      // updating existing
      existingKeys.forEach(key -> {
        properties.put(key, newProperties.get(key));
      });

      //** Write Properties back in old file **//
      writeProps(properties, oldPath);

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public Properties readProps(String path) throws IOException {
    Properties props = new Properties();
    InputStream is = new FileInputStream(path);
    props.load(is);
    is.close();
    return props;
  }

  public void writeProps(Properties prop, String path) throws IOException {
    OutputStream output = new FileOutputStream(path);
    prop.store(output, null);
    output.close();
  }

  private boolean isEmpty(String str) {
    return  str == null || str.trim().isEmpty();
  }

}
