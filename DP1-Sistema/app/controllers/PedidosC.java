package controllers;

import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.pedido.*;

import play.Logger;
//import org.joda.time.LocalDateTime;
//import org.joda.time.DateTime;
import java.util.List;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;

import play.libs.Json;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.File;

import play.Play; 
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.mail.*;

import play.mvc.Security;

@Security.Authenticated(SecuredC.class)
public class PedidosC extends Controller {
    @play.db.jpa.Transactional      
	public static Result index() {            
        return ok(views.html.pedido.index.render(Pedidos.getAll()));
    }

	public static Result newO() {            
        return ok(views.html.pedido.newPedido.render("Titulo"));
    }

    @play.db.jpa.Transactional
    public static Result create() {
        try{
            DynamicForm requestData = Form.form().bindFromRequest();

            String ciudad_origen = requestData.get("ciudad_origen");
            String ciudad_destino = requestData.get("ciudad_destino");
            Long personas_id = new Long(Integer.parseInt(requestData.get("personas_id")));
            Long personas2_id = new Long(Integer.parseInt(requestData.get("personas2_id")));
            
            Long id;

            Pedidos pedido = new Pedidos(ciudad_origen, ciudad_destino, personas_id, personas2_id); 
           
            //Aca se debe llamar al algoritmo
			Logger.info("Se lee informacion para el algoritmo");
			
			
			GestorCiudades temporal=GestorCiudades.getInstance();
			
			Logger.info("Se leyo informacion con exito");
			
			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			DateFormat hourFormat = new SimpleDateFormat("HH:mm");
			
			Date date = new Date();
			
			
			ConjRutas mejorRuta=temporal.DFS(ciudad_origen,ciudad_destino,1,hourFormat.format(date),1,dateFormat.format(date));
			
			if(mejorRuta.exito==1){//1 es Factible
			// 	pedido.save();
			// 	Logger.info("Se lee informacion para el algoritmo");	
			// 	Logger.info("Ruta: "+ mejorRuta.imprimirRecorrido());
				
			// 	Logger.info("ID Pedido "+pedido.id);
				
			// 	Logger.info("Longitud lista: "+mejorRuta.getListaRutaEscogida().size());
				
			// 	for(int i=0;i<mejorRuta.getListaRutaEscogida().size();i++){
			// 		Ruta r = mejorRuta.getListaRutaEscogida().get(i);
					
			// 		Logger.info("pedido.id " + pedido.id);
			// 		Logger.info("peronas_id " + personas_id);					
			// 		Logger.info("getHoraOrigen " + hourFormat.format(hourFormat.parse(r.getHoraOrigen())));
			// 		Logger.info("getHoraOrigen " + hourFormat.parse(r.getHoraOrigen()));
			// 		Logger.info("getHoraFin " + hourFormat.format(hourFormat.parse(r.getHoraFin())));
			// 		Logger.info("getHoraFin " + hourFormat.parse(r.getHoraFin()));
			// 		Logger.info("i " + i);					

			// 		Vuelos v = Vuelos.getIdByOtherValues(r.getCiudadOrigen(),r.getCiudadFin(), hourFormat.parse(r.getHoraOrigen()), hourFormat.parse(r.getHoraFin()));
					
			// 		Logger.info("v.id " + v.id);

			// 		Logger.info("Se encontro vuelo");
					
			// 		Integer tiempoEspera = mejorRuta.getTiemposEspera().get(i);
			// 		Integer tiemposTraslado = mejorRuta.getTiemposTraslado().get(i);
					
			// 		Logger.info("tiempoEspera " + tiempoEspera);
			// 		Logger.info("tiemposTraslado " + tiemposTraslado);					
					
			// 		Pedidos_x_vuelos pXV = new Pedidos_x_vuelos(pedido.id,personas_id,v.id,i,tiempoEspera,tiemposTraslado);
			// 		Logger.info("Se creo pedidoXvuelo");
					
			// 		pXV.save();
			// 		Logger.info("Se guardo pedidoXvuelo");
			// 	}
				
				Personas p1 = Personas.getById(personas_id);
				Personas p2 = Personas.getById(personas2_id);
				Ciudades c1 = Ciudades.getById(ciudad_origen);
				Ciudades c2 = Ciudades.getById(ciudad_destino);
				DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm");									

				Email email = new SimpleEmail();
	            email.setHostName("smtp.gmail.com");
	            email.setSmtpPort(587);
	            email.setAuthenticator(new DefaultAuthenticator("contact.simusoft@gmail.com", "simusoft123"));
	            email.setSSLOnConnect(true);
	            email.setFrom("contact.simusoft@gmail.com", "SIMUSOFT");
	            email.setSubject("[Simusoft] - Pedido");
	            email.setMsg("Buenas tardes," + '\n' + "Simusoft le informa que se ha registrado el siguiente pedido:" +
	            	"código N°: " + pedido.id + '\n' +
	            	"Remitente: " + p1.nombre + '\n' +
	            	"Destinatario: " + p2.nombre + '\n' + '\n' +
	            	"Origen: " + c1.nombre + '\n' +
	            	"Destino: " + c2.nombre + '\n' + '\n' +
	            	"Fecha: " + f.format(pedido.fecha_registro)
	            	);
	            email.addTo(p1.correo);
	            email.addTo(p2.correo);
	            email.send();       
				
				flash("success", "El pedido fue creado con éxito");
			}else{
				Logger.info("No se encontro ruta");
				flash("error", "No se encontro ruta para el paquete");
			}

            
            return redirect(controllers.routes.PedidosC.index());

        }catch (Exception e){

            Logger.error(e.getMessage());
            flash("error", "Ocurrió un Error");
            return redirect(controllers.routes.PedidosC.index());
        }

    }
	
