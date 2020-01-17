import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MagazineService {

  registrationBaseUrl = '/api/magazine/';

  constructor(private http: HttpClient) { }

  startProcess(username) {
    return this.http.get(this.registrationBaseUrl.concat(username));
  }

  submitForm(taskId, value) {
    return this.http.post(this.registrationBaseUrl.concat(taskId), value);
  }

}
