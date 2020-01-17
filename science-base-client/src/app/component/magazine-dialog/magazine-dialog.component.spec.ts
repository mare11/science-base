import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MagazineDialogComponent } from './magazine-dialog.component';

describe('MagazineDialogComponent', () => {
  let component: MagazineDialogComponent;
  let fixture: ComponentFixture<MagazineDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MagazineDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MagazineDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
