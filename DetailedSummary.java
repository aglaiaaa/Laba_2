package org.example;

import java.util.*;

public class DetailedSummary implements Summary {
    private Summary wrapped;
    private Mission mission;

    public DetailedSummary(Summary wrapped, Mission mission) {
        this.wrapped = wrapped;
        this.mission = mission;
    }

    @Override
    public String getText() {
        String base = wrapped.getText();
        StringBuilder sb = new StringBuilder(base);

        List sorcerers = mission.getSorcerers();
        if (sorcerers != null && !sorcerers.isEmpty()) {
            sb.append("\n--- Участники ---\n");
            for (int i = 0; i < sorcerers.size(); i++) {
                sb.append("  ").append(i+1).append(") ").append(sorcerers.get(i)).append("\n");
            }
        }

        List techniques = mission.getTechniques();
        if (techniques != null && !techniques.isEmpty()) {
            sb.append("\n--- Техники ---\n");
            for (int i = 0; i < techniques.size(); i++) {
                sb.append("  ").append(i+1).append(") ").append(techniques.get(i)).append("\n");
            }
        }


        Map extra = mission.getExtraFields();
        if (extra != null && !extra.isEmpty()) {
            sb.append("\n--- Дополнительная информация ---\n");
            for (Object entryObj : extra.entrySet()) {
                Map.Entry entry = (Map.Entry) entryObj;
                sb.append(entry.getKey()).append(":\n");
                sb.append(formatValue(entry.getValue(), "  "));
            }
        }

        return sb.toString();
    }

    private String formatValue(Object value, String indent) {
        StringBuilder sb = new StringBuilder();
        if (value instanceof Map) {
            Map map = (Map) value;
            for (Object entryObj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) entryObj;
                sb.append(indent).append(entry.getKey()).append(": ");
                Object val = entry.getValue();
                if (val instanceof Map || val instanceof List) {
                    sb.append("\n").append(formatValue(val, indent + "  "));
                } else {
                    sb.append(val).append("\n");
                }
            }
        } else if (value instanceof List) {
            List list = (List) value;
            for (Object item : list) {
                if (item instanceof Map) {
                    sb.append(formatValue(item, indent));
                } else {
                    sb.append(indent).append("- ").append(item).append("\n");
                }
            }
        } else {
            sb.append(indent).append(value).append("\n");
        }
        return sb.toString();
    }
}