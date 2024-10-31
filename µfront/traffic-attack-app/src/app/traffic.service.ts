import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TrafficService {

  constructor(private http: HttpClient) { }

  generateTraffic(url: string, trafficType: string): Observable<any> {
    return this.http.get(`${url}?trafficType=${trafficType}`);
  }

  generateMaliciousTraffic(url: string, attackType: string): Observable<any> {
    return this.http.get(`${url}?attackType=${attackType}`);
  }
}