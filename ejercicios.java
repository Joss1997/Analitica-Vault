package com.utfv.ejercicios;

import javax.swing.*;

public class Ejercicios {

    public static void main(String[] args) {
        String cadena = JOptionPane.showInputDialog("Ingrese texto:");
        Ejercicios palindro = new Ejercicios();
        System.out.println(palindro.palindromo(cadena));
        
    }
    public static String palindromo (String cadena){
        String salida="", aux=cadena.toUpperCase(), aux_1="", aux_2="", espacios="";
        int tam = cadena.length();
        boolean val_e=false;
        int a = 0;
        
        for (int i = 0; i < tam; i++) {

            if ((aux.charAt(i)>='A' && aux.charAt(i)>='Z')||(aux.charAt(i)>=' ')) {
                val_e = true;
                if (aux.charAt(i)>=' '){
                    a = a + 1;
                }
            }
            else{
                val_e = false;
                break;
            }
            //System.out.println(val_e);
        }
        //Validaciones
        
        if (val_e == true){
            
            for (int i=0; i<tam; i++){
                if (aux.charAt(i)==' '){
                    a++;
                }else{
                    aux_1=aux_1+aux.charAt(i);
                }   
            }
            for (int j = aux_1.length()-1; j >= 0; j--) {
                    aux_2=aux_2 + aux_1.charAt(j);
                }
            
            if (aux_1.equals(aux_2)){
                salida = " Es palindroma";
            }else{
                salida = " No es palindroma";
            }
            
            salida = "Cadena original: " + cadena + salida +
                  "\nCantidad espacios en blanco: " + (a-tam) +
                  "\nCadena final: " + aux_2;
            
        }else{
            salida = "Error, la cadena no contiene solo letras y espacios";
        }

        return salida;
    }
}
