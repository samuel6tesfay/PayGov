export interface IHostedCheckout {
  RETURNMAC?: string;
  hostedCheckoutId?: string;
  partialRedirectUrl?: string;
}

export class HostedCheckout implements IHostedCheckout {
  constructor(public RETURNMAC?: string, public hostedCheckoutId?: string, public partialRedirectUrl?: string) {}
}
