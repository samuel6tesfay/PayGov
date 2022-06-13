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
  pay: any;
  cik:any

  editForm = this.fb.group({
    id: [],
    cik: [null, [Validators.required]],
    ccc: [null, [Validators.required]],
    paymentAmount: [null, [Validators.required]],
    name: [null, [Validators.required]],
    email: [null, [Validators.required]],
    phone: [null, [Validators.required]],
  });

  constructor(protected paymentService: PaymentService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder,protected modalService: NgbModal,private router:Router) {}

  ngOnInit(): void {
    this.pay = sessionStorage.getItem("payment");
    this.payment = JSON.parse(this.pay);

    this.activatedRoute.data.subscribe(({ payment }) => {
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
