import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { App } from './app';
import { AuthService } from './core/services/auth.service';
import { RechercheService } from './core/services/recherche.service';

const authServiceStub = {
  deconnexion: () => undefined,
  getUtilisateurCourant: () => null,
  utilisateurCourant: () => null,
};

const rechercheServiceStub = {
  setTermeRecherche: (_terme: string) => undefined,
};

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [
        provideRouter([]),
        provideAnimations(),
        { provide: AuthService, useValue: authServiceStub },
        { provide: RechercheService, useValue: rechercheServiceStub },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render shell components', async () => {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-header')).toBeTruthy();
    expect(compiled.querySelector('router-outlet')).toBeTruthy();
    expect(compiled.querySelector('app-bottom-nav')).toBeTruthy();
  });
});
