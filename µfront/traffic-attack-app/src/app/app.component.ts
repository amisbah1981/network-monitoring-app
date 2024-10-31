import { Component, ViewChild } from '@angular/core';
import { TrafficDisplayComponent } from './traffic-display/traffic-display.component';
import { TrafficDisplayService } from './traffic-display.service';
import { TrafficMessage } from './traffic-message.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  @ViewChild(TrafficDisplayComponent) trafficDisplay!: TrafficDisplayComponent;
  trafficData: TrafficMessage[] = [];
  maliciousTrafficData: TrafficMessage[] = [];

  constructor(private trafficDisplayService: TrafficDisplayService) {}

  ngOnInit() {
    //this.subscribeToTrafficData();
  }

  subscribeToTrafficData() {
    this.trafficDisplayService.getTrafficData().subscribe(data => {
      this.trafficData.push(data);
    });

    this.trafficDisplayService.getMaliciousTrafficData().subscribe(data => {
      this.maliciousTrafficData.push(data);
    });
  }
  onFormSubmitted() {
    this.trafficDisplay.fetchTrafficData();
  }
}