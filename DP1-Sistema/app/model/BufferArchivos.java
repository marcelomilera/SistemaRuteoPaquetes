/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import play.Logger;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import play.Play; 
/**

/**
 *
 * @author Sebastián
 */



public class BufferArchivos {
    
    
    //private TreeMap<Integer, TreeMap<Integer,String[]>> listasPorEscala = new TreeMap<Integer, TreeMap<Integer,String[]>>();
    private TreeMap<Integer,String[]> listaPedidosEscala1 =new TreeMap<Integer, String[]>();
    private TreeMap<Integer,String[]> listaPedidosEscala2 =new TreeMap<Integer, String[]>();
    //private TreeMap<Integer,String[]> listaPedidosEscala3 =new TreeMap<Integer, String[]>();
    private int escala1=1;
    private int escala2=3;
    //private int escala3=6;
    
	private static BufferArchivos instance;
	
	public static BufferArchivos getInstance(){
		return instance;
	}
	
	static {
		Gson gson = new Gson();		
		try (Reader reader = new FileReader( Play.application().getFile("/conf/pedidosArutear_7.json"))) {
			instance=gson.fromJson(reader, BufferArchivos.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private BufferArchivos(){
		
	}
	
    public void generarJson(){
        Gson gson = new Gson();

        try (FileWriter writer = new FileWriter("pedidosData3Dias.json")) {
            gson.toJson(this,writer);
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    
	/*
    public void organizarPedidos(GestorCiudades gestor){
        Ciudad siguienteCiudad;
        String fechaActual="";
        boolean primero=true;
        int horaInicio;
        int limiteInferiorEscala1=0,limiteInferiorEscala2=0,limiteInferiorEscala3=0,limiteSuperiorEscala1=0,limiteSuperiorEscala2=0,limiteSuperiorEscala3=0;
        int numeroPedidosEscala1=0,numeroPedidosEscala2=0,numeroPedidosEscala3=0;
        int indiceEscala1=0,indiceEscala2=0,indiceEscala3=0;
        String[] pedidosAlmacenados1=new String[2000],pedidosAlmacenados2=new String[2000],pedidosAlmacenados3=new String[2000];
        while((siguienteCiudad=gestor.siguienteEnvio()) != null){
            int horaPedido=Integer.parseInt((siguienteCiudad.getUltimaHora().split(":")[0]));
            String temporalFecha[]=new String[3];
            temporalFecha=siguienteCiudad.getUltimaFecha().split("/");
            if(primero){
                limiteInferiorEscala1=limiteInferiorEscala2=limiteInferiorEscala3=horaInicio=horaPedido;
                limiteSuperiorEscala1=limiteInferiorEscala1+getEscala1();
                limiteSuperiorEscala2=limiteInferiorEscala2+getEscala2();
                limiteSuperiorEscala3=limiteInferiorEscala3+getEscala3();
                if(limiteSuperiorEscala1>24)limiteSuperiorEscala1-=24;
                if(limiteSuperiorEscala2>24)limiteSuperiorEscala2-=24;
                if(limiteSuperiorEscala3>24)limiteSuperiorEscala3-=24;
                primero=false;
            }
            //cuando el indice menor es realmente menor que el superior numericamente
            if(limiteInferiorEscala1<limiteSuperiorEscala1)
            {
                //si se encuentra dentro del intervalo
                if(limiteInferiorEscala1<=horaPedido && limiteSuperiorEscala1>=horaPedido)
                    pedidosAlmacenados1[numeroPedidosEscala1++]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                
                //en caso no se encuentre dentro del intervalo se modifica el intervalo
                else{
                    String[] temporalAmeter=new String[numeroPedidosEscala1];
                    for(int i=0;i<numeroPedidosEscala1;++i)temporalAmeter[i]=pedidosAlmacenados1[i];
                    getListaPedidosEscala1().put(indiceEscala1++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                    for(int i=0;i<pedidosAlmacenados1.length;++i)pedidosAlmacenados1[i]="";//limpiamos los pedidos
                    pedidosAlmacenados1[0]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();//se inserta el leido
                    numeroPedidosEscala1=1;//reseteamos el contador de pedidos tomando el cuenta el ya ingresado
                    limiteInferiorEscala1+=getEscala1();//se cambian los intervalos
                    limiteSuperiorEscala1+=getEscala1();
                    if(limiteInferiorEscala1>24)limiteInferiorEscala1-=24;//se normalizan
                    if(limiteSuperiorEscala1>24)limiteSuperiorEscala1-=24;
                }
            }
            else{//minimo 23 maixmo 2 por ejemplo
                if(limiteInferiorEscala1<= horaPedido || horaPedido <=limiteSuperiorEscala1)
                    pedidosAlmacenados1[numeroPedidosEscala1++]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                
                //en caso no se encuentre dentro del intervalo se modifica el intervalo
                else
                {
                    String[] temporalAmeter=new String[numeroPedidosEscala1];
                    for(int i=0;i<numeroPedidosEscala1;++i)temporalAmeter[i]=pedidosAlmacenados1[i];
                    getListaPedidosEscala1().put(indiceEscala1++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                    for(int i=0;i<pedidosAlmacenados1.length;++i)pedidosAlmacenados1[i]="";//limpiamos los pedidos
                    pedidosAlmacenados1[0]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                    numeroPedidosEscala1=1;
                    limiteInferiorEscala1+=getEscala1();
                    limiteSuperiorEscala1+=getEscala1();
                    if(limiteInferiorEscala1>24)limiteInferiorEscala1-=24;
                    if(limiteSuperiorEscala1>24)limiteSuperiorEscala1-=24;
                }
            }
            if(limiteInferiorEscala2<limiteSuperiorEscala2)
            {
                //si se encuentra dentro del intervalo
                if(limiteInferiorEscala2<=horaPedido && limiteSuperiorEscala2>=horaPedido)
                    pedidosAlmacenados2[numeroPedidosEscala2++]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                
                //en caso no se encuentre dentro del intervalo se modifica el intervalo
                else{
                    String[] temporalAmeter=new String[numeroPedidosEscala2];
                    for(int i=0;i<numeroPedidosEscala2;++i)temporalAmeter[i]=pedidosAlmacenados2[i];
                    getListaPedidosEscala2().put(indiceEscala2++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                    for(int i=0;i<pedidosAlmacenados2.length;++i)pedidosAlmacenados2[i]="";//limpiamos los pedidos
                    pedidosAlmacenados2[0]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                    numeroPedidosEscala2=1;
                    limiteInferiorEscala2+=getEscala1();
                    limiteSuperiorEscala2+=getEscala1();
                    if(limiteInferiorEscala2>24)limiteInferiorEscala2-=24;
                    if(limiteSuperiorEscala2>24)limiteSuperiorEscala2-=24;
                }
            }
            else{
                if(limiteInferiorEscala2<= horaPedido || horaPedido <=limiteSuperiorEscala2)
                    pedidosAlmacenados2[numeroPedidosEscala1++]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                
                //en caso no se encuentre dentro del intervalo se modifica el intervalo
                else{
                    String[] temporalAmeter=new String[numeroPedidosEscala2];
                    for(int i=0;i<numeroPedidosEscala2;++i)temporalAmeter[i]=pedidosAlmacenados2[i];
                    getListaPedidosEscala2().put(indiceEscala2++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                    for(int i=0;i<pedidosAlmacenados2.length;++i)pedidosAlmacenados2[i]="";//limpiamos los pedidos
                    pedidosAlmacenados2[0]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                    numeroPedidosEscala2=1;
                    limiteInferiorEscala2+=getEscala1();
                    limiteSuperiorEscala2+=getEscala1();
                    if(limiteInferiorEscala2>24)limiteInferiorEscala2-=24;
                    if(limiteSuperiorEscala2>24)limiteSuperiorEscala2-=24;
                }
            }
            if(limiteInferiorEscala3<limiteSuperiorEscala3)
            {
                //si se encuentra dentro del intervalo
                if(limiteInferiorEscala3<=horaPedido && limiteSuperiorEscala3>=horaPedido)
                    pedidosAlmacenados3[numeroPedidosEscala3++]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                
                //en caso no se encuentre dentro del intervalo se modifica el intervalo
                else{
                    String[] temporalAmeter=new String[numeroPedidosEscala3];
                    for(int i=0;i<numeroPedidosEscala3;++i)temporalAmeter[i]=pedidosAlmacenados3[i];
                    getListaPedidosEscala3().put(indiceEscala3++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                    for(int i=0;i<pedidosAlmacenados3.length;++i)pedidosAlmacenados3[i]="";//limpiamos los pedidos
                    pedidosAlmacenados3[0]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                    numeroPedidosEscala3=1;
                    limiteInferiorEscala3+=getEscala1();
                    limiteSuperiorEscala3+=getEscala1();
                    if(limiteInferiorEscala3>24)limiteInferiorEscala3-=24;
                    if(limiteSuperiorEscala3>24)limiteSuperiorEscala3-=24;
                }
            }
            else{
                if(limiteInferiorEscala3<= horaPedido || horaPedido <=limiteSuperiorEscala3)
                    pedidosAlmacenados3[numeroPedidosEscala3++]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                
                //en caso no se encuentre dentro del intervalo se modifica el intervalo
                else{
                    String[] temporalAmeter=new String[numeroPedidosEscala3];
                    for(int i=0;i<numeroPedidosEscala3;++i)temporalAmeter[i]=pedidosAlmacenados3[i];
                    getListaPedidosEscala3().put(indiceEscala3++, temporalAmeter);//insertamos los paquetes correspondientes al intervalo
                    for(int i=0;i<pedidosAlmacenados3.length;++i)pedidosAlmacenados3[i]="";//limpiamos los pedidos
                    pedidosAlmacenados3[0]=temporalFecha[2]+temporalFecha[1]+temporalFecha[0]+"-"+siguienteCiudad.getUltimaHora()+"-"+siguienteCiudad.getCodigo()+"-"+siguienteCiudad.getUltimoDestino();
                    numeroPedidosEscala3=1;
                    limiteInferiorEscala3+=getEscala1();
                    limiteSuperiorEscala3+=getEscala1();
                    if(limiteInferiorEscala3>24)limiteInferiorEscala3-=24;
                    if(limiteSuperiorEscala3>24)limiteSuperiorEscala3-=24;
                }
            }
            
            siguienteCiudad.avanzarBuffer();
        }
    }*/

    /**
     * @return the listaPedidosEscala1
     */
    public TreeMap<Integer,String[]> getListaPedidosEscala1() {
        return listaPedidosEscala1;
    }

    /**
     * @param listaPedidosEscala1 the listaPedidosEscala1 to set
     */
    public void setListaPedidosEscala1(TreeMap<Integer,String[]> listaPedidosEscala1) {
        this.listaPedidosEscala1 = listaPedidosEscala1;
    }

    /**
     * @return the listaPedidosEscala2
     */
    public TreeMap<Integer,String[]> getListaPedidosEscala2() {
        return listaPedidosEscala2;
    }

    /**
     * @param listaPedidosEscala2 the listaPedidosEscala2 to set
     */
    public void setListaPedidosEscala2(TreeMap<Integer,String[]> listaPedidosEscala2) {
        this.listaPedidosEscala2 = listaPedidosEscala2;
    }
	/*
     
    public TreeMap<Integer,String[]> getListaPedidosEscala3() {
        return listaPedidosEscala3;
    }

    public void setListaPedidosEscala3(TreeMap<Integer,String[]> listaPedidosEscala3) {
        this.listaPedidosEscala3 = listaPedidosEscala3;
    }*/

    /**
     * @return the escala1
     */
    public int getEscala1() {
        return escala1;
    }

    /**
     * @param escala1 the escala1 to set
     */
    public void setEscala1(int escala1) {
        this.escala1 = escala1;
    }

    /**
     * @return the escala2
     */
    public int getEscala2() {
        return escala2;
    }

    /**
     * @param escala2 the escala2 to set
     */
    public void setEscala2(int escala2) {
        this.escala2 = escala2;
    }

	/*
    public int getEscala3() {
        return escala3;
    }

    public void setEscala3(int escala3) {
        this.escala3 = escala3;
    }*/


}
