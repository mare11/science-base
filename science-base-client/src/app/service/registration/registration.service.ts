import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  registrationBaseUrl = '/api/registration/';

  constructor(private http: HttpClient) { }

  startProcess() {
    return this.http.get(this.registrationBaseUrl);
  }

  submitForm(taskId, value) {
    return this.http.post(this.registrationBaseUrl.concat(taskId), value);
  }

  verifyUser(code) {
    return this.http.put(this.registrationBaseUrl.concat('verify/').concat(code), null);
  }
}
