import { Component, OnInit } from '@angular/core';
import {User} from "../../../_model/user.model";
import {AuthService} from "../../../_services/auth.service";
import {Role} from "../../../_model/role";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  // @ts-ignore
  notify: string;
  // @ts-ignore
  currentUser: User;
  role: typeof Role = Role;

  constructor(public authService: AuthService) {
  }

  ngOnInit(): void {
    this.currentUser = this.authService.userValue;

  }

  logout() {
    this.authService.logout();
  }
}
