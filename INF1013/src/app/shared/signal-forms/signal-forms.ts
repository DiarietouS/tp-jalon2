import { computed, effect, signal, Signal, WritableSignal } from '@angular/core';

/**
 * ============================================================================
 * SIGNAL FORMS (Pattern) - INF1013
 * ============================================================================
 *
 * Les notes de cours décrivent "Signal Forms" avec :
 * - Un DTO typé (ex: LoginDTO)
 * - Un modèle signal: signal<LoginDTO>({...})
 * - Un form(model, schema) qui expose des formFields
 * - Chaque formField expose des signaux: invalid(), errors(), pending()...
 * - Le template peut binder: <input [formField]="loginForm.email" />
 *
 * Angular évolue vite et les APIs peuvent différer selon les versions.
 * Ce fichier implémente un **pattern** minimaliste qui correspond aux concepts
 * du cours, sans dépendre d'une API expérimentale.
 */

export type ValidationError = { key: string; message: string };

export interface FieldState<T> {
  value: Signal<T>;
  touched: Signal<boolean>;
  dirty: Signal<boolean>;
  pending: Signal<boolean>; // réservé pour validations async
  errors: Signal<ValidationError[]>;
  invalid: Signal<boolean>;
  valid: Signal<boolean>;
  setValue: (v: T) => void;
  markTouched: () => void;
}

export interface FormState {
  valid: Signal<boolean>;
  invalid: Signal<boolean>;
}

export type SchemaPath<T extends object> = {
  [K in keyof T]-?: K;
};

type ValidatorFn<T> = (value: T) => ValidationError | null;

export interface FormRef<T extends object> {
  /** Lecture de l'état global: loginForm().valid() */
  (): FormState;
  /** Champs dynamiques: loginForm.email(), loginForm.password() */
  [key: string]: any;
}

/**
 * Helpers de validation (sync)
 */
export function required(path: any, opts: { message: string }) {
  return {
    path: String(path),
    fn: (v: any) => (String(v ?? '').trim() ? null : { key: 'required', message: opts.message }),
  };
}

export function email(path: any, opts: { message: string }) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return {
    path: String(path),
    fn: (v: any) => (re.test(String(v ?? '').trim()) ? null : { key: 'email', message: opts.message }),
  };
}

export function minLength(path: any, len: number, opts: { message: string }) {
  return {
    path: String(path),
    fn: (v: any) => (String(v ?? '').length >= len ? null : { key: 'minLength', message: opts.message }),
  };
}

/**
 * Factory: crée un form() à partir d'un modèle signal.
 */
export function form<T extends Record<string, any>>(
  model: WritableSignal<T>,
  schemaCb?: (schema: SchemaPath<T>) => Array<{ path: string; fn: ValidatorFn<any> }> | void,
): FormRef<T> {
  const validatorsByField: Record<string, ValidatorFn<any>[]> = {};

  // Proxy "schemaPath" : schema.email => 'email'
  const schemaPath = new Proxy({} as any, {
    get(_t, prop) {
      return String(prop);
    },
  }) as SchemaPath<T>;

  // Collecte des règles
  const rules: Array<{ path: string; fn: ValidatorFn<any> }> = [];
  const ret = schemaCb?.(schemaPath);
  if (Array.isArray(ret)) rules.push(...ret);

  for (const r of rules) {
    validatorsByField[r.path] ??= [];
    validatorsByField[r.path].push(r.fn);
  }

  const fieldStates: Record<string, FieldState<any>> = {};
  for (const key of Object.keys(model())) {
    const touched = signal(false);
    const dirty = signal(false);
    const pending = signal(false);
    const errorsSig = signal<ValidationError[]>([]);

    const value = computed(() => model()[key]);
    const invalid = computed(() => errorsSig().length > 0);
    const valid = computed(() => !invalid());

    effect(() => {
      const v = value();
      const validators = validatorsByField[key] ?? [];
      const errs: ValidationError[] = [];
      for (const fn of validators) {
        const e = fn(v);
        if (e) errs.push(e);
      }
      errorsSig.set(errs);
    });

    fieldStates[key] = {
      value,
      touched: touched.asReadonly(),
      dirty: dirty.asReadonly(),
      pending: pending.asReadonly(),
      errors: errorsSig.asReadonly(),
      invalid,
      valid,
      setValue: (v: any) => {
        dirty.set(true);
        model.update((old) => ({ ...old, [key]: v }));
      },
      markTouched: () => touched.set(true),
    };
  }

  const formValid = computed(() => Object.values(fieldStates).every((f) => f.valid()));
  const formState: FormState = {
    valid: formValid,
    invalid: computed(() => !formValid()),
  };

  const formFn: any = () => formState;
  for (const [k, fs] of Object.entries(fieldStates)) {
    formFn[k] = () => fs;
  }

  return formFn as FormRef<T>;
}
