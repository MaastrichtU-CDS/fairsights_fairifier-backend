package nl.maastro.fairifier.repository;

import nl.maastro.fairifier.entity.TestData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {
    List<TestData> findByPatientId(String patientId);
    List<TestData> findByAge(Integer age);
    List<TestData> findByClinicalTStage(String clinicalTStage);
    List<TestData> findByClinicalNStage(String clinicalNStage);
    List<TestData> findByClinicalMStage(String clinicalMStage);
    List<TestData> findByOverallStage(String overall_stage);
    List<TestData> findByGender(String gender);
    List<TestData> findBySurvivalTime(String survival_time);
    List<TestData> findByDeadStatus(String dead_status);
}
