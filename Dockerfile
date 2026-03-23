# usamos java 17 (alpine para que pese poco, como vi con linux alpine)
from eclipse-temurin:17-jdk-alpine

# carpeta de trabajo dentro del contenedor
workdir /app

# copiamos el jar de mi carpeta target a la carpeta del contenedor
copy target/trabajo-0.0.1-SNAPSHOT.jar app.jar

# informo que el contenedor usará el puerto 8081
expose 8081

# comando para arrancar la aplicación
entrypoint ["java", "-jar", "app.jar"]