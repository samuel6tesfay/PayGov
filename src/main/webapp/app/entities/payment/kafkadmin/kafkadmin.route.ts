import { Route } from '@angular/router';
import { KafkadminComponent } from './kafkadmin.component';

export const Kafkadmin_ROUTE: Route = {
  path: 'kafkadmin',
  component: KafkadminComponent,
  data: {
    pageTitle: 'Welcome, Kafka-Admin !',
  },
};
