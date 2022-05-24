import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {catchError, Observable, throwError} from "rxjs";
import {Router} from "@angular/router";
import {AuthService} from "../_services/auth.service";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor{


  constructor(private authService: AuthService, private router:Router) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(catchError(err => {
      if (err.status==401){
        this.authService.logout();
        this.router.navigate(["login"]);
      }
      if (err.status==403){
        this.router.navigate([""]);
      }
      const error = err.statusText;
      return throwError(error) ;
    }))
  }



}
