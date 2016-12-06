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
public class Pedidos_x_vuelos {
    @Id @GeneratedValue
	public Long id=null;
	@NotNull
    public Long pedidos_id;
    @NotNull
    public Long pedidos_personas_id;
    @NotNull
    public Long vuelos_id;
    @NotNull
    public Integer num_Vuelo;

	public Integer tiempo_EsperaH;
	
	public Integer tiempo_TrasladoH;

    public String estado;

	@ManyToOne
    @JoinColumn(name="pedidos_personas_id", insertable = false, updatable = false)
    public Personas persona;
	
	@ManyToOne
    @JoinColumn(name="vuelos_id", insertable = false, updatable = false)
    public Vuelos vuelo;
	
	@ManyToOne
    @JoinColumn(name="pedidos_id", insertable = false, updatable = false)
    public Pedidos pedido;
	
    private Pedidos_x_vuelos(){

    }

    public Pedidos_x_vuelos(Long pedidos_id, Long pedidos_personas_id, Long vuelos_id, Integer numVuelo){
        this.pedidos_id=pedidos_id;
        this.pedidos_personas_id=pedidos_personas_id;
        this.vuelos_id=vuelos_id;        
        this.num_Vuelo=numVuelo;                  
    }
    
	public Pedidos_x_vuelos(Long pedidos_id, Long pedidos_personas_id, Long vuelos_id, Integer numVuelo,Integer tiempo_EsperaH, Integer tiempo_TrasladoH){
        this.pedidos_id=pedidos_id;
        this.pedidos_personas_id=pedidos_personas_id;
        this.vuelos_id=vuelos_id;        
        this.num_Vuelo=numVuelo;     
		this.tiempo_EsperaH=tiempo_EsperaH;
		this.tiempo_TrasladoH=tiempo_TrasladoH;
    }
	
        
    public static List<Pedidos_x_vuelos> getAll(){                   
        TypedQuery<Pedidos_x_vuelos> query = JPA.em().createQuery(
           "FROM Pedidos_x_vuelos", Pedidos_x_vuelos.class);
        return query.getResultList();                  
    }

    public static List<Pedidos_x_vuelos> getByPedido(Long idPedido){                   
        TypedQuery<Pedidos_x_vuelos> query = JPA.em().createQuery(
           "Select v from Pedidos_x_vuelos v " +
                    "where v.pedidos_id=:pedidos_id", Pedidos_x_vuelos.class);
        query.setParameter("pedidos_id", idPedido);
                    
        return query.getResultList();                  
    }

	public void save(){
        JPA.em().persist(this);
        JPA.em().flush();
    }
}
