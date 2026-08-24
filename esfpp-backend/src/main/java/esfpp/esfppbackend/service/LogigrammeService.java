package esfpp.esfppbackend.service;

import esfpp.esfppbackend.dto.DashboardDTO;
import esfpp.esfppbackend.dto.ModuleDTO;
import esfpp.esfppbackend.dto.PlanificationDTO;
import esfpp.esfppbackend.entities.*;
import esfpp.esfppbackend.entities.Module;
import esfpp.esfppbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogigrammeService {

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private PlanificationRepository planificationRepository;
    @Autowired
    private AnneeScolaireRepository anneeScolaireRepository;
    @Autowired
    private FormateurRepository formateurRepository;

    // ==================== GESTION DES FILIÈRES ====================
    public Filiere createFiliere(Filiere filiere) {
        return filiereRepository.save(filiere);
    }

    public void deleteFiliere(Long id) {
        filiereRepository.deleteById(id);
    }

    // ==================== GESTION DES ANNÉES SCOLAIRES ====================
    public List<AnneeScolaire> getAllAnnees() {
        return anneeScolaireRepository.findAll();
    }

    public AnneeScolaire createAnnee(AnneeScolaire annee) {
        return anneeScolaireRepository.save(annee);
    }

    public void deleteAnnee(Long id) {
        anneeScolaireRepository.deleteById(id);
    }

    // ==================== GESTION DYNAMIQUE DES ÉVÉNEMENTS ====================
    @Transactional
    public void sauvegarderEvenement(Long filiereId, Integer semaineIndex, String type, String titre) {
        Optional<Evenement> existant = evenementRepository.findByFiliereIdAndSemaineIndex(filiereId, semaineIndex);

        if ("AUCUN".equals(type)) {
            // Si l'utilisateur clique sur "Aucun", on supprime l'événement de la base
            existant.ifPresent(evenement -> evenementRepository.delete(evenement));
        } else {
            Evenement.TypeEvenement typeEnum = Evenement.TypeEvenement.valueOf(type);
            if (existant.isPresent()) {
                // Mise à jour de l'événement existant (ex: transformer des vacances en examen)
                Evenement ev = existant.get();
                ev.setType(typeEnum);
                ev.setTitre(titre != null ? titre : type);
                evenementRepository.save(ev);
            } else {
                // Création d'un nouvel événement
                Filiere filiere = filiereRepository.findById(filiereId)
                        .orElseThrow(() -> new RuntimeException("Filière introuvable"));
                Evenement ev = new Evenement(null, titre != null ? titre : type, typeEnum, semaineIndex, filiere);
                evenementRepository.save(ev);
            }
        }
    }

    public List<Evenement> getEvenementsByFiliere(Long filiereId) {
        return evenementRepository.findByFiliereId(filiereId);
    }

    // 1. NOUVEAUTÉ : La méthode de sauvegarde en masse
    @Transactional
    public void sauvegarderPlanifications(List<PlanificationDTO> dtos) {
        for (PlanificationDTO dto : dtos) {
            Module module = moduleRepository.findById(dto.getModuleId())
                    .orElseThrow(() -> new RuntimeException("Module introuvable"));

            Optional<Planification> existante = planificationRepository
                    .findByModuleIdAndSemaineIndex(dto.getModuleId(), dto.getSemaineIndex());

            if (dto.getHeures() == null || dto.getHeures() == 0) {
                // Si l'utilisateur a effacé la case, on supprime la donnée en base
                existante.ifPresent(p -> planificationRepository.delete(p));
            } else {
                if (existante.isPresent()) {
                    // Si la case existait déjà, on met à jour les heures
                    Planification p = existante.get();
                    p.setHeures(dto.getHeures());
                    planificationRepository.save(p);
                } else {
                    // Si la case était vide, on crée une nouvelle planification
                    Planification p = new Planification(null, dto.getSemaineIndex(), dto.getHeures(), module);
                    planificationRepository.save(p);
                }
            }
        }
    }

    // 2. MISE À JOUR : On récupère les VRAIES heures
    public List<ModuleDTO> getModulesByFiliere(Long filiereId) {
        List<Module> modules = moduleRepository.findByFiliereId(filiereId);

        return modules.stream().map(mod -> {
            ModuleDTO dto = new ModuleDTO();
            dto.setId(mod.getId());
            dto.setNomModule(mod.getNomModule());
            dto.setVolumeHoraireGlobal(mod.getVolumeHoraireGlobal());

            if (mod.getFormateurs() != null && !mod.getFormateurs().isEmpty()) {
                String noms = mod.getFormateurs().stream()
                        .map(f -> f.getNom() + " " + f.getPrenom())
                        .collect(Collectors.joining(", "));
                dto.setFormateur(noms);
            } else {
                dto.setFormateur("Non assigné");
            }

            // Récupérer les planifications de ce module
            List<Planification> planifs = planificationRepository.findByModuleId(mod.getId());
            Map<Integer, Integer> repartition = new HashMap<>();
            for (Planification p : planifs) {
                repartition.put(p.getSemaineIndex(), p.getHeures());
            }
            // On injecte les vraies données dans le DTO pour que React les affiche
            dto.setRepartition(repartition);

            return dto;
        }).collect(Collectors.toList());
    }

    public Module createModule(Module module, Long filiereId, Long formateurId) {
        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> new RuntimeException("Filière introuvable"));

        module.setFiliere(filiere);

        if(module.getFormateurs() == null) {
            module.setFormateurs(new java.util.HashSet<>());
        }

        // 1. On sauvegarde d'abord le module pour qu'il obtienne un ID
        Module savedModule = moduleRepository.save(module);

        // 2. Si un formateur a été sélectionné, on fait la liaison
        if (formateurId != null) {
            Formateur formateur = formateurRepository.findById(formateurId)
                    .orElseThrow(() -> new RuntimeException("Formateur introuvable"));

            // On met à jour la relation bidirectionnelle (Formateur possède le @JoinTable)
            formateur.getModules().add(savedModule);
            savedModule.getFormateurs().add(formateur);

            formateurRepository.save(formateur);
        }

        return savedModule;
    }

    public List<Filiere> getAllFilieres() {
        return filiereRepository.findAll();
    }

    @Transactional // Très important pour exécuter ces actions en un seul bloc
    public void deleteModule(Long id) {
        Module module = moduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Module introuvable"));

        // 1. Casser la relation avec les Formateurs (Nettoie la table formateur_module)
        if (module.getFormateurs() != null) {
            for (Formateur formateur : module.getFormateurs()) {
                formateur.getModules().remove(module);
            }
            module.getFormateurs().clear();
        }

        // 2. (Optionnel mais recommandé) Casser la relation avec les Planifications
        // Si tu as créé la table Planification pour l'Inline Editing, il faut aussi supprimer les heures planifiées
        List<Planification> planifications = planificationRepository.findByModuleId(id);
        if (!planifications.isEmpty()) {
            planificationRepository.deleteAll(planifications);
        }

        // 3. La voie est libre, on supprime le module
        moduleRepository.delete(module);
    }

    @Transactional
    public void deleteFormateur(Long id) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur introuvable"));

        // On détache le formateur de tous ses modules pour ne pas violer la contrainte SQL
        for (Module m : formateur.getModules()) {
            m.getFormateurs().remove(formateur);
        }
        formateur.getModules().clear();

        formateurRepository.delete(formateur);
    }

    // ==================== GESTION DU DASHBOARD ====================
    public DashboardDTO getDashboardData() {
        DashboardDTO dashboard = new DashboardDTO();

        // 1. Calcul de la semaine actuelle par rapport au calendrier réel
        LocalDate today = LocalDate.now();
//        LocalDate today = LocalDate.of(2026, 10, 15);
        // Si on est avant septembre, on appartient à l'année scolaire précédente
        int year = today.getMonthValue() >= 9 ? today.getYear() : today.getYear() - 1;
        LocalDate startOfYear = LocalDate.of(year, 9, 1);

        // Trouver le premier lundi de septembre de cette année-là
        while (startOfYear.getDayOfWeek() != DayOfWeek.MONDAY) {
            startOfYear = startOfYear.plusDays(1);
        }

        // Calcul du nombre de semaines écoulées
        long weeksBetween = ChronoUnit.WEEKS.between(startOfYear, today);
        int semaineActuelle = (int) weeksBetween + 1;
        if (semaineActuelle < 1) semaineActuelle = 1;
        if (semaineActuelle > 48) semaineActuelle = 48;

        dashboard.setSemaineActuelle(semaineActuelle);

        // 2. Récupérer les alertes pour la SEMAINE PROCHAINE
        // 2. Récupérer les alertes pour la SEMAINE PROCHAINE (seulement si on n'est pas à la fin)
        List<Evenement> eventsNextWeek = new ArrayList<>();
        if (semaineActuelle < 48) {
            eventsNextWeek = evenementRepository.findBySemaineIndex(semaineActuelle + 1);
        }

        List<DashboardDTO.AlerteDTO> alertes = new ArrayList<>();
        for (Evenement ev : eventsNextWeek) {
            DashboardDTO.AlerteDTO alerte = new DashboardDTO.AlerteDTO();
            alerte.setType(ev.getType().name());
            alerte.setFiliereNom(ev.getFiliere().getNomFiliere() + " - " + ev.getFiliere().getNiveau());
            alerte.setMessage(ev.getTitre() + " prévu(s) en Semaine " + (semaineActuelle + 1));
            alertes.add(alerte);
        }
        dashboard.setAlertes(alertes);

        // 3. Calcul du taux d'avancement (heures faites vs totales)
        List<Filiere> filieres = filiereRepository.findAll();
        List<DashboardDTO.AvancementFiliereDTO> avancements = new ArrayList<>();

        int totalHeuresGlobal = 0;
        int totalFaitesGlobal = 0;

        for (Filiere f : filieres) {
            List<Module> modules = moduleRepository.findByFiliereId(f.getId());
            int totalHeuresFiliere = 0;
            int totalFaitesFiliere = 0;

            for (Module m : modules) {
                totalHeuresFiliere += m.getVolumeHoraireGlobal() != null ? m.getVolumeHoraireGlobal() : 0;

                List<Planification> planifs = planificationRepository.findByModuleId(m.getId());
                for (Planification p : planifs) {
                    // On ne compte que les heures planifiées JUSQU'À la semaine actuelle incluse
                    if (p.getSemaineIndex() <= semaineActuelle) {
                        totalFaitesFiliere += p.getHeures() != null ? p.getHeures() : 0;
                    }
                }
            }

            DashboardDTO.AvancementFiliereDTO av = new DashboardDTO.AvancementFiliereDTO();
            av.setFiliereNom(f.getNomFiliere() + " - " + f.getNiveau());
            av.setHeuresTotales(totalHeuresFiliere);
            av.setHeuresFaites(totalFaitesFiliere);
            av.setTaux(totalHeuresFiliere > 0 ? (double) totalFaitesFiliere / totalHeuresFiliere * 100 : 0.0);
            avancements.add(av);

            totalHeuresGlobal += totalHeuresFiliere;
            totalFaitesGlobal += totalFaitesFiliere;
        }

        dashboard.setAvancementParFiliere(avancements);
        dashboard.setAvancementGlobal(totalHeuresGlobal > 0 ? (double) totalFaitesGlobal / totalHeuresGlobal * 100 : 0.0);

        return dashboard;
    }
}