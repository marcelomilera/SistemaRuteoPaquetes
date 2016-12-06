package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
//import org.joda.time.LocalDateTime;
//import org.joda.time.DateTime;
import play.db.jpa.JPA;

import java.util.Date;
import java.text.DateFormat;
import java.util.List;

@Entity
public class Pedidos {
    @Id @GeneratedValue
    public Long id=null;
    @NotNull    
    public String ciudad_origen;
    @NotNull
    public String ciudad_destino;
    @NotNull    
    public Date fecha_registro;
    @NotNull
    public Long personas_id;
    @NotNull
    public Long personas2_id;

    @ManyToOne
    @JoinColumn(name="ciudad_origen", insertable = false, updatable = false)
    public Ciudades ciudadOrigen;

    @ManyToOne
    @JoinColumn(name="ciudad_destino", insertable = false, updatable = false)
    public Ciudades ciudadDestino;

    @ManyToOne
    @JoinColumn(name="personas_id", insertable = false, updatable = false)
    public Personas persona;

    @ManyToOne
    @JoinColumn(name="personas2_id", insertable = false, updatable = false)
    public Personas persona2;

    /*@ManyToOne
    @JoinColumn(name="id", insertable = false, updatable = false)    
    public Pedidos_x_vuelos ruta;
    */
    private Pedidos(){

    }

    public Pedidos(String ciudad_origen, String ciudad_destino, Long personas_id, Long personas2_id){
        this.ciudad_origen=ciudad_origen;
        this.ciudad_destino=ciudad_destino;
        this.personas_id=personas_id;
        this.personas2_id=personas2_id;
        this.fecha_registro=new Date();        
    }
    
        
    public static List<Pedidos> getAll(){                   
        TypedQuery<Pedidos> query = JPA.em().createQuery(
           "FROM Pedidos", Pedidos.class);
        return query.getResultList();                  
    }

    public static Pedidos getById(Long id){
        Pedidos t = JPA.em().find(Pedidos.class, id);
        
        return t;
    }

    public void save(){
        JPA.em().persist(this);
        JPA.em().flush();
    }

    public static void delete(Long id){
        Pedidos p = Pedidos.getById(id);
        JPA.em().remove(p);
    }
}
