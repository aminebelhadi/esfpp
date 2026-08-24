package esfpp.esfppbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Formateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;


    // Optionnel : un volume horaire maximum imposé par contrat
    private Integer chargeHoraire;

    // Un formateur peut enseigner plusieurs modules (et un module peut avoir plusieurs formateurs)
    @ManyToMany
    @JoinTable(
            name = "formateur_module",
            joinColumns = @JoinColumn(name = "formateur_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    private Set<Module> modules = new HashSet<>();
}