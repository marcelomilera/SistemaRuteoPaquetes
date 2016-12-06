
var oTable = document.getElementById('rutas_tabla');
//gets rows of table
var rowLength = oTable.rows.length;
var ciudadesRuta= [];
//loops through rows    
for (i = 0; i < rowLength; i++){

   //gets cells of current row
   var rutaFila = oTable.rows.item(i).cells;
   var ciudadO=cities.get(rutaFila.item(0).innerHTML);
   var ciudadD=cities.get(rutaFila.item(2).innerHTML);
   ciudadesRuta.push(ciudadO);
   ciudadesRuta.push(ciudadD);
}
///////GRAFICOS PARA LA SIMULACION (INICIALIZACION)
AmCharts.ready(function() {
    AmCharts.theme = AmCharts.themes.dark;
  map = new AmCharts.AmMap();
    map.path = "http://www.amcharts.com/lib/3/";

  map.areasSettings = {
    unlistedAreasColor: "#000000",
    unlistedAreasAlpha: 0.1
  };
  map.imagesSettings.balloonText = "<span style='font-size:14px;'><b>[[title]]</b>: [[value]]</span>";
  map.addTitle("Ruta del pedido",14);
  var dataProvider = {
    mapVar: AmCharts.maps.worldLow,
    images: [],
    lines: []
  }
var targetSVG = "M9,0C4.029,0,0,4.029,0,9s4.029,9,9,9s9-4.029,9-9S13.971,0,9,0z M9,15.93 c-3.83,0-6.93-3.1-6.93-6.93S5.17,2.07,9,2.07s6.93,3.1,6.93,6.93S12.83,15.93,9,15.93 M12.5,9c0,1.933-1.567,3.5-3.5,3.5S5.5,10.933,5.5,9S7.067,5.5,9,5.5 S12.5,7.067,12.5,9z";
 var planeSVG = "m2,106h28l24,30h72l-44,-133h35l80,132h98c21,0 21,34 0,34l-98,0 -80,134h-35l43,-133h-71l-24,30h-28l15,-47";

// svg path for plane icon
var latitudes=[];
var longitudes=[];
    // CREAR  PUNTOS DE LAS CIUDADES
    for (var i = 0; i < ciudadesRuta.length; i++) {
        var ciudad= ciudadesRuta[i];
        var lat = ciudad.latitud;
        latitudes.push(lat);
        var long = ciudad.longitud;
        longitudes.push(long);
        var nombre = ciudad.nombre; 
        dataProvider.images.push({
            id: i,
            svgPath: targetSVG,
            longitude: long,
            title: nombre,
            latitude: lat,
            scale: 0.3
        });
    }
    dataProvider.lines.push({
      id: "linea",
      arc: -0.85,
      alpha: 0.3,
      color: "#990000",
      latitudes: latitudes,
      longitudes: longitudes
    });

    dataProvider.images.push({
      svgPath: planeSVG,
      positionOnLine:0,
      color: "#585869",
      animateAlongLine: true,
      lineId: "linea",
      flipDirection: false,
      loop: false,
      scale: 0.03,
      positionScale:1.3
    });



  map.dataProvider = dataProvider;

  map.write("chartdiv");

});