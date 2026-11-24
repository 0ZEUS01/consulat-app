import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Demande } from '../models/demande.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DemandeService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getAllDemandes(): Observable<Demande[]> {
    return this.http.get<Demande[]>(this.apiUrl);
  }

  createDemande(demande: Demande): Observable<Demande> {
    return this.http.post<Demande>(this.apiUrl, demande);
  }
}