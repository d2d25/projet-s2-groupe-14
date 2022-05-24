import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../../../_services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  form: any = {
    email: null,
    password: null
  };

  isLoginFailed = false;
  errorMessage = '';
  roles: string[] = [];

  constructor(private authService: AuthService, private router: Router) {

  }

  ngOnInit(): void {
    const user = this.authService.userValue;
    if (user && user.token) {
      this.router.navigate([''])
    }
  }

  onSubmit() : void {
    const {email, password} = this.form;
    this.authService.login(email, password).subscribe({
      next: () => {
        this.isLoginFailed = false;
        // @ts-ignore
        this.roles = this.authService.userValue.role;
        this.router.navigate(['']);

      }, error: err => {
        this.errorMessage = err;
        this.isLoginFailed = true;
        this.router.navigate(['']);

      }
    })

  }
}
