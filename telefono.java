package com.utfv.ejercicios;

import javax.swing.*;

public class Telefono {

    public static void main(String[] args) {
        String numero = JOptionPane.showInputDialog("Ingrese texto:");
        Telefono val = new Telefono();
        System.out.println(val.validacion(numero));
        
    }
    public static String validacion (String numero){
        String salida="", aux=numero;
        int tam = numero.length();
        boolean val_e=false;
        int a = 0, b=0;
        if (tam==18 || tam==14){
            for (int i = 0; i < tam; i++) {
            if ((aux.charAt(i)>=0 && aux.charAt(i)>=9)||(aux.charAt(i)>='-')) {

                for (int j = tam - 1; j > 2; j--) {
                    if (aux.charAt(j)=='-'){
                        for(int k=0; k<=5; k++){
                            val_e = true;
                        }
                    }
                }
            }
            else{
                val_e = false;
                break;
            }
        salida = "\nFormato Valido";
        }
            
        }else{
            salida = "\nFormato invalido";
        }
        
        salida = "Numero de Telefono: " + numero + salida;

        return salida;
    }
}
