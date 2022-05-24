import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmargementRegisterComponent } from './emargement-register.component';

describe('EmargementRegisterComponent', () => {
  let component: EmargementRegisterComponent;
  let fixture: ComponentFixture<EmargementRegisterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmargementRegisterComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmargementRegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
