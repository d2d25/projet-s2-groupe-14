import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {UserService} from "../../../_services/user.service";
import {User} from "../../../_model/user.model";
import {GroupService} from "../../../_services/group.service";
import {EmargementService} from "../../../_services/emargement.service";
import {Emargement} from "../../../_model/emargement.model";

@Component({
  selector: 'app-emargement-edit',
  templateUrl: './emargement-edit.component.html',
  styleUrls: ['./emargement-edit.component.css']
})
export class EmargementEditComponent implements OnInit {

  id:string = "";
  isSuccessful = false;
  isSignUpFailed = false;
  errorMessage = "";
  // @ts-ignore
  groupName: string;
  // @ts-ignore
  subGroupName:string;
  form: any = {
    teacher: User,
    subject: null,
    beginsAt: null,
    endAt: null,
    idGroup: null,
    idSubGroup: null,
    emargements: null,
    isValidate: null,
  };

  constructor(private groupService: GroupService,
              private route: ActivatedRoute,
              private router: Router,
              private userService: UserService,
              private emargementService:EmargementService) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];

    this.emargementService.getById(this.id).subscribe( {
      next: data => {
        this.form = data
        this.form.emargements.sort((a:Emargement,b:Emargement)=>(a.user.lastName<b.user.lastName ? -1:1))
        this.groupName = data.nameGroup
        this.subGroupName = data.nameSubGroup
        console.log(this.form.emargements[0])
      },
      error: err => {
        this.errorMessage = err.error.message;
      }
      });
  }

  switchValidate() {
    this.form.isValidate = !this.form.isValidate;
  }

  onSubmit() {
    this.emargementService.edit(this.id, this.form.subject, this.form.emargements, this.form.isValidate).subscribe({
      next: () => {
        this.isSuccessful = true;
        this.isSignUpFailed = false;
        this.router.navigate(['emargement-list']);
      },
      error: err => {
        this.errorMessage = err.message;
        this.isSignUpFailed = true;
      }
    });  }


}
