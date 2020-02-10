import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginRegistrationDialogComponent } from './login-registration-dialog.component';

describe('LoginRegistrationDialogComponent', () => {
  let component: LoginRegistrationDialogComponent;
  let fixture: ComponentFixture<LoginRegistrationDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LoginRegistrationDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginRegistrationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
