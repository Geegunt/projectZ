# Интеграция с Angular фронтендом

## Текущий статус

✅ **CORS настроен для работы с Angular**

Проект готов к работе с Angular фронтендом. CORS конфигурация позволяет:
- Angular dev server на `http://localhost:4200`
- Альтернативные порты для разработки
- Отправку JWT токенов в заголовках
- Все необходимые HTTP методы (GET, POST, PUT, DELETE, OPTIONS)

---

## Конфигурация CORS

### Разрешенные origins:
- `http://localhost:4200` - стандартный порт Angular dev server
- `http://localhost:3000` - альтернативный порт
- `http://127.0.0.1:4200` и `http://127.0.0.1:3000`
- Паттерны `http://localhost:*` и `http://127.0.0.1:*` для гибкости

### Разрешенные методы:
- GET, POST, PUT, DELETE, OPTIONS, PATCH

### Разрешенные заголовки:
- `Authorization` - для JWT токенов
- `Content-Type` - для JSON данных
- `X-Requested-With`, `Accept`, `Origin` - стандартные заголовки
- `Access-Control-Request-Method`, `Access-Control-Request-Headers` - для preflight

### Exposed headers:
- `Authorization` - клиент может читать заголовок авторизации
- `Content-Type`
- `X-Total-Count` - для пагинации (если понадобится)

### Credentials:
- `allowCredentials: true` - разрешена отправка credentials (JWT токены)

---

## Настройка Angular приложения

### 1. Создание Angular проекта (если еще не создан)

```bash
# Установите Angular CLI (если еще не установлен)
npm install -g @angular/cli

# Создайте новый проект
ng new volunteer-platform-frontend

# Перейдите в директорию проекта
cd volunteer-platform-frontend
```

### 2. Настройка HTTP клиента

#### 2.1. Создайте сервис для работы с API

```bash
ng generate service services/api
ng generate service services/auth
```

#### 2.2. Создайте файл `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'  // URL вашего Spring Boot приложения
};
```

#### 2.3. Создайте HTTP Interceptor для добавления JWT токена:

```bash
ng generate interceptor interceptors/auth
```

**Файл `src/app/interceptors/auth.interceptor.ts`:**

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getAccessToken();
    
    if (token) {
      const cloned = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
      return next.handle(cloned);
    }
    
    return next.handle(req);
  }
}
```

#### 2.4. Зарегистрируйте interceptor в `app.config.ts` (Angular 17+) или `app.module.ts`:

**Для Angular 17+ (`app.config.ts`):**

```typescript
import { ApplicationConfig, provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor])
    )
  ]
};
```

**Для Angular < 17 (`app.module.ts`):**

```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppComponent } from './app.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  declarations: [AppComponent],
  imports: [BrowserModule, HttpClientModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

### 3. Создание сервисов

#### 3.1. Auth Service (`src/app/services/auth.service.ts`):

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../environments/environment';

export interface LoginRequest {
  login: string;
  password: string;
}

export interface RegisterRequest {
  login: string;
  password: string;
  fullName?: string;
  personalEmail?: string;
  maiEmail?: string;
  institute?: string;
  studentGroup?: string;
  birthDate?: string;
  clothingSize?: string;
  social?: { [key: string]: string };
  contactEmail?: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private accessTokenKey = 'accessToken';
  private refreshTokenKey = 'refreshToken';
  
  private currentUserSubject = new BehaviorSubject<string | null>(this.getAccessToken());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  register(request: RegisterRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/signup`, request)
      .pipe(
        tap(response => this.saveTokens(response))
      );
  }

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/login`, request)
      .pipe(
        tap(response => this.saveTokens(response))
      );
  }

  refreshToken(): Observable<TokenResponse> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    
    return this.http.post<TokenResponse>(`${this.apiUrl}/refresh`, { refreshToken })
      .pipe(
        tap(response => this.saveTokens(response))
      );
  }

  logout(): Observable<void> {
    const refreshToken = this.getRefreshToken();
    if (refreshToken) {
      this.http.post<void>(`${this.apiUrl}/logout`, { refreshToken }).subscribe();
    }
    this.clearTokens();
    return new Observable(observer => {
      observer.next();
      observer.complete();
    });
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.accessTokenKey);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.refreshTokenKey);
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  private saveTokens(response: TokenResponse): void {
    localStorage.setItem(this.accessTokenKey, response.accessToken);
    localStorage.setItem(this.refreshTokenKey, response.refreshToken);
    this.currentUserSubject.next(response.accessToken);
  }

  private clearTokens(): void {
    localStorage.removeItem(this.accessTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    this.currentUserSubject.next(null);
  }
}
```

#### 3.2. Profile Service (`src/app/services/profile.service.ts`):

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ProfileResponse {
  id: number;
  login: string;
  fullName: string;
  personalEmail: string;
  maiEmail: string | null;
  institute: string;
  studentGroup: string;
  birthDate: string | null;
  clothingSize: string | null;
  social: {
    telegram: string | null;
    vk: string | null;
  };
  contactEmail: string | null;
  role: string;
}

