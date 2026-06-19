package cl.duoc.ms_historial_bff.service;

import cl.duoc.ms_historial_bff.client.HistorialBsRestClient;
import cl.duoc.ms_historial_bff.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialService {

    @Autowired
    private HistorialBsRestClient historialBsRestClient;

    public List<HistorialDTO> listarHistoriales() {
        return historialBsRestClient.listarHistoriales();
    }

    public HistorialDTO registrarHistorial(HistorialDTO historialDTO) {
        return historialBsRestClient.registrarHistorial(historialDTO);
    }

    public void eliminarHistorial (Long id) {
        historialBsRestClient.eliminarHistorial(id);
    }

    public HistorialUpdateDTO actualizarHistorial (HistorialUpdateDTO historial) {
        return historialBsRestClient.actualizarHistorial(historial);
    }
}
