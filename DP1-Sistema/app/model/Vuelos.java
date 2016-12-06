package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import play.db.jpa.JPA;

import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import play.Logger;

@Entity
public class Vuelos {
    @Id @GeneratedValue
    public Long id;
    @NotNull
    public String ciudad_origen;
    @NotNull
    public String ciudad_destino;
    @NotNull
    public Date hora_salida;
    @NotNull
    public Date hora_llegada;

    private Vuelos(){

    }

    public Vuelos(String ciudad_origen, String ciudad_destino, Date hora_salida, Date hora_llegada){
        this.ciudad_origen=ciudad_origen;
        this.ciudad_destino=ciudad_destino;        
        this.hora_salida=hora_salida;
        this.hora_llegada=hora_llegada;                    
    }
    
        
    public static List<Vuelos> getAll(){                   
        TypedQuery<Vuelos> query = JPA.em().createQuery(
           "FROM Vuelos", Vuelos.class);
        return query.getResultList();                  
    }

    public void save(){
        JPA.em().persist(this);
        JPA.em().flush();
    }

	public static Vuelos getIdByOtherValues(String ciudad_origen,String ciudad_destino,Date hora_salida, Date hora_llegada){
                
        Vuelos vuelo = null;
        DateFormat hourFormat = new SimpleDateFormat("HH:mm");                
        try{            
            TypedQuery<Vuelos> query1 = JPA.em().createQuery(
                    "Select v from Vuelos v " +
                    "where v.ciudad_origen=:ciudad_origen " +
                    "AND v.ciudad_destino=:ciudad_destino " +
                    "AND v.hora_salida=:hora_salida " +
                    "AND v.hora_llegada=:hora_llegada ", Vuelos.class);
                    
            query1.setParameter("ciudad_origen", ciudad_origen);
			query1.setParameter("ciudad_destino", ciudad_destino);
			query1.setParameter("hora_salida", hora_salida);
			query1.setParameter("hora_llegada", hora_llegada);
			
			//vuelo=query1.getSingleResult();
            vuelo = query1.setFirstResult(0).setMaxResults(1).getSingleResult(); 
        }catch(NoResultException e){
            Logger.info("Vuelos: El query no obtuvo resultados");
            Logger.error(e.getMessage());
        }

        return vuelo;
    }
	
    public static Vuelos getById(Long id){
        
        Vuelos t = JPA.em().find(Vuelos.class, id);
        
        return t;
    }

    public static void delete(Long id){
        Vuelos p = Vuelos.getById(id);
        JPA.em().remove(p);
    }
}
