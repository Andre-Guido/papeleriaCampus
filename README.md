# Papelería Campus

Aplicación de escritorio en JavaFX para registrar, buscar, actualizar y eliminar los productos de la Papelería Campus. Los datos se guardan en PostgreSQL.

## Versiones utilizadas

| Herramienta | Versión |
|---|---|
| JDK | 21 (Microsoft Build of OpenJDK 21.0.10) |
| JavaFX | 21.0.6 |
| PostgreSQL | 18.6 |
| Maven | 3.8.5 (incluido en el proyecto mediante Maven Wrapper: `mvnw` / `mvnw.cmd`) |
| IDE | IntelliJ IDEA 2026.1 |

Cualquier JDK 21 sirve (por ejemplo, Eclipse Temurin 21). No hace falta instalar Maven ni JavaFX por separado: Maven los descarga al abrir o compilar el proyecto.

## Dependencias

Están declaradas en `pom.xml` y Maven las descarga automáticamente.

| Dependencia | Versión | Uso |
|---|---|---|
| `org.openjfx:javafx-controls` | 21.0.6 | Componentes de la interfaz gráfica |
| `org.openjfx:javafx-fxml` | 21.0.6 | Carga de la vista `papeleria-campus-view.fxml` |
| `org.postgresql:postgresql` | 42.7.13 | Controlador JDBC para conectarse a PostgreSQL |
| `org.junit.jupiter:junit-jupiter-api` y `junit-jupiter-engine` | 5.12.1 | Pruebas unitarias (alcance `test`) |

| Plugin de Maven | Versión | Uso |
|---|---|---|
| `maven-compiler-plugin` | 3.13.0 | Compila el proyecto con Java 21 |
| `javafx-maven-plugin` | 0.0.8 | Ejecuta la aplicación con `mvnw javafx:run` |

## Creación de la base de datos

El script `database/script.sql` crea:

- La base de datos `papeleria_campus`.
- El esquema `catalogos`.
- La tabla `catalogos.productos`, con sus restricciones (código único y en mayúsculas, nombre de 3 a 80 caracteres, categoría Papelería/Tecnología/Accesorios, precio mayor que 0 y stock mayor o igual a 0).
- 12 productos de prueba (P001–P004, T001–T004 y A001–A004).

> **Importante:** el script se ejecuta en dos partes. `CREATE DATABASE` se ejecuta conectado a la base `postgres`, y el resto del script, conectado a la nueva base `papeleria_campus`. Si se ejecuta todo en la misma conexión, el esquema y la tabla se crean en la base equivocada.

### Opción A: pgAdmin 4

1. Abre pgAdmin y conéctate al servidor PostgreSQL 18.
2. Selecciona la base `postgres`, abre **Query Tool** y ejecuta solo esta línea:
   ```sql
   CREATE DATABASE papeleria_campus;
   ```
3. Actualiza la lista de bases (clic derecho en **Databases → Refresh**), selecciona `papeleria_campus` y abre un nuevo **Query Tool**.
4. Copia el contenido de `database/script.sql` desde `CREATE SCHEMA catalogos;` hasta el final y ejecútalo con **F5**.
5. La consulta final del script debe mostrar los 12 productos.

### Opción B: psql (PowerShell)

Desde la carpeta del proyecto:

```powershell
$env:PGCLIENTENCODING = "UTF8"
psql -U postgres -c "CREATE DATABASE papeleria_campus;"
psql -U postgres -d papeleria_campus -f database/script.sql
```

- `PGCLIENTENCODING = "UTF8"` hace que psql lea correctamente las tildes del script ("Papelería", "Tecnología").
- El tercer comando muestra el error `la base de datos «papeleria_campus» ya existe` en la primera línea del script. Es normal: la base ya se creó con el comando anterior y psql continúa con el resto.
- Si `psql` no se reconoce como comando, usa la ruta completa: `& "C:\Program Files\PostgreSQL\18\bin\psql.exe"` en lugar de `psql`.

Para empezar de cero, ejecuta `DROP DATABASE papeleria_campus;` conectado a la base `postgres` y repite los pasos.

## Configuración

La aplicación lee los datos de conexión del archivo `db.properties`, que va en la carpeta principal del proyecto (la misma de `pom.xml`). Ese archivo no está en el repositorio porque contiene la contraseña: `.gitignore` lo excluye.

1. Copia la plantilla `db.example.properties` como `db.properties`:
   ```powershell
   Copy-Item db.example.properties db.properties
   ```
2. Abre `db.properties` y escribe la contraseña de tu usuario de PostgreSQL:
   ```properties
   db.url=jdbc:postgresql://localhost:5432/papeleria_campus
   db.user=postgres
   db.password=tu_contraseña
   ```
   Si tu servidor usa otro puerto, host o usuario, cámbialos también.

El archivo se busca en la carpeta desde la que se ejecuta la aplicación. IntelliJ IDEA usa la carpeta principal del proyecto por defecto. Si no lo encuentra, la aplicación muestra "No se pudo conectar con la base de datos" y la consola indica `No se pudo leer db.properties`.

## Pasos de ejecución

1. Clona el repositorio:
   ```powershell
   git clone https://github.com/Andre-Guido/papeleriaCampus.git
   ```
2. Verifica que el servicio de PostgreSQL esté iniciado y crea la base de datos (sección [Creación de la base de datos](#creación-de-la-base-de-datos)).
3. Crea el archivo `db.properties` (sección [Configuración](#configuración)).
4. Ejecuta la aplicación con una de las dos opciones siguientes.

En ambos casos se abre la ventana **Control y Registro** y la tabla muestra los 12 productos de prueba.

### Opción A: IntelliJ IDEA

1. Abre la carpeta del proyecto (**File → Open**, carpeta que contiene `pom.xml`) y espera a que Maven descargue las dependencias.
2. Revisa que el proyecto use JDK 21 en **File → Project Structure → Project → SDK**.
3. Abre `src/main/java/ni/edu/uam/papeleriacampus/Launcher.java` y ejecútalo con el botón ▶ junto al método `main` (**Run 'Launcher.main()'**).

### Opción B: terminal con Maven Wrapper

No necesita IntelliJ ni Maven instalado, solo un JDK 21. Desde la carpeta del proyecto, en PowerShell:

1. Indica dónde está el JDK 21, si la variable `JAVA_HOME` no está configurada (cambia la ruta por la de tu equipo):
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
   ```
2. Ejecuta la aplicación:
   ```powershell
   .\mvnw.cmd javafx:run
   ```
   La primera vez, Maven descarga las dependencias y puede tardar unos minutos. Al cerrar la ventana, Maven termina con `BUILD SUCCESS`.

Para solo compilar, sin abrir la aplicación: `.\mvnw.cmd compile`.

## Evidencias de pruebas

Las pruebas realizadas y sus capturas de pantalla están en [pruebas.md](pruebas.md):

- Búsqueda y filtros (B1 a B7).
- Validaciones y pruebas de error.
- Excepciones y operaciones fallidas (E1 a E4).

Las imágenes están en la carpeta [`evidencias/`](evidencias/).
