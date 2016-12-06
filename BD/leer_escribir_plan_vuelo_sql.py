def leer_escribir_sql(nombArch):
    archLectura=open(nombArch+'.txt','r')
    archEscritura=open(nombArch+'.sql','w')
    id=1
    for linea in archLectura.readlines():
        lineaSinFinLinea=linea.split('\n')
        datos=lineaSinFinLinea[0].split('-')
        lineaW="INSERT INTO `vuelos` (`id`,`ciudad_origen`,`ciudad_destino`,`hora_salida`,`hora_llegada`) VALUES ("
        lineaW+=str(id)+",'"
        lineaW+=datos[0]+"','"
        lineaW+=datos[1]+"','"
        lineaW+=datos[2]+"','"
        lineaW+=datos[3]+"');"
        archEscritura.write(lineaW+'\n')
        id+=1
    archLectura.close()
    archEscritura.close()

leer_escribir_sql('plan_vuelo')
