package esfpp.esfppbackend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DashboardDTO {
    private int semaineActuelle;
    private double avancementGlobal;
    private List<AlerteDTO> alertes;
    private List<AvancementFiliereDTO> avancementParFiliere;

    @Data
    public static class AlerteDTO {
        private String type; // EXAMEN, VACANCES, CONTROLE
        private String filiereNom;
        private String message;
    }

    @Data
    public static class AvancementFiliereDTO {
        private String filiereNom;
        private double taux;
        private int heuresFaites;
        private int heuresTotales;
    }
}