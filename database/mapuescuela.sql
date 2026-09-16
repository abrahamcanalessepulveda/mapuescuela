-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: mapuescuela
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `mapuescuela`
--

/*!40000 DROP DATABASE IF EXISTS `mapuescuela`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `mapuescuela` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `mapuescuela`;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` bigint NOT NULL AUTO_INCREMENT,
  `rut` varchar(20) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `razon_social` varchar(150) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `nombre_contacto` varchar(150) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `telefono` varchar(30) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `rut` (`rut`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'12345678-9','Cliente de Prueba','Juan Perez','juan.perez@correo.cl','912345678','Santiago','2026-09-10 12:06:20');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comprobante_pago`
--

DROP TABLE IF EXISTS `comprobante_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comprobante_pago` (
  `id_comprobante` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `archivo` varchar(500) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `fecha_carga` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `estado_validacion` varchar(30) COLLATE utf8mb4_unicode_520_ci DEFAULT 'PENDIENTE',
  `observacion` varchar(500) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  PRIMARY KEY (`id_comprobante`),
  KEY `fk_comprobante_pedido` (`id_pedido`),
  CONSTRAINT `fk_comprobante_pedido` FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comprobante_pago`
--

LOCK TABLES `comprobante_pago` WRITE;
/*!40000 ALTER TABLE `comprobante_pago` DISABLE KEYS */;
INSERT INTO `comprobante_pago` VALUES (1,1,'uploads\\comprobantes\\84cb1dfe-011e-460e-95f7-76fd780b3689_comprobante.pdf','2026-09-10 18:31:35','APROBADO','Transferencia verificada correctamente'),(2,2,'uploads\\comprobantes\\9bbd97be-0285-4a04-8d0b-c233ea28a097_comprobante.pdf','2026-09-11 09:40:40','APROBADO','Transferencia verificada correctamente'),(3,3,'uploads\\comprobantes\\2e810fd6-4c11-4e7d-bab4-3188279f0ca6_comprobante.pdf','2026-09-11 11:53:52','RECHAZADO','Comprobante no válido'),(4,4,'uploads\\comprobantes\\323523ba-8054-40e8-b3fb-555e7090afc0_comprobante.pdf','2026-09-12 20:43:12','APROBADO','Pago aprobado desde Flowable'),(5,5,'uploads\\comprobantes\\545e2c95-b6d9-400b-8ef6-2ca17184e332_comprobante.pdf','2026-09-12 23:11:46','APROBADO','Pago aprobado desde Flowable'),(6,6,'uploads\\comprobantes\\36dc27d6-0920-48af-9f88-92e20344d6dc_comprobante.pdf','2026-09-13 11:54:14','APROBADO','Pago aprobado desde Flowable'),(7,7,'uploads\\comprobantes\\3c59de46-7ea4-4d1b-b543-e4795082f39f_comprobante.pdf','2026-09-13 14:12:21','APROBADO','Pago aprobado desde Flowable'),(8,8,'uploads\\comprobantes\\d6c76210-35dd-49e0-b0c0-9429e6a9af49_comprobante_pedido8.txt','2026-09-13 20:50:50','RECHAZADO','Comprobante rechazado en prueba'),(9,9,'uploads\\comprobantes\\c95fc274-78bb-4b3a-8f83-4a48ad33b287_comprobante_pedido9.txt','2026-09-13 21:54:36','RECHAZADO','Comprobante rechazado durante la validacion manual'),(10,11,'uploads\\comprobantes\\3024fd63-ab39-40e4-844a-48ed1fb9df3f_RetroalimentacionEva2-01.pdf','2026-09-14 06:42:27','PENDIENTE',NULL),(11,12,'uploads\\comprobantes\\1a12387c-e3bd-4e32-8113-276a365fac53_CMR 24-08-2026 2.pdf','2026-09-15 13:17:48','PENDIENTE',NULL),(12,13,'uploads\\comprobantes\\304adfa9-3667-4633-b2e6-7c7e2e0fee4e_comprobante.pdf','2026-09-15 13:34:31','APROBADO','Pago aprobado mediante External Worker'),(13,14,'uploads\\comprobantes\\bf43699a-65c3-478b-9fd8-34a997eb4db5_comprobante.pdf','2026-09-16 09:22:59','APROBADO','Pago aprobado mediante External Worker'),(14,15,'uploads\\comprobantes\\6aec4152-22a2-46c6-83b0-66690b90905b_comprobante.pdf','2026-09-16 11:05:36','RECHAZADO','Comprobante rechazado durante la validacion manual'),(15,16,'uploads\\comprobantes\\947e5c18-02c5-45a4-a82b-edf572d36c40_comprobante.pdf','2026-09-16 13:02:30','APROBADO','Pago aprobado mediante External Worker'),(16,17,'uploads\\comprobantes\\10f6bd5f-9bc7-4bc6-9dc0-3f1284abfdf5_comprobante.pdf','2026-09-16 13:24:46','RECHAZADO','Monto no corresponde al total, falta incluir IVA'),(17,18,'uploads\\comprobantes\\dfc5a87d-d534-4cf2-9f76-b6779a3376ba_comprobante.pdf','2026-09-16 14:08:09','APROBADO','Pago aprobado mediante External Worker');
/*!40000 ALTER TABLE `comprobante_pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `despacho`
--

DROP TABLE IF EXISTS `despacho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  CONSTRAINT `fk_despacho_pedido` FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `despacho`
--

LOCK TABLES `despacho` WRITE;
/*!40000 ALTER TABLE `despacho` DISABLE KEYS */;
INSERT INTO `despacho` VALUES (1,2,'DESPACHO','Chilexpress','CHX123456789','2026-09-11 09:52:00','2026-09-11 10:00:20'),(2,7,'DESPACHO',NULL,'MAP-0007','2026-09-13 14:30:15','2026-09-13 14:32:51'),(3,16,'DESPACHO','Starken','1234567890','2026-09-16 13:03:00','2026-09-16 13:04:59'),(4,18,'DESPACHO','Chilexpress','1234567890','2026-09-16 14:11:00','2026-09-16 14:12:36');
/*!40000 ALTER TABLE `despacho` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_pedido` (
  `id_detalle` bigint NOT NULL AUTO_INCREMENT,
  `id_pedido` bigint NOT NULL,
  `id_producto` bigint NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `fk_detalle_pedido` (`id_pedido`),
  KEY `fk_detalle_producto` (`id_producto`),
  CONSTRAINT `fk_detalle_pedido` FOREIGN KEY (`id_pedido`) REFERENCES `pedido` (`id_pedido`),
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_pedido`
--

LOCK TABLES `detalle_pedido` WRITE;
/*!40000 ALTER TABLE `detalle_pedido` DISABLE KEYS */;
INSERT INTO `detalle_pedido` VALUES (1,1,1,1,5000.00),(2,1,4,1,15000.00),(3,2,2,1,12000.00),(4,3,3,1,8000.00),(5,4,3,1,8000.00),(6,5,1,1,5000.00),(7,6,2,1,12000.00),(8,7,4,1,15000.00),(9,8,1,1,5000.00),(10,9,1,1,5000.00),(11,10,1,1,5000.00),(12,11,1,1,5000.00),(13,12,1,1,5000.00),(14,13,1,1,5000.00),(15,14,1,10,5000.00),(16,15,3,3,8000.00),(17,16,3,15,8000.00),(18,17,4,5,15000.00),(19,18,4,5,15000.00);
/*!40000 ALTER TABLE `detalle_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  CONSTRAINT `fk_pedido_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` VALUES (1,1,'2026-09-10 12:46:06','RETIRO','FINALIZADO',20000.00,NULL,NULL,NULL,NULL),(2,1,'2026-09-11 09:36:33','DESPACHO','FINALIZADO',12000.00,NULL,NULL,NULL,NULL),(3,1,'2026-09-11 11:16:00','RETIRO','CANCELADO',8000.00,NULL,NULL,NULL,NULL),(4,1,'2026-09-12 20:22:38','RETIRO','PAGO_APROBADO',8000.00,'b2d83cb0-aee7-11f1-8c85-6a75a306eecc',NULL,NULL,NULL),(5,1,'2026-09-12 22:58:46','RETIRO','EN_PREPARACION',5000.00,'827d3b7d-aefd-11f1-8c85-6a75a306eecc',NULL,NULL,NULL),(6,1,'2026-09-13 11:50:24','RETIRO','FINALIZADO',12000.00,'4e57f519-af69-11f1-8df1-e6b98ec79b3b',NULL,NULL,NULL),(7,1,'2026-09-13 14:05:04','DESPACHO','FINALIZADO',15000.00,'1e725265-af7c-11f1-8df1-e6b98ec79b3b',NULL,NULL,NULL),(8,1,'2026-09-13 18:09:35','RETIRO','PAGO_RECHAZADO',5000.00,'4723d45c-af9e-11f1-8aa6-3a982b4de3cd',NULL,NULL,NULL),(9,1,'2026-09-13 21:49:19','RETIRO','CANCELADO',5000.00,'f8fb30f6-afbc-11f1-8aa6-3a982b4de3cd',NULL,NULL,NULL),(10,1,'2026-09-13 22:09:54','RETIRO','CANCELADO',5000.00,'d92ec07f-afbf-11f1-8aa6-3a982b4de3cd',NULL,NULL,NULL),(11,1,'2026-09-14 06:38:29','RETIRO','PAGO_EN_REVISION',5000.00,'e598e223-b006-11f1-8aa6-3a982b4de3cd',NULL,NULL,NULL),(12,1,'2026-09-15 13:14:17','RETIRO','PAGO_EN_REVISION',5000.00,'5b440da3-b107-11f1-9875-0ed9befb2a31',NULL,NULL,NULL),(13,1,'2026-09-15 13:33:18','RETIRO','FINALIZADO',5000.00,'02f95542-b10a-11f1-9875-0ed9befb2a31',NULL,NULL,NULL),(14,1,'2026-09-16 09:21:13','RETIRO','FINALIZADO',50000.00,'f663e148-b1af-11f1-9ac9-fe4cf151bdea','Usuario de prueba','12.345.678-9','2026-09-16 12:45:13'),(15,1,'2026-09-16 11:05:16','DESPACHO','CANCELADO',24000.00,'7f4f456d-b1be-11f1-aa3b-8a750186c6fb',NULL,NULL,NULL),(16,1,'2026-09-16 13:02:15','DESPACHO','FINALIZADO',120000.00,'d71cbd16-b1ce-11f1-aa3b-8a750186c6fb',NULL,NULL,NULL),(17,1,'2026-09-16 13:24:36','DESPACHO','CANCELADO',75000.00,'f636be59-b1d1-11f1-aa3b-8a750186c6fb',NULL,NULL,NULL),(18,1,'2026-09-16 14:07:05','DESPACHO','FINALIZADO',75000.00,'e5eb161c-b1d7-11f1-aa3b-8a750186c6fb',NULL,NULL,NULL);
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Libro Matemática Básica','Libro usado en buen estado','Libros',5000.00,0,'DISPONIBLE','libro-matematica.jpg','2026-09-09 14:00:10'),(2,'Silla Escolar','Silla escolar usada','Muebles',12000.00,5,'DISPONIBLE','silla-escolar.jpg','2026-09-09 14:00:10'),(3,'Juego Didáctico','Juego educativo para niños','Juguetes',8000.00,5,'DISPONIBLE','juego-didactico.jpg','2026-09-09 14:00:10'),(4,'Mesa Infantil','Mesa infantil usada en buen estado','Muebles',15000.00,25,'DISPONIBLE','mesa-infantil.jpg','2026-09-10 08:16:26');
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'mapuescuela'
--

--
-- Dumping routines for database 'mapuescuela'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-16 20:42:02
