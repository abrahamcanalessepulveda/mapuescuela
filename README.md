# Proyecto Mapuescuela

## Integrantes Grupo 1

- Alexis Rosales
- Camilo Quezada
- Cristóbal Celis
- Abraham Canales

## Videos de demostración

Video GUI usuario: https://youtu.be/ismbgR4EPbQ

Video profesor: https://youtu.be/rT2rYMvOxzg

Proyecto desarrollado para la asignatura **Integración de Plataformas**.

El proyecto consiste en una aplicación para apoyar el proceso de venta de productos donados de Mapuescuela.

Durante las distintas unidades se fue desarrollando el proyecto, comenzando con el modelamiento BPMN y posteriormente incorporando Web Services, Spring Boot, MySQL, Flowable y una interfaz web.

## Objetivo

Desarrollar una aplicación que permita manejar el proceso principal de venta de Mapuescuela.

El sistema permite registrar clientes, consultar los productos disponibles, generar pedidos y adjuntar comprobantes de pago, además de controlar si el pedido será retirado en el local o enviado por despacho.

También se agregó un plazo de 24 horas para adjuntar el comprobante de pago. Si el comprobante no es ingresado dentro de ese tiempo, el proceso puede continuar por la ruta de cancelación.

El resultado final corresponde a un MVP funcional que permite realizar las principales operaciones relacionadas con el proceso de venta definido para el proyecto.

## Desarrollo por unidades

### Unidad 1

En la Unidad 1 se realizó principalmente el modelamiento BPMN del proceso de venta.

Se trabajó en:

- Modelo AS-IS.
- Modelo TO-BE.
- Formularios.
- Registro del pedido.
- Carga y validación del comprobante.
- Retiro de productos.
- Despacho a domicilio.
- Temporizador de 24 horas.

Los archivos de esta unidad se encuentran en:

`documentacion/unidad1`

Este trabajo fue utilizado como base para las siguientes unidades.

### Unidad 2

En la Unidad 2 se comenzó a trabajar con Web Services utilizando Java.

Se desarrollaron operaciones relacionadas con:

- Registro de clientes.
- Generación de pedidos.
- Procesamiento de pagos.
- Actualización del estado de los pedidos.

También se realizaron cambios al BPMN para comenzar a relacionar el proceso con los servicios desarrollados.

Los archivos de esta unidad se encuentran en:

`documentacion/unidad2`

### Unidad 3

En la Unidad 3 se integraron los diferentes componentes desarrollados durante el proyecto.

Se trabajó con:

- Spring Boot.
- MySQL.
- Servicios REST.
- Interfaz web.
- Flowable.
- User Tasks.
- Tareas HTTP.
- External Worker.
- Retiro y despacho.
- Control de inventario.
- Cancelación de pedidos.
- Validación y rechazo de comprobantes.

Los documentos correspondientes a esta unidad se encuentran en:

`documentacion/unidad3`

La última versión del BPMN utilizada es:

`bpmn/Proceso_de_venta_-_Mapuescuela_U3_v9.bpmn20.xml`

Esta corresponde a la versión V9 final del proceso.

## Tecnologías utilizadas

- Java 17
- Spring Boot 4.0.8
- Maven
- MySQL 8
- HTML
- CSS
- JavaScript
- BPMN
- Flowable
- Docker
- Git
- GitHub
- Visual Studio Code

## Funcionamiento general

La interfaz web se comunica con Spring Boot mediante servicios REST.

Spring Boot se encarga de realizar las operaciones del sistema y guardar la información en MySQL.

Cuando se genera un pedido, también se inicia una instancia del proceso BPMN en Flowable.

De forma simple:

`Interfaz web -> Spring Boot -> MySQL`

Y para el proceso BPMN:

`Spring Boot -> Flowable -> Servicios REST -> MySQL`

También se utiliza un External Worker para la confirmación del pago:

`Flowable -> confirmarPago -> ConfirmarPagoWorker -> MySQL`

Flowable controla el avance del proceso y Spring Boot realiza la lógica de la aplicación.

