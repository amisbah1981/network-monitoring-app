import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { TrafficMessage } from './traffic-message.model';

@Injectable({
  providedIn: 'root'
})
export class TrafficDisplayService {
  private trafficWebSocket$: WebSocketSubject<any>;
  private trafficMessages = new Subject<TrafficMessage>();
  private maliciousTrafficMessages = new Subject<TrafficMessage>();

  constructor() {
    this.trafficWebSocket$ = this.connectToWebSocket('ws://localhost:8080');
  }

  private connectToWebSocket(url: string): WebSocketSubject<any> {
    const socket$ = webSocket<any>(url);

    socket$.subscribe(
      (message) => {
        console.log('Message received from WebSocket:', message);
        
        // Parse the `value` field to get the traffic data
        const parsedData: TrafficMessage = JSON.parse(message.value);

        // Emit the parsed message based on topic
        if (message.topic === 'edge-traffic') {
          this.trafficMessages.next(parsedData);
        } else if (message.topic === 'malicious-traffic') {
          this.maliciousTrafficMessages.next(parsedData);
        }
      },
      (err) => console.error('WebSocket error:', err),
      () => console.log('WebSocket connection closed')
    );

    return socket$;
  }

  // Observable for edge traffic data
  getTrafficData(): Observable<TrafficMessage> {
    return this.trafficMessages.asObservable();
  }

  // Observable for malicious traffic data
  getMaliciousTrafficData(): Observable<TrafficMessage> {
    return this.maliciousTrafficMessages.asObservable();
  }
}
