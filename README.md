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

El sistema permite utilizar clientes y productos previamente registrados, generar pedidos y adjuntar comprobantes de pago, además de controlar si el pedido será retirado en el local o enviado por despacho.

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

`bpmn/Proceso_de_venta_-_Mapuescuela_U3_v7.bpmn20.xml`

Esta corresponde a la versión V7 del proceso.

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
- Consultar y seleccionar clientes previamente registrados.
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

La aplicación utiliza MySQL.

La base de datos utilizada es:

`mapuescuela`

Entre las tablas principales se encuentran:

- `cliente`
- `producto`
- `pedido`
- `detalle_pedido`
- `comprobante_pago`
- `despacho`

En la carpeta:

`database`

se encuentra un respaldo de la base de datos utilizado en el proyecto.

Este respaldo permite contar con la estructura y datos utilizados durante las pruebas del MVP.

La contraseña de MySQL no se guarda directamente en el proyecto.

Se utiliza la variable de entorno:

`DB_PASSWORD`

## Flowable y Docker

Para ejecutar Flowable se utilizaron dos contenedores Docker:

- Flowable UI: puerto `8081`
- Flowable REST: puerto `8082`

## Configuración de red Docker para Flowable

Para permitir la comunicación entre Flowable UI y Flowable REST se utiliza
una red Docker compartida llamada `mapuescuela-net`.

Crear la red:

docker network create mapuescuela-net

Conectar los contenedores:

docker network connect mapuescuela-net flowable-mapuescuela
docker network connect mapuescuela-net flowable-rest-mapuescuela

En Flowable Admin, el endpoint del Process Engine debe configurarse con:

- Server address: http://flowable-rest-mapuescuela
- Server port: 8080
- Context root: /flowable-rest
- REST root: service
- Username: rest-admin

Spring Boot utiliza el puerto:

`8080`

Flowable REST permite que Spring Boot inicie y consulte los procesos.

También es utilizado por `ConfirmarPagoWorker` para trabajar con los External Workers.

La última versión del proceso utilizada corresponde a V7.

## Ejecutar el proyecto

Para ejecutar el proyecto se debe contar con:

- Java 17.
- MySQL 8.
- Docker Desktop.
- Flowable UI.
- Flowable REST.

La base de datos debe estar disponible en MySQL y se debe configurar la contraseña mediante la variable de entorno:

`DB_PASSWORD`

También deben estar configurados los datos necesarios para la conexión con Flowable y para el acceso administrativo de la aplicación.

Las contraseñas utilizadas localmente no se incluyen en el repositorio.

Después de tener MySQL y los servicios de Flowable funcionando, se puede iniciar la aplicación Spring Boot.

Desde PowerShell se debe ingresar a la carpeta donde se encuentre el proyecto.

Por ejemplo:

```powershell
cd "C:\ruta\del\proyecto\mapuescuela"
```

Para compilar:

```powershell
.\mvnw.cmd clean compile
```

Para ejecutar:

```powershell
.\mvnw.cmd spring-boot:run
```

Si todo funciona correctamente, Spring Boot se inicia en el puerto `8080`.

Al iniciar la aplicación también queda funcionando `ConfirmarPagoWorker` para consultar los trabajos externos del tópico `confirmarPago`.

Para utilizar el proceso final se debe tener desplegado en Flowable el archivo:

`bpmn/Proceso_de_venta_-_Mapuescuela_U3_v7.bpmn20.xml`

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

Durante el desarrollo se fueron generando diferentes versiones del proceso para realizar correcciones y agregar nuevas funciones.

Entre las últimas versiones se encuentran:

`Proceso_de_venta_-_Mapuescuela_U3_v6.bpmn20.xml`

`Proceso_de_venta_-_Mapuescuela_U3_v7.bpmn20.xml`

La clave utilizada por el proceso en Flowable se mantiene como:

`procesoVentaMapuescuelaV3`

La última versión desplegada en Flowable corresponde a:

`version: 7`

En V6 se incorporó el External Worker `confirmarPago`.

Posteriormente se generó V7 para incorporar los últimos ajustes al flujo de validación del comprobante, incluyendo el uso del motivo de rechazo ingresado desde la interfaz administrativa.

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

El repositorio contiene el código de la aplicación, los archivos BPMN, el respaldo de la base de datos y los documentos utilizados durante las diferentes unidades.

Durante el desarrollo se fueron realizando commits para registrar distintos avances del proyecto.

Esto permite mantener un respaldo y revisar los cambios realizados durante su desarrollo.

También se agregó evidencia del trabajo realizado por el grupo mediante las actas incluidas en la documentación del proyecto.

## Resumen

El proyecto comenzó en la Unidad 1 con el modelamiento BPMN.

En la Unidad 2 se trabajó con Web Services en Java.

En la Unidad 3 se integraron Spring Boot, MySQL, Flowable, servicios REST, tareas humanas, tareas HTTP, un External Worker y las interfaces del sistema.

Como resultado se obtuvo un MVP funcional del proceso principal de venta de Mapuescuela, donde se integran la interfaz web, los servicios desarrollados en Java, la base de datos MySQL y el proceso BPMN ejecutado mediante Flowable.

El MVP utiliza clientes y productos previamente registrados en la base de datos y permite realizar el proceso de compra desde la generación del pedido hasta su retiro o despacho.