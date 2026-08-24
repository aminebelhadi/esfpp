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
public class Planification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // L'index de la semaine (de 1 à 48 environ)
    private Integer semaineIndex;

    // Le nombre d'heures planifiées pour cette semaine
    private Integer heures;

    // La planification appartient à un module spécifique
    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;
}