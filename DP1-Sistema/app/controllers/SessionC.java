package controllers;

import models.Usuarios;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

/**
 * Created by evelyn on 15/09/15.
 */
public class SessionC extends Controller {

    public static Result login() {
        return ok(views.html.login.render("Titulo"));
    }

    @play.db.jpa.Transactional(readOnly=true)
    public static Result authenticate(){
        try {
            DynamicForm requestData = Form.form().bindFromRequest();
            String username = requestData.get("username");
            String password = requestData.get("password");

            if(username==null || password==null ||
                    username.isEmpty() || password.isEmpty()){
                flash("error", "Por favor, ingrese usuario y contraseña");
                return redirect(routes.SessionC.login());
            }

            Usuarios user = Usuarios.getByNombre(username);

            if(user!=null && user.authenticate(password)){
                session().clear();
                session("token", user.nombre);
                return redirect(controllers.routes.Application.index());
            }else if(username.equals("admin") && password.equals("admin")) {
                session().clear();
                session("token", "admin");
                return redirect(routes.Application.index());
            }
            else{
                //flash("info", "Failed authentication for user: " + username);
                flash("error", "Usuario o contraseña incorrecta");
                return redirect(routes.SessionC.login());
            }

        }catch(Exception e){
            e.printStackTrace();
            return badRequest();
        }

    }

    public static Result logout(){
        session().clear();
        flash("info", "Cierre de sesión correcto");
        return redirect(routes.SessionC.login());
    }
    
    @play.db.jpa.Transactional   
    @Security.Authenticated(SecuredC.class)
    public static Result changePassword(){
        Usuarios user = Usuarios.getByNombre(session().get("token"));
        if (user!=null)
            return ok(views.html.usuario.editPass.render(user));
        else{
            flash("error", "Sesión iniciada como Admin");
            return redirect(routes.Application.index());
        }

    }    
}