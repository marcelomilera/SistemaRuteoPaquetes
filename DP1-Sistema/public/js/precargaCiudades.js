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
      codigo: ciudadFila.item(0).innerHTML,
      nombre: ciudadFila.item(1).innerHTML,
      pais: ciudadFila.item(2).innerHTML,
      latitud: ciudadFila.item(3).innerHTML,
      longitud: ciudadFila.item(4).innerHTML,
      huso: ciudadFila.item(5).innerHTML
   }
   cities.set(codCiud,city);
}