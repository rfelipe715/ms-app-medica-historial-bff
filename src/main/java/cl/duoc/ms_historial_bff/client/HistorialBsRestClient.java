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

    @DeleteMapping
    public void eliminarHistorial(Long id);

    @PutMapping
    public HistorialUpdateDTO actualizarHistorial(@RequestBody HistorialUpdateDTO historial);
}
