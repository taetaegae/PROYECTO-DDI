# PROYECTO-DDI · AppWear

Aplicación para wearables (Wear OS) con una app complementaria para el teléfono, desarrollada para la materia **DDI**.

> **Caso de estudio: LevelUp.** Convierte las tareas diarias en misiones de un videojuego. Se crean en el teléfono y se cumplen desde el reloj, con un temporizador de concentración, y cada misión da XP, monedas y racha para subir de nivel.

- [Plan de implementación](docs/PLAN_DE_IMPLEMENTACION.md): reglas del juego, arquitectura y guion de la demo.
- [Roadmap](docs/ROADMAP.md): fases, tareas y estado de cada hito.

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

### Si el reloj no recibe nada del teléfono

Android Studio conecta los emuladores vinculados solo mientras está abierto. Si ejecutas las apps sin Android Studio, o el reloj se queda con datos viejos, conecta los emuladores a mano. Busca sus nombres con `adb devices`. En este ejemplo, el teléfono es `emulator-5556` y el reloj es `emulator-5554`:

```bash
adb -s emulator-5556 forward tcp:5601 tcp:5601
adb -s emulator-5554 reverse tcp:5601 tcp:5601
```

Para comprobar la conexión, este comando debe mostrar `1 connected out of 1`:

```bash
adb -s emulator-5554 shell dumpsys activity service WearableService | grep "connected out of"
```

## Modo demo

En la versión de depuración, **Ajustes** (el engrane de la pantalla de inicio del teléfono) muestra el modo demo para preparar la exposición:

- **Temporizador de 10 segundos:** las misiones con temporizador duran 10 s en el reloj en lugar de sus minutos.
- **Dejar a 10 XP de subir de nivel:** la siguiente misión que completes sube al jugador de nivel.
- **Cargar misiones de ejemplo:** agrega tres misiones. Si se carga dos veces, no se duplican.
- **Forzar sincronización con el reloj:** vuelve a enviar todo el estado al reloj.

Guion de la demo: en el teléfono se crea "Estudiar 25 min" (dificultad Media, "Temporizador del reloj"). En el reloj se abre la misión y se toca **Iniciar · 10 s**. Al terminar, el reloj vibra y muestra **+31 XP** (con la racha en 0) y la subida de nivel, y el teléfono se actualiza.

## Flujo de trabajo con Git

- `main` contiene solo código revisado; no se hacen commits directos.
- Cada funcionalidad se trabaja en su propia rama, por ejemplo `feature/app-reloj` o `feature/vinculacion`.
- Los cambios se integran a `main` mediante un **Pull Request**.
- La carpeta `.idea/` y `local.properties` no se suben al repositorio; cada quien los genera al abrir el proyecto.

## Integrantes 

- Bazaldua Hernandez Juan Miguel 
- Vasquez Flores Andrea
- Torres Luna Jorge Humberto