import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {VerificationComponent} from './component/verification/verification.component';
import {HomepageComponent} from './component/homepage/homepage.component';
import {MagazineOverviewComponent} from './component/magazine-overview/magazine-overview.component';

const routes: Routes = [
  {path: '', component: HomepageComponent},
  {path: 'magazines/:name', component: MagazineOverviewComponent},
  {path: 'verify/:code', component: VerificationComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
