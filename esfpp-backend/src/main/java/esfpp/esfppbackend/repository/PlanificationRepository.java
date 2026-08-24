package esfpp.esfppbackend.repository;

import esfpp.esfppbackend.entities.Planification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanificationRepository extends JpaRepository<Planification, Long> {
    List<Planification> findByModuleId(Long moduleId);

    // Chercher une case spécifique (un module pour une semaine précise)
    Optional<Planification> findByModuleIdAndSemaineIndex(Long moduleId, Integer semaineIndex);
}