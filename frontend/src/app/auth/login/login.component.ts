import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent{

  form: FormGroup;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }
 
  submit() {
    this.auth.login(this.form.value).subscribe({
      next: () => this.router.navigate(['/tasks']),
      error: err => alert('Login failed')
    });
  }

  onLogin(): void {
    if (this.auth.isAuthenticated()) {
      this.router.navigate(['/tasks']);  
    }
  }

}
