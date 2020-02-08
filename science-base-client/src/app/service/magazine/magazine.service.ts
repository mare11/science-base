import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class MagazineService {

  magazinesBaseUrl = '/api/magazine/';

  constructor(private http: HttpClient) {
  }

  getAllMagazines() {
    return this.http.get(this.magazinesBaseUrl);
  }

  startProcess(username) {
    return this.http.get(this.magazinesBaseUrl.concat(username));
  }

  submitForm(taskId, value) {
    return this.http.post(this.magazinesBaseUrl.concat(taskId), value);
  }

}
