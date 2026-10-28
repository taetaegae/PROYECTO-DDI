# PROYECTO-DDI · AppWear

Aplicación para wearables (Wear OS) con una app complementaria para el teléfono, desarrollada para la materia **DDI**.

> **Caso de estudio:** por definir.

## Estructura del proyecto

| Módulo | Descripción |
|---|---|
| `mobile/` | App para el teléfono (Kotlin + Jetpack Compose). |
| `wear/` | App para el reloj Wear OS (Kotlin + Compose for Wear OS). |

Ambos módulos comparten el `applicationId` **`com.appw.appwear`**. Es obligatorio para que el teléfono y el reloj se comuniquen mediante la **Wearable Data Layer API** (`play-services-wearable`). No lo cambies en un solo módulo.

## Requisitos

- Android Studio (versión reciente) con el SDK de **Wear OS** instalado.
- Gradle 9.6 con JDK 25 (definido en `gradle/gradle-daemon-jvm.properties`).
- `minSdk 30` · `targetSdk 37`.

## Cómo ejecutarlo

1. Clona el repositorio:
   ```bash
   git clone https://github.com/taetaegae/PROYECTO-DDI.git
   ```
2. En Android Studio: **File → Open** y selecciona la carpeta `PROYECTO-DDI`.
3. Espera a que termine el **Gradle sync**.
4. Crea los emuladores en **Device Manager**:
   - Un teléfono con imagen **Google Play** (API 30 o superior).
   - Un reloj **Wear OS**.
5. Vincula el reloj con el teléfono: en Device Manager, menú ⋮ del emulador Wear OS → **Pair Wearable**.
6. Ejecuta la configuración **mobile** en el teléfono y **wear** en el reloj.

## Flujo de trabajo con Git

- `main` contiene solo código revisado; no se hacen commits directos.
- Cada funcionalidad se trabaja en su propia rama, por ejemplo `feature/app-reloj` o `feature/vinculacion`.
- Los cambios se integran a `main` mediante un **Pull Request**.
- La carpeta `.idea/` y `local.properties` no se suben al repositorio; cada quien los genera al abrir el proyecto.

## Integrantes 

- Bazaldua Hernandez Juan Miguel 
- Vasquez Flores Andrea
- Torres Luna Jorge Hummberto