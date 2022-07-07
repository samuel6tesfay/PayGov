export interface IPayment {
  id?: number;
  cik?: string;
  ccc?: string;
  paymentAmount?: string;
  name?: string;
  email?: string;
  phone?: string;
  approvalStatus: string;

}

export class Payment implements IPayment {
  constructor(
    public id?: number,
    public cik?: string,
    public ccc?: string,
    public paymentAmount?: string,
    public name?: string,
    public email?: string,
    public phone?: string,
    public approvalStatus: string = "Decline"

  ) {}
}

export function getPaymentIdentifier(payment: IPayment): number | undefined {
  return payment.id;
}
