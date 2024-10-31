import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { TrafficFormComponent } from './traffic-form/traffic-form.component';
import { AttackFormComponent } from './attack-form/attack-form.component';
import { TrafficDisplayComponent } from './traffic-display/traffic-display.component';
import { TrafficDisplayService } from './traffic-display.service';

@NgModule({
  declarations: [
    AppComponent,
    TrafficFormComponent,
    AttackFormComponent,
    TrafficDisplayComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [TrafficDisplayService],
  bootstrap: [AppComponent]
})
export class AppModule { }