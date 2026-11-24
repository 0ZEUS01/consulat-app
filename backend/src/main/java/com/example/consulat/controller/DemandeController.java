package com.example.consulat.controller;

import com.example.consulat.entity.Demande;
import com.example.consulat.service.DemandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes")
@CrossOrigin(origins = "*")
public class DemandeController {
    @Autowired
    public DemandeService demandeService;

    @PostMapping
    public Demande PostNewDemande(@RequestBody Demande demande){
        return demandeService.createDemande(demande);
    }

    @GetMapping
    public List<Demande> GetAllDemands(){
        return demandeService.getDemandes();
    }
}
