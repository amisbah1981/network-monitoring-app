import { Component, Output, EventEmitter } from '@angular/core';
import { TrafficService } from '../traffic.service';

@Component({
  selector: 'app-attack-form',
  templateUrl: './attack-form.component.html',
  styleUrls: ['./attack-form.component.css']
})
export class AttackFormComponent {
  url: string = '';
  attackType: string = '';
  @Output() formSubmitted = new EventEmitter<void>();

  constructor(private trafficService: TrafficService) { }

  onSubmit() {
    this.trafficService.generateMaliciousTraffic(this.url, this.attackType).subscribe(response => {
      console.log('Malicious traffic generated:', response);
      this.formSubmitted.emit(); // Emit the event after form submission
    });
  }
}