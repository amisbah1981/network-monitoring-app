import { Component, OnInit, ChangeDetectorRef, Input } from '@angular/core';
import { TrafficDisplayService } from '../traffic-display.service';
import { TrafficMessage } from '../traffic-message.model';

@Component({
  selector: 'app-traffic-display',
  templateUrl: './traffic-display.component.html',
  styleUrls: ['./traffic-display.component.css']
})
export class TrafficDisplayComponent implements OnInit {
  @Input() trafficData: TrafficMessage[] = [];
  @Input() maliciousTrafficData: TrafficMessage[] = [];

  constructor(private trafficDisplayService: TrafficDisplayService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchTrafficData();
  }

  fetchTrafficData(): void {
    // Subscribe to edge traffic data
    this.trafficDisplayService.getTrafficData().subscribe((data: TrafficMessage) => {
      console.log('Edge traffic data received:', data);
      this.trafficData.push(data);

      if (this.trafficData.length > 10) {
        this.trafficData.shift(); // Keep only the last 10 records
      }
      this.cdr.detectChanges(); // Trigger change detection to update the UI
    });

    // Subscribe to malicious traffic data (if necessary)
    this.trafficDisplayService.getMaliciousTrafficData().subscribe((data: TrafficMessage) => {
      console.log('Malicious traffic data received:', data);
      this.maliciousTrafficData.push(data);

      if (this.maliciousTrafficData.length > 10) {
        this.maliciousTrafficData.shift(); // Keep only the last 10 records
      }
      this.cdr.detectChanges(); // Trigger change detection to update the UI
    });
  }
}
