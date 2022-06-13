import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PaymentService } from '../service/payment.service';

import { PaymentComponent } from './payment.component';

describe('Payment Management Component', () => {
  let comp: PaymentComponent;
  let fixture: ComponentFixture<PaymentComponent>;
  let service: PaymentService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PaymentComponent],
    })
      .overrideTemplate(PaymentComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaymentComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PaymentService);

    const headers = new HttpHeaders();
    // jest.spyOn(service, 'query').mockReturnValue(
    //   of(
    //     new HttpResponse({
    //       body: [{ id: 123 }],
    //       headers,
    //     })
    //   )
    // );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    // expect(comp.payments?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
