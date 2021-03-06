import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPayment } from '../payment.model';
import { PaymentService } from '../service/payment.service';
import { PaymentDeleteDialogComponent } from '../delete/payment-delete-dialog.component';

@Component({
  selector: 'jhi-payment',
  templateUrl: './payment.component.html',
})
export class PaymentComponent implements OnInit {
  payment?: any;
  pay?: any;
  isLoading = false;

  paymentAmount?: any;
  payments?: any;

 
  constructor(protected paymentService: PaymentService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.payments = sessionStorage.getItem("payment");
    this.paymentAmount = JSON.parse(this.payments).paymentAmount;
    
    this.paymentService.getTransactionId().subscribe({
      next: (res: HttpResponse<string>) => {
        this.isLoading = false;
        this.payment = res.body ?? "";
        this.pay = JSON.parse(JSON.stringify(this.payment));
        // sessionStorage.removeItem("payment");
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IPayment): number {
    return item.id!;
  }

  delete(payment: IPayment): void {
    const modalRef = this.modalService.open(PaymentDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.payment = payment;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
