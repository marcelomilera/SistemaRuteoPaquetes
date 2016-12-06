/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.*;
import com.google.gson.Gson;

import play.Logger;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import play.Play; 
/**
 *
 * @author JoseLuis
 */
public class GestorCiudades {
    // public static final String ANSI_RESET = "\u001B[0m";
    // public static final String ANSI_BLACK = "\u001B[30m";
    // public static final String ANSI_RED = "\u001B[34m"; //en verdad es azul
    private TreeMap<String,Ciudad> ciudades=new TreeMap<String,Ciudad>();//MAP KEY-Codigo Ciudad y el VALUE-Objeto Ciudad
    private HashMap<String,Ruta> vuelosBD= new HashMap<String,Ruta>(); // key = origen-fin-horaPartida
    private int maxCapacidadCiudades=600;
    private int maxCapacidadesVuelos=200;
    private int maxTiempoContinental=24;
    private int maxTiempoIntercontinental=48;
    private int porcentajeEvaluacion=4;
    private int estadoRutaFactible=0;
    private int estadoRutaXTiempo=1;
    private int estadoRutaXCapacidadAlmacen=2;
    private int estadoRutaXCapacidadVuelo=3;

    public Random rnd;
    private int TiempoEntregaPaquetes=0;

	private static GestorCiudades instance;
	

	
	static {
		Gson gson = new Gson();
		Logger.info("Gestor ha instanciarse");
		try (Reader reader = new FileReader( Play.application().getFile("/conf/gestorFinal.json"))) {
			instance=gson.fromJson(reader, GestorCiudades.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.info("Gestor instanciado");
	}

    public static GestorCiudades getInstance(){
        Logger.info("Gestor GetInstance");
        return instance;
    }

    private GestorCiudades() {
        
    }
    
    
    private void leerCiudades(String archAeropuertos,String archHusos) throws FileNotFoundException{
        BufferedReader brAeropuertos = new BufferedReader(new FileReader(archAeropuertos));
        BufferedReader brHusos = new BufferedReader(new FileReader(archHusos));
        String linea;
        String continente="";
        try{
            while((linea=brAeropuertos.readLine()) != null){
                String [] datos=linea.trim().split("\t");
                if (datos.length<5){
                    continente=linea;
                }else{
                    String codigo=datos[1];
                    Ciudad newCiudad= new Ciudad(datos[0],codigo,datos[2],datos[3],continente);
                    getCiudades().put(codigo, newCiudad);
                }
            }
            brAeropuertos.close();
            while((linea=brHusos.readLine()) != null){
                String [] datos=linea.trim().split("\t");
                String codigo=datos[1];
                int huso_horario=Integer.parseInt(datos[2]);
                Ciudad ciudadBuscada=getCiudades().get(codigo);
                ciudadBuscada.setHuso_horario(huso_horario);
            }
            brHusos.close();
            
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                brAeropuertos.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        } 
    }
    
    private void leerRutas(String archRutas) throws FileNotFoundException{
        BufferedReader brRutas = new BufferedReader(new FileReader(archRutas));
        String linea;
        
        
        try{
            while((linea=brRutas.readLine()) != null){
                String [] datos=linea.trim().split("-");
                String ciudadO=datos[1];
                String ciudadF=datos[2];
                String horaO=datos[3];
                String horaF=datos[4];

                String[] hhmm=horaO.split(":");        
                int horaPartida=Integer.parseInt(hhmm[0]);
                String[] hhmm2=horaF.split(":");        
                int horaFin=Integer.parseInt(hhmm2[0]);
                
                Ruta newRuta=new Ruta(ciudadO,ciudadF,horaO,horaF);
                newRuta.horaF=horaFin;
                newRuta.horaO=horaPartida;
                if(getCiudades().get(ciudadO).getContinente().equals(getCiudades().get(ciudadF).getContinente())) newRuta.setTiempo(12);
                else newRuta.setTiempo(24);
                Ciudad ciudadOrigen=getCiudades().get(ciudadO);
                ciudadOrigen.agregarRuta(newRuta);
            }
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                brRutas.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        } 
        
    }
    
    
	
    public GestorCiudades(String archVuelos,String archAeropuertos,String archHusos) throws FileNotFoundException{
        leerCiudades(archAeropuertos,archHusos);
        leerRutas(archVuelos);
        generarConjRutas();//generar todas las rutasXDestino posibles
        //UTILIZAR ALGORITMO DE TODAS LAS CIUDADES A TODAS LAS CIUDADES
        
        this.rnd=new Random();
    }
	
        

    
    public void asignarPedidos(String archPedidos)throws FileNotFoundException{
        BufferedReader brPedidos = new BufferedReader(new FileReader(archPedidos));
        String linea;
        //int horaPedido=9;
        String fechaActual="";
        try{
            int numPedido=1;
            while((linea=brPedidos.readLine()) != null){
                String [] datos=linea.trim().split("-");
                String codCiudadO=datos[0];
                String codCiudadF=datos[1];
                int cantPaquetes=Integer.parseInt(datos[2]);
                String horaPedido=datos[3];               
                String fechaPedido=datos[4];
                
                if(!fechaPedido.equals(fechaActual)){//Se limpia el dia si ha cambiado de todos los almacenes
                    //limpiarCapacidad_Almacenes_Rutas(fechaActual);
                    fechaActual=fechaPedido;
                }
                DFS(codCiudadO,codCiudadF,numPedido,horaPedido,cantPaquetes,fechaPedido);
                
                numPedido++;
            }
            System.out.println("Tiempo Total por paquetes: "+this.TiempoEntregaPaquetes);
        }catch (IOException e)
        {
            System.err.println(e.toString());
        }
        finally
        {
            try
            {
                brPedidos.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        } 
    }
	
	public void generarConjRutas(){
        int tEspera;
        int tiempoRuta;
        for(Ciudad ciudad : ciudades.values()) {
            for(Ciudad ciudFin : ciudades.values()){
                if(!ciudFin.getCodigo().equals(ciudad.getCodigo())){
                    int tMax=48; //maximo de horas
                    if(ciudFin.getContinente().equals(ciudad.getContinente())) tMax=24;
                    ArrayList<ConjRutas> rutas=encuentraRutas(ciudad,ciudFin.getCodigo(),tMax,0);
                    if(rutas.size()>0) ciudad.rutasXDestino.put(ciudFin.getCodigo(), rutas);
                }

            }
        }         
    }
    
    public  ArrayList<ConjRutas> encuentraRutas(Ciudad ciudOrigen, String ciudFinal,int tiempoDisp,int niveles){
        ArrayList<ConjRutas> rutas= new ArrayList<>();
        if(tiempoDisp<=0 || niveles==3) return rutas; // si ya no hay más tiempo no seguir más
        ArrayList<Ruta>vuelos = ciudOrigen.rutasAnexas;

        for(int i=0;i<vuelos.size();i++){
            //caso directo
            Ruta vuelo=vuelos.get(i);
            Ciudad ciudadFinVuelo=ciudades.get(vuelo.getCiudadFin());
            if(vuelo.getCiudadFin().equals(ciudFinal)){ // si cumple el destino
                if(vuelo.getTiempo()<=tiempoDisp) // si cumple la regla de negocio
                    rutas.add(new ConjRutas(vuelo,vuelo.getTiempo()));
            }
            else{ //intento con escala
                int nivelesManda=niveles+1;
                ArrayList<ConjRutas> rutasEscala=encuentraRutas(ciudadFinVuelo,ciudFinal
                        ,tiempoDisp-vuelo.getTiempo(),nivelesManda);// se obtiene las rutas desde la escala hasta el destino
                
                for(int j=0;j<rutasEscala.size();j++){ //verificar que se cumple el tiempo(considerando espera) x ruta
                    ConjRutas ruta =rutasEscala.get(j);
                    int tEspera;
                    int tEsperaTotal=0;
                    int tVueloTotal=vuelo.getTiempo();
                    for(int h=0;h<ruta.vuelos.size();h++){
                        Ruta vueloInt=ruta.vuelos.get(h);
                        if(h==0) tEspera=vueloInt.horaO-vuelo.horaF; //para el primer vuelo
                        else tEspera= vueloInt.horaO-ruta.vuelos.get(h-1).horaF;
                        if(tEspera<0) tEspera+=24;
                        tEsperaTotal+=tEspera;
                        tVueloTotal+=vueloInt.getTiempo();
                    }
                    int tTotal=tVueloTotal+tEsperaTotal;
                    if(tTotal<=tiempoDisp) {
                        ruta.vuelos.add(0, vuelo); // agregamos el vuelo inicial(origen-escala)
                        ruta.tiempo=tTotal;
                        rutas.add(ruta);
                    }                  
                    
                }
            }
        }      
        return rutas;
    }
        
    public ConjRutas DFS(String codCiudadO,String codCiudadF,int numPedido, String horaPedido,int cantPaquetes,String fechaPedido){
        Ciudad ciudadO=getCiudades().get(codCiudadO);
        Ciudad ciudadF=getCiudades().get(codCiudadF);
        int maxTiempoVuelo;
        String[] superTemporal=horaPedido.split(":");
        int horaPed=Integer.parseInt(superTemporal[0]);
        int minPed=Integer.parseInt(superTemporal[1]);
        //Establece el tiempo maximo de vuelo
        if(ciudadO.getContinente().equals(ciudadF.getContinente()))
            maxTiempoVuelo=maxTiempoContinental;
        else
            maxTiempoVuelo=maxTiempoIntercontinental;
        
        
        //ArrayList<Ruta> RutasAnexadasO=ciudadO.getRutasAnexas(); //CAMBIAR A LAS RUTAS QUE PUEDAN SER FACTIBLES        
        ArrayList<ConjRutas> listaRutasPreFactibles=(ArrayList<ConjRutas>) ciudadO.rutasXDestino.get(codCiudadF);
        // se descarta las q no cumplen con el tiempo tomando en cuenta la hora de ingreso del pedido
        //System.out.println("tamListaAntes: "+listaRutasPreFactibles.size());
        ArrayList<ConjRutas> listaRutasFactibles=depurarRutas(listaRutasPreFactibles,horaPed,maxTiempoVuelo,minPed);
                            //se puede alterar el orden aleatoriamente para que no siempre prefiera los primeros
         //obtenemos el dia de la semana a examinar para el origen del pedido
         //System.out.println("tamListadespues: "+listaRutasFactibles.size()+" HoraPedido: "+horaPedido);
        Calendar c=Calendar.getInstance();
        try {
            c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaPedido));
        } catch (ParseException ex) {
            //Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
        }
        int dayweek=c.get(Calendar.DAY_OF_WEEK)-1;//porque la semana comienza el domingo y el arreglo del 0-6
            
        int cantidadAnexos=listaRutasFactibles.size();
        if(cantidadAnexos==0) {
//             System.out.println(ANSI_RED +horaPedido+"   "+fechaPedido+" Numero de Pedido: "+numPedido+" "+codCiudadO+
//                     "-"+codCiudadF +" No se encontró ruta TIEMPO AGOTADO"+ANSI_RESET);
            return new ConjRutas(); // error de los archivos
        }
        int maximasRutasAevaluar=6;
        if(cantidadAnexos<=maximasRutasAevaluar) maximasRutasAevaluar=cantidadAnexos;
        int contadorRutas=0;
        int encontroAlMenosUno=0; 
        
        Set<String> setciudadesColapsadas = new HashSet<String>();// tienen formato: Ciudad-dia-hora
        Set<String> setvuelosColapsados = new HashSet<String>();// tienen formato: LlaveVuelo/dia
        
        ConjRutas mejorRutaReal= listaRutasFactibles.get(0);
        Collections.shuffle(listaRutasFactibles);// desordenamos un poco para q no escoja siempre las mismas 
        
        for(ConjRutas rutaPrueba :listaRutasFactibles){
            //si encontró solución en el maximo de rutas a evaluar
            if( (contadorRutas!=0) && (contadorRutas%maximasRutasAevaluar==0) &&(encontroAlMenosUno!=0) ) break; 
            //verificamos capacidades
            int encontro=verificarCapacidades(rutaPrueba,setciudadesColapsadas,setvuelosColapsados,dayweek,horaPed,0);// el 0 significa consulta
            encontroAlMenosUno+=encontro;
            
            //evaluamos la mejor
            if(encontro==1){ //se encontro ruta posible
                if((mejorRutaReal.capacidades-mejorRutaReal.tiempo)<(rutaPrueba.capacidades-rutaPrueba.tiempo)) mejorRutaReal=rutaPrueba; // esta formula puede cambiar a una mejor
            }
            contadorRutas++;
        }
        if(encontroAlMenosUno!=0){ // si encontró solucion
            //actualizamos las caps
            verificarCapacidades(mejorRutaReal,setciudadesColapsadas,setvuelosColapsados,dayweek,horaPed,1); //el 1 significa actualizar
           Logger.info(fechaPedido+" Numero de Pedido: "+numPedido+ " Ciudad Origen: "+codCiudadO+
                   " - Ciudad Fin: "+codCiudadF+" Mejor Ruta: "+mejorRutaReal.imprimirRecorrido()
                   +" Mejor Tiempo: "+mejorRutaReal.tiempo);
            mejorRutaReal.exito=1;
            return mejorRutaReal;
        }
        else{
            //reruteo : utilizar -> ciudadesColapsadas ,vuelosColapsados
             mejorRutaReal.registrarColapsos(setciudadesColapsadas, setvuelosColapsados);
             mejorRutaReal.exito=0;
             Logger.info(fechaPedido+" Numero de Pedido: "+numPedido+" "+codCiudadO+
                     "-"+codCiudadF +" No se encontro ruta -no hay capacidad");
             Logger.info("Caida x ciudades: ==========");
             for(String ciudadCol :setciudadesColapsadas){
                 String[] partir=ciudadCol.split("-");
                 Logger.info(ciudadCol+" Capacidad ciudad: "+
                         ciudades.get(partir[0]).proyeccionCiudad.get(partir[1]+"-"+partir[2]));
             }
             Logger.info("Caida x vuelos: ==========");
             for(String vueloCol :setvuelosColapsados){
                 String[] partir2=vueloCol.split("/");
                 int capVuelo=vuelosBD.get(partir2[0]).cantidadPaquetesXDia.get(Integer.parseInt(partir2[1]));            
                 System.out.println(vueloCol+" Capacidad vuelo: "+capVuelo);
             }      
             return mejorRutaReal;
        }
        
    }
    
    
    
    
    public void imprimirCiudades(){
        Set set = getCiudades().entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            System.out.print(me.getKey() + ": ");
            Ciudad ciudadActual=(Ciudad)me.getValue();

            System.out.println("Nombre:"+ciudadActual.getNombre()+" Cantidad Paquetes: "+ciudadActual.getCantPaquetes());
            
        }
        //getCiudades().get("SKBO").print();
    }
    


