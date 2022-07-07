import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPayment, getPaymentIdentifier } from '../payment.model';
import { IHostedCheckout } from "../hostedCheckout.model";

export type EntityResponseType = HttpResponse<IPayment>;
export type EntityArrayResponseType = HttpResponse<IPayment[]>;

@Injectable({ providedIn: 'root' })
export class PaymentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/payments');
  protected kafka = this.applicationConfigService.getEndpointFor('api/kafka');
  protected kafkadmin = this.applicationConfigService.getEndpointFor('api/kafkadmin');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(payment: IPayment): Observable<HttpResponse<IPayment>> {
    return this.http.post<IPayment>(`${this.resourceUrl}`, payment, { observe: 'response' });
  }

  kafkaQueue(payment: IPayment): Observable<HttpResponse<any>> {
    return this.http.post<IPayment>(`${this.kafka}`, payment, { observe: 'response' });
  }

  kafkaAdmin(): Observable<IPayment[]> {
    return this.http.get<IPayment[]>(`${this.kafkadmin}`);
  }

  getPaypalToken(payment: IPayment): Observable<HttpResponse<any>> {
    return this.http.post<any>(`${this.resourceUrl}/paypal`, payment , {  observe: 'response' });
  }

  update(payment: IPayment): Observable<EntityResponseType> {
    return this.http.put<IPayment>(`${this.resourceUrl}/${getPaymentIdentifier(payment) as number}`, payment, { observe: 'response' });
  }

  partialUpdate(payment: IPayment): Observable<EntityResponseType> {
    return this.http.patch<IPayment>(`${this.resourceUrl}/${getPaymentIdentifier(payment) as number}`, payment, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPayment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<HttpResponse<EntityArrayResponseType>> {
    const options = createRequestOption(req);
    return this.http.get<EntityArrayResponseType>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTransactionId(req?:any): Observable<HttpResponse<string>> {
     const options = createRequestOption(req);
    return this.http.get<string>(`${this.resourceUrl}/mockbin`, { params: options, observe: 'response' });
  }


  addPaymentToCollectionIfMissing(paymentCollection: IPayment[], ...paymentsToCheck: (IPayment | null | undefined)[]): IPayment[] {
    const payments: IPayment[] = paymentsToCheck.filter(isPresent);
    if (payments.length > 0) {
      const paymentCollectionIdentifiers = paymentCollection.map(paymentItem => getPaymentIdentifier(paymentItem)!);
      const paymentsToAdd = payments.filter(paymentItem => {
        const paymentIdentifier = getPaymentIdentifier(paymentItem);
        if (paymentIdentifier == null || paymentCollectionIdentifiers.includes(paymentIdentifier)) {
          return false;
        }
        paymentCollectionIdentifiers.push(paymentIdentifier);
        return true;
      });
      return [...paymentsToAdd, ...paymentCollection];
    }
    return paymentCollection;
  }
}
