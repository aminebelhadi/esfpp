package esfpp.esfppbackend.repository;

import esfpp.esfppbackend.entities.Formateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormateurRepository extends JpaRepository<Formateur, Long> {
}
