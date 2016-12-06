var websocket;
var url=window.location.href;
var res = url.split(":"); 
var ip=res[1].substring(2);
console.log("ip: "+ip);
var websocketEchoServerUri = "ws://"+ip+":9000/socket";
console.log(websocketEchoServerUri);
var serverLog = document.getElementById("maplog");
var escala=1;
///////GRAFICOS PARA LA SIMULACION (INICIALIZACION)
AmCharts.ready(function() {
    AmCharts.theme = AmCharts.themes.dark;
  map = new AmCharts.AmMap();
    map.path = "http://www.amcharts.com/lib/3/";

  map.areasSettings = {
    unlistedAreasColor: "#000000",
    unlistedAreasAlpha: 0.1
  };
  map.imagesSettings.balloonText = "<span style='font-size:14px;'><b>[[title]]</b>: [[description]]</span>";
  map.addTitle("Hora actual: 00:00", 14);
  map.addTitle("Dias transcurridos: 0",14);
  var dataProvider = {
    mapVar: AmCharts.maps.worldLow,
    images: []
  }
var targetSVG = "M9,0C4.029,0,0,4.029,0,9s4.029,9,9,9s9-4.029,9-9S13.971,0,9,0z M9,15.93 c-3.83,0-6.93-3.1-6.93-6.93S5.17,2.07,9,2.07s6.93,3.1,6.93,6.93S12.83,15.93,9,15.93 M12.5,9c0,1.933-1.567,3.5-3.5,3.5S5.5,10.933,5.5,9S7.067,5.5,9,5.5 S12.5,7.067,12.5,9z";

// svg path for plane icon

  // CREAR  PUNTOS DE LAS CIUDADES
  for (var i = 0; i < rowLength; i++) {
      var city = oTable.rows.item(i).cells;
      var cod = city.item(0).innerHTML+2000; 
      var lat = parseInt(city.item(3).innerHTML); 
      var long = parseInt(city.item(4).innerHTML); 
      var nombre = city.item(1).innerHTML; 
      dataProvider.images.push({
          id: cod,
          svgPath: targetSVG,
          longitude: long,
          title: nombre,
          latitude: lat,
          scale: 0.3,    
          color: "#000000",
          description: "Normal"  
      });
  }

  map.dataProvider = dataProvider;

  map.write("chartdiv");
  websocket = initWebSocket(websocketEchoServerUri);
});

// Funciones del socket

function initWebSocket(wsUri) {
  var ws = new WebSocket(wsUri);
  ws.onopen = onConnect;
  ws.onclose = onClose;
  ws.onerror = onError;
  ws.onmessage = updateLog;
  return ws;
}


function onConnect(wsEvent) {
  writeToScreen("Server connection successful. Listening for data now.");  
  //interval = setInterval(getDataFromServer, 2000); //we're simulating a datafeed by calling our getDataFromServer method every 2 seconds
}

function onError(wsEvent) {
  writeToScreen("<span style='color: red'>ERROR:" + wsEvent + "</span>");
}

function onClose(wsEvent) {
  writeToScreen("Server connection closed");
  clearInterval(interval);
}

//For debug messaging
function writeToScreen(message) {
  var pre = document.createElement("p");
  pre.style.wordWrap = "break-word";
  pre.innerHTML = message;
  serverLog.appendChild(pre);
  serverLog.scrollTop = serverLog.scrollHeight;
}

function action(act){
  $.get( "/simulation/action?act="+act).done(function (data){    
    if (data == "1"){
      if(isSendRequest==0){
          isSendRequest=1;
          recursiveVuelosPaquetes(contador);
      }  
    }
  });
}

function updateLog(wsEvent) {
  //var newData = JSON.parse(wsEvent.data);
  //chartData.push.apply(chartData, newData);
  // keep only 50 datapoints on screen for the demo
  if (wsEvent.data == "1"){
      writeToScreen("INICIO DE SIMULACION");
      togglePlay();      
  }
  else
    if (wsEvent.data == "0"){
      writeToScreen("PAUSA SIMULACION");
      pause=1;           
      // stop playing (clear interval)
      clearInterval( interval );
      //currentTime=0;
      interval=undefined;
    }
    else {
      writeToScreen("<span style='color: blue'>Received: " + wsEvent.data + "</span>");
    }
  //chart.validateData(); //call to redraw the chart with new data
}


function mostrarResultadosRuteos(data){
  //el data representa la informacion traida del algoritmo en formato Json
  //alert( data);
  if(data.factible==2){
    writeToScreen("<span style='color: red'>No es factible por capacidad de almacén Id paquete:"+data.id+"</span>");    
    map.validateData();
  }else{
    if(data.factible==3){
      writeToScreen("<span style='color: red'>No es factible por capacidad de vuelo Id paquete:"+data.id+"</span>");
    }
  }
}

