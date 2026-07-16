package cl.duoc.ms_historial_bff.service;

import cl.duoc.ms_historial_bff.client.CitasBffRestClient;
import cl.duoc.ms_historial_bff.client.HistorialBsRestClient;
import cl.duoc.ms_historial_bff.client.PacientesBffRestClient;
import cl.duoc.ms_historial_bff.exception.HistorialNotFoundException;
import cl.duoc.ms_historial_bff.exception.ServicioNoDisponibleException;
import cl.duoc.ms_historial_bff.model.dto.CitaDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialConDetallesDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialDTO;
import cl.duoc.ms_historial_bff.model.dto.HistorialUpdateDTO;
import cl.duoc.ms_historial_bff.model.dto.PacienteBffDto;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    @Mock
    private HistorialBsRestClient historialBsRestClient;

    @Mock
    private PacientesBffRestClient pacientesBffRestClient;

    @Mock
    private CitasBffRestClient citasBffRestClient;

    @InjectMocks
    private HistorialService historialService;

    private HistorialDTO historialDTO;

    @BeforeEach
    void setUp() {
        historialDTO = new HistorialDTO(1L, 10L, 20L, "2026-08-01", "Resfrío común", "Reposo 3 días");
    }

    @Test
    void listarHistoriales_delegaEnLaCapaBs() {
        when(historialBsRestClient.listarHistoriales()).thenReturn(List.of(historialDTO));

        List<HistorialDTO> resultado = historialService.listarHistoriales();

        assertThat(resultado).containsExactly(historialDTO);
    }

    @Test
    void obtenerHistorialPorId_retornaElHistorial_cuandoExiste() {
        when(historialBsRestClient.obtenerHistorialPorId(1L)).thenReturn(historialDTO);

        HistorialDTO resultado = historialService.obtenerHistorialPorId(1L);

        assertThat(resultado).isEqualTo(historialDTO);
    }

    @Test
    void obtenerHistorialPorId_lanzaHistorialNotFoundException_cuandoBsRetorna404() {
        when(historialBsRestClient.obtenerHistorialPorId(99L)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> historialService.obtenerHistorialPorId(99L))
                .isInstanceOf(HistorialNotFoundException.class);
    }

    @Test
    void obtenerHistorialPorId_lanzaServicioNoDisponible_cuandoFallaElServicioRemoto() {
        when(historialBsRestClient.obtenerHistorialPorId(1L)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> historialService.obtenerHistorialPorId(1L))
                .isInstanceOf(ServicioNoDisponibleException.class);
    }

    @Test
    void registrarHistorial_retornaElHistorialCreado() {
        when(historialBsRestClient.registrarHistorial(historialDTO)).thenReturn(historialDTO);

        HistorialDTO resultado = historialService.registrarHistorial(historialDTO);

        assertThat(resultado).isEqualTo(historialDTO);
    }

    @Test
    void registrarHistorial_lanzaServicioNoDisponible_cuandoFallaElServicioRemoto() {
        when(historialBsRestClient.registrarHistorial(historialDTO)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> historialService.registrarHistorial(historialDTO))
                .isInstanceOf(ServicioNoDisponibleException.class);
    }

    @Test
    void eliminarHistorial_delegaEnLaCapaBs() {
        historialService.eliminarHistorial(1L);

        verify(historialBsRestClient).eliminarHistorial(1L);
    }

    @Test
    void eliminarHistorial_lanzaHistorialNotFoundException_cuandoBsRetorna404() {
        org.mockito.Mockito.doThrow(mock(FeignException.NotFound.class)).when(historialBsRestClient).eliminarHistorial(99L);

        assertThatThrownBy(() -> historialService.eliminarHistorial(99L))
                .isInstanceOf(HistorialNotFoundException.class);
    }

    @Test
    void actualizarHistorial_retornaElHistorialActualizado() {
        HistorialUpdateDTO update = new HistorialUpdateDTO(1L, 10L, 20L, "2026-08-02", "Gripe", "Reposo 5 días");
        when(historialBsRestClient.actualizarHistorial(1L, update)).thenReturn(historialDTO);

        HistorialDTO resultado = historialService.actualizarHistorial(1L, update);

        assertThat(resultado).isEqualTo(historialDTO);
    }

    @Test
    void actualizarHistorial_lanzaHistorialNotFoundException_cuandoBsRetorna404() {
        HistorialUpdateDTO update = new HistorialUpdateDTO(99L, 10L, 20L, "2026-08-02", "Gripe", "Reposo 5 días");
        when(historialBsRestClient.actualizarHistorial(99L, update)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> historialService.actualizarHistorial(99L, update))
                .isInstanceOf(HistorialNotFoundException.class);
    }

    @Test
    void listarHistorialesConDetalles_enriqueceCadaHistorialConPacienteYCita() {
        PacienteBffDto paciente = new PacienteBffDto(10L, "11111111-1", "R1", "F1", "Juan", "Perez", "M", "1990-01-01", "Calle 1", "123456");
        CitaDTO cita = new CitaDTO(20L, 10L, "2026-08-01", "10:00", "CONFIRMADA");
        when(historialBsRestClient.listarHistoriales()).thenReturn(List.of(historialDTO));
        when(pacientesBffRestClient.obtenerPaciente(10L)).thenReturn(paciente);
        when(citasBffRestClient.obtenerCita(20L)).thenReturn(cita);

        List<HistorialConDetallesDTO> resultado = historialService.listarHistorialesConDetalles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPaciente()).isEqualTo(paciente);
        assertThat(resultado.get(0).getCita()).isEqualTo(cita);
    }

    @Test
    void listarHistorialesConDetalles_dejaPacienteYCitaEnNull_siFallanLosServiciosRemotos() {
        when(historialBsRestClient.listarHistoriales()).thenReturn(List.of(historialDTO));
        when(pacientesBffRestClient.obtenerPaciente(10L)).thenThrow(new RuntimeException("paciente no encontrado"));
        when(citasBffRestClient.obtenerCita(20L)).thenThrow(new RuntimeException("cita no encontrada"));

        List<HistorialConDetallesDTO> resultado = historialService.listarHistorialesConDetalles();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPaciente()).isNull();
        assertThat(resultado.get(0).getCita()).isNull();
    }

    @Test
    void obtenerHistorialConDetalles_retornaNull_siElHistorialNoExiste() {
        when(historialBsRestClient.listarHistoriales()).thenReturn(List.of(historialDTO));

        HistorialConDetallesDTO resultado = historialService.obtenerHistorialConDetalles(999L);

        assertThat(resultado).isNull();
    }

    @Test
    void obtenerHistorialConDetalles_retornaElHistorialEnriquecido_siExiste() {
        PacienteBffDto paciente = new PacienteBffDto(10L, "11111111-1", "R1", "F1", "Juan", "Perez", "M", "1990-01-01", "Calle 1", "123456");
        CitaDTO cita = new CitaDTO(20L, 10L, "2026-08-01", "10:00", "CONFIRMADA");
        when(historialBsRestClient.listarHistoriales()).thenReturn(List.of(historialDTO));
        when(pacientesBffRestClient.obtenerPaciente(10L)).thenReturn(paciente);
        when(citasBffRestClient.obtenerCita(20L)).thenReturn(cita);

        HistorialConDetallesDTO resultado = historialService.obtenerHistorialConDetalles(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPaciente()).isEqualTo(paciente);
        assertThat(resultado.getCita()).isEqualTo(cita);
    }
}
