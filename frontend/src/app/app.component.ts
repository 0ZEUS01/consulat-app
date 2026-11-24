import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { DemandeListComponent } from './components/demande-list/demande-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet , DemandeListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'consulat-app';
}
