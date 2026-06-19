package cl.duoc.ms_historial_bff.controller;

import cl.duoc.ms_historial_bff.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialConDetallesDTO;
import cl.duoc.ms_historial_bff.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @PostMapping("/registrar")
    public ResponseEntity<HistorialDTO> registrarHistorial(@RequestBody HistorialDTO historialDTO) {
        try {
            HistorialDTO historial = historialService.registrarHistorial(historialDTO);
            return new ResponseEntity<>(historial, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<HistorialDTO>> listarHistoriales() {
        try {
            List<HistorialDTO> historiales = historialService.listarHistoriales();
            return ResponseEntity.ok(historiales);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/listar/detalles")
    public ResponseEntity<List<HistorialConDetallesDTO>> listarHistorialesConDetalles() {
        try {
            List<HistorialConDetallesDTO> historiales = historialService.listarHistorialesConDetalles();
            return ResponseEntity.ok(historiales);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<HistorialConDetallesDTO> obtenerHistorialConDetalles(@PathVariable Long id) {
        try {
            HistorialConDetallesDTO historial = historialService.obtenerHistorialConDetalles(id);
            if (historial != null) {
                return ResponseEntity.ok(historial);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarHistorial(@PathVariable Long id) {
        try {
            historialService.eliminarHistorial(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/actualizar")
    public ResponseEntity<HistorialUpdateDTO> actualizarHistorial(@RequestBody HistorialUpdateDTO historial) {
        try {
            HistorialUpdateDTO historialActualizado = historialService.actualizarHistorial(historial);
            return ResponseEntity.ok(historialActualizado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
