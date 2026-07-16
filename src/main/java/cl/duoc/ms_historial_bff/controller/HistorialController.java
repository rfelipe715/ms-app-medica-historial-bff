package cl.duoc.ms_historial_bff.controller;

import cl.duoc.ms_historial_bff.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialConDetallesDTO;
import cl.duoc.ms_historial_bff.service.HistorialService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
@Tag(name = "Historial", description = "Gestión del historial clínico de pacientes")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
@OpenAPIDefinition(servers = @Server(url = "${api-gateway.url:http://localhost:8080}"))
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @Operation(summary = "Registrar un nuevo historial clínico", description = "Crea un nuevo registro de historial clínico para un paciente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Historial registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    @PostMapping("/registrar")
    public ResponseEntity<HistorialDTO> registrarHistorial(@Valid @RequestBody HistorialDTO historialDTO) {
        HistorialDTO historial = historialService.registrarHistorial(historialDTO);
        return new ResponseEntity<>(historial, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos los historiales", description = "Retorna la lista completa de historiales clínicos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de historiales obtenida exitosamente")
    @GetMapping("/listar")
    public ResponseEntity<List<HistorialDTO>> listarHistoriales() {
        List<HistorialDTO> historiales = historialService.listarHistoriales();
        return ResponseEntity.ok(historiales);
    }

    @Operation(summary = "Buscar historial por ID", description = "Retorna los datos de un historial clínico específico según su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistorialDTO> obtenerHistorialPorId(@PathVariable Long id) {
        return ResponseEntity.ok(historialService.obtenerHistorialPorId(id));
    }

    @Operation(summary = "Listar historiales con detalles", description = "Retorna los historiales enriquecidos con los datos del paciente y las citas asociadas.")
    @ApiResponse(responseCode = "200", description = "Lista de historiales con detalles obtenida exitosamente")
    @GetMapping("/listar/detalles")
    public ResponseEntity<List<HistorialConDetallesDTO>> listarHistorialesConDetalles() {
        List<HistorialConDetallesDTO> historiales = historialService.listarHistorialesConDetalles();
        return ResponseEntity.ok(historiales);
    }

    @Operation(summary = "Buscar historial con detalles", description = "Retorna un historial clínico específico enriquecido con los datos del paciente y las citas asociadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @GetMapping("/{id}/detalles")
    public ResponseEntity<HistorialConDetallesDTO> obtenerHistorialConDetalles(@PathVariable Long id) {
        HistorialConDetallesDTO historial = historialService.obtenerHistorialConDetalles(id);
        if (historial != null) {
            return ResponseEntity.ok(historial);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un historial", description = "Elimina de forma permanente un registro de historial clínico del sistema, identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Historial eliminado exitosamente, sin contenido de respuesta"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long id) {
        historialService.eliminarHistorial(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Actualizar un historial existente", description = "Actualiza los datos de un historial clínico ya registrado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No existe un historial con el ID indicado")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<HistorialDTO> actualizarHistorial(@PathVariable Long id, @Valid @RequestBody HistorialUpdateDTO historial) {
        HistorialDTO historialActualizado = historialService.actualizarHistorial(id, historial);
        return ResponseEntity.ok(historialActualizado);
    }
}
