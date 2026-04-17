package org.example;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import java.util.*;

public class Mission {
    private String missionId;
    private String outcome;
    private String date;
    private String location;
    private int damageCost;
    private Curse curse;
    private List<Sorcerer> sorcerers;      // типизированный список
    private List<Technique> techniques;    // типизированный список
    private String comment;
    private Map<String, Object> extraFields;

    public Mission() {
        this.sorcerers = new ArrayList<>();
        this.techniques = new ArrayList<>();
        this.extraFields = new LinkedHashMap<>();
    }

    // Геттеры и сеттеры
    public String getMissionId() { return missionId; }
    public void setMissionId(String missionId) { this.missionId = missionId; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getDamageCost() { return damageCost; }
    public void setDamageCost(int damageCost) { this.damageCost = damageCost; }

    public Curse getCurse() { return curse; }
    public void setCurse(Curse curse) { this.curse = curse; }
    public void setCurse(String name, String threatLevel) { this.curse = new Curse(name, threatLevel); }

    public List<Sorcerer> getSorcerers() { return sorcerers; }
    public void setSorcerers(List<Sorcerer> sorcerers) { this.sorcerers = sorcerers; }

    public List<Technique> getTechniques() { return techniques; }
    public void setTechniques(List<Technique> techniques) { this.techniques = techniques; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Map<String, Object> getExtraFields() { return extraFields; }

    @JsonAnySetter
    public void addExtraField(String key, Object value) {
        this.extraFields.put(key, value);
    }
}