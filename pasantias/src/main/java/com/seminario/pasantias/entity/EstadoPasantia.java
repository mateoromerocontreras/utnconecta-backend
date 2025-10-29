package com.seminario.pasantias.entity;

import lombok.Getter;

@Getter
public enum EstadoPasantia {
    DRAFT("Borrador", "La empresa está completando la oferta. No es visible para nadie más."),
    PENDIENTE_APROBACION("Pendiente de Aprobación", "El borrador fue enviado por la empresa y está en la cola para ser revisado por la facultad."),
    PUBLICADA("Publicada", "La oferta está aprobada y visible para los estudiantes. Se pueden recibir postulaciones."),
    EN_SELECCION("En Selección", "El período de postulación ha cerrado. La oferta no es visible para nuevas postulaciones, pero la empresa está revisando CVs y el sistema está esperando la confirmación de contratación."),
    CUBIERTA("Cubierta", "Se ha alcanzado el cupo máximo (CANTIDAD DE PASANTES). La oferta se cierra y se mueve a un historial."),
    CANCELADA("Cancelada", "La empresa retira la oferta por motivos internos (antes de que cualquier pasante haya iniciado)."),
    FINALIZADA("Finalizada", "Todas las relaciones de esta pasantía han terminado (todos los pasantes iniciados han finalizado su contrato).");

    private final String displayName;
    private final String descripcion;

    EstadoPasantia(String displayName, String descripcion) {
        this.displayName = displayName;
        this.descripcion = descripcion;
    }

    public boolean esVisibleParaEstudiantes() {
        return this == PUBLICADA;
    }

    public boolean aceptaPostulaciones() {
        return this == PUBLICADA;
    }

    public boolean esActiva() {
        return this != CANCELADA && this != FINALIZADA && this != CUBIERTA;
    }

    public boolean permiteEdicion() {
        return this == DRAFT || this == PENDIENTE_APROBACION;
    }

    public static EstadoPasantia fromString(String estado) {
        if (estado == null) {
            return null;
        }
        try {
            return EstadoPasantia.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
