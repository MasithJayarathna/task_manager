import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/auth/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})

export class NavbarComponent implements OnInit {
  isLoggedIn: boolean = false;
  username: string | null = null;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe(isAuth => {
      this.isLoggedIn = isAuth;
      this.username = isAuth ? this.authService.getUsername() : null;
    });

    this.authService.isAuthenticated().subscribe();
  }

  logout(): void {
    this.authService.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }

  isLogged(): boolean {
    return this.isLoggedIn;
  }
}