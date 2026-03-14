# 📚 BookTrack

Aplicación web para el seguimiento personal de libros leídos, desarrollada con **Spring Boot + Thymeleaf + Spring Security**.

---

## 🚀 Estado actual (Entrega 1)

En esta primera entrega está implementado el sistema de **autenticación completo**:

- Pantalla de login personalizada
- Protección de rutas con Spring Security
- Cifrado de contraseñas con BCrypt
- Página de bienvenida `/home` (Resumen personal)
- Cierre de sesión

> Las funcionalidades de biblioteca, estadísticas y seguimiento de lectura se irán añadiendo en las siguientes entregas.

---

## 🛠️ Tecnologías utilizadas

| Tecnología       | Versión  | Uso                        |
|------------------|----------|----------------------------|
| Java             | 17       | Lenguaje principal         |
| Spring Boot      | 3.4.0    | Framework principal        |
| Spring Security  | 6.x      | Autenticación y seguridad  |
| Thymeleaf        | 3.x      | Motor de plantillas HTML   |
| Maven            | 3.x      | Gestión de dependencias    |

---

## ▶️ Cómo ejecutar el proyecto

### Requisitos previos

- Java 17 o superior instalado
- Maven instalado (o usar el wrapper `./mvnw` incluido)

### Pasos

**1. Clona el repositorio:**
```bash
git clone https://github.com/tu-usuario/booktrack.git
cd booktrack
```

**2. Ejecuta la aplicación:**
```bash
./mvnw spring-boot:run
```
O si tienes Maven instalado:
```bash
mvn spring-boot:run
```

**3. Abre el navegador en:**
```
http://localhost:8080/login
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
src/
├── main/
│   ├── java/es/colegiocalasanz/booktrack/
│   │   ├── BooktrackApplication.java       # Clase principal
│   │   ├── controller/
│   │   │   └── HomeController.java         # Controlador de rutas
│   │   └── security/
│   │       └── SecurityConfig.java         # Configuración de seguridad
│   └── resources/
│       ├── templates/
│       │   ├── login.html                  # Pantalla de login
│       │   └── home.html                   # Página de bienvenida
│       └── application.properties
```





