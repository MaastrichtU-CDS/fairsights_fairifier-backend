package nl.maastro.fairifier.services;

import nl.maastro.fairifier.entity.TestData;
import nl.maastro.fairifier.repository.TestDataRepository;
import org.springframework.stereotype.Service;

@Service
public class TestDataService {
    private TestDataRepository testDataRepository;

    public TestDataService(TestDataRepository testDataRepository) {
    }

    public TestData createTestData(String patientId, Integer age, String clinicalTStage, String clinicalNStage,
                                   String clinicalMStage, String overallStage, String history, String gender,
                                   String survivalTime, String deadStatus){
        TestData entity = new TestData();
        entity.setPatientId(patientId);
        entity.setAge(age);
        entity.setClinicalTStage(clinicalTStage);
        entity.setClinicalNStage(clinicalNStage);
        entity.setClinicalMStage(clinicalMStage);
        entity.setOverallStage(overallStage);
        entity.setHistory(history);
        entity.setGender(gender);
        entity.setSurvivalTime(survivalTime);
        entity.setDeadStatus(deadStatus);
        return testDataRepository.save(entity);
    }
}