    @play.db.jpa.Transactional      
    public static Result detail(Long idPedido) {
    	Pedidos pedido = Pedidos.getById(idPedido);
    	List<Pedidos_x_vuelos> rutas = Pedidos_x_vuelos.getByPedido(idPedido);
    	Date fecha = new Date();
    	Logger.info("fecha acual: " + fecha);    	
    	Logger.info("fecha de registro: " + pedido.fecha_registro);
    	int diaAnt = pedido.fecha_registro.getDate();
    	int horaAnt = pedido.fecha_registro.getHours();
    	int tam = rutas.size();
    	int i = 1;

    	for(Pedidos_x_vuelos ruta : rutas){    
    		Logger.info("hora salida: " + ruta.vuelo.hora_salida.getHours());
    		if(ruta.vuelo.hora_salida.getHours()>=horaAnt){
    			ruta.vuelo.hora_salida.setDate(diaAnt);
    		}
    		else{
    			ruta.vuelo.hora_salida.setDate(++diaAnt);	
    		}
    		ruta.vuelo.hora_salida.setMonth(pedido.fecha_registro.getMonth());
    		ruta.vuelo.hora_salida.setYear(pedido.fecha_registro.getYear());    		
    		
    		if(ruta.vuelo.hora_salida.getHours()<ruta.vuelo.hora_llegada.getHours()){
    			ruta.vuelo.hora_llegada.setDate(diaAnt);	
    		}
    		else{
    			ruta.vuelo.hora_llegada.setDate(++diaAnt);    			
    		}
    		ruta.vuelo.hora_llegada.setMonth(pedido.fecha_registro.getMonth());
    		ruta.vuelo.hora_llegada.setYear(pedido.fecha_registro.getYear());

    		Logger.info("hora_salida: " + ruta.vuelo.hora_salida);
    		Logger.info("hora_llegada: " + ruta.vuelo.hora_llegada);
    		if (fecha.compareTo(ruta.vuelo.hora_salida)>0 && fecha.compareTo(ruta.vuelo.hora_llegada)<0){
    			ruta.estado = "En vuelo";
    		}
    		if (fecha.compareTo(ruta.vuelo.hora_salida)<=0){
    			ruta.estado = "En espera";	
    		}
			if (fecha.compareTo(ruta.vuelo.hora_llegada)>=0){
				if (i==tam){
					ruta.estado = "Entregado";
				}else{
					ruta.estado = "Aterrizado";	
				}    			
    		}
    		horaAnt = ruta.vuelo.hora_llegada.getHours();
    		Logger.info("horaAnd: " + horaAnt);
			ruta.save();
			i++;
		}

        return ok(views.html.pedido.detail.render(Ciudades.getAll(),pedido, rutas));
    }

    @play.db.jpa.Transactional      
    public static Result delete(Long idPedido) {            
        Pedidos.delete(idPedido);
        return ok(views.html.pedido.index.render(Pedidos.getAll()));
    }
}
