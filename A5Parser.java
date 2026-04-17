package org.example;

import java.io.*;
import java.util.*;

public class A5Parser implements Reader {
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
        List<Map<String, String>> timeline = new ArrayList<>();
        List<String> enemyActions = new ArrayList<>();

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
                String[] parts = line.split("\\|");
                if (parts.length < 2) continue;

                String type = parts[0];
                switch (type) {
                    case "MISSION_CREATED":
                        if (parts.length >= 4) {
                            mission.setMissionId(parts[1]);
                            mission.setDate(parts[2]);
                            mission.setLocation(parts[3]);
                        } else {
                            System.err.println("Неполная MISSION_CREATED: " + line);
                        }
                        break;
                    case "CURSE_DETECTED":
                        if (parts.length >= 3) {
                            mission.setCurse(parts[1], parts[2]);
                        } else {
                            System.err.println("Неполная CURSE_DETECTED: " + line);
                        }
                        break;
                    case "SORCERER_ASSIGNED":
                        if (parts.length >= 3) {
                            sorcerers.add(new Sorcerer(parts[1], parts[2]));
                        } else {
                            System.err.println("Неполная SORCERER_ASSIGNED: " + line);
                        }
                        break;
                    case "TECHNIQUE_USED":
                        if (parts.length >= 5) {
                            try {
                                int damage = Integer.parseInt(parts[4]);
                                techniques.add(new Technique(parts[1], parts[2], parts[3], damage));
                            } catch (NumberFormatException e) {
                                System.err.println("Некорректный урон техники: " + parts[4]);
                            }
                        } else {
                            System.err.println("Неполная TECHNIQUE_USED: " + line);
                        }
                        break;
                    case "TIMELINE_EVENT":
                        if (parts.length >= 4) {
                            Map<String, String> event = new LinkedHashMap<>();
                            event.put("timestamp", parts[1]);
                            event.put("type", parts[2]);
                            event.put("description", parts[3]);
                            timeline.add(event);
                        } else {
                            System.err.println("Неполная TIMELINE_EVENT: " + line);
                        }
                        break;
                    case "ENEMY_ACTION":
                        if (parts.length >= 3) {
                            enemyActions.add(parts[1] + ": " + parts[2]);
                        } else {
                            System.err.println("Неполная ENEMY_ACTION: " + line);
                        }
                        break;
                    case "CIVILIAN_IMPACT":
                        Map<String, String> civilian = new LinkedHashMap<>();
                        for (int i = 1; i < parts.length; i++) {
                            String[] kv = parts[i].split("=");
                            if (kv.length == 2) civilian.put(kv[0], kv[1]);
                        }
                        mission.addExtraField("civilianImpact", civilian);
                        break;
                    case "MISSION_RESULT":
                        if (parts.length >= 2) {
                            mission.setOutcome(parts[1]);
                            if (parts.length > 2) {
                                String[] kv = parts[2].split("=");
                                if (kv.length == 2) mission.addExtraField(kv[0], kv[1]);
                            }
                        } else {
                            System.err.println("Неполная MISSION_RESULT: " + line);
                        }
                        break;
                }
            }
        }

        mission.setSorcerers(sorcerers);
        mission.setTechniques(techniques);
        if (!timeline.isEmpty()) mission.addExtraField("timeline", timeline);
        if (!enemyActions.isEmpty()) mission.addExtraField("enemyActions", enemyActions);
        return mission;
    }
}