import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-magazine-overview',
  templateUrl: './magazine-overview.component.html',
  styleUrls: ['./magazine-overview.component.css']
})
export class MagazineOverviewComponent implements OnInit, OnDestroy {

  sub: any;
  magazineName: string;
  texts: [];

  constructor(private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.sub = this.activatedRoute.params.subscribe(params => {
      this.magazineName = params.name;
      console.log('magazine name: ' + this.magazineName);
      // this.registrationService.verifyUser(this.code).subscribe(
      //   () => {
      //     alert('Verified successfully! Please log in to continue browsing the site.');
      //     this.router.navigate(['/']);
      //   },
      //   err => {
      //     this.router.navigate(['/']);
      //   }
      // );
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
