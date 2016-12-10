/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


/**
 *
 * @author JoseLuis
 */
public class Ciudad {
    private int Id;
    public String codigo;
    private String nombre;
    private String pais;
    private String Continente;
    private int huso_horario;
    private int cantPaquetes;
    public ArrayList<Ruta> rutasAnexas=new ArrayList<Ruta>();
    private ArrayList<Integer> cantVisitadasRutasAnexas=new ArrayList<Integer>();
    public HashMap<String,ArrayList<ConjRutas>> rutasXDestino = new HashMap<String,ArrayList<ConjRutas>>(); // llave es el codigo de la ciudad destino, valor es las rutasXDestino posibles
    public HashMap<String,ArrayList<Integer>> cantVisitadasrutasXDestino = new HashMap<String,ArrayList<Integer>>();
    public HashMap<String,Integer> proyeccionCiudad= new HashMap<String,Integer>(); // key = dia-hora
    public int capacidadMaxima=600;
    //private BufferedReader brAeropuertos;
    private String ultimaHora;
    private String ultimaFecha;
    private String ultimoDestino;
    private int linea=0;
    private String[] archivoPedidos;
    
   
    
    public Ciudad(String id,String codigo,String nombre,String pais,String continente) throws FileNotFoundException{
        this.Id=Integer.parseInt(id);
        this.codigo=codigo;
        this.nombre=nombre;
        this.pais=pais;
        this.Continente=continente;
        this.cantPaquetes=0;
        for(int i=0;i<7;i++){

            for(int j=0;j<24;++j){
                String key=""+i+"-"+j;
                //System.out.println("ciudad: "+key);
                proyeccionCiudad.put(key, 0);
            }
        }
        //brAeropuertos = new BufferedReader(new FileReader("Extras/_archivos_1dia/1arch_"+codigo+".txt"));
    }

    Ciudad() {

    }
    
    /**
     * @return the Id
     */
    public int getId() {
        return Id;
    }
    
