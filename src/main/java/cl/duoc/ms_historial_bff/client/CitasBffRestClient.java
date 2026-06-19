package cl.duoc.ms_historial_bff.client;

import cl.duoc.ms_historial_bff.model.dto.CitaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "citas-bff", url = "http://localhost:8090/api/v1/citas")
public interface CitasBffRestClient {

    @GetMapping("/{id}")
    CitaDTO obtenerCita(@PathVariable Long id);

}
