import { Injectable, signal, computed, Signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, catchError, tap, finalize } from 'rxjs';




export interface Resource<T> {

  value: Signal<T | undefined>;

  isLoading: Signal<boolean>;

  error: Signal<Error | undefined>;

  reload: () => void;
}


export function createResource<T>(loaderFn: () => Observable<T>): Resource<T> {
  // Signals internes pour l'état
  const _value: WritableSignal<T | undefined> = signal<T | undefined>(undefined);
  const _isLoading: WritableSignal<boolean> = signal<boolean>(false);
  const _error: WritableSignal<Error | undefined> = signal<Error | undefined>(undefined);


  const value: Signal<T | undefined> = _value.asReadonly();
  const isLoading: Signal<boolean> = _isLoading.asReadonly();
  const error: Signal<Error | undefined> = _error.asReadonly();


  const reload = (): void => {
    _isLoading.set(true);
    _error.set(undefined);

    loaderFn().pipe(
      tap(data => _value.set(data)),
      catchError(err => {
        _error.set(err);
        return of(undefined);
      }),
      finalize(() => _isLoading.set(false))
    ).subscribe();
  };

  // Charger automatiquement au création
  reload();

  return { value, isLoading, error, reload };
}


@Injectable({
  providedIn: 'root'
})
export class ResourceExempleService {

  constructor(private http: HttpClient) {}


  getRestaurantsResource(): Resource<any[]> {
    return createResource(() =>
      this.http.get<any[]>('assets/mock/restaurant.json')
    );
  }
}
