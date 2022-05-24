import { Component, OnInit } from '@angular/core';
import {User} from "../../../_model/user.model";
import {Router} from "@angular/router";
import {AuthService} from "../../../_services/auth.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  // @ts-ignore
  currentUser: User;

  constructor(private router: Router, private authService: AuthService) {
    // @ts-ignore
  }

  ngOnInit(): void {
    this.currentUser = this.authService.userValue
  }

}
