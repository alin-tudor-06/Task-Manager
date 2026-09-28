# Task Manager API

## Description
A REST API for task management built with Spring Boot, MySQL and JWT authentication. It allows users to create projects, invite members, and manage tasks within a collaborative environment.

## Features

- User registration and login with JWT authentication
- Role-based user model (USER, ADMIN)
- Project management (create, update, delete, list)
- Team collaboration with project members (OWNER, MEMBER roles)
- Task management with status tracking (TODO, DONE)
- Fine-grained authorization (only members can access project data, only owners can modify projects)
- Global exception handling with clear error responses
- Interactive API documentation via Swagger UI

## Technologies Used
| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Programming language |
| Spring Boot | 4.1.0 | Main framework |
| Spring Security | 7.x | Authentication and JWT |
| Spring Data JPA | 4.1.0 | Database access |
| MySQL | 8.x+ | Relational database |
| MySQL Connector/J | 9.7.0 | JDBC driver |
| JJWT | 0.12.6 | JWT generation and validation |
| SpringDoc OpenAPI | 3.1.0 | Interactive API documentation |
| Lombok | 1.18.46 | Boilerplate reduction |
| Maven | 3.9+ | Dependency management |

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/alin-tudor-06/Task-Manager.git
cd Task-Manager
```

---

### 2. Create the MySQL database
Make sure MySQL is running on `localhost:3306`, then create the database:
```sql
CREATE DATABASE taskmanager_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
---

### 3. Configure `application.properties`
The file `src/main/resources/application.properties` is not tracked in Git for security reasons. Create it manually and fill in your credentials:
```properties
spring.application.name=taskmanager

spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager_db?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your-secret-key-at-least-32-characters-long
jwt.expirationMs=86400000

server.port=8080
```
#### Notes:
- `jwt.secret` must be at least 32 characters long (HS256 requirement)
- `jwt.expirationMs` is set to 24 hours (86400000 ms)

---

### 4. Run the application
Use IntelliJ IDEA or run from terminal:
```bash
./mvnw spring-boot:run
```
The application will start on `http://localhost:8080`.

---

### 5. Access Swagger UI
Open in browser:
```text
http://localhost:8080/swagger-ui/index.html
```

### How to Test Protected Endpoints
1. Register a user via POST /api/auth/register
2. Login via POST /api/auth/login to obtain a JWT token
3. Click the Authorize button in Swagger UI (top right)
4. Paste the token (without the Bearer prefix)
5. Click Authorize, then Close
6. You can now test any protected endpoint

## Main Endpoints

### Authentication (`/api/auth`)
| Method | Endpoint   | Description |
|--------|------------|-------------|
| POST	  | `/register`	 | Register a new user |
|POST	|`/login`	|Authenticate and return JWT token|

### Users (`/api/users`)
| Method | Endpoint | Description |
|--------|-------|-------------|
| GET    | `/me` | Get current user profile|
| PUT    | `/me` | Update current user profile|

### Projects (`/api/projects`)
| Method | Endpoint | Description |
|--------|----|-------------|
|POST	| `/` |Create a new project (creator becomes OWNER)|
|GET	|`/`	|List all projects of the current user|
|GET	|`/{projectId}`|	Get project by ID (members only)|
|PUT	|`/{projectId}`|	Update project (OWNER only)|
|DELETE	|`/{projectId}`|	Delete project (OWNER only)|
|GET	|`/{projectId}/members`|	List project members (members only)|
|POST	|`/{projectId}/members`	|Add member to project (OWNER only)|
|DELETE	|`/{projectId}/members/{username}`|	Remove member (OWNER only)|


### Tasks (`/api`)
| Method | Endpoint | Description |
|--------|----------|-------------|
|POST|	`/projects/{projectId}/tasks`|	Create a task in a project (members only)|
|GET|	`/projects/{projectId}/tasks`|	List tasks in a project (members only)|
|GET|	`/tasks/{taskId}`|	Get task by ID (project members only)|
|PUT|	`/tasks/{taskId}`|	Update task (project members only)|
|DELETE|	`/tasks/{taskId}`|	Delete task (project members only)|

## Roles and Permissions

### Global Roles (UserRole)
|Role|Permissions|
|----|-----------|
|USER|Standard user|
|ADMIN| Reserved for future administrative features|

### Project Roles (ProjectRole)
|Role|Permissions|
|----|-----------|
|OWNER|Full control over project, can add/remove members, update/delete project|
|MEMBER|	Can view project, create/update/delete tasks, view members|

## Error Handling
The API returns consistent error responses in the following format:
```json
{
  "timestamp": "2026-09-28T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Project not found",
  "path": "/api/projects/999"
}
```
|Status|When|
|-----|-----|
|400 Bad Request|	Invalid input or business rule violation|
|401 Unauthorized|	Missing or invalid JWT token|
|403 Forbidden|	Authenticated but not allowed (not a member, not the owner)|
|404 Not Found|	Resource does not exist|
|409 Conflict|	Duplicate resource (username, email, membership)|
|500 Internal Server Error|	Unexpected error|

## Project Structure
```text
Task-Manager/
│
├─── src/
│    └─── main/
│         ├─── java/com/alin/taskmanager/
│         │    │
│         │    ├─── TaskmanagerApplication.java    # Entry point
│         │    │
│         │    ├─── config/                        # OpenAPI / Swagger config
│         │    ├─── controller/                    # REST controllers
│         │    ├─── dto/                           # Data Transfer Objects
│         │    ├─── exception/                     # Custom exceptions + handler
│         │    ├─── model/                         # JPA entities and enums
│         │    ├─── repository/                    # Spring Data JPA repositories
│         │    ├─── security/                      # JWT + Spring Security
│         │    └─── service/                       # Business logic
│         │
│         └─── resources/
│              └─── application.properties         # Not tracked in Git
│
├─── .gitignore
├─── mvnw / mvnw.cmd                               # Maven wrapper
├─── pom.xml                                       # Maven configuration
└─── README.md
```


## Notes
- JWT Secret must be at least 32 characters long.

- Token expiration is set to 24 hours (configurable in application.properties).

- Database tables are automatically created on first run (if ddl-auto is set to update).

- Swagger public endpoints do not require a token; protected ones require authorization.

## Author

Constantin-Alin Tudor – GitHub: https://github.com/alin-tudor-06
