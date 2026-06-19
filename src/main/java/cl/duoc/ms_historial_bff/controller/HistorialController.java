package cl.duoc.ms_historial_bff.controller;

import cl.duoc.ms_historial_bff.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bff.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/historiales")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @PostMapping("/registrar")
    public HistorialDTO registrarHistorial(@RequestBody HistorialDTO historialDTO) {
        return historialService.registrarHistorial(historialDTO);
    }

    @GetMapping("/listar")
    public List<HistorialDTO> listarHistoriales() {
        return historialService.listarHistoriales();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarHistorial (@RequestParam Long id) {

        historialService.eliminarHistorial(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/actualizar")
    public ResponseEntity<HistorialUpdateDTO> actualizarHistorial (@RequestBody HistorialUpdateDTO historial) {
        return ResponseEntity.ok(historialService.actualizarHistorial(historial));
    }
}