## Funciones implementadas

El sistema permite:

- Consultar productos previamente registrados.
- Registrar clientes e iniciar sesión en el Portal de Clientes.
- Crear pedidos.
- Consultar pedidos.
- Seleccionar retiro o despacho.
- Adjuntar comprobantes.
- Ver el comprobante desde la interfaz administrativa.
- Validar comprobantes.
- Aprobar o rechazar pagos.
- Registrar el motivo de rechazo de un comprobante.
- Actualizar el stock cuando corresponde.
- Preparar pedidos.
- Registrar retiro.
- Registrar despacho.
- Registrar número de seguimiento.
- Confirmar entregas.
- Cancelar pedidos.

Actualmente el sistema no incluye una interfaz para registrar nuevos clientes o productos. Para las pruebas del MVP se utilizaron clientes y productos previamente cargados en la base de datos.

## Proceso BPMN

El proceso de venta funciona de forma general de la siguiente manera:

1. Se selecciona un cliente previamente registrado.
2. Se seleccionan los productos.
3. Se genera el pedido.
4. Se entregan los datos para realizar la transferencia.
5. El cliente adjunta el comprobante.
6. Un voluntario revisa el comprobante.
7. El pago puede ser aprobado o rechazado.
8. Si es aprobado, se actualiza el inventario.
9. Se prepara el pedido.
10. Se continúa por retiro o despacho.
11. Se registra la entrega.
12. El proceso finaliza.

También se utiliza un temporizador:

`PT24H`

Este representa el plazo de 24 horas disponible para adjuntar el comprobante.

Durante las pruebas se comprobó la ruta del temporizador mediante la ejecución controlada del Timer Job de Flowable.

## External Worker

Se agregó un External Worker para la actividad:

`Confirmar pago y descontar stock`

El tópico utilizado es:

`confirmarPago`

El componente desarrollado en Spring Boot es:

`ConfirmarPagoWorker`

Cuando Flowable llega a esta actividad genera un trabajo externo.

`ConfirmarPagoWorker` consulta estos trabajos y, cuando encuentra uno del tópico `confirmarPago`, utiliza el identificador del comprobante para realizar la aprobación correspondiente.

Para esto se utiliza la variable:

`idComprobante`

Después de realizar correctamente la operación, el Worker informa a Flowable que el trabajo terminó y el proceso puede continuar.

## Variables utilizadas en Flowable

Entre las principales variables utilizadas se encuentran:

- `idPedido`
- `idComprobante`
- `modalidadEntrega`
- `resultadoValidacion`
- `motivoRechazo`

`idPedido` permite relacionar el proceso con el pedido guardado en MySQL.

`idComprobante` es utilizado por el External Worker para saber qué comprobante debe procesar.

`modalidadEntrega` permite decidir entre retiro y despacho.

`resultadoValidacion` permite decidir si el pago fue aprobado o rechazado.

`motivoRechazo` permite enviar al proceso el motivo ingresado por el administrador cuando un comprobante es rechazado.

## Interfaz web

Se desarrolló una interfaz utilizando HTML, CSS y JavaScript.

Desde la interfaz del usuario se puede:

- Ver productos disponibles.
- Seleccionar un cliente previamente registrado.
- Crear un pedido.
- Seleccionar retiro o despacho.
- Consultar el pedido.
- Adjuntar un comprobante de pago.

También se desarrolló una interfaz administrativa que permite continuar con las diferentes etapas del proceso.

Desde esta interfaz se puede revisar el comprobante, aprobar o rechazar el pago, preparar el pedido y continuar con el retiro o despacho según corresponda.

La interfaz se comunica con los servicios REST de Spring Boot.

## Uso general del sistema

El uso principal del sistema comienza desde la interfaz del cliente.

Primero se selecciona un cliente previamente registrado y los productos disponibles para generar un pedido.

Al generar el pedido se debe indicar si la modalidad de entrega será retiro o despacho.

Después de generar el pedido, el cliente puede adjuntar su comprobante de pago.

