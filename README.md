# cdc-java-replication
POC para probar el mecanismo de réplica CDC utilizando Java. 

## Spring Boot App

- Utilizando maven, ejecutar `mvn clean install`
- Iniciar aplicativo con la clase `SpringQuartzApp`

## Configuraciones

### Generales

- `application.yml`: configure el perfile activo.
- `application-{profile}.yml`: configuraciones de spring por profile.
- `databases-{profile}.yml`: configuraciones de las bases de datos.
- `jobs-{profile}.yml`: configuraciones de los particiones y tablas a sincronizar. 
- `quartz-{profile}.properties`: configuraciones de quartz

### Perfiles

- `local-h2`: utliza base de datos h2 en memoria
- `local-sql`: utliza base de datos SQL SERVER utilizando docker. 
- `local-full-h2`: utliza base de datos h2 en memoria, se configura un escenario similar a producción
- `local-full-sql`: utliza base de datos SQL SERVER utilizando docker, se configura un escenario similar a producción

## SQL Server Docker

Tener instalado Docker y ejecutar:

- `cd docker-sql`
- `docker-compose -f docker-compose.yml -f docker-compose-init.yml up -d`

Se necesita 100GB de disco libres (25GB por cada base de datos). Si desea modificarlos esto ir a `docker-sql\scripts\` 
y modificar los archivos `Database-*.sql` en la línea 7: `SIZE = 27392000KB` por el tamaño que considere. 

## Configuración JobStore

### Utilizar h2

- Se utiliza **h2** en memoria para aumentar la velocidad de ejecución y paralelismo. 
- Es el valor por defecto configurado. 

### Utilizar SQL Server

En caso se quiera utilizar SQL Server, en la base de datos de origen (PSNBAS) 
ejecutar el siguiente script `scripts\QRZT_TABLES.sql`. Utilizar SQL Server Management Studio. 

- Cambiar en `quartz-*.properties` la siguiente propiedad:
    `org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.MSSQLDelegate`
- Cambiar en `databases-*.properties` la siguiente propiedad: `databases:jobStore` por los parámetros de conexión a utilizar. 

## Docker
- Ejecutar: `docker build -t nuamx.com/cdc-java-replication:poc .`
- Ejecutar: `docker run --name cdc-java-replication -it --rm nuamx.com/cdc-java-replication:poc`

## Información útil

- Configuración Hikari: https://github.com/brettwooldridge/HikariCP. Los parámetros más relevantes fueron expuestos en 
  `databases-{profile}.yml`
