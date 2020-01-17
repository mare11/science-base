import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RegistrationService } from 'src/app/service/registration/registration.service';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrls: ['./verification.component.css']
})
export class VerificationComponent implements OnInit, OnDestroy {

  private sub: any;
  private code: string;

  constructor(
    private registrationService: RegistrationService,
    private activatedRoute: ActivatedRoute,
    private router: Router) { }

  ngOnInit() {
    this.sub = this.activatedRoute.params.subscribe(params => {
      this.code = params.code;
      this.registrationService.verifyUser(this.code).subscribe(
        () => {
          alert('Verified successfully! Please log in to continue browsing the site.');
          this.router.navigate(['/']);
        },
        err => {
          this.router.navigate(['/']);
        }
      );
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
