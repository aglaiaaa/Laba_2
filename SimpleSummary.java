package org.example;


public class SimpleSummary implements Summary {
    private Mission mission;

    public SimpleSummary(Mission mission) {
        this.mission = mission;
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ОТЧЕТ ПО МИССИИ ===\n\n");
        sb.append("ID: ").append(mission.getMissionId()).append("\n");
        sb.append("Дата: ").append(mission.getDate()).append("\n");
        sb.append("Локация: ").append(mission.getLocation()).append("\n");
        sb.append("Результат: ").append(mission.getOutcome()).append("\n");
        sb.append("Проклятие: ").append(mission.getCurse()).append("\n");
        if (mission.getComment() != null && !mission.getComment().isEmpty()) {
            sb.append("Комментарий: ").append(mission.getComment()).append("\n");
        }
        return sb.toString();
    }
}