El administrador puede consultar el pedido desde el panel administrativo y revisar el comprobante ingresado.

Si el comprobante es aprobado, el proceso continúa con la actualización correspondiente del inventario y la preparación del pedido.

Si el comprobante es rechazado, el administrador puede ingresar el motivo del rechazo y el proceso continúa por la ruta correspondiente.

Cuando el pago es aprobado, el pedido continúa dependiendo de la modalidad seleccionada.

Para retiro se continúa con la preparación y posteriormente se registra la entrega del pedido en el local.

Para despacho se pueden registrar los datos de transporte y el número de seguimiento, para posteriormente confirmar la entrega.

Flowable controla el avance del proceso durante estas etapas.

## Base de datos

La aplicación utiliza MySQL 8 y la base de datos se llama:

`mapuescuela`

Las tablas principales son:

- `cliente`
- `producto`
- `pedido`
- `detalle_pedido`
- `comprobante_pago`
- `despacho`

En la carpeta `database` se incluye el archivo:

`database/mapuescuela.sql`

Este archivo corresponde al script de instalación limpia de la base de datos. Crea la estructura necesaria para ejecutar el proyecto e incorpora seis productos iniciales para realizar pruebas.

El script no contiene los clientes, pedidos, comprobantes ni despachos históricos utilizados durante el desarrollo. Los nuevos clientes pueden registrarse directamente desde el Portal de Clientes.

> Importante: el script elimina y vuelve a crear la base de datos `mapuescuela`. No debe ejecutarse sobre una base de datos que contenga información que se quiera conservar.

### Importar la base de datos en Windows

Con MySQL 8 instalado, desde la carpeta raíz del proyecto se puede ejecutar:

```powershell
cmd /c """C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"" -u root -p --default-character-set=utf8mb4 < ""database\mapuescuela.sql"""
```
MySQL solicitará la contraseña del usuario `root`.

Se recomienda realizar la importación directamente desde el archivo, como se muestra arriba, para conservar correctamente la codificación UTF-8 y los caracteres especiales.

La aplicación utiliza por defecto:

- Base de datos: `mapuescuela`
- Servidor: `localhost`
- Puerto MySQL: `3306`
- Usuario: `root`

La contraseña de MySQL no se almacena en el repositorio. Se entrega mediante la variable de entorno `DB_PASSWORD`.

## Flowable y Docker

El proceso BPMN se ejecuta mediante Flowable 6.8.0.

Para el entorno utilizado durante el desarrollo se emplean dos contenedores Docker:

- `flowable-mapuescuela`: Flowable UI, publicado en el puerto `8081`.
- `flowable-rest-mapuescuela`: Flowable REST, publicado en el puerto `8082`.

La aplicación Spring Boot se comunica directamente con Flowable REST para iniciar procesos, consultar tareas, actualizar variables y completar actividades del proceso.

El componente `ConfirmarPagoWorker` forma parte de la aplicación Spring Boot y consulta automáticamente los trabajos externos del tópico `confirmarPago`. No requiere ejecutarse como un programa separado.

## Configuración de red Docker para Flowable

Para permitir la comunicación entre Flowable UI y Flowable REST se utiliza una red Docker compartida llamada:

`mapuescuela-net`

Crear la red:

```powershell
docker network create mapuescuela-net
```

Conectar los contenedores:

```powershell
docker network connect mapuescuela-net flowable-mapuescuela
docker network connect mapuescuela-net flowable-rest-mapuescuela
```

En Flowable Admin, la conexión al Process Engine puede configurarse con:

- Server address: `http://flowable-rest-mapuescuela`
- Server port: `8080`
- Context root: `/flowable-rest`
- REST root: `service`
- Username: `rest-admin`

Desde Windows, los servicios utilizados durante el desarrollo quedan disponibles en:

- Aplicación Mapuescuela: `http://localhost:8080`
- Flowable UI: `http://localhost:8081/flowable-ui/`
- Flowable REST: `http://localhost:8082/flowable-rest/service`

