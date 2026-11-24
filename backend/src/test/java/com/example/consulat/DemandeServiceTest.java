package com.example.consulat;

import com.example.consulat.entity.Demande;
import com.example.consulat.repository.DemandeRepository;
import com.example.consulat.service.DemandeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DemandeServiceTest {

    @Mock
    private DemandeRepository demandeRepository;

    @InjectMocks
    private DemandeService demandeService;

    @Test
    void createDemande_ShouldSetDefaultStatusToEnAttente() {
        Demande input = new Demande();
        input.setCin("TEST123");
        input.setTypeDocument("Passeport");

        when(demandeRepository.save(any(Demande.class))).thenAnswer(i -> i.getArgument(0));

        Demande result = demandeService.createDemande(input);

        assertEquals("EN_ATTENTE", result.getStatut());
    }
}