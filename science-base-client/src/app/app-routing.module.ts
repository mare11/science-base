import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { VerificationComponent } from './component/verification/verification.component';
import { HomepageComponent } from './component/homepage/homepage.component';

const routes: Routes = [
    { path: '', component: HomepageComponent },
    { path: 'verify/:code', component: VerificationComponent },
    { path: '**', redirectTo: '' }
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }
export const RoutingComponents = [];
