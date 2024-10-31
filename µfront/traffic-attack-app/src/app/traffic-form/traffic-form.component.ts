import { Component, Output, EventEmitter } from '@angular/core';
import { TrafficService } from '../traffic.service';

@Component({
  selector: 'app-traffic-form',
  templateUrl: './traffic-form.component.html',
  styleUrls: ['./traffic-form.component.css']
})
export class TrafficFormComponent {
  url: string = '';
  trafficType: string = '';
  @Output() formSubmitted = new EventEmitter<void>();

  constructor(private trafficService: TrafficService) { }

  onSubmit() {
    this.trafficService.generateTraffic(this.url, this.trafficType).subscribe(response => {
      console.log('Traffic generated:', response);
      this.formSubmitted.emit(); // Emit the event after form submission
    });
  }
}