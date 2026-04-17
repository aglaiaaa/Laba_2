package org.example;

import java.io.*;
import java.util.*;

public class YamlParser implements Reader {

    @Override
    public Mission read(File file) throws Exception {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + file);
        }
        return readFile(file);
    }

    private Mission readFile(File file) throws Exception {
        Mission mission = new Mission();
        List<Map<String, Object>> currentList = null;
        Map<String, Object> currentListItem = null;
        Map<String, Object> currentBlock = null;
        String currentBlockName = "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            br.mark(1);
            int firstChar = br.read();
            if (firstChar != 0xFEFF) {
                br.reset();
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                int indent = countIndent(line);
                String trimmed = line.trim();

                if (indent == 0) {
                    if (currentListItem != null && currentList != null) {
                        currentList.add(new HashMap<>(currentListItem));
                        currentListItem = null;
                    }
                    if (currentList != null) {
                        saveList(mission, currentBlockName, currentList);
                        currentList = null;
                    }
                    if (currentBlock != null) {
                        saveBlock(mission, currentBlockName, currentBlock);
                        currentBlock = null;
                    }

                    int sep = trimmed.indexOf(':');
                    if (sep == -1) continue;
                    String key = trimmed.substring(0, sep).trim();
                    String value = trimmed.substring(sep + 1).trim();

                    if (value.isEmpty()) {
                        currentBlockName = key;
                    } else {
                        setBaseField(mission, key, value);
                    }
                }
                else if (indent == 2) {
                    if (trimmed.startsWith("- ")) {
                        if (currentListItem != null && currentList != null) {
                            currentList.add(new HashMap<>(currentListItem));
                        }
                        if (currentList == null) {
                            currentList = new ArrayList<>();
                        }
                        currentListItem = new HashMap<>();
                        currentBlock = null;

                        String rest = trimmed.substring(2).trim();
                        if (rest.contains(":")) {
                            int sep = rest.indexOf(':');
                            String key = rest.substring(0, sep).trim();
                            String value = rest.substring(sep + 1).trim();
                            currentListItem.put(key, value);
                        } else {
                            currentListItem.put("value", rest);
                        }
                    } else {
                        if (currentBlock == null) {
                            currentBlock = new HashMap<>();
                        }
                        int sep = trimmed.indexOf(':');
                        if (sep == -1) continue;
                        String key = trimmed.substring(0, sep).trim();
                        String value = trimmed.substring(sep + 1).trim();
                        currentBlock.put(key, value);
                    }
                }
                else if (indent == 4) {
                    if (currentListItem != null) {
                        int sep = trimmed.indexOf(':');
                        if (sep == -1) continue;
                        String key = trimmed.substring(0, sep).trim();
                        String value = trimmed.substring(sep + 1).trim();
                        currentListItem.put(key, value);
                    } else if (currentBlock != null) {
                        int sep = trimmed.indexOf(':');
                        if (sep == -1) continue;
                        String key = trimmed.substring(0, sep).trim();
                        String value = trimmed.substring(sep + 1).trim();
                        currentBlock.put(key, value);
                    }
                }
            }

            if (currentListItem != null && currentList != null) {
                currentList.add(new HashMap<>(currentListItem));
            }
            if (currentList != null) {
                saveList(mission, currentBlockName, currentList);
            }
            if (currentBlock != null) {
                saveBlock(mission, currentBlockName, currentBlock);
            }
        }

        return mission;
    }

    private void setBaseField(Mission mission, String key, String value) {
        switch (key) {
            case "missionId": mission.setMissionId(value); break;
            case "date":      mission.setDate(value); break;
            case "location":  mission.setLocation(value); break;
            case "outcome":   mission.setOutcome(value); break;
            case "damageCost":
                try {
                    mission.setDamageCost(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    System.err.println("Некорректное значение damageCost: " + value);
                }
                break;
            case "comment":   mission.setComment(value); break;
            default:          mission.addExtraField(key, value); break;
        }
    }

    private void saveBlock(Mission mission, String name, Map<String, Object> block) {
        if (name.equals("curse")) {
            mission.setCurse(
                    (String) block.getOrDefault("name", ""),
                    (String) block.getOrDefault("threatLevel", "")
            );
        } else {
            mission.addExtraField(name, new LinkedHashMap<>(block));
        }
    }

    private void saveList(Mission mission, String name, List<Map<String, Object>> list) {
        if (name.equals("sorcerers")) {
            List<Sorcerer> sorcerers = new ArrayList<>();
            for (Map<String, Object> item : list) {
                String sName = (String) item.getOrDefault("name", "");
                String rank = (String) item.getOrDefault("rank", "");
                sorcerers.add(new Sorcerer(sName, rank));
            }
            mission.setSorcerers(sorcerers);
        } else if (name.equals("techniques")) {
            List<Technique> techniques = new ArrayList<>();
            for (Map<String, Object> item : list) {
                String tName = (String) item.getOrDefault("name", "");
                String type = (String) item.getOrDefault("type", "");
                String owner = (String) item.getOrDefault("owner", "");
                int damage = 0;
                Object dmg = item.get("damage");
                if (dmg != null) {
                    try {
                        if (dmg instanceof Number) damage = ((Number) dmg).intValue();
                        else damage = Integer.parseInt(dmg.toString());
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректный урон техники: " + dmg);
                    }
                }
                techniques.add(new Technique(tName, type, owner, damage));
            }
            mission.setTechniques(techniques);
        } else {
            mission.addExtraField(name, new ArrayList<>(list));
        }
    }

    private int countIndent(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == ' ') count++;
            else break;
        }
        return count;
    }
}