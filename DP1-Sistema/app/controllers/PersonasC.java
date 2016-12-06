package controllers;

import models.Personas;
import models.Usuarios;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.persona.*;

import play.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.DateTime;
import java.util.Date;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;

import play.mvc.Security;

@Security.Authenticated(SecuredC.class)
public class PersonasC extends Controller {
    
    @play.db.jpa.Transactional   
	public static Result index() {            
        return ok(views.html.persona.index.render(Personas.getAll()));
    }

	public static Result newO() {            
        return ok(views.html.persona.newPersona.render("Titulo"));
    }

    @play.db.jpa.Transactional
    public static Result create() {
        try{
            DynamicForm requestData = Form.form().bindFromRequest();
            
            String nombre = requestData.get("nombre");
            String apellido = requestData.get("apellido");
            String dni = requestData.get("dni");
            String correo = requestData.get("correo");
            
            Personas per = new Personas(nombre + " " + apellido, dni, correo, 1);
            
            per.save();                                

            flash("success", "La persona fue creada con éxito");
            return redirect(controllers.routes.PersonasC.index());

        }catch (Exception e){

            Logger.error(e.getMessage());
            flash("error", "Ocurrió un Error");
            return redirect(controllers.routes.PersonasC.index());
        }

    }
	
    @play.db.jpa.Transactional      
    public static Result edit(Long idPersona) {            
        return ok(views.html.persona.edit.render(Personas.getById(idPersona)));
    }

    @play.db.jpa.Transactional
    public static Result update() {
        try{
            DynamicForm requestData = Form.form().bindFromRequest();
            
            Long id = new Long(Integer.parseInt(requestData.get("id")));
            String nombre = requestData.get("nombre");
            String apellido = requestData.get("apellido");
            String dni = requestData.get("dni");
            String correo = requestData.get("correo");
            
            Personas per = Personas.getById(id);
            
            per.correo = correo;

            per.save();                                

            flash("success", "La persona fue actualizada con éxito");
            return redirect(controllers.routes.PersonasC.index());

        }catch (Exception e){

            Logger.error(e.getMessage());
            flash("error", "Ocurrió un Error");
            return redirect(controllers.routes.PersonasC.index());
        }

    }

    @play.db.jpa.Transactional      
    public static Result delete(Long idPersona) {            
        Personas.delete(idPersona);
        return ok(views.html.persona.index.render(Personas.getAll()));
    }
}
