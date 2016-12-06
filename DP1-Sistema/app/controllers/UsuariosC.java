package controllers;

import models.Usuarios;
import models.Personas;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.usuario.*;

import play.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.DateTime;
import java.util.Date;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.JPA;

import org.mindrot.jbcrypt.BCrypt;
import play.mvc.Security;

import org.apache.commons.mail.*;

@Security.Authenticated(SecuredC.class)
public class UsuariosC extends Controller {
    
    @play.db.jpa.Transactional   
	public static Result index() {            
        return ok(views.html.usuario.index.render(Usuarios.getAll()));
    }

	public static Result newO() {            
        return ok(views.html.usuario.newUser.render("Titulo"));
    }

    @play.db.jpa.Transactional
    public static Result create() {
        try{
            DynamicForm requestData = Form.form().bindFromRequest();

            String cuenta = requestData.get("cuenta");
            String nombre = requestData.get("nombre");
            String apellido = requestData.get("apellido");
            String dni = requestData.get("dni");
            String correo = requestData.get("correo");
            
            Personas per = new Personas(nombre + " " + apellido, dni, correo, 1);
            
            per.save();                    

            Usuarios user = new Usuarios(cuenta, "1234", per.id); 
            
            user.save();

            Email email = new SimpleEmail();
            email.setHostName("smtp.gmail.com");
            email.setSmtpPort(587);
            email.setAuthenticator(new DefaultAuthenticator("contact.simusoft@gmail.com", "simusoft123"));
            email.setSSLOnConnect(true);
            email.setFrom("contact.simusoft@gmail.com", "SIMUSOFT");
            email.setSubject("[Simusoft] - Nuevo Usuario");            
            email.setMsg("Buenas tardes," + '\n' + "Simusoft le informa que se ha registrado exitosamente su nuevo usuario:" +  '\n' + "Nombre de usuario: " + user.nombre + '\n' + "contraseña: " + user.contraseña);
            email.addTo(user.persona.correo);                    
            email.send();              

            flash("success", "El usuario fue creado con éxito");
            return redirect(controllers.routes.UsuariosC.index());

        }catch (Exception e){

            Logger.error(e.getMessage());
            flash("error", "Ocurrió un Error");
            return redirect(controllers.routes.UsuariosC.index());
        }

    }
	
    @play.db.jpa.Transactional      
    public static Result delete(Long idUsuario) {            
        try{
            Usuarios.delete(idUsuario);
            flash("success", "Usuario eliminado con éxito");
            return redirect(controllers.routes.UsuariosC.index());
        }catch (Exception e){            
            flash("error", "Ocurrió un error");
            return redirect(controllers.routes.UsuariosC.index());
        }
        
    }    

    @play.db.jpa.Transactional  
    public static Result updatePassword(){        
        
        try{
            Usuarios user = Usuarios.getByNombre(session().get("token"));

            DynamicForm requestData = Form.form().bindFromRequest();

            String pwActual = requestData.get("pwActual");
            String pwNueva = requestData.get("pwNueva");
            String pwNueva2 = requestData.get("pwNueva2");            
            
            //Verificar
            if (BCrypt.checkpw(pwActual,user.contraseña)){
                if (pwNueva.equals(pwNueva2)){
                    user.setPassword(pwNueva);
            
                    user.save();

                    flash("success", "Se cambió la contraseña con éxito");

                    Email email = new SimpleEmail();
                    email.setHostName("smtp.gmail.com");
                    email.setSmtpPort(587);
                    email.setAuthenticator(new DefaultAuthenticator("contact.simusoft@gmail.com", "simusoft123"));
                    email.setSSLOnConnect(true);
                    email.setFrom("contact.simusoft@gmail.com", "SIMUSOFT");
                    email.setSubject("[Simusoft] - Cambio de contraseña");
                    email.setMsg("Buenas tardes," + '\n' + "Simusoft le informa que su contraseña ha sido modificada." + '\n' + "nueva contraseña: " + pwNueva);                    
                    email.addTo(user.persona.correo);                    
                    email.send();                    

                    return redirect(controllers.routes.Application.index());        
                }
                else{
                    flash("error", "Confirmar contraseña");
                    return redirect(controllers.routes.SessionC.changePassword());           
                }

            }
            else{
                session().clear();                
                flash("error", "La contraseña ingresada no es correcta");                
                return redirect(routes.SessionC.login());
            }
            

        }catch (Exception e){

            Logger.error(e.getMessage());
            flash("error", "Ocurrió un error");
            return redirect(controllers.routes.Application.index());
        }        
    }

    public static Result account(){
        return null;
    }

}
