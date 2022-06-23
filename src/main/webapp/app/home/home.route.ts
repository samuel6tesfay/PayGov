import { Route } from '@angular/router';

// import { HomeComponent } from './home.component';
import {PaymentUpdateComponent} from '../entities/payment/update/payment-update.component'

export const HOME_ROUTE: Route = {
  path: '',
  component: PaymentUpdateComponent,
  data: {
    // pageTitle: 'Welcome, Java Hipster!',
  },
};
