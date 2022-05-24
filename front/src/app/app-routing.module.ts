import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RegisterComponent} from "./component/pages/register/register.component";
import {LoginComponent} from "./component/pages/login/login.component";
import {HomeComponent} from "./component/pages/home/home.component";
import {AuthGuard} from "./_helpers/auth.guard";
import {UserListComponent} from "./component/pages/user-list/user-list.component";
import {HomeEtudiantComponent} from "./component/pages/home-etudiant/home-etudiant.component";
import {HomeProfesseurComponent} from "./component/pages/home-professeur/home-professeur.component";
import {ProfilComponent} from "./component/pages/profil/profil.component";
import {EmargementRegisterComponent} from "./component/pages/emargement-register/emargement-register.component";
import {EmargementListComponent} from "./component/pages/emargement-list/emargement-list.component";
import {GroupListComponent} from "./component/pages/group-list/group-list.component";
import {GroupFormComponent} from "./component/pages/group-form/group-form.component";
import {QrCodeComponent} from "./component/pages/qr-code/qr-code.component";
import {EmargementEditComponent} from "./component/pages/emargement-edit/emargement-edit.component";
import {Role} from "./_model/role";

const routes: Routes = [
  {path: 'register', component: RegisterComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin]}},
  {path: 'user/:id/update', component: RegisterComponent, canActivate: [AuthGuard]},
  {path: 'login', component: LoginComponent},
  {path: '', component: HomeComponent, canActivate:[AuthGuard]},
  {path: 'user-list', component: UserListComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin]}},
  {path: 'home-etudiant', component: HomeEtudiantComponent, canActivate: [AuthGuard], data: {roles: [Role.Student]}},
  {path: 'home-professeur', component:HomeProfesseurComponent, canActivate: [AuthGuard], data: {roles: [Role.Teacher]}},
  {path: 'profil', component:ProfilComponent, canActivate: [AuthGuard]},
  {path: 'user/:id', component:ProfilComponent, canActivate: [AuthGuard]},
  {path: 'emargement', component:EmargementRegisterComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin, Role.Teacher]}},
  {path: 'emargement-list', component:EmargementListComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin, Role.Teacher]}},
  {path: 'group', component:GroupFormComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin]}},
  {path: 'group/:idMain/update', component:GroupFormComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin]}},
  {path: 'group/:idMain/:idSub/update', component:GroupFormComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin]}},
  {path: 'group-list', component:GroupListComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin]}},
  {path: 'group-list/:idMain', component:GroupListComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin]}},
  {path: 'qrCode', component:QrCodeComponent, canActivate: [AuthGuard]},
  {path: 'emargement/:id/update', component:EmargementEditComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin, Role.Teacher]}},
  {path: 'emargement/:id/qrCode', component:QrCodeComponent, canActivate: [AuthGuard], data: {roles: [Role.Admin, Role.Teacher]}},

  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
