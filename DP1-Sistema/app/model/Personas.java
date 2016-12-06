package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
//import org.joda.time.LocalDateTime;
//import org.joda.time.DateTime;
import play.db.jpa.JPA;

import java.util.Date;
import java.util.List;


@Entity
public class Personas {
    @Id @GeneratedValue    
    public Long id=null;
    @NotNull
    public String nombre;
    @NotNull
    public String dni;
    @NotNull
    public String correo;
    @NotNull
    public int tipo_persona;

    //@OneToOne(cascade = CascadeType.ALL)
    //public Usuarios usuario;

    private Personas(){

    }

    public Personas(String nombre, String dni, String correo, int tipo_persona){
        this.nombre=nombre;
        this.dni=dni;
        this.correo=correo;
        this.tipo_persona=tipo_persona;                    
    }
    
        
    public static List<Personas> getAll(){                   
        TypedQuery<Personas> query = JPA.em().createQuery(
           "FROM Personas", Personas.class);
        return query.getResultList();                  
    }

    public void save(){
        JPA.em().persist(this);
        JPA.em().flush();
    }

    public static Personas getById(Long id){
        Personas t = JPA.em().find(Personas.class, id);
        
        return t;
    }

    public static void delete(Long id){
        Personas p = Personas.getById(id);
        JPA.em().remove(p);
    }
}
