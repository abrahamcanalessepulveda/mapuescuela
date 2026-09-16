# Proyecto Mapuescuela

## Integrantes Grupo 1

- Alexis Rosales
- Camilo Quezada
- Cristóbal Celis
- Abraham Canales

Proyecto desarrollado para la asignatura **Integración de Plataformas**.

El proyecto consiste en una aplicación para apoyar el proceso de venta de productos donados de Mapuescuela.

Durante las tres unidades se fue desarrollando el proceso, comenzando con el modelamiento BPMN y posteriormente incorporando Web Services, Spring Boot, MySQL y Flowable.

## Objetivo

Desarrollar una aplicación que permita manejar el proceso principal de venta de Mapuescuela.

El sistema permite registrar clientes, productos, pedidos y comprobantes de pago, además de controlar si el pedido será retirado en el local o enviado por despacho.

También se agregó un plazo de 24 horas para adjuntar el comprobante de pago. Si el comprobante no es ingresado dentro de ese tiempo, el proceso puede continuar por la ruta de cancelación.

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

La versión final del BPMN utilizada para las pruebas es:

`bpmn/Proceso_de_venta_-_Mapuescuela_U3_v6.bpmn20.xml`

Esta corresponde a la versión V6 del proceso.

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

En la versión V6 también se agregó un External Worker para la confirmación del pago:

`Flowable -> confirmarPago -> ConfirmarPagoWorker -> MySQL`

Flowable controla el avance del proceso y Spring Boot realiza la lógica de la aplicación.

## Funciones implementadas

El sistema permite:

- Consultar productos.
- Consultar clientes.
- Crear pedidos.
- Consultar pedidos.
- Seleccionar retiro o despacho.
- Adjuntar comprobantes.
- Validar comprobantes.
- Aprobar o rechazar pagos.
- Actualizar el stock.
- Preparar pedidos.
- Registrar retiro.
- Registrar despacho.
- Registrar número de seguimiento.
- Confirmar entregas.
- Cancelar pedidos.

## Proceso BPMN

El proceso de venta funciona de forma general de la siguiente manera:

1. Se genera el pedido.
2. Se registran los datos del comprador.
3. Se entregan los datos para realizar la transferencia.
4. El cliente adjunta el comprobante.
5. Un voluntario revisa el comprobante.
6. El pago puede ser aprobado o rechazado.
7. Si es aprobado, se actualiza el inventario.
8. Se prepara el pedido.
9. Se continúa por retiro o despacho.
10. Se registra la entrega.
11. El proceso finaliza.

También se utiliza un temporizador:

`PT24H`

Este representa el plazo de 24 horas disponible para adjuntar el comprobante.

Durante las pruebas se comprobó la ruta del temporizador mediante la ejecución controlada del Timer Job de Flowable.

## External Worker

En la versión V6 se agregó un External Worker para la actividad:

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

`idPedido` permite relacionar el proceso con el pedido guardado en MySQL.

`idComprobante` es utilizado por el External Worker para saber qué comprobante debe procesar.

`modalidadEntrega` permite decidir entre retiro y despacho.

`resultadoValidacion` permite decidir si el pago fue aprobado o rechazado.

## Interfaz web

Se desarrolló una interfaz sencilla utilizando HTML, CSS y JavaScript.

Desde la interfaz se puede:

- Ver productos.
- Seleccionar un cliente.
- Crear un pedido.
- Seleccionar retiro o despacho.
- Ver pedidos.
- Adjuntar un comprobante.

La interfaz se comunica con los servicios REST de Spring Boot.

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

La contraseña de MySQL no se guarda directamente en el proyecto.

Se utiliza la variable de entorno:

`DB_PASSWORD`

## Flowable y Docker

Para ejecutar Flowable se utilizaron dos contenedores Docker:

- Flowable UI: puerto `8081`
- Flowable REST: puerto `8082`

Spring Boot utiliza el puerto:

`8080`

Flowable REST permite que Spring Boot inicie y consulte los procesos.

También es utilizado por `ConfirmarPagoWorker` para trabajar con los External Workers.

## Ejecutar el proyecto

Antes de iniciar se debe tener funcionando:

- Java 17.
- MySQL.
- Docker Desktop.
- Flowable UI.
- Flowable REST.

También se debe configurar la variable `DB_PASSWORD`.

Desde PowerShell se puede ingresar a la carpeta del proyecto:

```powershell
cd "C:\Users\Abraham2026\Desktop\Proyectos VS Code\mapuescuela"
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

## Pruebas realizadas

Durante el desarrollo se probaron diferentes situaciones del proceso:

- Compra aprobada con retiro.
- Compra aprobada con despacho.
- Pago rechazado.
- Cancelación mediante el temporizador.
- Conservación del stock cuando el pago no es aprobado.
- External Worker `confirmarPago`.
- Finalización completa del proceso.

Para la prueba final de V6 se utilizó el Pedido N.º 13.

En esta prueba se comprobó que el External Worker recibió y procesó el trabajo `confirmarPago`.

Después de esto, Flowable continuó con la preparación y retiro del pedido hasta finalizar el proceso.

Al terminar se comprobó que el pedido quedó en estado:

`FINALIZADO`

También se verificó que no quedaran tareas activas en Flowable.

## Versiones del BPMN

Durante el desarrollo se fueron generando diferentes versiones del proceso para realizar correcciones y agregar nuevas funciones.

La versión final utilizada en las pruebas fue V6:

`Proceso_de_venta_-_Mapuescuela_U3_v6.bpmn20.xml`

La clave utilizada por el proceso en Flowable se mantiene como:

`procesoVentaMapuescuelaV3`

La versión registrada en Flowable es:

`version: 6`

La principal modificación de V6 fue cambiar la actividad de confirmación del pago para utilizar el External Worker `confirmarPago`.

## Control de versiones

Para guardar los cambios del proyecto se utilizó Git y GitHub.

El repositorio contiene el código de la aplicación, los archivos BPMN y los documentos utilizados durante las diferentes unidades.

Esto permite mantener un respaldo del proyecto y revisar los cambios realizados durante su desarrollo.

## Resumen

El proyecto comenzó en la Unidad 1 con el modelamiento BPMN.

En la Unidad 2 se trabajó con Web Services en Java.

Finalmente, en la Unidad 3 se integraron Spring Boot, MySQL, Flowable, servicios REST, tareas humanas, tareas HTTP y un External Worker.

Con esto se logró tener un prototipo funcional del proceso principal de venta de Mapuescuela.