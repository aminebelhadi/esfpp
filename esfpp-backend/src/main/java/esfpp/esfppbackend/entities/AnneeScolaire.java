package esfpp.esfppbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnneeScolaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String libelle; // ex: "2026-2027"

    // Utile pour le calcul dynamique des dates (le 1er lundi de septembre)
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Boolean estActive; // Permet de définir l'année par défaut au chargement
}