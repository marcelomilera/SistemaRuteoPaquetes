package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by evelyn on 17/10/15.
 */
public class SecuredC extends Security.Authenticator{

    @Override
    public String getUsername(Context ctx) {
        return ctx.session().get("token");
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        //return redirect(ctx.request().uri());
        return redirect(controllers.routes.SessionC.login());
    }
}
