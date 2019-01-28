package nl.maastro.fairifier.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TestData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String patientId;
    private Integer age;
    private String clinicalTStage;
    private String clinicalNStage;
    private String clinicalMStage;
    private String overallStage;
    private String history;
    private String gender;
    private String survivalTime;
    private String deadStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getClinicalTStage() {
        return clinicalTStage;
    }

    public void setClinicalTStage(String clinicalTStage) {
        this.clinicalTStage = clinicalTStage;
    }

    public String getClinicalNStage() {
        return clinicalNStage;
    }

    public void setClinicalNStage(String clinicalNStage) {
        this.clinicalNStage = clinicalNStage;
    }

    public String getClinicalMStage() {
        return clinicalMStage;
    }

    public void setClinicalMStage(String clinicalMStage) {
        this.clinicalMStage = clinicalMStage;
    }

    public String getOverallStage() {
        return overallStage;
    }

    public void setOverallStage(String overallStage) {
        this.overallStage = overallStage;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSurvivalTime() {
        return survivalTime;
    }

    public void setSurvivalTime(String survivalTime) {
        this.survivalTime = survivalTime;
    }

    public String getDeadStatus() {
        return deadStatus;
    }

    public void setDeadStatus(String deadStatus) {
        this.deadStatus = deadStatus;
    }

    @Override
    public String toString() {
        return "TestData{" +
                "id='" + id + '\'' +
                ", patientd='" + patientId + '\'' +
                ", age='" + age + '\'' +
                ", clinicalTStage='" + clinicalTStage + '\'' +
                ", clinicalNStage='" + clinicalNStage + '\'' +
                ", clinicalMStage='" + clinicalMStage + '\'' +
                ", overallStage='" + overallStage + '\'' +
                ", history='" + history + '\'' +
                ", gender='" + gender + '\'' +
                ", survivalTime='" + survivalTime + '\'' +
                ", deadStatus='" + deadStatus + '\'' +
                '}';
    }
}
