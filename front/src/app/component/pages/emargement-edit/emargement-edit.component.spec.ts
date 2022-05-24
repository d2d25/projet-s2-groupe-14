import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmargementEditComponent } from './emargement-edit.component';

describe('EmargementEditComponent', () => {
  let component: EmargementEditComponent;
  let fixture: ComponentFixture<EmargementEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EmargementEditComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmargementEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
