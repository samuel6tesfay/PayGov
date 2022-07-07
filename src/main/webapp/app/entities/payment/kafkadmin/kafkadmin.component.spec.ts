import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KafkadminComponent } from './kafkadmin.component';

describe('KafkadminComponent', () => {
  let component: KafkadminComponent;
  let fixture: ComponentFixture<KafkadminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [KafkadminComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(KafkadminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
