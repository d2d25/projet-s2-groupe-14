import { Component, OnInit } from '@angular/core';
import {UserService} from "../../../_services/user.service";
import {User} from "../../../_model/user.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-profil',
  templateUrl: './profil.component.html',
  styleUrls: ['./profil.component.css']
})
export class ProfilComponent implements OnInit {

  id:string = "";
  canModify = false;
  isEtudiant = true;
  isSuccessful = false;
  isEditFailed = false;
  errorMessage = "";
  isEditMode = false;
  mustMatchError = false;
  // @ts-ignore
  user:User;
  form: any = {
    password: null,
    confirmation: null
  };

  constructor(private userService: UserService, private route: ActivatedRoute, private router: Router) { }

  get f() { return this.form.controls; }

  ngOnInit(): void {
    this.canModify = false;
    if(this.route.snapshot.params['id'] != undefined){
      this.id = this.route.snapshot.params['id'];
      this.userService.getById(this.id).subscribe((user:User) => {
        this.user = user;

        if (this.user.role == "ROLE_TEACHER" || this.user.role == "ROLE_ADMIN")
          this.isEtudiant = false;
      });
      this.userService.current().subscribe((current: User) => {

        if (current.id == this.user.id)
          this.router.navigate(['profil']);
      });

    }else{
      this.userService.current().subscribe((response: User) => {
        this.user = response;
        this.canModify = true;
        if (this.user.role == "ROLE_TEACHER" || this.user.role == "ROLE_ADMIN")
          this.isEtudiant = false;
      });
    }
  }

  onSubmit(): void {
    if (this.form.password==this.form.confirmation){
      this.mustMatchError = false;
      this.userService.editPassword(this.form.password).subscribe({
        next: () => {
          this.isSuccessful = true;
          this.isEditFailed = false;
          this.router.navigate(['profil']);
          this.isEditMode = false;
        },
        error: err => {
          console.log(err.status);
          this.errorMessage = err.message;
          this.isEditFailed = true;
        }
      });
    } else {
      this.mustMatchError = true;
    }
  }

  edit() {
    this.isEditMode = true;
  }

  cancelEdit() {
    this.isEditMode = false;
  }
}
