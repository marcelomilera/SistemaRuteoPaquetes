package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
//import org.joda.time.LocalDateTime;
//import org.joda.time.DateTime;
import play.db.jpa.JPA;

import java.util.Date;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

@Entity
public class Usuarios {
    @Id @GeneratedValue
    public Long id=null;
    @NotNull
    public String nombre;
    @NotNull
    public String contraseña;  
    @NotNull
    public Long personas_id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="personas_id", insertable = false, updatable = false)
    public Personas persona;

    private Usuarios(){ }

    public Usuarios(String nombre, String contraseña, Long personas_id) {
        this.nombre =nombre;        
        this.personas_id=personas_id;

        if(contraseña!=null && !contraseña.isEmpty()){
            this.contraseña= BCrypt.hashpw(contraseña, BCrypt.gensalt());
        }
    }

    public static Usuarios get(Long id){
        Usuarios a = JPA.em().find(Usuarios.class, id);
        if(a!=null)
            return null;
        return a;
    }

    public static Usuarios getByNombre (String nombre){
        Usuarios user = null;

        try{
            TypedQuery<Usuarios> query1 = JPA.em().createQuery(
                    "Select e from Usuarios e where e.nombre=:nombre", Usuarios.class);
            user = query1.setParameter("nombre", nombre).getSingleResult();
        }catch(NoResultException e){
        }

        return user;
    }

    public static List<Usuarios> getAll(){
        TypedQuery<Usuarios> query = JPA.em().createQuery(
                "FROM Usuarios ORDER BY nombre", Usuarios.class);
        return query.getResultList();
    }

    public boolean authenticate(String contraseña_candidate){
        try{
            return BCrypt.checkpw(contraseña_candidate,this.contraseña);
        }catch(Exception e){
            play.Logger.error("Failed authentication for user: " + this.nombre);
            e.printStackTrace();
            return false;
        }
    }

    public void save(){
        JPA.em().persist(this);
        JPA.em().flush();
    }

    public static Usuarios getById(Long id){
        Usuarios t = JPA.em().find(Usuarios.class, id);
        
        return t;
    }

    public static void delete(Long id){
        Usuarios p = Usuarios.getById(id);
        JPA.em().remove(p);
    }

    public void setPassword(String password){
        this.contraseña= BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
