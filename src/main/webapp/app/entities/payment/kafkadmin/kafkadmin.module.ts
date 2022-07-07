import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'app/shared/shared.module';
import { Kafkadmin_ROUTE } from './kafkadmin.route';
import { KafkadminComponent } from './kafkadmin.component';

@NgModule({
  imports: [SharedModule, FormsModule, RouterModule.forChild([Kafkadmin_ROUTE])],
  declarations: [KafkadminComponent],
})
export class KafkadminModule {}
