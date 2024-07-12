import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AirplaneInfoComponent } from './airplane-info.component';

describe('AirplaneInfoComponent', () => {
  let component: AirplaneInfoComponent;
  let fixture: ComponentFixture<AirplaneInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AirplaneInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AirplaneInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
