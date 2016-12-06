package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.joda.time.DateTime;
import play.db.jpa.JPA;

import java.util.Date;
import java.util.List;


@Entity
public class Ciudades {
    @Id
    @NotNull
    public String cod_ciudad;
    @NotNull
    public String nombre;
    @NotNull
    public String pais;
    @NotNull
    public String abreviado;
    @NotNull
    public int capacidad_almacen;    
    @NotNull
    public int latitud;  
    @NotNull
    public int longitud;      
    @NotNull
    public int huso;     
    /*
    @OneToMany(mappedBy="ciudadDestino")
    public Pedidos pedido_in;
    @OneToMany(mappedBy="ciudadOrigen")
    public Pedidos pedido_out;
    */
    private Ciudades(){

    }

    public Ciudades(String cod_ciudad, String nombre, String pais, String abreviado, int capacidad_almacen,int lati,int longi,int huso){        
        this.cod_ciudad=cod_ciudad;
        this.nombre=nombre;
        this.pais=pais;
        this.abreviado=abreviado;            
        this.capacidad_almacen=capacidad_almacen;     
        this.latitud=lati;      
        this.longitud=longi; 
        this.huso=huso;
    }
    
    public static List<Ciudades> getAll(){                   
        TypedQuery<Ciudades> query = JPA.em().createQuery(
           "FROM Ciudades", Ciudades.class);
        return query.getResultList();                  
    }

    public static Ciudades getById(String id){
        Ciudades t = JPA.em().find(Ciudades.class, id);
        
        return t;
    }

    public void save(){
        JPA.em().persist(this);
        JPA.em().flush();
    }

    public static void delete(String id){
        Ciudades p = Ciudades.getById(id);
        JPA.em().remove(p);
    }
}    

