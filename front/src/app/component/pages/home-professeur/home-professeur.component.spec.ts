import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeProfesseurComponent } from './home-professeur.component';

describe('HomeProfesseurComponent', () => {
  let component: HomeProfesseurComponent;
  let fixture: ComponentFixture<HomeProfesseurComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HomeProfesseurComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeProfesseurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
