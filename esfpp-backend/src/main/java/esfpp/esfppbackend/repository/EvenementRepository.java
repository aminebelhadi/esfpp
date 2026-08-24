package esfpp.esfppbackend.repository;


import esfpp.esfppbackend.entities.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    // Spring Data JPA génère automatiquement la requête SQL pour chercher par filière
    List<Evenement> findByFiliereId(Long filiereId);
    Optional<Evenement> findByFiliereIdAndSemaineIndex(Long filiereId, Integer semaineIndex);
    List<Evenement> findBySemaineIndex(Integer semaineIndex);
}