function recursiveVuelosPaquetes(contador) {
  var stop=0;
  var factible=0;
  
  if (pause==1){
    writeToScreen("SCREEN - PAUSADO");    
    return;
  }
  writeToScreen("ESCALA: " + escala);
  $.get( "/simulation/requestPackage?scale="+escala+"&time="+contador).done(function (data) {
    stop=data.stop;
    factible=data.factible;    
    if (stop==0) {
      isSendRequest=0;
      recursiveVuelosPaquetes(contador+1);
    }else{
      if(factible!=0){
        var id = cities.get(data.destino).id;
        
        map.dataProvider.images[id].color= "#CC0000";
        map.dataProvider.images[id].scale= 1;
        map.dataProvider.images[id].description= "Colapsado";
        map.validateData();
        writeToScreen("FIN DE SIMULACION");
        pause=1;           
        // stop playing (clear interval)
        clearInterval( interval );
        //currentTime=0;
        interval=undefined;
        mostrarResultadosRuteos(data);
      }
    }

  });  
}


/**
 * The code responsible for animating the motion chart data
 */

// initilize variables
var isSendRequest=0;
var currentTime = 0;
var diaSimulacion=0;
var interval;
var speed = 1000; // time between frames in milliseconds
var inicioAviones=cities.size; 
var planeSVG = "m2,106h28l24,30h72l-44,-133h35l80,132h98c21,0 21,34 0,34l-98,0 -80,134h-35l43,-133h-71l-24,30h-28l15,-47";
var contador=0;
var pause=0;

// function to start stop
function togglePlay() {
  pause=0;
  //var divisor=1;
  //var opcion=document.getElementById('selVel').selectedIndex;
  //if(opcion==1) divisor=5;
  //else if(opcion==2) divisor=10;

  escala=parseInt($('#escalaTiempo').val());
  
  // check if animation is playing (intverla is set)
  if ( interval!=undefined ) {
    
    // stop playing (clear interval)
    clearInterval( interval );
    //currentTime=0;
    interval=undefined;
    return;
  }
  
  // start playing
  interval = setInterval( function () {

    // var numLineas=map.dataProvider.lines.length;
    // for(var i=0;i<)
    var hora=currentTime;
    if(currentTime<10) hora="0"+hora;
    map.titles.pop();   
    map.titles.pop(); 
    map.addTitle("Hora actual: "+ hora+":00", 14);
    map.addTitle("Dias transcurridos: "+diaSimulacion,14);
    //map.validateNow();      
      while (map.dataProvider.lines.length > 0) {
          map.dataProvider.lines.pop();
      } 
    
    var inicioIndex=indicesVuelos[currentTime];
    var finIndex=vuelos.length;
    if(currentTime!=23) finIndex=indicesVuelos[currentTime+1];

    ///////actualizamos aviones
    for(var j=inicioAviones;j<map.dataProvider.images.length;j++){
        var avion=map.dataProvider.images[j];
        var vuelo=vuelos[avion.id];

        if(avion.customData==0){
          avion.deleteObject();
          //console.log("Borrando");
        }
        else{
          var lon=parseInt(avion.longitude)+vuelo.stepLongitud;
          var lat=parseInt(avion.latitude)+vuelo.stepLatitud;
          avion.longitude=lon;
          avion.latitude=lat;
          avion.customData--;         
        }

    }

    //agregamos los nuevos aviones
    for(var i=inicioIndex;i<finIndex;i++){
        var vuelo=vuelos[i];
        //datos de la ciudad origen
        var id=i;
        var latO=cities.get(vuelo.ciudadO).latitud;
        var lonO=cities.get(vuelo.ciudadO).longitud;
        //datos de la ciudad destino
        var latF=cities.get(vuelo.ciudadF).latitud;
        var lonF=cities.get(vuelo.ciudadF).longitud;

        var ciudO=cities.get(vuelo.ciudadO).nombre;
        var ciudF=cities.get(vuelo.ciudadF).nombre;        
        var titulo=ciudO+"-"+ciudF; 

        //CREAR AVIONES
        map.dataProvider.images.push({
          id: id,
          type: "circle",
          title: titulo,
          latitude: latO,
          longitude: lonO,
          width: 3,
          customData: vuelo.tiempo,
          height: 3
        });

    }

      currentTime++;
      if(currentTime==24) diaSimulacion++;
      currentTime%=24;
    // check if maybe we need to wrap to frame 0
    // update size of each bubble for the specific frame

    // for( var i = 0; i < map.dataProvider.images.length; i++ ) {
    if (pause==1){
      writeToScreen("MAP - PAUSADO");    
      return;
    }
    map.validateData();
    
  }, speed );  
  
}
