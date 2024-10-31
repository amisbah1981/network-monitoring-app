import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttackFormComponent } from './attack-form.component';

describe('AttackFormComponent', () => {
  let component: AttackFormComponent;
  let fixture: ComponentFixture<AttackFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AttackFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AttackFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
