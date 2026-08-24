package esfpp.esfppbackend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ModuleDTO {
    private Long id;
    private String nomModule;
    private Integer volumeHoraireGlobal;
    private String formateur; // Contiendra "Nom Prénom"
    private Map<Integer, Integer> repartition;
}
