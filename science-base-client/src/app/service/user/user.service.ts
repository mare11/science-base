import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  usersBaseUrl = '/api/users/';

  constructor(private http: HttpClient) { }

  getUserTasks(username) {
    return this.http.get(this.usersBaseUrl.concat(username));
  }
}
