package cl.duoc.ms_historial_bff.service;

import cl.duoc.ms_historial_bff.client.HistorialBsRestClient;
import cl.duoc.ms_historial_bff.client.PacientesBffRestClient;
import cl.duoc.ms_historial_bff.client.CitasBffRestClient;
import cl.duoc.ms_historial_bff.exception.HistorialNotFoundException;
import cl.duoc.ms_historial_bff.exception.ServicioNoDisponibleException;
import cl.duoc.ms_historial_bff.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialConDetallesDTO;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    @Autowired
    private HistorialBsRestClient historialBsRestClient;

    @Autowired
    private PacientesBffRestClient pacientesBffRestClient;

    @Autowired
    private CitasBffRestClient citasBffRestClient;

    public List<HistorialDTO> listarHistoriales() {
        return historialBsRestClient.listarHistoriales();
    }

    public HistorialDTO obtenerHistorialPorId(Long id) {
        try {
            return historialBsRestClient.obtenerHistorialPorId(id);
        } catch (FeignException.NotFound e) {
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-historial-bs", e);
        }
    }

    public List<HistorialConDetallesDTO> listarHistorialesConDetalles() {
        return historialBsRestClient.listarHistoriales().stream()
            .map(this::enriquecerHistorial)
            .collect(Collectors.toList());
    }

    public HistorialDTO registrarHistorial(HistorialDTO historialDTO) {
        try {
            return historialBsRestClient.registrarHistorial(historialDTO);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-historial-bs", e);
        }
    }

    public void eliminarHistorial (Long id) {
        try {
            historialBsRestClient.eliminarHistorial(id);
        } catch (FeignException.NotFound e) {
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-historial-bs", e);
        }
    }

    public HistorialDTO actualizarHistorial (Long id, HistorialUpdateDTO historial) {
        try {
            return historialBsRestClient.actualizarHistorial(id, historial);
        } catch (FeignException.NotFound e) {
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            throw new ServicioNoDisponibleException("ms-historial-bs", e);
        }
    }

    public HistorialConDetallesDTO obtenerHistorialConDetalles(Long id) {
        HistorialDTO historial = historialBsRestClient.listarHistoriales().stream()
            .filter(h -> h.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (historial != null) {
            return enriquecerHistorial(historial);
        }
        return null;
    }

    private HistorialConDetallesDTO enriquecerHistorial(HistorialDTO historialDTO) {
        HistorialConDetallesDTO detalles = new HistorialConDetallesDTO();
        detalles.setId(historialDTO.getId());
        detalles.setPacienteId(historialDTO.getPacienteId());
        detalles.setCitaId(historialDTO.getCitaId());
        detalles.setFecha(historialDTO.getFecha());
        detalles.setDiagnostico(historialDTO.getDiagnostico());
        detalles.setObservaciones(historialDTO.getObservaciones());
        
        try {
            detalles.setPaciente(pacientesBffRestClient.obtenerPaciente(historialDTO.getPacienteId()));
        } catch (Exception e) {
            // Silenciar error si no encuentra paciente
        }
        
        try {
            detalles.setCita(citasBffRestClient.obtenerCita(historialDTO.getCitaId()));
        } catch (Exception e) {
            // Silenciar error si no encuentra cita
        }
        
        return detalles;
    }
}
