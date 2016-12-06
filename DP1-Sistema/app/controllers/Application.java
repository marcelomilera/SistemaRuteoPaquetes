package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import play.Logger;
import play.libs.Json;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import play.Play; 
import static java.lang.Math.toIntExact;
import java.util.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import play.mvc.WebSocket;
import play.mvc.WebSocket.*;
import play.mvc.LegacyWebSocket;
import play.mvc.LegacyWebSocket.*;
import java.io.IOException;
import java.text.ParseException;
import play.mvc.Security;

@Security.Authenticated(SecuredC.class)
public class Application extends Controller {
	public static boolean pausa=false;

    @play.db.jpa.Transactional(readOnly=true)
    public static Result index() {            
        return ok(views.html.ciudad.index.render(Ciudades.getAll()));
    }
    @play.db.jpa.Transactional(readOnly=true)
    public static Result simulation() {            
        return ok(views.html.simulation.render(Ciudades.getAll(),Vuelos.getAll()));
    }
	
	// Websocket interface
	public static LegacyWebSocket<String> socket(){
		return WebSocket.whenReady((in, out) -> {
			SimpleChat.start(in, out);
		});
	}
	
	public static class Capacidades{
		public ArrayList<Integer> capsCiudad;
		public ArrayList<Integer> capsVuelo;
	}
	
	public static class Paquete{
		public String id;
		public String fecha;
		public String hora;
		public String origen;
		public String destino;
		public Integer factible;
		public Integer stop;
	}
	public static Result action(String act){
		if (act.equals("1")){
			pausa=false;
		}
		else{
			pausa=true;
		}
		SimpleChat.notifyAll(act);
		return ok(act);
	}	

	public static Result requestPackage(Long scale, Long time){
		//Se debe correr todos los paquetes que calcen en ese periodo de tiempo y escala
		Logger.info("Escala: "+scale+" Time: "+time);
		
		BufferArchivos baPedidos = BufferArchivos.getInstance();
		
		TreeMap<Integer,String[]> listaPedidosEscala=null;
		if(scale==1)
			listaPedidosEscala = baPedidos.getListaPedidosEscala1();
		else
			listaPedidosEscala = baPedidos.getListaPedidosEscala2();		

		
		Logger.info("Cantidad horas en escala: "+listaPedidosEscala.size());
			
		GestorCiudades gc=GestorCiudades.getInstance();
		Logger.info("Se leyo informacion con exito");	
		
		String [] pedidos = null;
		pedidos = listaPedidosEscala.get(toIntExact(time));
		
		Paquete pk = new Paquete();
		if(toIntExact(time)==listaPedidosEscala.size()){
			pk.stop=1;//para que el front sepa que ya se debe terminar de iterar
		}else{
			pk.stop=0;//para que en el front sepa que se seguira iterando
		}
		pk.factible=0;

		
		
		
		Logger.info("Cantidad paquetes: "+pedidos.length+"-"+time);
		Gson gson = new Gson();
		
		Boolean todosFactibles=true;
		for(int i=0;i<pedidos.length && todosFactibles && !pausa;i++){			
			String [] datosPaquete = pedidos[i].trim().split("-");//0:id 1:fecha 2:hora 3:ciudad origen 4:ciudad fin					
			Logger.info("Va a entrar a DFS");


			String fechaActual="";
            String fechaPedido=datosPaquete[1].substring(6,8)+"/"+datosPaquete[1].substring(4,6)+"/"+datosPaquete[1].substring(0,4);
            Calendar c=Calendar.getInstance();
            try {
                c.setTime(new SimpleDateFormat("dd/M/yyyy").parse(fechaPedido));
            } catch (ParseException ex) {
                //Logger.getLogger(DFS.class.getName()).log(Level.SEVERE, null, ex);
            }
            int dayweek=c.get(Calendar.DAY_OF_WEEK)-1;//porque la semana comienza el domingo y el arreglo del 0-6  
            if(!fechaPedido.equals(fechaActual)){//Se limpia el dia si ha cambiado de todos los almacenes

                gc.limpiarTodo(dayweek);
                fechaActual=fechaPedido;
                if(i==0) Logger.info("Primer pedido: "+fechaPedido);
            }

			ConjRutas mejorRuta=gc.DFS(datosPaquete[3],datosPaquete[4],i,datosPaquete[2],1,datosPaquete[1]);
			Logger.info("Salio de DFS");
			String resultado=null;


			Capacidades caps= new Capacidades();
			String [] hora=datosPaquete[2].trim().split(":");

			caps.capsCiudad=gc.capsCiudades(dayweek+"-"+Integer.parseInt(hora[0]));
			//Logger.info("ggggggggggggggg     "+dayweek+"-"+hora[0]+ " ggggggggggggggggggggggggg");
			caps.capsVuelo=gc.capsAviones(dayweek);



			if(mejorRuta.exito==1){//1 es Factible
				resultado="Numpedido: "+i+" "+pedidos[i]+" Ruta: "+ mejorRuta.imprimirRecorrido();
				String resultadoJSON=(String)gson.toJson(caps, Capacidades.class);
				SimpleChat.notifyAll(resultadoJSON);//Acá se podría mandar un Json con los datos del paquete
				
			}else{
				/*Logger.info("Entro aca Estado: "+mejorRuta.getEstadoRuta());
				Logger.info("Id: "+datosPaquete[0]);
				Logger.info("fecha: "+datosPaquete[1]);
				Logger.info("Hora: "+datosPaquete[2]);
				Logger.info("Ciudad Origen: "+datosPaquete[3]);
				Logger.info("Ciudad Fin: "+datosPaquete[4]);*/
				
				// if(mejorRuta.exito==0){//Si se cae por condición de capacidades
				// 	todosFactibles=false;
				// 	Logger.info("Entro aca Estado: "+mejorRuta.exito);
				// 	pk.factible=mejorRuta.exito;
				// 	pk.stop=1;//para que el front sepa que ya se debe terminar de iterar
				// 	pk.id=datosPaquete[0];
				// 	pk.fecha=datosPaquete[1];
				// 	pk.hora=datosPaquete[2];
				// 	pk.origen=datosPaquete[3];
				// 	pk.destino=datosPaquete[4];
				// }
				// resultado="Numpedido: "+i+" No se encontro ruta - Ciudad Origen: "+ datosPaquete[3]+" Ciudad Fin: "+datosPaquete[4];	
			}	
			//SimpleChat.notifyAll(resultado);
			Logger.info(resultado);
		}
		return ok(Json.toJson(pk));
	}
	
	
    public static Result login() {            
        return ok(views.html.login.render("Titulo"));
    }   
    
}