La versión final del proceso corresponde a V9 y utiliza la clave:

`procesoVentaMapuescuelaV3`

El archivo BPMN final es:

`bpmn/Proceso_de_venta_-_Mapuescuela_U3_v9.bpmn20.xml`

## Ejecutar el proyecto

### Requisitos previos

Para ejecutar Mapuescuela se debe contar con:

- Java 17.
- MySQL 8.
- Docker Desktop.
- Acceso a PowerShell en Windows.

El proyecto incluye Maven Wrapper (`mvnw.cmd`), por lo que no es necesario instalar Maven de forma independiente.

### Variables de entorno

Antes de iniciar Spring Boot se deben configurar las contraseñas locales.

Ejemplo para la sesión actual de PowerShell:

```powershell
$env:DB_PASSWORD="CONTRASENA_MYSQL"
$env:ADMIN_PASSWORD="CONTRASENA_ADMIN"
```

También pueden configurarse, si es necesario:

```powershell
$env:DB_USERNAME="root"
$env:FLOWABLE_URL="http://localhost:8082/flowable-rest/service"
$env:FLOWABLE_USERNAME="rest-admin"
$env:FLOWABLE_PASSWORD="test"
$env:ADMIN_USERNAME="admin"
```

Las contraseñas reales utilizadas durante el desarrollo no se incluyen en el repositorio.

### Compilar la aplicación

Desde la carpeta raíz del proyecto:

```powershell
.\mvnw.cmd clean compile
```

### Ejecutar Spring Boot

```powershell
.\mvnw.cmd spring-boot:run
```

Si el inicio es correcto, la aplicación queda disponible en:

`http://localhost:8080`

Al iniciar Spring Boot también comienza automáticamente `ConfirmarPagoWorker`, que consulta periódicamente los trabajos externos asociados al tópico `confirmarPago`.

### Acceso al sistema

El Portal de Clientes permite registrar un nuevo cliente, iniciar sesión, realizar compras y consultar sus propios pedidos.

El acceso administrativo permite gestionar pedidos, validar comprobantes, revisar inventario y clientes, y continuar las actividades administrativas requeridas por el proceso.

Las sesiones de cliente y administrador se manejan de forma separada para evitar que un cliente pueda consultar pedidos pertenecientes a otros clientes.

### BPMN utilizado

Antes de ejecutar pruebas completas debe encontrarse desplegado en Flowable el archivo:

`bpmn/Proceso_de_venta_-_Mapuescuela_U3_v9.bpmn20.xml`

La clave del proceso utilizada por Spring Boot es:

`procesoVentaMapuescuelaV3`


## Pruebas realizadas

Durante el desarrollo se probaron diferentes situaciones del proceso:

- Compra aprobada con retiro.
- Compra aprobada con despacho.
- Pago rechazado.
- Registro del motivo de rechazo.
- Cancelación mediante el temporizador.
- Conservación del stock cuando el pago no es aprobado.
- External Worker `confirmarPago`.
- Visualización del comprobante desde el panel administrativo.
- Registro de datos de retiro.
- Registro de datos de despacho.
- Finalización completa del proceso.

Las pruebas permitieron comprobar tanto las rutas principales como diferentes situaciones que podían ocurrir durante la ejecución del proceso.

También se realizaron pruebas completas de principio a fin para comprobar la integración entre la interfaz, Spring Boot, MySQL y Flowable.

Con la versión V7 se comprobó además que el motivo ingresado al rechazar un comprobante quedara almacenado y disponible para su consulta.

## Versiones del BPMN

Durante el desarrollo se fueron generando diferentes versiones del proceso para realizar correcciones, integrar nuevas funciones y mejorar la representación del flujo.

Entre las últimas versiones se encuentran:

- `Proceso_de_venta_-_Mapuescuela_U3_v6.bpmn20.xml`
- `Proceso_de_venta_-_Mapuescuela_U3_v7.bpmn20.xml`
- `Proceso_de_venta_-_Mapuescuela_U3_v8.bpmn20.xml`
- `Proceso_de_venta_-_Mapuescuela_U3_v9.bpmn20.xml`

