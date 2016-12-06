package controllers;

import models.Vuelos;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.vuelo.*;

import play.Logger;
//import org.joda.time.LocalDateTime;
//import org.joda.time.DateTime;
import java.util.Date;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import play.mvc.Security;

@Security.Authenticated(SecuredC.class)
public class VuelosC extends Controller {
    
    @play.db.jpa.Transactional   
	public static Result index() {            
        return ok(views.html.vuelo.index.render(Vuelos.getAll()));
    }
    
	public static Result newO() {            
        return ok(views.html.vuelo.newVuelo.render("Titulo"));
    }
    
    @play.db.jpa.Transactional
    public static Result create() {
        try{
            DynamicForm requestData = Form.form().bindFromRequest();

            String ciudad_origen = requestData.get("ciudad_origen");
            String ciudad_destino = requestData.get("ciudad_destino");
            String hora_salidaS = requestData.get("hora_salida");
            String hora_llegadaS = requestData.get("hora_llegada");            

            DateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
            Date hora_salida = format.parse(hora_salidaS);
            Date hora_llegada = format.parse(hora_llegadaS);

            Vuelos vuelo = new Vuelos(ciudad_origen, ciudad_destino, hora_salida, hora_llegada); 
                            
            vuelo.save();

            flash("success", "El plan de vuelo fue creado con éxito");
            return redirect(controllers.routes.VuelosC.index());

        }catch (Exception e){

            Logger.error(e.getMessage());
            flash("error", "Ocurrió un Error");
            return redirect(controllers.routes.VuelosC.index());
        }

    }
    
    @play.db.jpa.Transactional      
    public static Result edit(Long idVuelo) {            
        return ok(views.html.vuelo.edit.render(Vuelos.getById(idVuelo)));
    }

    @play.db.jpa.Transactional
    public static Result update() {
        try{
            DynamicForm requestData = Form.form().bindFromRequest();

            Long id = new Long(Integer.parseInt(requestData.get("id")));
            String ciudad_origen = requestData.get("ciudad_origen");
            String ciudad_destino = requestData.get("ciudad_destino");
            String hora_salidaS = requestData.get("hora_salida");
            String hora_llegadaS = requestData.get("hora_llegada");            

            DateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
            Date hora_salida = format.parse(hora_salidaS);
            Date hora_llegada = format.parse(hora_llegadaS);

            Vuelos vuelo = Vuelos.getById(id); 

            vuelo.hora_salida=hora_salida;
            vuelo.hora_llegada=hora_llegada;

            vuelo.save();

            flash("success", "El plan de vuelo fue actualizado con éxito");
            return redirect(controllers.routes.VuelosC.index());

        }catch (Exception e){

            Logger.error(e.getMessage());
            flash("error", "Ocurrió un Error");
            return redirect(controllers.routes.VuelosC.index());
        }

    }
    
    @play.db.jpa.Transactional      
    public static Result delete(Long idVuelo) {            
        Vuelos.delete(idVuelo);
        return redirect(controllers.routes.VuelosC.index());
    }	
}