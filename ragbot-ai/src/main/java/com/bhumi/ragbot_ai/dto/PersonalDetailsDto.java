package com.bhumi.ragbot_ai.dto;

public class PersonalDetailsDto {

    private String name;
    private Integer age;
    private String occupation;
    private String goals;
    private String interests;
    private String notes;

    public PersonalDetailsDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
