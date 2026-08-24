package esfpp.esfppbackend.repository;

import esfpp.esfppbackend.entities.AnneeScolaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnneeScolaireRepository extends JpaRepository<AnneeScolaire, Long> {

}
