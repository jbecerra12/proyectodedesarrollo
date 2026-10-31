package com.example.georgiavet;

public class Sesion {
    public static String id = "";
    public static String nombre = "";
    public static String rol = "";

    // Borra los datos de la sesion al cerrar sesion
    public static void cerrar() {
        id = "";
        nombre = "";
        rol = "";
    }
}