    /**
     * @return the ciudades
     */
    public TreeMap<String,Ciudad> getCiudades() {
        return ciudades;
    }

    /**
     * @param ciudades the ciudades to set
     */
    public void setCiudades(TreeMap<String,Ciudad> ciudades) {
        this.ciudades = ciudades;
    }

    public void limpiarTodo(int dayweek){
        int dayweekAnterior=dayweek-3;
        if(dayweekAnterior<0)dayweekAnterior+=7;
        for(Ciudad ciudad: ciudades.values()){
            ciudad.inicializarProyeccion(dayweekAnterior);
        }
        for(Ruta vuelo: vuelosBD.values()){
            vuelo.inicializarCaps(dayweekAnterior);
        }

    }

    private int verificarCapacidades(ConjRutas rutaPrueba,Set<String> setciudadesColapsadas,Set<String> setvuelosColapsados,
                                     int dayweekcaps, int horaPedcaps,int flag){
        int i;
        int dayweek=dayweekcaps;
        int horaPed=horaPedcaps;
        ArrayList<Ruta> vuelosPrueba=rutaPrueba.vuelos;
        for(i=0;i<vuelosPrueba.size();i++){
            Ruta vuelo=vuelosPrueba.get(i);
            int horaPartida=vuelo.horaO;
            int horaDestino=vuelo.horaF;
            int horaInicio=horaPed;
            if(i>0) {
                horaInicio=vuelosPrueba.get(i-1).horaF;

            } // la hora de inicio pasa a ser 
               //hora de llegada del vuelo anterior
            //verificamos ciudad entrada
            Ciudad ciudad=ciudades.get(vuelo.getCiudadOrigen());
            // se verifica desde q entro o arrivo hasta que tomó el vuelo
            String llaveColapso=verificarCiudad(rutaPrueba,ciudad,horaInicio,dayweek,horaPartida,flag);
            if(!llaveColapso.equals("Libre")) { // ya no hay espacio en el almacen
                setciudadesColapsadas.add(vuelo.getCiudadOrigen()+"-"+llaveColapso);
                break;
            }
            //Ahora se verifica el vuelo
            if(horaPartida<horaInicio) //esto quiere decir q pasó un día solo por esperar el vuelo
                dayweek++;
            dayweek=dayweek%7;
            String llaveVuelo=vuelo.getCiudadOrigen()+"-"+vuelo.getCiudadFin()+"-"+horaPartida; //llave de los vuelosBD en general
            if(vuelosBD.get(llaveVuelo).cantidadPaquetesXDia.get(dayweek)==
                    vuelosBD.get(llaveVuelo).capMaxima){ // ya no hay espacio en el avion
                setvuelosColapsados.add(llaveVuelo+"/"+dayweek);
                break;
            }
            vuelosBD.get(llaveVuelo).cantidadPaquetesXDia.set(dayweek,vuelosBD.get(llaveVuelo).cantidadPaquetesXDia.get(dayweek)+flag);
            rutaPrueba.capacidades+=vuelosBD.get(llaveVuelo).cantidadPaquetesXDia.get(dayweek); //se considera caps d vuelosBD

            //ahora se verifica llegada al destino 
            if(horaDestino<horaPartida) dayweek=(dayweek+1)%7; // pasó un día mientras volaba

            //verificamos ciudad llegada
            Ciudad ciudadLlegada=ciudades.get(vuelo.getCiudadFin());
            if(i==vuelosPrueba.size()-1) { //si es destino final
                String llaveColapso2=verificarCiudad(rutaPrueba,ciudadLlegada,horaDestino,dayweek,horaDestino,flag);
                if(!llaveColapso2.equals("Libre")) { // ya no hay espacio en el almacen
                    setciudadesColapsadas.add(vuelo.getCiudadFin()+"-"+llaveColapso2);
                    break;
                }                        
            }                

        }
        if(i==vuelosPrueba.size()) return 1; //se encontró por lo menos una ruta
        else return 0;
    }

