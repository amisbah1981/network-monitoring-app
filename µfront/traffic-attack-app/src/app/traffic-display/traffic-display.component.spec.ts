import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrafficDisplayComponent } from './traffic-display.component';

describe('TrafficDisplayComponent', () => {
  let component: TrafficDisplayComponent;
  let fixture: ComponentFixture<TrafficDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrafficDisplayComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrafficDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
