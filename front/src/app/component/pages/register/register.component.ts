import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../../_services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../_services/user.service";

interface Role{
  name:string;
  value:string
}

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {


  // @ts-ignore
  isRegistration: boolean;
  id: string = "";
  form: any = {
    email: null,
    password: null,
    firstName: null,
    lastNamge: null,
    numEtu: null,

  };

  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = "";

  // @ts-ignore
  selectedRole: string;
  roles:Role[] = [
    {name: 'Etudiant', value: 'ROLE_STUDENT'},
    {name: 'Professeur', value: 'ROLE_TEACHER'},
    {name: 'Administrateur', value: 'ROLE_ADMIN'}
  ]

  constructor(private authService: AuthService,
              private route: ActivatedRoute,
              private router: Router,
              private userService: UserService,) {}

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.isRegistration = !this.id;

    // this.form = this.formBuilder.group({
    //   email: ['', [Validators.required, Validators.email]],
    //   password: ['', [Validators.minLength(6), this.isRegistration ? Validators.required : Validators.nullValidator]],
    //   firstName: ['', Validators.required],
    //   lastName: ['', Validators.required],
    //   numEtu: ['', Validators.required],
    //   roles: ['', Validators.required],
    // });

    if (!this.isRegistration) {

      this.userService.getById(this.id).subscribe({
        next: data => {
          this.form = data
          this.selectedRole=data.role
          },
          error: err => {
          this.errorMessage = err.error.message;
        }
      });
    }
  }


  get f() { return this.form.controls; }

  onSubmit(): void {
    if (this.selectedRole!="ROLE_STUDENT"){
      this.form.numEtu=null;
    }
    if (this.isRegistration) {
      this.authService.register(this.form.email, this.form.password, this.form.firstName, this.form.lastName, this.selectedRole, this.form.numEtu).subscribe({
        next: data => {
          console.log(data);
          this.isSuccessful = true;
          this.isSignUpFailed = false;
          this.router.navigate(['user-list']);
        },
        error: err => {
          this.errorMessage = err.message;
          this.isSignUpFailed = true;
        }
      });

    } else {
      this.userService.edit(this.id, this.form.email, this.form.firstName, this.form.lastName, this.selectedRole, this.form.numEtu).subscribe({
        next: data => {
          console.log(data);
          this.isSuccessful = true;
          this.isSignUpFailed = false;
          this.router.navigate(['user-list']);
        },
        error: err => {
          this.errorMessage = err.message;
          this.isSignUpFailed = true;
        }
      });
    }
  }


}
