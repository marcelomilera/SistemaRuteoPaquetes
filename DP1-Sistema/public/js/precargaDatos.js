///////////////////// OBTENER CIUDADES
var  cities= new Map();

var oTable = document.getElementById('ciudades_tabla');
//gets rows of table
var rowLength = oTable.rows.length;

//loops through rows    
for (i = 0; i < rowLength; i++){

   //gets cells of current row
   var ciudadFila = oTable.rows.item(i).cells;

   var codCiud=ciudadFila.item(0).innerHTML;
   var city= {
      id: i,
      codigo: ciudadFila.item(0).innerHTML,
      nombre: ciudadFila.item(1).innerHTML,
      pais: ciudadFila.item(2).innerHTML,
      latitud: ciudadFila.item(3).innerHTML,
      longitud: ciudadFila.item(4).innerHTML,
      huso: ciudadFila.item(5).innerHTML
   }
   cities.set(codCiud,city);
}
////////OBTENER VUELOS
var vuelos = [];

var oTableV = document.getElementById('vuelos_tabla');
//gets rows of table
var rowLengthV = oTableV.rows.length;

//loops through rows    
for (i = 0; i < rowLengthV; i++){
  var vueloFila = oTableV.rows.item(i).cells;
  //Procesamos la hora
  var dt = new Date(vueloFila.item(3).innerHTML);
  var horaSalida = dt.getUTCHours();
  var husoO=cities.get(vueloFila.item(1).innerHTML).huso;
  //console.log(horaSalida+"-"+husoO);
  var horaSalidaEstandar=horaSalida-husoO;
  if(horaSalidaEstandar<0) horaSalidaEstandar+=24;
  horaSalidaEstandar%=24;
  var dt2 = new Date(vueloFila.item(4).innerHTML);
  var horaLlegada = dt2.getUTCHours();
  var husoF=cities.get(vueloFila.item(2).innerHTML).huso;
  var horaLlegadaEstandar=horaLlegada-husoF;
  if(horaLlegadaEstandar<0) horaLlegadaEstandar+=24;
  horaLlegadaEstandar%=24;  
  var tiempo=horaLlegadaEstandar-horaSalidaEstandar;
  if(tiempo<0) tiempo+=24;
  var ciudadOri=cities.get(vueloFila.item(1).innerHTML);
  var ciudadFin=cities.get(vueloFila.item(2).innerHTML);
  var sla=(ciudadFin.latitud-ciudadOri.latitud)/tiempo;
  var slo= (ciudadFin.longitud-ciudadOri.longitud)/tiempo;
  //console.log("SLA: "+sla+" SLO: "+slo);
    var vuelo={
        codigo: vueloFila.item(0).innerHTML,
        ciudadO: vueloFila.item(1).innerHTML,
        ciudadF: vueloFila.item(2).innerHTML,
        horaSalida: horaSalidaEstandar,
        horaLlegada: horaLlegadaEstandar,
        stepLatitud: sla,
        stepLongitud: slo,
        tiempo: tiempo
    }
   vuelos.push(vuelo);
   //console.log(vuelos[i]);
}
vuelos.sort(function (a, b) { //ordenamos por hora :v
  if (a.horaSalida > b.horaSalida) {
    return 1;
  }
  if (a.horaSalida < b.horaSalida) {
    return -1;
  }
  // a must be equal to b
  return 0;
});
var indicesVuelos= new Array(); //indices de vuelos segun hora
indicesVuelos.push(0);// marcamos el inicio
var ini=0;
for(var n=0;n<vuelos.length; n++){
    if(vuelos[n].horaSalida!=ini) {
      indicesVuelos.push(n);
      ini=vuelos[n].horaSalida;
    }  
}