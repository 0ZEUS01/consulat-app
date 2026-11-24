package com.example.consulat.controller;

import com.example.consulat.entity.Demande;
import com.example.consulat.service.DemandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/Demande")
@CrossOrigin(origins = "*")
public class DemandeController {
    @Autowired
    public DemandeService demandeService;

    @PostMapping
    public Demande PostNewDemande(@RequestBody Demande demande){
        return demandeService.CreateDemande(demande);
    }
}