    private String verificarCiudad(ConjRutas rutaPrueba,Ciudad ciudad,int horaInicio,int dayweek,int horaPartidaCaps,int flag){
        String horaColapso="Libre";// no colapsa
        int horaPartida=horaPartidaCaps;
        if(horaPartida<horaInicio)horaPartida+=24;
        for(int i=horaInicio;i<=horaPartida;i++){
            if(i==24) dayweek++; // pasó un día
            dayweek=dayweek%7;
            int horaLlave=i%24;
            String key=""+dayweek+"-"+horaLlave;
            //System.out.println(key);
            if(ciudad.proyeccionCiudad.get(key)==ciudad.capacidadMaxima) return key;
            ciudad.proyeccionCiudad.put(key,ciudad.proyeccionCiudad.get(key)+flag);  //se actualiza si flag es 1
            //registramos el espacio libre q posee tomar esta ruta
            //Solo para ciudad origen y escalas ( No destino final)
            if((i==horaPartida) && (horaInicio!=horaPartidaCaps)) rutaPrueba.capacidades+=ciudad.proyeccionCiudad.get(key);
        }
        return horaColapso;
    }

    private  ArrayList<ConjRutas>  depurarRutas( ArrayList<ConjRutas> rutasPrecargadas,int horaPedido,int maxTiempoVuelo,int minPedido){
        ArrayList<ConjRutas> rutasfacti= new ArrayList<>();
        for(ConjRutas rutas :rutasPrecargadas){
            int horaPartida=rutas.vuelos.get(0).horaO;
            if((horaPartida<horaPedido) ||((horaPartida==horaPedido) && (minPedido!=0)) ) horaPartida+=24;
            int tEsperaInicial=horaPartida-horaPedido;
            int tTotal=tEsperaInicial+rutas.tiempo;
            if(tTotal<=maxTiempoVuelo) {
                rutasfacti.add(rutas);
                rutasfacti.get(rutasfacti.size()-1).tiempo=tTotal;
            }
            else{
                //System.out.println("Tiempo total: "+tTotal);
            }
        }
        return rutasfacti;
    }

    public ArrayList<Integer> capsCiudades(String Key){
        ArrayList<Integer> caps= new ArrayList<Integer>();
        for(Ciudad ciudad: ciudades.values()){
            caps.add(ciudad.proyeccionCiudad.get(Key));
        }
        return caps;
    }
    
    public ArrayList<Integer> capsAviones(int dia){
        ArrayList<Integer> caps= new ArrayList<Integer>();
        for(Ruta vuelo: vuelosBD.values()){
            caps.add(vuelo.cantidadPaquetesXDia.get(dia));
        }    
        return caps;
    }        
}
