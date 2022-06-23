import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { PaymentService } from '../service/payment.service';

import { finalize } from 'rxjs/operators';
import { HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Router} from '@angular/router';
import { IHostedCheckout } from '../hostedCheckout.model';

@Component({
  templateUrl: './payment-create-dialog.component.html',
})
export class PaymentCreateDialogComponent {
  // payment?: IPayment;
  isSaving = false;
  payment: any;
  pay: any;
  length: any;
  time: number;
  display: any ;
  interval: any;
  hostedCheckout: any;
 

  constructor(protected paymentService: PaymentService, protected activeModal: NgbActiveModal,private router:Router) {
    this.time = 8
  }

  
  ngOnInit(): void {
    this.startTimer();
   }
     
  startTimer(): void{
   console.log("=====>");
    this.interval = setInterval(() => {
      if (this.time !== 0) {
        this.time--;
      } else {
          this.confirmCreate()
          // this.router.navigate(['/payment']);
          this.activeModal.dismiss();
          clearInterval(this.interval);
      } 
      return this.time;
    }, 1000);
  }


  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmCreate(): void {
     clearInterval(this.interval);
     this.pay = sessionStorage.getItem("payment");
     this.payment = JSON.parse(this.pay);
     this.length = this.payment.cik.length;
    
     for (let index = 0; index < 10-this.length; index++) {
       this.payment.cik = "0".concat(this.payment.cik);
     }
    
      this.subscribeToSaveResponse(this.paymentService.create(this.payment));
      // this.pay = sessionStorage.removeItem("payment");

  }
   
  previousState(): void {
    window.history.back();
  }
   protected subscribeToSaveResponse(result: Observable<HttpResponse<IHostedCheckout>>): void {
     result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
       next: (res: HttpResponse<IHostedCheckout>) =>
       {
         this.onSaveSuccess()
         this.hostedCheckout = res.body;
         window.location.href = "https://payment.".concat(this.hostedCheckout.partialRedirectUrl);
         
      },  
      
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
       this.activeModal.close('save');
    // this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }
}