La clave utilizada por el proceso en Flowable se mantiene como:

`procesoVentaMapuescuelaV3`

La versión final desplegada y utilizada por el proyecto corresponde a:

`version: 9`

En V6 se incorporó el External Worker `confirmarPago`.

Posteriormente se generó V7 con ajustes al flujo de validación del comprobante, incluyendo el manejo del motivo de rechazo ingresado desde la interfaz administrativa.

V8 correspondió a una versión intermedia de integración del proceso, manteniendo el External Worker y las ramas correspondientes a retiro en local y despacho.

Finalmente, V9 conserva la lógica de V8 y mejora la claridad de algunas actividades del modelo. La tarea `Registrar datos y generar pedido` pasó a denominarse `Registrar datos de compra y generar pedido`, y el evento inicial `Solicitud de compra iniciada` pasó a `Cliente registrado inicia solicitud de compra`.

El archivo V9 incluido en el repositorio corresponde al proceso final utilizado para las pruebas de integración con Spring Boot, MySQL y Flowable.

## Uso de IA y tecnologías complementarias

Durante el desarrollo del proyecto se utilizaron diferentes tecnologías para poder integrar los componentes de la solución.

Spring Boot fue utilizado para desarrollar la aplicación y los servicios, MySQL para almacenar la información, Flowable para ejecutar el proceso BPMN y Docker para ejecutar los componentes utilizados de Flowable.

También se utilizó inteligencia artificial como herramienta de apoyo durante el desarrollo.

La IA se utilizó principalmente para ayudar a revisar errores, comprender mensajes de ejecución, revisar partes del código y orientar algunos pasos de la integración entre Java, Flowable, MySQL y la interfaz web.

Las soluciones y cambios realizados durante el proyecto fueron posteriormente probados en el entorno de desarrollo para comprobar su funcionamiento.

## Documentación

La documentación del proyecto se encuentra dentro de la carpeta:

`documentacion`

Esta carpeta contiene los trabajos y archivos generados durante las diferentes unidades de la asignatura.

Actualmente se encuentra organizada principalmente en:

- `documentacion/unidad1`
- `documentacion/unidad2`
- `documentacion/unidad3`

Dentro de estas carpetas se encuentran documentos, modelos BPMN, actas de trabajo y otras evidencias utilizadas durante el desarrollo.

## Control de versiones

Para guardar los cambios del proyecto se utilizó Git y GitHub.

El repositorio contiene el código de la aplicación, los archivos BPMN, el script de instalación limpia de la base de datos y los documentos utilizados durante las diferentes unidades.

Durante el desarrollo se fueron realizando commits para registrar distintos avances del proyecto.

Esto permite mantener un respaldo y revisar los cambios realizados durante su desarrollo.

También se agregó evidencia del trabajo realizado por el grupo mediante las actas incluidas en la documentación del proyecto.

## Resumen

El proyecto comenzó en la Unidad 1 con el modelamiento BPMN.

En la Unidad 2 se trabajó con Web Services en Java.

En la Unidad 3 se integraron Spring Boot, MySQL, Flowable, servicios REST, tareas humanas, tareas HTTP, un External Worker y las interfaces del sistema.

Como resultado se obtuvo un MVP funcional del proceso principal de venta de Mapuescuela, donde se integran la interfaz web, los servicios desarrollados en Java, la base de datos MySQL y el proceso BPMN ejecutado mediante Flowable.

El MVP permite registrar clientes desde el Portal de Clientes, utilizar el catálogo inicial de productos y realizar el proceso de compra desde la generación del pedido hasta su retiro o despacho.
## Video demostrativo

Como evidencia del funcionamiento del MVP de MapuEscuela, se realizó un video demostrativo donde se presenta el funcionamiento de la aplicación y la ejecución del proceso de venta integrado con Flowable.

**Video demostrativo del proyecto:**  
https://youtu.be/3zf-ZYFmFbE
