# ms-app-medica-historial-bff

Capa **BFF** (Backend for Frontend) del módulo **Historial**. Puerta de entrada del módulo para el cliente (vía API Gateway): valida el contrato (`@Valid`) y delega en `ms-app-medica-historial-bs`. No accede a la base de datos.

| | |
|---|---|
| **Puerto** | `9090` |
| **Patrón** | Controller → Service → Client (Feign) |
| **Ruta base** | `/api/v1/historiales` |
| **Llama a** | `historial-bs` (9091) · `citas-bff` · `pacientes-bff` |
| **Pruebas** | `HistorialServiceTest` (JUnit 5 + Mockito) |
| **Swagger** | `http://localhost:9090/swagger-ui.html` — agregado también al Gateway como pestaña **Historial** |

Endpoints principales: `POST /registrar`, `GET /listar`, `GET /{id}`, `GET /listar/detalles`, `GET /{id}/detalles`.

## Ejecución

```bash
# Con todo el ecosistema (recomendado), desde app-medica-et-fullstack-1/
docker compose up --build

# Individual
./mvnw spring-boot:run     # mvnw.cmd en Windows
./mvnw test
```
