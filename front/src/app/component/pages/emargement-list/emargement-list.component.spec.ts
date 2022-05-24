import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmargementListComponent } from './emargement-list.component';

describe('EmargementListComponent', () => {
  let component: EmargementListComponent;
  let fixture: ComponentFixture<EmargementListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmargementListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmargementListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
