-- MapuEscuela
-- Base de datos para instalación limpia
-- MySQL 8.0
-- Codificación: utf8mb4

DROP DATABASE IF EXISTS `mapuescuela`;
CREATE DATABASE `mapuescuela`
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_520_ci;

USE `mapuescuela`;

CREATE TABLE `cliente` (
  `id_cliente` bigint NOT NULL AUTO_INCREMENT,
  `rut` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `razon_social` varchar(150) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `nombre_contacto` varchar(150) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `telefono` varchar(30) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `rut` (`rut`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE `producto` (
  `id_producto` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_520_ci,
  `categoria` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `precio` decimal(12,2) NOT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `estado` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'DISPONIBLE',
  `imagen` varchar(500) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE `pedido` (
  `id_pedido` bigint NOT NULL AUTO_INCREMENT,
  `id_cliente` bigint NOT NULL,
  `fecha_pedido` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `modalidad_entrega` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `estado` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL DEFAULT 'PENDIENTE_PAGO',
  `total` decimal(12,2) NOT NULL,
  `flowable_process_instance_id` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `nombre_persona_retira` varchar(150) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `rut_persona_retira` varchar(20) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `fecha_retiro` datetime DEFAULT NULL,
  PRIMARY KEY (`id_pedido`),
  KEY `fk_pedido_cliente` (`id_cliente`),
  CONSTRAINT `fk_pedido_cliente`
    FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE `detalle_pedido` (
  `id_detalle` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `id_producto` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `fk_detalle_pedido` (`id_pedido`),
  KEY `fk_detalle_producto` (`id_producto`),
  CONSTRAINT `fk_detalle_pedido`
    FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`),
  CONSTRAINT `fk_detalle_producto`
    FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE `comprobante_pago` (
  `id_comprobante` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `archivo` varchar(500) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `fecha_carga` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `estado_validacion` varchar(30) COLLATE utf8mb4_unicode_520_ci DEFAULT 'PENDIENTE',
  `observacion` varchar(500) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  PRIMARY KEY (`id_comprobante`),
  KEY `fk_comprobante_pedido` (`id_pedido`),
  CONSTRAINT `fk_comprobante_pedido`
    FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

CREATE TABLE `despacho` (
  `id_despacho` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `tipo_entrega` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `empresa_transporte` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `numero_seguimiento` varchar(100) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `fecha_envio` datetime DEFAULT NULL,
  `fecha_entrega` datetime DEFAULT NULL,
  PRIMARY KEY (`id_despacho`),
  KEY `fk_despacho_pedido` (`id_pedido`),
  CONSTRAINT `fk_despacho_pedido`
    FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;

-- Catálogo inicial de productos.
-- No se incluyen clientes ni pedidos de prueba.

INSERT INTO `producto`
(`id_producto`, `nombre`, `descripcion`, `categoria`, `precio`, `stock`, `estado`, `imagen`)
VALUES
(1, 'Libro Matemática Básica', 'Libro usado en buen estado', 'Libros', 5000.00, 2, 'DISPONIBLE', NULL),
(2, 'Silla Escolar', 'Silla escolar usada', 'Muebles', 12000.00, 4, 'DISPONIBLE', NULL),
(3, 'Juego Didáctico', 'Juego educativo para niños', 'Juguetes', 8000.00, 5, 'DISPONIBLE', NULL),
(4, 'Mesa Infantil', 'Mesa infantil usada en buen estado', 'Muebles', 15000.00, 3, 'DISPONIBLE', NULL),
(5, 'Libro de Historia II° Medio', 'Libro nuevo', 'Libros', 10000.00, 4, 'DISPONIBLE', NULL),
(6, 'Silla Escolar doble', 'Silla Escolar doble', 'Muebles', 18000.00, 2, 'DISPONIBLE', NULL);