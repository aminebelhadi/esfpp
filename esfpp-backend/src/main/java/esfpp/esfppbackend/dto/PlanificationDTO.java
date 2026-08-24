package esfpp.esfppbackend.dto;

import lombok.Data;

@Data
public class PlanificationDTO {
    private Long moduleId;
    private Integer semaineIndex;
    private Integer heures; // Ex: 3 (ou 0 si l'utilisateur efface la case)
}