export interface ProfileUpdateRequest {
  fullName?: string;
  personalEmail?: string;
  maiEmail?: string;
  institute?: string;
  studentGroup?: string;
  birthDate?: string;
  clothingSize?: string;
  social?: { [key: string]: string };
  contactEmail?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private apiUrl = `${environment.apiUrl}/profile`;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(`${this.apiUrl}/me`);
  }

  updateProfile(request: ProfileUpdateRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(`${this.apiUrl}/me`, request);
  }
}
```

### 4. Пример компонента для входа

**Файл `src/app/components/login/login.component.ts`:**

```typescript
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      login: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.error = null;
      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.router.navigate(['/profile']);
        },
        error: (err) => {
          this.error = err.error?.message || 'Ошибка входа';
        }
      });
    }
  }
}
```

**Файл `src/app/components/login/login.component.html`:**

```html
<div class="login-container">
  <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
    <h2>Вход</h2>
    
    <div *ngIf="error" class="error-message">
      {{ error }}
    </div>
    
    <div class="form-group">
      <label for="login">Логин:</label>
      <input 
        id="login" 
        type="text" 
        formControlName="login"
        [class.error]="loginForm.get('login')?.invalid && loginForm.get('login')?.touched">
      <div *ngIf="loginForm.get('login')?.invalid && loginForm.get('login')?.touched" 
           class="error-text">
        Логин обязателен
      </div>
    </div>
    
    <div class="form-group">
      <label for="password">Пароль:</label>
      <input 
        id="password" 
        type="password" 
        formControlName="password"
        [class.error]="loginForm.get('password')?.invalid && loginForm.get('password')?.touched">
      <div *ngIf="loginForm.get('password')?.invalid && loginForm.get('password')?.touched" 
           class="error-text">
        Пароль обязателен
      </div>
    </div>
    
    <button type="submit" [disabled]="loginForm.invalid">
      Войти
    </button>
  </form>
</div>
```

### 5. Пример компонента профиля

**Файл `src/app/components/profile/profile.component.ts`:**

```typescript
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ProfileService, ProfileResponse } from '../../services/profile.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profile: ProfileResponse | null = null;
  profileForm: FormGroup;
  loading = false;
  error: string | null = null;

  constructor(
    private profileService: ProfileService,
    private fb: FormBuilder
  ) {
    this.profileForm = this.fb.group({
      fullName: [''],
      personalEmail: [''],
      maiEmail: [''],
      institute: [''],
      studentGroup: [''],
      birthDate: [''],
      clothingSize: [''],
      contactEmail: ['']
    });
  }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.loading = true;
    this.profileService.getProfile().subscribe({
      next: (profile) => {
        this.profile = profile;
        this.profileForm.patchValue(profile);
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Ошибка загрузки профиля';
        this.loading = false;
      }
    });
  }

  updateProfile(): void {
    if (this.profileForm.valid) {
      this.loading = true;
      this.profileService.updateProfile(this.profileForm.value).subscribe({
        next: (profile) => {
          this.profile = profile;
          this.loading = false;
          alert('Профиль обновлен!');
        },
        error: (err) => {
          this.error = err.error?.message || 'Ошибка обновления профиля';
          this.loading = false;
        }
      });
    }
  }
}
```

---

## Тестирование интеграции

### 1. Запустите Spring Boot приложение

```bash
cd /Users/glebgrigorev/Desktop/programming/projectEpsilan/user-info-svc
./gradlew bootRun
```

### 2. Запустите Angular приложение

```bash
cd /path/to/your/angular/project
ng serve
```

Angular приложение будет доступно на `http://localhost:4200`

### 3. Проверка CORS

Откройте консоль браузера (F12) и выполните:

```javascript
fetch('http://localhost:8080/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    login: 'testuser',
    password: 'testpassword'
  })
})
.then(response => response.json())
.then(data => console.log('Success:', data))
.catch(error => console.error('Error:', error));
```

Если CORS настроен правильно, вы не увидите ошибок CORS в консоли.

---

## Обработка ошибок

### Ошибка 401 Unauthorized

Если токен истек, нужно обновить его:

```typescript
// В AuthInterceptor
intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
  return next.handle(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && this.authService.getRefreshToken()) {
        return this.authService.refreshToken().pipe(
          switchMap((tokens) => {
            const cloned = req.clone({
              headers: req.headers.set('Authorization', `Bearer ${tokens.accessToken}`)
            });
            return next.handle(cloned);
          })
        );
      }
      return throwError(() => error);
    })
  );
}
```

---

## Production настройки

Для production измените `environment.prod.ts`:

```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-api-domain.com'  // URL вашего production API
};
```

И обновите CORS в `SecurityConfig.java`:

```java
config.setAllowedOrigins(List.of(
    "https://your-frontend-domain.com"
));
```

---

## Полезные ссылки

- [Angular HTTP Client](https://angular.io/guide/http)
- [Angular Interceptors](https://angular.io/guide/http-intercept-requests-and-responses)
- [CORS в Spring Boot](https://spring.io/guides/gs/rest-service-cors/)

---

## Резюме

✅ CORS настроен для работы с Angular
✅ Все необходимые методы и заголовки разрешены
✅ JWT токены могут быть отправлены и получены
✅ Готов пример кода для Angular приложения

Проект полностью готов к интеграции с Angular фронтендом!

