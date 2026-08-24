package esfpp.esfppbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomModule; // ex: "Anatomie et physiologie"

    @Column(name = "volume_horaire_global")
    private Integer volumeHoraireGlobal; // ex: 95

    // Un module appartient à une seule filière
    @ManyToOne
    @JoinColumn(name = "filiere_id")
    private Filiere filiere;
    @ManyToMany(mappedBy = "modules")
    private Set<Formateur> formateurs = new HashSet<>();
}