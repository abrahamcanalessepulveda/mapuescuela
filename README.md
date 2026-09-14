# Proyecto Mapuescuela

Integrantes Grupo 1
Alexis Rosales
Camilo Quezada
Cristóbal Celis
Abraham Canales

Proyecto desarrollado para la asignatura Integración de Plataformas.

El objetivo del proyecto es apoyar a Mapuescuela en la gestión de la venta de productos donados, permitiendo registrar productos, clientes, pedidos, comprobantes de pago y las distintas formas de entrega.

Para el desarrollo se utilizó BPMN y Flowable para administrar el proceso de venta, además de Java con Spring Boot, servicios REST y una base de datos MySQL.

## Objetivo

Desarrollar una solución funcional que permita gestionar el proceso principal de venta de Mapuescuela.

El sistema permite realizar operaciones desde la generación del pedido hasta su entrega o cancelación, dependiendo de las decisiones tomadas durante el proceso.

También se implementó un plazo de 24 horas para que el cliente pueda adjuntar el comprobante de pago. Si el comprobante no es ingresado dentro del plazo, Flowable puede ejecutar automaticamente la cancelación del pedido.

## Desarrollo por unidades

### Unidad 1

Durante la Unidad 1 se realizó principalmente el modelamiento BPMN del proceso de venta.

Se desarrollaron:

- Modelo AS-IS.
- Modelo TO-BE.
- Formularios para las tareas del proceso.
- Registro de datos del pedido.
- Carga de comprobante.
- Validación del comprobante.
- Registro de retiro.
- Registro de despacho.

Los archivos originales de esta unidad se encuentran en:

`documentacion/unidad1`

Estos modelos y formularios fueron utilizados como base para continuar el desarrollo en las unidades siguientes.

### Unidad 2

En la Unidad 2 se comenzó la implementación de Web Services utilizando Java.

Se desarrolló un proyecto SOAP con operaciones relacionadas con:

- Registro de clientes.
- Generación de pedidos.
- Procesamiento de pagos.
- Actualización del estado de los pedidos.

También se realizaron modificaciones al proceso BPMN para comenzar a relacionar las actividades del proceso con los servicios desarrollados.

Los archivos de esta etapa se encuentran en:

`documentacion/unidad2`

### Unidad 3

En la Unidad 3 se continuó el proyecto hasta obtener una solución integrada y funcional.

Se implementó:

- Backend con Spring Boot.
- Base de datos MySQL.
- Servicios REST.
- Interfaz web.
- Integración con Flowable.
- Tareas humanas.
- Tareas HTTP.
- Actualización de estados.
- Control de inventario.
- Retiro y despacho.
- Cancelación automática por tiempo.

La versión final del proceso BPMN utilizada en las pruebas se encuentra en:

`bpmn/Proceso_de_venta_-_Mapuescuela_U3_v5.bpmn20.xml`

## Tecnologías utilizadas

- Java 17
- Spring Boot 4.0.8
- Maven
- MySQL 8
- Spring Data JPA
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

La interfaz web se comunica con el backend mediante servicios REST.

El backend se encarga de procesar las operaciones y guardar la información en MySQL.

Cuando se genera un pedido, la aplicación también inicia una instancia del proceso correspondiente en Flowable.

De forma resumida:

`Interfaz web -> Spring Boot -> MySQL`

Para el proceso automatizado:

`Spring Boot -> Flowable -> Servicios REST -> MySQL`

Flowable permite controlar las tareas humanas y las tareas automaticas que forman parte del proceso.

## Funciones implementadas

Actualmente el sistema permite:

- Consultar productos.
- Consultar clientes.
- Crear pedidos.
- Consultar pedidos.
- Seleccionar retiro o despacho.
- Adjuntar comprobantes.
- Validar comprobantes.
- Aprobar pagos.
- Rechazar pagos.
- Descontar stock cuando corresponde.
- Preparar pedidos.
- Registrar retiro.
- Registrar despacho.
- Registrar número de seguimiento.
- Confirmar entregas.
- Cancelar pedidos.

## Proceso BPMN

El proceso BPMN utilizado en Flowable considera las principales actividades de la venta.

De forma general el flujo es:

1. Se genera el pedido.
2. Se registran los datos necesarios.
3. Se informan los datos para realizar la transferencia.
4. Se espera el comprobante.
5. Un voluntario valida el comprobante.
6. El pago puede ser aprobado o rechazado.
7. Si es aprobado se actualiza el inventario.
8. Se prepara el pedido.
9. Se selecciona retiro o despacho.
10. Se registra la entrega.
11. El proceso finaliza.

También existe un temporizador configurado como:

`PT24H`

Este temporizador controla el plazo disponible para adjuntar el comprobante de pago.

Si se cumple el plazo sin recibir el comprobante, Flowable ejecuta la ruta de cancelación del pedido.

## Interfaz web

Se desarrolló una interfaz web simple utilizando HTML, CSS y JavaScript.

Desde la interfaz se puede:

- visualizar productos;
- seleccionar cliente;
- generar pedidos;
- visualizar pedidos registrados;
- adjuntar comprobantes de pago.

La interfaz se conecta directamente con los servicios REST de Spring Boot.

Durante las pruebas se comprobó que al generar un pedido desde la interfaz, este queda registrado en la base de datos y se inicia su proceso en Flowable.

También se comprobó la carga de comprobantes desde la interfaz y la actualización del estado del pedido.

## Base de datos

La aplicación utiliza MySQL.

Base de datos utilizada:

`mapuescuela`

Entre las principales tablas utilizadas se encuentran:

- cliente
- producto
- pedido
- detalle_pedido
- comprobante_pago
- despacho

Las credenciales privadas no se guardan directamente en el repositorio.

La contraseña de MySQL debe configurarse mediante la variable de entorno:

`DB_PASSWORD`

## Flowable y Docker

Para las pruebas se utilizaron dos contenedores de Flowable:

- Flowable UI: puerto 8081.
- Flowable REST: puerto 8082.

Se debe verificar que Docker Desktop se encuentre funcionando y que ambos contenedores estén iniciados.

El contenedro de Flowable REST es utilizado por Spring Boot para iniciar y consultar las instancias del proceso.

## Ejecutar el proyecto

Primero se debe tener:

- Java 17.
- MySQL funcionando.
- Docker Desktop funcionando.
- Flowable UI y REST iniciados.
- Variable `DB_PASSWORD` configurada.

Desde PowerShell ingresar a la carpeta del proyecto:

```powershell
cd "C:\Users\Abraham2026\Desktop\Proyectos VS Code\mapuescuela"