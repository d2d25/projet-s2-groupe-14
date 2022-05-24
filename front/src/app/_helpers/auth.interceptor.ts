import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {AuthService} from "../_services/auth.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {


  constructor(private authService: AuthService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const user = this.authService.userValue;
    const isLoggedIn = user && user.token;

    if (isLoggedIn) {
      request = request.clone(
        {setHeaders: {Authorization: `Bearer ${user.token}`}}
      );
    }
    return next.handle(request);
  }

}

