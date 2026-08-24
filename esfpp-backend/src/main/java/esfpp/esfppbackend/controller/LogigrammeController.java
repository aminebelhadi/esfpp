package esfpp.esfppbackend.controller;


import esfpp.esfppbackend.dto.ModuleDTO;
import esfpp.esfppbackend.dto.PlanificationDTO;
import esfpp.esfppbackend.entities.AnneeScolaire;
import esfpp.esfppbackend.entities.Evenement;
import esfpp.esfppbackend.entities.Filiere;
import esfpp.esfppbackend.entities.Module;
import esfpp.esfppbackend.repository.EvenementRepository;
import esfpp.esfppbackend.repository.FiliereRepository;
import esfpp.esfppbackend.repository.ModuleRepository;
import esfpp.esfppbackend.service.LogigrammeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logigramme")
@CrossOrigin(origins = "*") // ⚠️ Très important pour autoriser React à lire ces données
public class LogigrammeController {

    // On injecte uniquement le Service, plus les Repositories
    @Autowired
    private LogigrammeService logigrammeService;
    @Autowired
    private EvenementRepository evenementRepository;
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private esfpp.esfppbackend.repository.FormateurRepository formateurRepository;

    @GetMapping("/evenements/filiere/{filiereId}")
    public List<Evenement> getEvenementsByFiliere(@PathVariable Long filiereId) {
        return logigrammeService.getEvenementsByFiliere(filiereId);
    }

    @GetMapping("/modules/filiere/{filiereId}")
    public List<ModuleDTO> getModulesByFiliere(@PathVariable Long filiereId) {
        return logigrammeService.getModulesByFiliere(filiereId);
    }

    @GetMapping("/filieres")
    public List<Filiere > getAllFilieres() {
        return logigrammeService.getAllFilieres();
    }

    @PostMapping("/planifications/bulk")
    public void sauvegarderPlanifications(@RequestBody List<PlanificationDTO> planifications) {
        logigrammeService.sauvegarderPlanifications(planifications);
    }

    @PostMapping("/evenements/filiere/{filiereId}")
    public void sauvegarderEvenement(
            @PathVariable Long filiereId,
            @RequestParam Integer semaineIndex,
            @RequestParam String type, // "EXAMEN", "CONTROLE", "VACANCES", ou "AUCUN"
            @RequestParam(required = false) String titre) {

        // 1. Chercher s'il y a déjà un événement pour cette filière à cette semaine
        Optional<Evenement> existant = evenementRepository.findByFiliereIdAndSemaineIndex(filiereId, semaineIndex);

        if (type.equals("AUCUN")) {
            existant.ifPresent(evenement -> evenementRepository.delete(evenement));
        } else {
            Evenement.TypeEvenement typeEnum = Evenement.TypeEvenement.valueOf(type);
            if (existant.isPresent()) {
                Evenement ev = existant.get();
                ev.setType(typeEnum);
                ev.setTitre(titre != null ? titre : type);
                evenementRepository.save(ev);
            } else {
                Filiere filiere = filiereRepository.findById(filiereId).orElseThrow();
                Evenement ev = new Evenement(null, titre != null ? titre : type, typeEnum, semaineIndex, filiere);
                evenementRepository.save(ev);
            }
        }
    }

    // ==================== ENDPOINTS FILIÈRES ====================
    @PostMapping("/filieres")
    public Filiere createFiliere(@RequestBody Filiere filiere) {
        return logigrammeService.createFiliere(filiere);
    }

    @DeleteMapping("/filieres/{id}")
    public void deleteFiliere(@PathVariable Long id) {
        logigrammeService.deleteFiliere(id);
    }

    // ==================== ENDPOINTS ANNÉES SCOLAIRES ====================
    @GetMapping("/annees")
    public List<AnneeScolaire> getAllAnnees() {
        return logigrammeService.getAllAnnees();
    }

    @PostMapping("/annees")
    public AnneeScolaire createAnnee(@RequestBody AnneeScolaire annee) {
        return logigrammeService.createAnnee(annee);
    }

    @DeleteMapping("/annees/{id}")
    public void deleteAnnee(@PathVariable Long id) {
        logigrammeService.deleteAnnee(id);
    }

    // ==================== ENDPOINT MODULES ====================
    @PostMapping("/modules/filiere/{filiereId}")
    public Module createModule(
            @RequestBody Module module,
            @PathVariable Long filiereId,
            @RequestParam(required = false) Long formateurId) { // <-- Nouveau paramètre
        return logigrammeService.createModule(module, filiereId, formateurId);
    }
    @DeleteMapping("/modules/{id}")
    public void deleteModule(@PathVariable Long id) {
        // On appelle le service au lieu du repository direct
        logigrammeService.deleteModule(id);
    }

    @GetMapping("/formateurs")
    public List<java.util.Map<String, Object>> getAllFormateurs() {
        return formateurRepository.findAll().stream().map(f -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", f.getId());
            map.put("nom", f.getNom());
            map.put("prenom", f.getPrenom());
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/formateurs/details")
    public List<java.util.Map<String, Object>> getFormateursDetails() {
        return formateurRepository.findAll().stream().map(f -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", f.getId());
            map.put("nom", f.getNom());
            map.put("prenom", f.getPrenom());
            map.put("dateNaissance", f.getDateNaissance());
            map.put("chargeHoraire", f.getChargeHoraire());

            // On extrait juste les noms des modules pour éviter la boucle infinie JSON
            List<String> modulesNoms = f.getModules().stream()
                    .map(Module::getNomModule)
                    .collect(java.util.stream.Collectors.toList());
            map.put("modules", modulesNoms);

            return map;
        }).collect(java.util.stream.Collectors.toList());
    }

    // 2. Créer un nouveau formateur
    @PostMapping("/formateurs")
    public esfpp.esfppbackend.entities.Formateur createFormateur(@RequestBody esfpp.esfppbackend.entities.Formateur formateur) {
        return formateurRepository.save(formateur);
    }

    // 3. Supprimer un formateur
    @DeleteMapping("/formateurs/{id}")
    public void deleteFormateur(@PathVariable Long id) {
        logigrammeService.deleteFormateur(id);
    }

    @GetMapping("/dashboard")
    public esfpp.esfppbackend.dto.DashboardDTO getDashboard() {
        return logigrammeService.getDashboardData();
    }
}