    public void inicializarProyeccion(int day){        
        String key=""+day+"-";  
        for(int j=0;j<24;++j){
            key=key+j;
            proyeccionCiudad.put(key, 0);
        }

    }
    public void print(){
        System.out.println("========"+codigo+"========");
        Set set = rutasXDestino.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            System.out.println(me.getKey() + ": ");
            ArrayList<ConjRutas> conjRuta=(ArrayList<ConjRutas>)me.getValue();
            for(int j=0;j<conjRuta.size();j++){
                ConjRutas conj=conjRuta.get(j);
                ArrayList<Ruta> vuelos=conj.vuelos;
                for(int h=0;h<vuelos.size();h++){
                    System.out.print(vuelos.get(h).getCiudadOrigen()+"-"+vuelos.get(h).getCiudadFin());                  
                }
                System.out.println();
            }
        }    
    }
    /**
     * @param Id the Id to set
     */
    public void setId(int Id) {
        this.Id = Id;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the pais
     */
    public String getPais() {
        return pais;
    }

    /**
     * @param pais the pais to set
     */
    public void setPais(String pais) {
        this.pais = pais;
    }

    /**
     * @return the Continente
     */
    public String getContinente() {
        return Continente;
    }

    /**
     * @param Continente the Continente to set
     */
    public void setContinente(String Continente) {
        this.Continente = Continente;
    }

    /**
     * @return the huso_horario
     */
    public int getHuso_horario() {
        return huso_horario;
    }

    /**
     * @param huso_horario the huso_horario to set
     */
    public void setHuso_horario(int huso_horario) {
        this.huso_horario = huso_horario;
    }

    /**
     * @return the rutasAnexas
     */
    public ArrayList getRutasAnexas() {
        return rutasAnexas;
    }

    /**
     * @param rutasAnexas the rutasAnexas to set
     */
    public void setRutasAnexas(ArrayList rutasAnexas) {
        this.rutasAnexas = rutasAnexas;
    }
    
    public void agregarRuta(Ruta newruta){
        this.rutasAnexas.add(newruta);
    }

    /**
     * @return the cantPaquetes
     */
    public int getCantPaquetes() {
        return cantPaquetes;
    }

    /**
     * @param cantPaquetes the cantPaquetes to set
     */
    public void setCantPaquetes(int cantPaquetes) {
        this.cantPaquetes = cantPaquetes;
    }
    
    public void instanciarCantidadIdasRutasAnexas(){
        for(int i=0;i<this.rutasAnexas.size();i++){
            this.cantVisitadasRutasAnexas.add(1);//Se instancian con 1 para empezas con probabilidad 1
        }        
    }
    
    public void instanciarCantVisitadasrutasXDestino(String codCiudadF){
        ArrayList<ConjRutas> rutas=(ArrayList<ConjRutas>)this.rutasXDestino.get(codCiudadF);
        ArrayList<Integer> listaRutas= new ArrayList<Integer>();
 
        for(int i=0;rutas!=null &&i<rutas.size();i++){
            listaRutas.add(1);//Se instancian con 1 para empezas con probabilidad 1
        }
        
        this.cantVisitadasrutasXDestino.put(codCiudadF, listaRutas);
        
    }
    
   
    /**
     * @return the cantVisitadasRutasAnexas
     */
    public ArrayList<Integer> getCantVisitadasRutasAnexas() {
        return cantVisitadasRutasAnexas;
    }

    /**
     * @return the ultimaHora
     */
    public String getUltimaHora() {
        return ultimaHora;
    }

    /**
     * @param ultimaHora the ultimaHora to set
     */
    public void setUltimaHora(String ultimaHora) {
        this.ultimaHora = ultimaHora;
    }

    /**
     * @return the ultimaFecha
     */
    public String getUltimaFecha() {
        return ultimaFecha;
    }

    /**
     * @param ultimaFecha the ultimaFecha to set
     */
    public void setUltimaFecha(String ultimaFecha) {
        this.ultimaFecha = ultimaFecha;
    }

    /**
     * @return the ultimoDestino
     */
    public String getUltimoDestino() {
        return ultimoDestino;
    }

    /**
     * @param ultimoDestino the ultimoDestino to set
     */
    public void setUltimoDestino(String ultimoDestino) {
        this.ultimoDestino = ultimoDestino;
    }

    /**
     * @return the linea
     */
    public int getLinea() {
        return linea;
    }

    /**
     * @param linea the linea to set
     */
    public void setLinea(int linea) {
        this.linea = linea;
    }

    /**
     * @return the archivoPedidos
     */
    public String[] getArchivoPedidos() {
        return archivoPedidos;
    }

    /**
     * @param archivoPedidos the archivoPedidos to set
     */
    public void setArchivoPedidos(String[] archivoPedidos) {
        this.archivoPedidos = archivoPedidos;
    }
    
    public void avanzarBuffer() throws FileNotFoundException{
        String[] archivoAsociado=this.getArchivoPedidos();
        String linea;
        if((linea=archivoAsociado[this.getLinea()]) != null){
            String codigoPaquete=linea.substring(0, 9);
            String fechaLlegadaPaquete=linea.substring(15,17)+"/"+linea.substring(13,15)+"/"+linea.substring(9,13);
            String horaLlegadaPaquete=linea.substring(17,22);
            String DestinoPaquete=linea.substring(22,26);
            this.setLinea(this.linea+1);
            this.setUltimaFecha(fechaLlegadaPaquete);
            this.setUltimaHora(horaLlegadaPaquete);
            this.setUltimoDestino(DestinoPaquete);                     
        }
        else this.setUltimaHora(null);
        /*BufferedReader archivoCiudad= new BufferedReader(new FileReader("Extras/_archivos_1dia/1arch_"+codigo+".txt"));
        String linea;
        try{
            for(int i=0;i<this.linea;++i) linea=archivoCiudad.readLine();
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                archivoCiudad.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        }
        try{
            if((linea=archivoCiudad.readLine()) != null){
                String codigoPaquete=linea.substring(0, 9);
                String fechaLlegadaPaquete=linea.substring(15,17)+"/"+linea.substring(13,15)+"/"+linea.substring(9,13);
                String horaLlegadaPaquete=linea.substring(17,22);
                String DestinoPaquete=linea.substring(22,26);
                this.setLinea(this.linea+1);
                this.setUltimaFecha(fechaLlegadaPaquete);
                this.setUltimaHora(horaLlegadaPaquete);
                this.setUltimoDestino(DestinoPaquete);                     
            }
            else this.setUltimaHora(null);
            archivoCiudad.close();      
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                archivoCiudad.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        } */
    }

    /**
     * @return the brAeropuertos
     */
    //public BufferedReader getBrAeropuertos() {
    //    return brAeropuertos;
    //}

    /**
     * @param brAeropuertos the brAeropuertos to set
     */
    //public void setBrAeropuertos(BufferedReader brAeropuertos) {
    //    this.brAeropuertos = brAeropuertos;
    //}
    
}
