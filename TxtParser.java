package org.example;

import java.io.*;
import java.util.*;

public class TxtParser implements Reader {
    @Override
    public Mission read(File file) throws Exception {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("Файл не найден: " + file);
        }
        return readFile(file);
    }

    private Mission readFile(File file) throws Exception {
        Mission mission = new Mission();
        List<Sorcerer> sorcerers = new ArrayList<>();
        List<Technique> techniques = new ArrayList<>();
        Map<String, String> currentBlock = new LinkedHashMap<>();
        String currentSection = "";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            br.mark(1);
            int firstChar = br.read();
            if (firstChar != 0xFEFF) {
                br.reset();
            }

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("[") && line.endsWith("]")) {
                    processBlock(mission, currentSection, currentBlock, sorcerers, techniques);
                    currentSection = line.substring(1, line.length() - 1);
                    currentBlock = new LinkedHashMap<>();
                    continue;
                }

                int sep = line.indexOf('=');
                if (sep != -1) {
                    String key = line.substring(0, sep).trim();
                    String value = line.substring(sep + 1).trim();
                    currentBlock.put(key, value);
                }
            }
            processBlock(mission, currentSection, currentBlock, sorcerers, techniques);
        }

        if (!sorcerers.isEmpty()) mission.setSorcerers(sorcerers);
        if (!techniques.isEmpty()) mission.setTechniques(techniques);
        return mission;
    }

    private void processBlock(Mission mission, String section, Map<String, String> block,
                              List<Sorcerer> sorcerers, List<Technique> techniques) {
        if (block.isEmpty() || section.isEmpty()) return;

        switch (section) {
            case "MISSION":
                if (block.containsKey("missionId"))
                    mission.setMissionId(block.get("missionId"));
                if (block.containsKey("date"))
                    mission.setDate(block.get("date"));
                if (block.containsKey("location"))
                    mission.setLocation(block.get("location"));
                if (block.containsKey("outcome"))
                    mission.setOutcome(block.get("outcome"));
                if (block.containsKey("damageCost")) {
                    try {
                        mission.setDamageCost(Integer.parseInt(block.get("damageCost")));
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректное damageCost: " + block.get("damageCost"));
                    }
                }
                if (block.containsKey("comment"))
                    mission.setComment(block.get("comment"));
                break;
            case "CURSE":
                if (block.containsKey("name") && block.containsKey("threatLevel"))
                    mission.setCurse(block.get("name"), block.get("threatLevel"));
                break;
            case "SORCERER":
                if (block.containsKey("name") && block.containsKey("rank"))
                    sorcerers.add(new Sorcerer(block.get("name"), block.get("rank")));
                break;
            case "TECHNIQUE":
                if (block.containsKey("name") && block.containsKey("type") &&
                        block.containsKey("owner") && block.containsKey("damage")) {
                    try {
                        int damage = Integer.parseInt(block.get("damage"));
                        techniques.add(new Technique(block.get("name"), block.get("type"),
                                block.get("owner"), damage));
                    } catch (NumberFormatException e) {
                        System.err.println("Некорректный урон: " + block.get("damage"));
                    }
                }
                break;
            default:
                mission.addExtraField(section.toLowerCase(), new LinkedHashMap<>(block));
                break;
        }
    }
}