$(document).ready(function(){
  $('.bxslider').bxSlider();
});


$(document).ready(function(){
	$('.inputmodify').change(function(){
		document.getElementById('btn-savechanges1').disabled=false;
	});
});


function openLogin(){
	location.href="login";
}

function openHistoriaCallao(){
	location.href="historia-sede-callao-ver-mas#marca";
}

function hacerBusqueda(){
	location.href="reserva-bungalows-b-s#marca";
}
function openHistoriaPapusClub(){
	location.href="historia-papusclub-ver-mas#marca";
}



function confirmar_registro_solicitud_postulante(){
	location.href="REGISTRAR-SOLICITUD-POSTULANTE-O-AL";
}


function registrarAmbiente(){
	location.href="registrar-ambiente";
}

function modificarAmbiente(){
	location.href="modificar-ambiente";
}
function registrarNuevoUsuario(){
			location.href="usuario/create";
}

function anularReservaAmbiente(){
	location.href="anular-reserva-ambiente-s";
}

function anularReservaAmbienteB(){
	location.href="anular-reserva-ambiente-b-s";
}
function buscarAmbientes(){
	location.href="anular-reserva-ambiente-b-s";
}

function buscar_ambientes_x_user(){
	location.href="#";
}

function openTallerFutbol(){
	location.href="futbol-s";
}
function reservarBungalow(){
	location.href="reserva-bungalows-s";
}
function buscarBungalow(){
	location.href="bungalows";
}


function inputLimiter(e,allow) {
    var AllowableCharacters = '';

    if (allow == 'Letters'){AllowableCharacters=' ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz';}
    if (allow == 'Numbers'){AllowableCharacters='1234567890';}
    if (allow == 'NameCharacters'){AllowableCharacters=' ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz-.\'._@';}
    if (allow == 'NameCharactersAndNumbers'){AllowableCharacters='1234567890 ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz-\'_.@';}
    if (allow == 'DoubleFormat'){AllowableCharacters='1234567890,.';}
    if (allow == 'Nulo'){AllowableCharacters='';} //sirve para colocarle a las fechas deben ser obligatoriamente ingresadas por el picker

    var k = document.all?parseInt(e.keyCode): parseInt(e.which);
    if (k!=13 && k!=8 && k!=0){
        if ((e.ctrlKey==false) && (e.altKey==false)) {
        return (AllowableCharacters.indexOf(String.fromCharCode(k))!=-1);
        } else {
        return true;
        }
    } else {
        return true;
    }
}