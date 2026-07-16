package cl.duoc.ms_historial_bff.client;

import cl.duoc.ms_historial_bff.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "historial-bs", url = "${historial-bs.url:http://localhost:9091/api/v1/historiales}")
public interface HistorialBsRestClient {

    @PostMapping
    HistorialDTO registrarHistorial(@RequestBody HistorialDTO historialDTO);

    @GetMapping
    List<HistorialDTO> listarHistoriales();

    @GetMapping("/{id}")
    HistorialDTO obtenerHistorialPorId(@PathVariable("id") Long id);

    @DeleteMapping("/{id}")
    void eliminarHistorial(@PathVariable("id") Long id);

    @PutMapping("/{id}")
    HistorialDTO actualizarHistorial(@PathVariable("id") Long id, @RequestBody HistorialUpdateDTO historial);
}
