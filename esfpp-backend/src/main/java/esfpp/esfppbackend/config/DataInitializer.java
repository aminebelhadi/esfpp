package esfpp.esfppbackend.config;

import esfpp.esfppbackend.entities.Evenement;
import esfpp.esfppbackend.entities.Evenement.TypeEvenement;
import esfpp.esfppbackend.entities.Filiere;
import esfpp.esfppbackend.entities.Formateur;
import esfpp.esfppbackend.entities.Module;
import esfpp.esfppbackend.repository.EvenementRepository;
import esfpp.esfppbackend.repository.FiliereRepository;
import esfpp.esfppbackend.repository.FormateurRepository;
import esfpp.esfppbackend.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private FormateurRepository formateurRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Override
    public void run(String... args) throws Exception {
        // 1. On vérifie si la base est vide pour éviter de dupliquer les données à chaque redémarrage
        if (filiereRepository.count() == 0) {

            // 2. Création d'une Filière
            Filiere reanimation = new Filiere(null, "Infirmier en réanimation", "1ère année");
            filiereRepository.save(reanimation);

            // 3. Création de Modules liés à la filière
            // CORRECTION : Ajout de new HashSet<>() comme 5ème argument
            Module soinsDeBase = new Module(null, "Soins infirmiers de base", 150, reanimation, new HashSet<>());
            Module anatomie = new Module(null, "Anatomie et physiologie", 95, reanimation, new HashSet<>());
            moduleRepository.save(soinsDeBase);
            moduleRepository.save(anatomie);

            // 4. Création de Formateurs liés aux modules
            // Note : L'utilisation de new HashSet<>(Set.of(...)) est plus sécurisée pour Hibernate que Set.of() direct
            Formateur formateur1 = new Formateur(null, "Khalfi", "Abdelmajid", LocalDate.of(1980, 5, 15), 35, new HashSet<>(Set.of(soinsDeBase)));
            Formateur formateur2 = new Formateur(null, "Tahiri", "Ahmed", LocalDate.of(1975, 8, 22), 40, new HashSet<>(Set.of(anatomie)));
            formateurRepository.save(formateur1);
            formateurRepository.save(formateur2);

            // 5. Création des Événements du Logigramme
            Evenement examens = new Evenement(null, "Semaine d'examens", TypeEvenement.EXAMEN, 10, reanimation);
            Evenement vacances = new Evenement(null, "Vacances d'hiver", TypeEvenement.VACANCES, 16, reanimation);
            evenementRepository.save(examens);
            evenementRepository.save(vacances);

            System.out.println("✅ Données de test initialisées avec succès !");
        }
    }
}