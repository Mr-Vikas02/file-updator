package com.example.file.updater;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonOrYamlFileUpdater implements FileUpdater{

  private final ObjectMapper objectMapper;

  public JsonOrYamlFileUpdater(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean UpdateFile(String oldPath, String newPath) {
    if (isEmpty(oldPath) || isEmpty(newPath)) {
      System.out.println("Old and New Path of file is required for updating file content. Old File = " + oldPath + ", New File = " + newPath);
      return false;
    }

    try {
      Map<String, Object> oldValues = (Map<String, Object>) objectMapper.readValue(new File(oldPath), Map.class);
      Map<String, Object> newValues = (Map<String, Object>) objectMapper.readValue(new File(newPath), Map.class);

      List<String> oldKeys = getKeys(null, oldValues);
      List<String> newKeys = getKeys(null, newValues);

      List<String> removeKeys = oldKeys.stream().filter(key -> !newKeys.contains(key)).toList();
      List<String> existingKeys = newKeys.stream().filter(oldKeys::contains).toList();
      List<String> newAddedKeys = newKeys.stream().filter(key -> !oldKeys.contains(key)).toList();

      //** Removing Keys **//
      removeKeys.forEach(key -> remove(oldValues, key));

      //** Updating Existing Keys **//
      existingKeys.forEach(key -> {
        Object newValue = read(newValues, key);
        if (newValue != null) {
          put(oldValues, key, newValue);
        }
      });

      //** Adding New keys **//
      newAddedKeys.forEach(key -> {
        Object value = read(newValues, key);
        if (value != null) {
          put(oldValues, key, value);
        }
      });

      //** Write to old file **//
      objectMapper.writeValue(new File(oldPath), oldValues);

      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

  }

  @SuppressWarnings("unchecked")
  private List<String> getKeys(String prefix, Map<String, Object> map) {
    List<String> keys = new ArrayList<>();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      Object value = entry.getValue();
      if (value instanceof Map<?, ?>) {
        String childPrefix = prefix != null ? prefix + "." + entry.getKey() : entry.getKey();
        List<String> childKeys = getKeys(childPrefix, (Map<String, Object>) value);
        keys.addAll(childKeys);
      } else {
        keys.add(prefix != null ? prefix + "." + entry.getKey() : entry.getKey());
      }
    }
    return keys;
  }

  @SuppressWarnings("unchecked")
  private Object read(Map<String, Object> root, String key) {
    String[] keys = key.split("\\.");

    //** Getting the RootMap where Key is located **//
    for (int i = 0; i < keys.length - 1; i++) {
      if (root == null) return null;
      root = (Map<String, Object>) root.get(keys[i]);
    }
    if (root == null) return null;

    return root.get(keys[keys.length - 1]);
  }

  @SuppressWarnings("unchecked")
  private void remove(Map<String, Object> root, String key) {
    String[] keys = key.split("\\.");

    //** Getting the RootMap where Key is located **//
    for (int i = 0; i < keys.length - 1; i++) {
      if (root == null) return;
      root = (Map<String, Object>) root.get(keys[i]);
    }
    if (root == null) return;

    //** Removing the Key from RootMap where Key is located **//
    root.remove(keys[keys.length - 1]);
  }

  @SuppressWarnings("unchecked")
  private void put(Map<String, Object> root, String key, Object value) {
    String[] keys = key.split("\\.");

    //** Getting the RootMap where Key is located **//
    for (int i = 0; i < keys.length - 1; i++) {
      if (root == null) return;
      root = (Map<String, Object>) root.get(keys[i]);
    }
    if (root == null) return;

    //** Removing the Key from RootMap where Key is located **//
    root.put(keys[keys.length - 1], value);
  }

  private boolean isEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

}
