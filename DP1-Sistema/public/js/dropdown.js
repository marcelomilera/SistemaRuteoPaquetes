$("#departamento").change(function(event){
	$.get("provincias/"+event.target.value+"",function(response,departamento){
		$("#provincia").empty();		
		for(i=0;i<response.length;i++){
			$("#provincia").append("<option value='"+response[i].id+"'>"+response[i].nombre+"</option")	;
		}
	})

})

$("#provincia").change(function(event){
	$.get("distritos/"+event.target.value+"",function(response,provincia){
		// console.log(response);
		$("#distrito").empty();		
		for(i=0;i<response.length;i++){
			$("#distrito").append("<option value='"+response[i].id+"'>"+response[i].nombre+"</option")	;
		}
	})

})

// $("#departamento").change(event=>{
// 	$.get('provincias/${event.target.value}',function(res,departamento){
// 			$("#provincia").empty();		
// 			res.forEach(element=>{

// 				$("#provincia".append('<option value=$(element.id)> ${element.nombre} </option>'));
// 			});
// 	});
// });