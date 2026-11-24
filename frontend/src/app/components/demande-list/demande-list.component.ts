import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DemandeService } from '../../services/demande.service';
import { Demande } from '../../models/demande.model';

@Component({
  selector: 'app-demande-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './demande-list.component.html',
  styleUrl: './demande-list.component.css'
})
export class DemandeListComponent implements OnInit {
  demandes: Demande[] = [];
  newDemande: Demande = { cin: '', typeDocument: 'Passeport' };

  constructor(private demandeService: DemandeService) {}

  ngOnInit(): void {
    this.loadDemandes();
  }

  loadDemandes(): void {
    this.demandeService.getAllDemandes().subscribe({
      next: (data) => (this.demandes = data),
      error: (err) => console.error('Error loading demandes:', err)
    });
  }

  onSubmit(): void {
    if (!this.newDemande.cin.trim()) return;

    this.demandeService.createDemande(this.newDemande).subscribe({
      next: () => {
        this.newDemande = { cin: '', typeDocument: 'Passeport' };
        this.loadDemandes(); 
      },
      error: (err) => console.error('Error creating demande:', err)
    });
  }
}