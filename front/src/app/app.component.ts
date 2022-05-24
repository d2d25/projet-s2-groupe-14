import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {User} from "./_model/user.model";
import {AuthService} from "./_services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'front';

  // @ts-ignore
  currentUser: User;
  // @ts-ignore
  loading: boolean = false;

  constructor(private router: Router, private authService: AuthService) {
    this.currentUser = this.authService.userValue;
  }

  ngOnInit(): void {
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
 }
}
