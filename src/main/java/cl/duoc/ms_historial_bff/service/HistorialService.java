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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistorialService {

    private static final Logger log = LoggerFactory.getLogger(HistorialService.class);

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
            log.warn("Historial id={} no encontrado en ms-historial-bs", id);
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-historial-bs no disponible al buscar historial id={}: {}", id, e.getMessage());
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
            HistorialDTO registrado = historialBsRestClient.registrarHistorial(historialDTO);
            log.info("Historial registrado con id={}, pacienteId={}, citaId={}",
                    registrado.getId(), registrado.getPacienteId(), registrado.getCitaId());
            return registrado;
        } catch (FeignException e) {
            log.error("ms-historial-bs no disponible al registrar historial para pacienteId={}: {}",
                    historialDTO.getPacienteId(), e.getMessage());
            throw new ServicioNoDisponibleException("ms-historial-bs", e);
        }
    }

    public void eliminarHistorial (Long id) {
        try {
            historialBsRestClient.eliminarHistorial(id);
            log.info("Historial id={} eliminado correctamente", id);
        } catch (FeignException.NotFound e) {
            log.warn("Intento de eliminar un historial inexistente, id={}", id);
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-historial-bs no disponible al eliminar historial id={}: {}", id, e.getMessage());
            throw new ServicioNoDisponibleException("ms-historial-bs", e);
        }
    }

    public HistorialDTO actualizarHistorial (Long id, HistorialUpdateDTO historial) {
        try {
            HistorialDTO actualizado = historialBsRestClient.actualizarHistorial(id, historial);
            log.info("Historial id={} actualizado correctamente", id);
            return actualizado;
        } catch (FeignException.NotFound e) {
            log.warn("Intento de actualizar un historial inexistente, id={}", id);
            throw new HistorialNotFoundException(id);
        } catch (FeignException e) {
            log.error("ms-historial-bs no disponible al actualizar historial id={}: {}", id, e.getMessage());
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
            log.warn("No se pudo enriquecer el historial id={} con datos del paciente id={}: {}",
                    historialDTO.getId(), historialDTO.getPacienteId(), e.getMessage());
        }

        try {
            detalles.setCita(citasBffRestClient.obtenerCita(historialDTO.getCitaId()));
        } catch (Exception e) {
            log.warn("No se pudo enriquecer el historial id={} con datos de la cita id={}: {}",
                    historialDTO.getId(), historialDTO.getCitaId(), e.getMessage());
        }
        
        return detalles;
    }
}
