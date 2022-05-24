import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {BehaviorSubject, map, Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {User} from "../_model/user.model";
import {Router} from "@angular/router";


const AUTH_URL = environment.apiUrl + '/auth/';
const httpOptions = {
  headers: new HttpHeaders({'Content-Type' : 'application/json'})
};


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userSubject: BehaviorSubject<User>;
  public user: Observable<User>;

  constructor(private router: Router, private http:HttpClient) {
    // @ts-ignore
    this.userSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem('user')));
    this.user = this.userSubject.asObservable();
  }

  public get userValue(): User {
    return this.userSubject.value;
  }

  login(email:string, password:string) : Observable<any> {
    return this.http.post(AUTH_URL + 'signIn', {
      email,
      password
    }, httpOptions).pipe(
      map(user => {
        localStorage.setItem('user', JSON.stringify(user));
        // @ts-ignore
        this.userSubject.next(user);
        return user;
      })
    );
  }

  register(email:string, password:string, firstName:string, lastName:string, role:string, numEtu:string) : Observable<any> {
    return this.http.post(AUTH_URL + 'signUp', {
      email,
      password,
      firstName,
      lastName,
      role,
      numEtu
    }, httpOptions);

  }

  logout() {
    localStorage.removeItem('user');
    // @ts-ignore
    this.userSubject.next(null);
    this.router.navigate(['/login']).then(() => {
      window.location.reload();
    });

  }

}
