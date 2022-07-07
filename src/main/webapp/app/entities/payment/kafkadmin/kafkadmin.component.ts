import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { NgbModal, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { IPayment } from '../payment.model';
import { PaymentService } from '../service/payment.service';

@Component({
  selector: 'jhi-kafkadmin',
  templateUrl: './kafkadmin.component.html',
  styleUrls: ['./kafkadmin.component.scss'],
})
export class KafkadminComponent implements OnInit {
  data: any;
  pay: IPayment[] = [];
  isSaving = false;

  constructor(
    protected activatedRoute: ActivatedRoute,
    private router: Router,
    private route: ActivatedRoute,
    private modalService: NgbModal,
    protected payService: PaymentService
  ) {}

  ngOnInit(): void {
    this.kafkaAdmin();
  }

  kafkaAdmin(): any {
    setInterval(() => {
      this.payService.kafkaAdmin().subscribe((result: any) => {
        this.pay = result;
        console.log('Received Data ', this.pay);
      });
    }, 1000);
  }

  save(payment: IPayment): void {
    const data = payment;
    console.log('payment data to be saved :', data);
    console.log('Payment approval status ', data.approvalStatus);

    if (data.approvalStatus === 'Decline') {
      data.approvalStatus = 'Approve';
      this.isSaving = true;
      this.subscribeToSaveResponse(this.payService.create(data));
    } else {
      this.isSaving = true;
      this.subscribeToSaveResponse(this.payService.create(data));
    }
  }

  decline(payment: IPayment): void {
    const data = payment;

    console.log('payment data to be saved :', data);
    console.log('Payment approval status ', data.approvalStatus);

    if (data.approvalStatus === 'Approve') {
      data.approvalStatus = 'Decline';
      this.isSaving = true;
      this.subscribeToSaveResponse(this.payService.create(data));
    } else {
      this.isSaving = true;
      this.subscribeToSaveResponse(this.payService.create(data));
    }
  }


  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPayment>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    console.log('[]============== Payment saved successfully =====================[]');
  }

  protected onSaveError(): void {
    console.log('[]============== Payment not saved  =====================[]');
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
}