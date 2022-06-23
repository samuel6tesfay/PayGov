import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { IPayment, Payment } from '../payment.model';
import { PaymentService } from '../service/payment.service';
import { Router} from '@angular/router';


@Component({
  selector: 'jhi-payment-update',
  templateUrl: './payment-update.component.html',
})
export class PaymentUpdateComponent implements OnInit {
  isSaving = false;
  payment: any;
  pay: any ;
  cik: any
  value: any;
  length: any;
  maxlength: any=11;

  editForm = this.fb.group({
    id: [],
    cik: [null, [Validators.required,Validators.maxLength(11)]],
    ccc: [null, [Validators.required,Validators.pattern('((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$@#]).{8,30})')]],
    paymentAmount: [null, [Validators.required]],
    name: [null, [Validators.required]],
    email: [null, [Validators.required,Validators.email]],
    phone: [null, [Validators.required]],
  });

  constructor(protected paymentService: PaymentService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder,protected modalService: NgbModal,private router:Router) {}

  ngOnInit(): void {
    this.pay = sessionStorage.getItem("payment");
    this.payment = JSON.parse(this.pay);


    this.activatedRoute.data.subscribe(( payment ) => {
      this.updateForm(payment);
    });

    if (this.payment) {
       this.editForm.patchValue({
      id: this.payment.id,
      cik: this.payment.cik,
      ccc: this.payment.ccc,
      paymentAmount: this.payment.paymentAmount,
      name: this.payment.name,
      email: this.payment.email,
      phone: this.payment.phone,
    });
    }
   

  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const payment = this.createFromForm();
    sessionStorage.setItem("payment",JSON.stringify(payment));
    // if (payment.id !== undefined) {
    //   this.subscribeToSaveResponse(this.paymentService.update(payment));
    // } else {
    //   this.subscribeToSaveResponse(this.paymentService.create(payment));
    // }
  }

  clear(): void {
    sessionStorage.removeItem("payment");
    this.editForm.patchValue({
      id: null,
      cik: null,
      ccc: null,
      paymentAmount: null,
      name: null,
      email: null,
      phone: null,
    });

  }

  onKey(event: any): void { // without type info
     this.value = event.target.value;
     console.log(this.value);
     console.log(Number(this.value));
     console.log(String(Number(this.value)).length);

     this.value = String(Number(this.value));
    this.length = String(Number(this.value)).length;
    if (this.length === 10) {
      this.maxlength = 10;
    } else {
      this.maxlength = 11;
    }
    if (this.length <= 10) {
       
      for (let index = 0; index < 10-this.length; index++) {
        this.value = "0".concat(this.value);  
      }
      
      this.editForm.patchValue({
        cik: this.value,
      });
     }
    
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

  protected updateForm(payment: IPayment): void {
    this.editForm.patchValue({
      id: payment.id,
      cik: payment.cik,
      ccc: payment.ccc,
      paymentAmount: payment.paymentAmount,
      name: payment.name,
      email: payment.email,
      phone: payment.phone,
    });
  }

  protected createFromForm(): IPayment {
    return {
      ...new Payment(),
      id: this.editForm.get(['id'])!.value,
      cik: this.editForm.get(['cik'])!.value,
      ccc: this.editForm.get(['ccc'])!.value,
      paymentAmount: this.editForm.get(['paymentAmount'])!.value,
      name: this.editForm.get(['name'])!.value,
      email: this.editForm.get(['email'])!.value,
      phone: this.editForm.get(['phone'])!.value,
    };
  }
}
