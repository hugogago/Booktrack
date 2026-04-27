# 📚 BookTrack

Aplicación web para el seguimiento personal de libros leídos, desarrollada con **Spring Boot + Thymeleaf + Spring Security**.

---

## 🚀 Estado actual

La aplicación ya cubre el flujo base descrito en el anteproyecto:

- Registro e inicio de sesión con JWT
- Resumen personal con lecturas del mes, total anual, libros en curso y pendientes
- Biblioteca personal con alta, edición, borrado y cambio de estado de libros
- Detalle del libro con título, autor, género, editorial, páginas, nota personal y comentarios
- Buscador con filtros por estado, autor, género, editorial y texto libre
- Estadísticas de lectura por mes y distribución por géneros
- Objetivo anual configurable con seguimiento de progreso

---

## 🛠️ Tecnologías utilizadas

| Tecnología       | Versión  | Uso                                  |
|------------------|----------|--------------------------------------|
| Java             | 17       | Lenguaje principal                   |
| Spring Boot      | 3.4.0    | API REST                             |
| Spring Security  | 6.x      | Autenticación y seguridad            |
| JWT              | 0.12.6   | Gestión de sesión stateless          |
| Vue              | 3.x      | Interfaz web                         |
| Vite             | 5.x      | Entorno de desarrollo frontend       |
| Maven            | 3.9.6    | Gestión de dependencias backend      |

---

## ▶️ Cómo ejecutar el proyecto

### Requisitos previos

- Java 17 o superior instalado
- Node.js 18 o superior instalado

### Pasos

**1. Ejecuta el backend:**
```bash
cd booktrack
./maven/apache-maven-3.9.6/bin/mvn spring-boot:run
```

El backend queda disponible en:
```
http://localhost:8081
```

**2. Ejecuta el frontend:**
```bash
cd frontend
npm install
npm run dev
```

El frontend queda disponible en:
```
http://localhost:3000
```

---

## 🔐 Credenciales de prueba

| Campo      | Valor   |
|------------|---------|
| Usuario    | `alvaro` |
| Contraseña | `1234`  |

---

## 📁 Estructura del proyecto

```
booktrack/
├── src/main/java/es/colegiocalasanz/booktrack/
│   ├── controller/                         # Endpoints de auth, biblioteca y dashboard
│   ├── dto/                                # DTOs de peticiones y respuestas
│   ├── entity/                             # User, Book y ReadingStatus
│   ├── repository/                         # Persistencia JSON de usuarios y libros
│   ├── security/                           # JWT + configuración de seguridad
│   └── service/                            # Lógica de autenticación y estadísticas
├── src/main/resources/
│   ├── application.properties
│   ├── users.json
│   └── books.json                          # Se genera al guardar libros
frontend/
├── src/App.vue                             # Interfaz principal
├── src/api.js                              # Cliente Axios para la API
└── vite.config.js
```





