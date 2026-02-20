package org.example;

import javax.swing.*;

public class Validacion {

    public static void main(String[] args) {
        String numero = JOptionPane.showInputDialog("Ingrese texto:");
        Validacion val = new Validacion();
        System.out.println(val.validacion(numero));
    }

    public static String validacion(String numero) {
        String salida = "", aux = numero;
        int tam = numero.length();
        boolean val_e = true;

        if (tam == 18 || tam == 14) {
            for (int i = 0; i < tam; i++) {
                char c = aux.charAt(i);

                if (tam == 18) {
                    if (i == 3) {
                        if (c != '-') val_e = false;
                    } else {
                        if (!(c >= '0' && c <= '9')) val_e = false;
                    }
                }
                else if (tam == 14) {
                    if (i == 2 || i == 5 || i == 8 || i == 11) {
                        if (c != '-') val_e = false;
                    } else {
                        if (!(c >= '0' && c <= '9')) val_e = false;
                    }
                }

                if (!val_e) break;
            }

            if (val_e) {
                salida = "\nFormato Valido";
            } else {
                salida = "\nFormato Invalido";
            }

        } else {
            salida = "\nFormato invalido (Longitud incorrecta)";
        }

        salida = "Numero de Telefono: " + numero + salida;
        return salida;
    }
}
