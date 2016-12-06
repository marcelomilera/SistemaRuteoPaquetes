/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author GUERRA
 */
public class ConjRutas {
    public ArrayList<Ruta> vuelos = new ArrayList<Ruta>();
    public int tiempo=0;
    public int capacidades=0;
    public int exito=0; // si se consiguió con exito la ruta = 1 ,falló = 0
    Set<String> setciudadesColapsadas ;// tienen formato: Ciudad-dia-hora
    Set<String> setvuelosColapsados; // tienen formato: LlaveVuelo/dia
    ConjRutas(){
        exito=-1;
    }
    ConjRutas(Ruta vuel,int tiemp){
        vuelos.add(vuel);
        tiempo=tiemp;
    }    
    
    ConjRutas(Ruta vuel1, Ruta vuel2,int tiemp){
        vuelos.add(vuel1);
        vuelos.add(vuel2);
        tiempo=tiemp;
    }       
    public void print(){
        for(int i=0;i<vuelos.size();i++){
            System.out.print(vuelos.get(i).getCiudadOrigen()+"-"+
                            vuelos.get(i).getCiudadFin()+"//");
        }
        System.out.println(" Tiempo: "+tiempo);
    }    
    public void addRuta(Ruta vuelo,int tiemp){
        vuelos.add(vuelo);
        tiempo+=tiemp;
    }
    public String imprimirRecorrido(){
        try{
            String cadena=this.vuelos.get(0).getCiudadOrigen();//Se concatena la primera ciudad
            for(Ruta r : this.vuelos){
                cadena+="-"+r.getCiudadFin();
            }       
            return cadena;
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Method Halted!, continuing doing the next thing");
        }            
        return null;
    }
    
    public void registrarColapsos(Set<String> ciudadesColapsadas,Set<String> vuelosColapsados){
        setciudadesColapsadas= new HashSet<>();
        setciudadesColapsadas=ciudadesColapsadas;
        vuelosColapsados= new HashSet<>();
        setvuelosColapsados=vuelosColapsados;
        
    }

}
