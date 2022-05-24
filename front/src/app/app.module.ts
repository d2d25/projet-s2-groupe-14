import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './component/pages/login/login.component';
import { RegisterComponent } from './component/pages/register/register.component';
import { HomeComponent } from './component/pages/home/home.component';
import { ProfilComponent } from './component/pages/profil/profil.component';
import { BoardAdminComponent } from './component/pages/board-admin/board-admin.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {AuthInterceptor} from "./_helpers/auth.interceptor";
import { UserListComponent } from './component/pages/user-list/user-list.component';
import {ErrorInterceptor} from "./_helpers/error.interceptor";
import { HomeEtudiantComponent } from './component/pages/home-etudiant/home-etudiant.component';
import { HomeProfesseurComponent } from './component/pages/home-professeur/home-professeur.component';
import { EmargementListComponent } from './component/pages/emargement-list/emargement-list.component';
import { NavbarComponent } from './component/subcomponent/navbar/navbar.component';
import { EmargementRegisterComponent } from './component/pages/emargement-register/emargement-register.component';
import { GroupFormComponent } from './component/pages/group-form/group-form.component';
import { GroupListComponent } from './component/pages/group-list/group-list.component';
import { QrCodeComponent } from './component/pages/qr-code/qr-code.component';
import { QRCodeModule } from 'angular2-qrcode';
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {EmargementEditComponent} from "./component/pages/emargement-edit/emargement-edit.component";




@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    ProfilComponent,
    BoardAdminComponent,
    UserListComponent,
    HomeEtudiantComponent,
    HomeProfesseurComponent,
    EmargementListComponent,
    NavbarComponent,
    EmargementRegisterComponent,
    NavbarComponent,
    GroupFormComponent,
    GroupListComponent,
    QrCodeComponent,
    EmargementEditComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    QRCodeModule,
    ZXingScannerModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi:true},
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi:true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
