# FinanzasClaras

Aplicación nativa Android de finanzas personales construida con Kotlin, Jetpack Compose y Clean Architecture.

## Tecnologías

- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose + Material Design 3
- **Arquitectura:** MVI + Clean Architecture
- **Base de datos local:** Room (SQLite)
- **Sincronización:** Firebase Firestore + Firebase Auth
- **Inyección de dependencias:** Hilt
- **Navegación:** Jetpack Navigation Component
- **Gráficos:** Vico (Compose Charts)
- **Tareas en segundo plano:** WorkManager

## Requisitos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Gradle 8.5
- Min SDK 26 (Android 8.0)
- Target SDK 34

## Configuración de Firebase

1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Crea un nuevo proyecto o selecciona uno existente
3. Agrega una aplicación Android con el package name `com.finanzasclaras.app`
4. Descarga el archivo `google-services.json` y colócalo en la carpeta `app/`
5. Habilita los siguientes servicios:
   - **Authentication** → Método de inicio de sesión: Email/Password
   - **Firestore Database** → Crear base de datos en modo producción
6. Aplica las reglas de seguridad del archivo `firebase.rules` en la consola de Firestore

## Estructura de colecciones en Firestore

```
users/
  {userId}/
    transactions/
      {transactionId}/
        - id: string
        - categoryId: string
        - amount: number
        - currency: string
        - amountInBase: number
        - type: string ("income" | "expense")
        - note: string
        - date: timestamp
        - createdAt: timestamp
        - updatedAt: timestamp
        - deleted: boolean

    saving_goals/
      {goalId}/
        - id: string
        - name: string
        - targetAmount: number
        - currentAmount: number
        - currency: string
        - deadlineDate: timestamp
        - completed: boolean

    saving_contributions/
      {contributionId}/
        - id: string
        - goalId: string
        - amount: number
        - currency: string
        - date: timestamp
        - note: string

    investments/
      {investmentId}/
        - id: string
        - name: string
        - type: string
        - amountInvested: number
        - currentValue: number
        - currency: string
        - purchaseDate: timestamp
        - notes: string
```

## Compilación

```bash
# Limpiar y compilar
./gradlew clean assembleDebug

# Ejecutar tests
./gradlew test

# Ejecutar lint
./gradlew lint
```

## Instalación

1. Clona el repositorio
2. Agrega tu archivo `google-services.json` en `app/`
3. Abre el proyecto en Android Studio
4. Sincroniza Gradle
5. Ejecuta en un dispositivo o emulador

## Características

- ✅ Autenticación con Firebase (email/contraseña)
- ✅ Modo offline con sincronización automática
- ✅ Registro de ingresos y gastos
- ✅ 14 categorías predefinidas
- ✅ Múltiples monedas con conversión automática
- ✅ Metas de ahorro con barra de progreso
- ✅ Registro de inversiones con cálculo de ganancias/pérdidas
- ✅ Análisis financiero con gráficos
- ✅ Alertas y notificaciones
- ✅ Modo oscuro
- ✅ Soporte español/inglés
- ✅ Onboarding interactivo

## Licencia

MIT
