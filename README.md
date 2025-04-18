# Coffeetica ☕  
Coffeetica is a full-stack **coffee review portal** built for specialty coffee lovers to discover, review, and compare coffees and roasteries. Designed with a commercial-grade architecture using **Spring Boot (Java)** and **React (TypeScript)**.

## Features

- **Admin Panel** – Full CRUD over entities (coffees, roasteries, users)
- **Coffee Catalog** – Browse specialty coffee with average rating, origin, and user reviews
- **Roastery Explorer** – Explore roasteries and their offered products
- **User Accounts** – Register, login, update profile, view personal data
- **Role-Based Access** – User/Admin/SuperAdmin distinction with JWT authentication
- **Image Upload** – Serve images for coffees/roasteries via backend
- **Reviews** – Add, view and manage coffee reviews

## Live Demo

App is accessible at: https://gltracker.eu
Login by using "Login as a test user button" or by following credentials:  
- **Username**: `testadminuser`
- **Password**: `admin123`  

🕒 **Please note:**  
The database for this demo environment is **automatically reset** every day at:  **06:00**, **12:00**, **18:00** CEST

## Built With

**Backend**  
- Java 21
- Spring Boot 
- Spring Security + JWT  
- REST API  
- Maven  
- PostgreSQL  
- Hibernate (JPA)  
- ModelMapper  
- Constructor-based Dependency Injection  

**Frontend**  
- React  
- TypeScript  
- Bootstrap 5  
- Vite  
- Axios  
- React Router  

**Deployment / Infrastructure**  
- Proxmox VE (hypervisor)  
- Ubuntu Server (container VM)  
- Docker + Docker Compose  
- NGINX (reverse proxy)  
- Cloudflare Tunnel (secure public access)

## Test Coverage

> Test coverage pending integration with testing frameworks and CI tools (e.g., JUnit + JaCoCo).

## Configuration Highlights

- 🔐 `SecurityConfig.java`: Stateless JWT authentication with fine-grained endpoint access
- 🔄 `MapperConfig.java`: Central ModelMapper bean with custom field skips
- 📄 `WebConfig.java`: CORS config for frontend (default: `https://coffeetica.eu`) + file resource handler
- ⚙️ `DataInitializer.java`: Injects default roles and a SuperAdmin account on startup
- 📁 `FileHelper.java`: Simple utility for deleting uploaded files

## Challenges during development

- Architecting the full-stack application structure from scratch
- Implementing JWT-based authentication and role-based access control
- Designing and planning the **visual side of the app (UI/UX)** to ensure consistency and usability
- Coding the entire frontend application in React with TypeScript
- Deploying the app using Proxmox, Docker, and Cloudflare Tunnel without prior DevOps experience with local hosting

## How to Run Locally

### 1. Clone the Repository
```bash
git clone https://github.com/jbytow/coffeetica.git
cd coffeetica
```

### 2. Setup Environment Variables

- Copy `.env.example` to `.env` in both `coffeetica/` and `coffeetica-frontend/` folders.
- Adjust the values according to your local configuration.

### 3. Update `docker-compose.yml` for local development

Modify the `volumes:` section of the `backend` service to mount the correct local path:

```yaml
backend:
  ...
  volumes:
    # - ./uploads:/uploads            # Use this in production/server
    - ./coffeetica-backend/uploads:/uploads  # Use this locally
```

Additionally, in the `frontend` service, update the build arguments for local API/image URLs:

```yaml
frontend:
  ...
  build:
    context: ./coffeetica-frontend
    args:
      - VITE_API_BASE_URL=http://localhost:8080/api
      - VITE_IMAGE_BASE_URL=http://localhost:8080/uploads/
```

### 4. Enable local resource serving in `WebConfig.java`

In `WebConfig.java`, uncomment the following block to allow serving uploaded files locally:

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:/uploads/");
}
```

> ☝️ In production, static files are served via NGINX, so this block should be **disabled on server**.

---

### 5. Run the app

```bash
docker compose up --build
```

- Frontend: [http://localhost:3000](http://localhost:3000)  
- Backend: [http://localhost:8080](http://localhost:8080)  
- Uploaded images (locally): [http://localhost:8080/uploads/roasteries/example.png](http://localhost:8080/uploads/roasteries/example.png)


## License

[MIT](https://choosealicense.com/licenses/mit/)