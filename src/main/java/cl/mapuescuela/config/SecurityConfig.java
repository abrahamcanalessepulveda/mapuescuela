
package cl.mapuescuela.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import jakarta.servlet.http.HttpSession;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(autorizacion -> autorizacion

                // Catálogo público: únicamente consultas.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/productos",
                    "/api/productos/*"
                ).permitAll()

                // Archivos de la interfaz.
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/admin.html",
                    "/app.js",
                    "/admin.js",
                    "/inventario.js",
                    "/clientes.js",
                    "/seguridad.js",
                    "/estilos.css",
                    "/favicon.ico"
                ).permitAll()

                // Token CSRF y rutas de autenticación.
                .requestMatchers(
                    "/api/csrf",
                    "/api/admin/login",
                    "/api/admin/sesion",
                    "/api/admin/logout",
                    "/api/clientes/registro",
                    "/api/clientes/login",
                    "/api/clientes/sesion",
                    "/api/clientes/logout"
                ).permitAll()

                // El administrador puede acceder a las funciones
                // de administración únicamente con su sesión.
                .requestMatchers("/api/admin/**")
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esAdministrador(sesion)
                    );
                })

                // Consulta de la cuenta del comprador.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/clientes/mi-cuenta"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esCliente(sesion)
                    );
                })

                // Consultas de pedidos: el controlador comprueba
                // qué pedidos pertenecen al cliente.
                // El administrador conserva la consulta general.
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/pedidos",
                    "/api/pedidos/*"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esCliente(sesion)
                        || esAdministrador(sesion)
                    );
                })

                // Crear pedidos requiere una sesión de cliente.
                // El controlador obtiene el ID desde la sesión.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/pedidos"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esCliente(sesion)
                    );
                })

                // Preparación, retiro y cancelación de pedidos:
                // únicamente el administrador.
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/pedidos/*/preparar",
                    "/api/pedidos/*/listo-retiro",
                    "/api/pedidos/*/retirado",
                    "/api/pedidos/*/cancelar"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esAdministrador(sesion)
                    );
                })

                // El cliente puede adjuntar comprobantes.
                // El controlador comprueba que el pedido sea suyo.
                // También se conserva el acceso administrativo.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/comprobantes"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esCliente(sesion)
                        || esAdministrador(sesion)
                    );
                })

                // Validar comprobantes requiere una sesión
                // administrativa.
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/comprobantes/*/validar"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esAdministrador(sesion)
                    );
                })

                // Envío y entrega: únicamente el administrador.
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/despachos/*/enviar"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esAdministrador(sesion)
                    );
                })

                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/despachos/*/entregado"
                )
                .access((autenticacion, contexto) -> {

                    HttpSession sesion = contexto
                        .getRequest()
                        .getSession(false);

                    return new AuthorizationDecision(
                        esAdministrador(sesion)
                    );
                })

                // Cualquier ruta no autorizada expresamente
                // permanece bloqueada.
                .anyRequest().denyAll()
            )

            .exceptionHandling(excepciones -> excepciones
                .authenticationEntryPoint(
                    new HttpStatusEntryPoint(
                        HttpStatus.UNAUTHORIZED
                    )
                )
            )

            .formLogin(formulario -> formulario.disable())
            .httpBasic(basico -> basico.disable());

        return http.build();
    }

    private boolean esAdministrador(HttpSession sesion) {

        return sesion != null
            && Boolean.TRUE.equals(
                sesion.getAttribute("ADMIN_AUTENTICADO")
            );
    }

    private boolean esCliente(HttpSession sesion) {

        return sesion != null
            && sesion.getAttribute("CLIENTE_ID") instanceof Long;
    }
}
