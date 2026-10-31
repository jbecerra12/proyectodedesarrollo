package com.example.georgiavet;
public class Consulta {
    String id;
    String mascota;
    String especie;
    String servicio;
    String cliente;
    String telefono;
    String correo;
    String horario;
    String estado;
    String fecha;

    public Consulta(String id, String mascota, String especie, String servicio, String cliente, String telefono, String correo, String horario, String estado, String fecha) {
        this.id = id;
        this.mascota = mascota;
        this.especie = especie;
        this.servicio = servicio;
        this.cliente = cliente;
        this.telefono = telefono;
        this.correo = correo;
        this.horario = horario;
        this.estado = estado;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public String getMascota() {
        return mascota;
    }

    public String getEspecie() {
        return especie;
    }

    public String getServicio() {
        return servicio;
    }

    public String getCliente() {
        return cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getHorario() {
        return horario;
    }

    public String getEstado() {
        return estado;
    }

    public String getFecha() {
        return fecha;
    }
}