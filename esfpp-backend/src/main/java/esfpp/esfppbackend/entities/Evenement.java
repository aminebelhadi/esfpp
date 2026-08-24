package esfpp.esfppbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre; // ex: "Semaine d'examens (Théorie + Pratique)"

    @Enumerated(EnumType.STRING)
    private TypeEvenement type; // EXAMEN, CONTROLE, VACANCES

    // Le numéro de la semaine dans l'année scolaire (de 1 à environ 48)
    private Integer semaineIndex;

    @ManyToOne
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;

    // Enumération interne pour restreindre les types d'événements
    public enum TypeEvenement {
        EXAMEN, CONTROLE, VACANCES
    }
}
