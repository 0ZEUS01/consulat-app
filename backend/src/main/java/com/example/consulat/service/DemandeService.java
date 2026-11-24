package com.example.consulat.service;

import com.example.consulat.entity.Demande;
import com.example.consulat.repository.DemandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemandeService {
    @Autowired
    public DemandeRepository demandeRepository;

    public Demande createDemande(Demande demande){
        demande.setStatut("EN_ATTENTE");
        return demandeRepository.save(demande);
    }

    public List<Demande> getDemandes(){
        return demandeRepository.findAll();
    }

}
