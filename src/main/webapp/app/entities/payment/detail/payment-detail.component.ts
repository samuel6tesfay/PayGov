import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PaymentService } from '../service/payment.service';
import { finalize } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PaymentCreateDialogComponent } from '../create/payment-create-dialog.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';



import { IPayment } from '../payment.model';

@Component({
  selector: 'jhi-payment-detail',
  templateUrl: './payment-detail.component.html',
})
export class PaymentDetailComponent implements OnInit {
  // payment: IPayment | undefined;
  payment: any;
  pay: any;
  length: any;
  isSaving = false;

  payments?: IPayment[];
  isLoading = false;
  //  payment?: any;
 

  constructor(protected paymentService: PaymentService, protected activatedRoute: ActivatedRoute, protected modalService: NgbModal) { }
  
  loadAll(): void {
    this.isLoading = true;

    // this.paymentService.query().subscribe({
    //   next: (res: HttpResponse<IPayment[]>) => {
    //     this.isLoading = false;
    //     this.payments = res.body ?? [];
    //   },
    //   error: () => {
    //     this.isLoading = false;
    //   },
    // });
  }

  ngOnInit(): void {
      this.pay = sessionStorage.getItem("payment");
      this.payment = JSON.parse(this.pay);
       
      // this.length = this.payment.cik.length;
      // for (let index = 0; index < 10-this.length; index++) {
      //   this.payment.cik = "0".concat(this.payment.cik);

      // }
      this.loadAll();
  }
  previousState(): void {
    window.history.back();
  }

   save(): void {
     this.isSaving = true;
     const modalRef = this.modalService.open(PaymentCreateDialogComponent, { size: 'lg', backdrop: 'static' });
     modalRef.componentInstance.payment = this.payment;
     modalRef.closed.subscribe(reason => {
      if (reason === 'save') {
        this.loadAll();
      }
    });
   }
  
   protected subscribeToSaveResponse(result: Observable<HttpResponse<IPayment>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
  
  
  
}
