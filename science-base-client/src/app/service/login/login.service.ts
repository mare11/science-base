import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  storageKey = 'user';
  usersBaseUrl = '/api/users/';
  onSubject = new Subject<{ key: string, value: any }>();

  constructor(private http: HttpClient) {
    if (localStorage.getItem(this.storageKey)) {
      this.setSubject();
    }
  }

  loginUser(credentials) {
    return this.http.post(this.usersBaseUrl.concat('/login'), credentials);
  }

  getLocalStorageItem() {
    return JSON.parse(localStorage.getItem(this.storageKey));
  }

  setLocalStorageItem(userDto) {
    localStorage.setItem(this.storageKey, JSON.stringify(userDto));
    this.setSubject();
  }

  removeLocalStorageItem() {
    localStorage.removeItem(this.storageKey);
    this.onSubject.next({ key: this.storageKey, value: null });
  }

  setSubject() {
    this.onSubject.next({ key: this.storageKey, value: this.getLocalStorageItem() });
  }
}
