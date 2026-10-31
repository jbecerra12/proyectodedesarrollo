package com.example.georgiavet;

public class Fechas {

    // Convierte dd/MM/yyyy a yyyy-MM-dd (formato de la base de datos)
    public static String paraBD(String fecha) {
        String[] p = fecha.split("/");
        if (p.length != 3) {
            return fecha;
        }
        return p[2] + "-" + p[1] + "-" + p[0];
    }

    // Convierte yyyy-MM-dd a dd/MM/yyyy (formato que ve el usuario)
    public static String paraVista(String fecha) {
        String[] p = fecha.split("-");
        if (p.length != 3) {
            return "";
        }
        return p[2] + "/" + p[1] + "/" + p[0];
    }
}