import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, map, Observable, of, tap } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';
import {environment} from "../../environments/environment";


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private baseUrl = `${environment.baseUrl}/auth`;
  private tokenKey = environment.tokenKey; //no need of this since the cookies are handling the tokens
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor( private http: HttpClient,
    private cookieService: CookieService,
    private router: Router) {}


    login(data: { username: string; password: string }) {
      return this.http.post(`${this.baseUrl}/login`, data, {
        withCredentials: true,
        responseType: 'text'
      }).pipe(
        tap((username: string) => {
          localStorage.setItem('username', username);
          this.isLoggedInSubject.next(true);
          console.log('Login successful, storing username:', username);

        })
      );
    }


  register(data: { username: string; password: string }) {
    return this.http.post(`${this.baseUrl}/register`, data,{
      withCredentials: true,
      responseType: 'text'
    }).pipe(
      tap((res: string) => {
        console.log('Registration successful: ', res);
        this.router.navigate(['/login']);
      })
    );
  }


  logout() {
    localStorage.removeItem('username');
    return this.http.post(`${this.baseUrl}/logout`, {}, {
      withCredentials: true
    }).pipe(
      tap(() => {
        this.isLoggedInSubject.next(false);
        console.log('User logged out');
      })
    );


  }

  isAuthenticated(): Observable<boolean> {
    return this.http.get<{ username: string; status: string }>(`${this.baseUrl}/check`, {
      withCredentials: true
    }).pipe(
      map(response => {
        const isAuthenticated = response.status === 'authenticated';
        this.isLoggedInSubject.next(isAuthenticated);
        console.log('Authentication status:', isAuthenticated);
        if (isAuthenticated) {
          localStorage.setItem('username', response.username);
        } else {
          localStorage.removeItem('username');
        }
        return isAuthenticated;
      }),
      catchError(error => {
        if (error.status === 401) {
          console.warn('User not authenticated (401)');
        } else {
          console.error('Auth check failed:', error);
        }
        return of(false);
      })
    );
  }

//this will be needed when using the token as a Bearer, but rn i am using it as cookie
  // getTokenHeader(): any {
  //   const authToken = this.cookieService.get(this.tokenKey);
  //   const headers = new HttpHeaders({
  //     'Authorization': `Bearer ${authToken}`
  //   });
  //   return headers
  // }

  getUsername(): string | null {
    const username = localStorage.getItem('username');
    return username ? username : null;
  }
}
