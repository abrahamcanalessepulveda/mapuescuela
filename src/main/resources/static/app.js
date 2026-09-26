const API_BASE = "/api";

let clienteAutenticado = false;
let accionPendiente = null;


/* =========================================================
   INICIALIZACIÓN
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {

    document
        .getElementById("formLogin")
        .addEventListener("submit", iniciarSesion);

    document
        .getElementById("formRegistro")
        .addEventListener("submit", registrarCliente);

    document
        .getElementById("btnCerrarSesion")
        .addEventListener("click", cerrarSesion);

    document
        .getElementById("formPedido")
        .addEventListener("submit", crearPedido);

    document
        .getElementById("formComprobante")
        .addEventListener("submit", subirComprobante);

    document
        .getElementById("btnActualizarPedidos")
        .addEventListener("click", cargarPedidos);


    document
        .getElementById("btnIrPedidos")
        .addEventListener("click", () => {

            if (clienteAutenticado) {

                mostrarSeccion("pedido");

            } else {

                accionPendiente = "pedido";
                mostrarSeccion("acceso");
            }
        });


    document
        .getElementById("btnVerCatalogo")
        .addEventListener("click", () => {

            accionPendiente = null;
            mostrarSeccion("catalogo");
        });


    document
        .getElementById("btnVerMisPedidos")
        .addEventListener("click", async () => {

            if (clienteAutenticado) {

                await cargarPedidos();

                mostrarSeccion("pedidos");

            } else {

                accionPendiente = "pedidos";

                mostrarSeccion("acceso");
            }
        });


    ocultarSeccionesContenido();

    cargarProductos();

    consultarSesion();
});


/* =========================================================
   NAVEGACIÓN
   ========================================================= */

function ocultarSeccionesContenido() {

    document
        .getElementById("seccionAcceso")
        .hidden = true;

    document
        .getElementById("seccionCatalogo")
        .hidden = true;

    document
        .getElementById("seccionPedido")
        .hidden = true;

    document
        .getElementById("seccionPedidos")
        .hidden = true;

    document
        .getElementById("seccionComprobante")
        .hidden = true;
}


function mostrarSeccion(seccion) {

    ocultarSeccionesContenido();

    let destino = null;


    if (seccion === "acceso") {

        destino =
            document.getElementById(
                "seccionAcceso"
            );

    } else if (seccion === "catalogo") {

        destino =
            document.getElementById(
                "seccionCatalogo"
            );

    } else if (seccion === "pedido") {

        if (!clienteAutenticado) {

            accionPendiente =
                "pedido";

            destino =
                document.getElementById(
                    "seccionAcceso"
                );

        } else {

            destino =
                document.getElementById(
                    "seccionPedido"
                );
        }

    } else if (seccion === "pedidos") {

        if (!clienteAutenticado) {

            accionPendiente =
                "pedidos";

            destino =
                document.getElementById(
                    "seccionAcceso"
                );

        } else {

            destino =
                document.getElementById(
                    "seccionPedidos"
                );
        }

    } else if (seccion === "comprobante") {

        if (!clienteAutenticado) {

            destino =
                document.getElementById(
                    "seccionAcceso"
                );

        } else {

            destino =
                document.getElementById(
                    "seccionComprobante"
                );
        }
    }


    if (destino) {

        destino.hidden =
            false;


        destino.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
}


/* =========================================================
   SESIÓN DEL CLIENTE
   ========================================================= */

async function consultarSesion() {

    const estado =
        document.getElementById(
            "estadoSesion"
        );


    try {

        const respuesta = await fetch(
            `${API_BASE}/clientes/sesion`,
            {
                credentials:
                    "same-origin"
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                "No fue posible consultar la sesión."
            );
        }


        const datos =
            await respuesta.json();


        if (datos.autenticado === true) {

            actualizarInterfazSesion(
                true,
                datos
            );


            await cargarPedidos();

        } else {

            actualizarInterfazSesion(
                false
            );
        }


    } catch (error) {

        actualizarInterfazSesion(
            false
        );


        mostrarMensaje(
            estado,
            `Error al consultar la sesión: ${error.message}`,
            false
        );
    }
}


function actualizarInterfazSesion(
    autenticado,
    datos = {}
) {

    clienteAutenticado =
        autenticado;


    const estado =
        document.getElementById(
            "estadoSesion"
        );


    const formulariosAcceso =
        document.getElementById(
            "formulariosAcceso"
        );


    const sesionPedidos =
        document.getElementById(
            "sesionPedidos"
        );


    const nombreClientePedidos =
        document.getElementById(
            "nombreClientePedidos"
        );


    formulariosAcceso.hidden =
        autenticado;


    sesionPedidos.hidden =
        !autenticado;


    if (autenticado) {

        const nombreCliente =
            datos.razonSocial
            || datos.email
            || "Cliente";


        nombreClientePedidos.textContent =
            `👤 Cliente: ${nombreCliente}`;


        estado.className =
            "mensaje-exito";


        estado.textContent =
            `Sesión iniciada: ${nombreCliente}.`;

    } else {

        nombreClientePedidos.textContent =
            "👤 Cliente: Cliente";


        estado.className =
            "";


        estado.textContent =
            "Inicia sesión o registra una cuenta para generar pedidos.";


        document
            .getElementById("pedidos")
            .replaceChildren();
    }
}


/* =========================================================
   INICIAR SESIÓN
   ========================================================= */

async function iniciarSesion(evento) {

    evento.preventDefault();


    const resultado =
        document.getElementById(
            "resultadoLogin"
        );


    const email =
        document
            .getElementById("emailLogin")
            .value
            .trim();


    const password =
        document
            .getElementById("passwordLogin")
            .value;


    try {

        const respuesta = await fetch(
            `${API_BASE}/clientes/login`,
            {
                method:
                    "POST",

                headers:
                    await obtenerHeadersJson(),

                credentials:
                    "same-origin",

                body:
                    JSON.stringify({
                        email: email,
                        password: password
                    })
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );
        }


        document
            .getElementById("formLogin")
            .reset();


        resultado.textContent =
            "";


        await consultarSesion();


        if (accionPendiente === "pedidos") {

            accionPendiente =
                null;


            await cargarPedidos();


            mostrarSeccion(
                "pedidos"
            );

        } else {

            accionPendiente =
                null;


            mostrarSeccion(
                "pedido"
            );
        }


    } catch (error) {

        mostrarMensaje(
            resultado,
            `Error al iniciar sesión: ${error.message}`,
            false
        );
    }
}


/* =========================================================
   REGISTRAR CLIENTE
   ========================================================= */

async function registrarCliente(evento) {

    evento.preventDefault();


    const resultado =
        document.getElementById(
            "resultadoRegistro"
        );


    const cliente = {

        rut:
            document
                .getElementById(
                    "rutRegistro"
                )
                .value
                .trim(),

        razonSocial:
            document
                .getElementById(
                    "razonSocialRegistro"
                )
                .value
                .trim(),

        nombreContacto:
            document
                .getElementById(
                    "nombreContactoRegistro"
                )
                .value
                .trim(),

        email:
            document
                .getElementById(
                    "emailRegistro"
                )
                .value
                .trim(),

        telefono:
            document
                .getElementById(
                    "telefonoRegistro"
                )
                .value
                .trim(),

        direccion:
            document
                .getElementById(
                    "direccionRegistro"
                )
                .value
                .trim(),

        password:
            document
                .getElementById(
                    "passwordRegistro"
                )
                .value
    };


    try {

        const respuesta = await fetch(
            `${API_BASE}/clientes/registro`,
            {
                method:
                    "POST",

                headers:
                    await obtenerHeadersJson(),

                credentials:
                    "same-origin",

                body:
                    JSON.stringify(
                        cliente
                    )
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );
        }


        document
            .getElementById(
                "formRegistro"
            )
            .reset();


        mostrarMensaje(
            resultado,
            "Cliente registrado correctamente. Ahora puedes iniciar sesión.",
            true
        );


    } catch (error) {

        mostrarMensaje(
            resultado,
            `Error al registrar cliente: ${error.message}`,
            false
        );
    }
}


/* =========================================================
   CERRAR SESIÓN
   ========================================================= */

async function cerrarSesion() {

    const estado =
        document.getElementById(
            "estadoSesion"
        );


    try {

        const respuesta = await fetch(
            `${API_BASE}/clientes/logout`,
            {
                method:
                    "POST",

                headers:
                    await obtenerHeadersJson(),

                credentials:
                    "same-origin"
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );
        }


        actualizarInterfazSesion(
            false
        );


        document
            .getElementById(
                "formPedido"
            )
            .reset();


        document
            .getElementById(
                "formComprobante"
            )
            .reset();


        document
            .getElementById(
                "formLogin"
            )
            .reset();


        tokenCsrf =
            null;

        nombreCabeceraCsrf =
            null;

        accionPendiente =
            null;


        ocultarSeccionesContenido();


        document
            .querySelector(
                ".portada-mapuescuela"
            )
            .scrollIntoView({
                behavior: "smooth",
                block: "start"
            });


    } catch (error) {

        mostrarMensaje(
            estado,
            `Error al cerrar sesión: ${error.message}`,
            false
        );
    }
}


/* =========================================================
   PRODUCTOS
   ========================================================= */

async function cargarProductos() {

    const contenedor =
        document.getElementById(
            "productos"
        );


    const selectorProducto =
        document.getElementById(
            "producto"
        );


    try {

        const respuesta = await fetch(
            `${API_BASE}/productos`,
            {
                credentials:
                    "same-origin"
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                "No fue posible obtener los productos."
            );
        }


        const productos =
            await respuesta.json();


        contenedor.replaceChildren();

        selectorProducto.replaceChildren();


        const opcionInicial =
            document.createElement(
                "option"
            );


        opcionInicial.value =
            "";


        opcionInicial.textContent =
            "Seleccione un producto";


        selectorProducto.appendChild(
            opcionInicial
        );


        if (
            !Array.isArray(productos)
            || productos.length === 0
        ) {

            agregarParrafo(
                contenedor,
                "No hay productos registrados."
            );

            return;
        }


        productos.forEach(
            (producto) => {

                const tarjeta =
                    document.createElement(
                        "div"
                    );


                tarjeta.className =
                    "producto-card";


                const titulo =
                    document.createElement(
                        "h3"
                    );


                titulo.textContent =
                    producto.nombre;


                tarjeta.appendChild(
                    titulo
                );


                agregarParrafo(
                    tarjeta,
                    `Precio: ${formatearMoneda(
                        producto.precio
                    )}`
                );


                agregarParrafo(
                    tarjeta,
                    `Stock: ${producto.stock}`
                );


                agregarParrafo(
                    tarjeta,
                    `Estado: ${
                        producto.activo === false
                            ? "NO DISPONIBLE"
                            : "DISPONIBLE"
                    }`
                );


                contenedor.appendChild(
                    tarjeta
                );


                if (
                    producto.activo !== false
                    && Number(
                        producto.stock
                    ) > 0
                ) {

                    const opcion =
                        document.createElement(
                            "option"
                        );


                    opcion.value =
                        producto.idProducto;


                    opcion.textContent =
                        `${producto.nombre} - ${formatearMoneda(
                            producto.precio
                        )}`;


                    selectorProducto.appendChild(
                        opcion
                    );
                }
            }
        );


    } catch (error) {

        contenedor.replaceChildren();


        mostrarMensaje(
            contenedor,
            `Error al cargar productos: ${error.message}`,
            false
        );
    }
}


/* =========================================================
   CREAR PEDIDO
   ========================================================= */

async function crearPedido(evento) {

    evento.preventDefault();


    const resultado =
        document.getElementById(
            "resultadoPedido"
        );


    if (!clienteAutenticado) {

        mostrarMensaje(
            resultado,
            "Debes iniciar sesión para generar un pedido.",
            false
        );

        return;
    }


    const idProducto =
        Number(
            document
                .getElementById(
                    "producto"
                )
                .value
        );


    const cantidad =
        Number(
            document
                .getElementById(
                    "cantidad"
                )
                .value
        );


    const modalidadEntrega =
        document
            .getElementById(
                "modalidad"
            )
            .value;


    if (
        !Number.isInteger(idProducto)
        || idProducto < 1
        || !Number.isInteger(cantidad)
        || cantidad < 1
    ) {

        mostrarMensaje(
            resultado,
            "Selecciona un producto y una cantidad válida.",
            false
        );

        return;
    }


    const pedido = {

        modalidadEntrega:
            modalidadEntrega,

        productos: [
            {
                idProducto:
                    idProducto,

                cantidad:
                    cantidad
            }
        ]
    };


    try {

        const respuesta = await fetch(
            `${API_BASE}/pedidos`,
            {
                method:
                    "POST",

                headers:
                    await obtenerHeadersJson(),

                credentials:
                    "same-origin",

                body:
                    JSON.stringify(
                        pedido
                    )
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );
        }


        const pedidoCreado =
            await respuesta.json();


        mostrarMensaje(
            resultado,
            `Pedido ${pedidoCreado.idPedido} creado correctamente.`,
            true
        );


        document
            .getElementById(
                "formPedido"
            )
            .reset();


        document
            .getElementById(
                "cantidad"
            )
            .value = 1;


        await cargarProductos();

        await cargarPedidos();


    } catch (error) {

        mostrarMensaje(
            resultado,
            `Error al generar pedido: ${error.message}`,
            false
        );
    }
}


/* =========================================================
   MIS PEDIDOS
   ========================================================= */

async function cargarPedidos() {

    const contenedor =
        document.getElementById(
            "pedidos"
        );


    if (!clienteAutenticado) {

        contenedor.replaceChildren();

        return;
    }


    try {

        const respuesta = await fetch(
            `${API_BASE}/pedidos`,
            {
                credentials:
                    "same-origin"
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                "No fue posible obtener los pedidos."
            );
        }


        const pedidos =
            await respuesta.json();


        contenedor.replaceChildren();


        if (
            !Array.isArray(pedidos)
            || pedidos.length === 0
        ) {

            agregarParrafo(
                contenedor,
                "Aún no tienes pedidos registrados."
            );

            return;
        }


        const tablaContenedor =
            document.createElement(
                "div"
            );


        tablaContenedor.className =
            "tabla-contenedor";


        const tabla =
            document.createElement(
                "table"
            );


        const thead =
            document.createElement(
                "thead"
            );


        const filaCabecera =
            document.createElement(
                "tr"
            );


        [
            "Pedido",
            "Estado",
            "Modalidad",
            "Total"
        ].forEach(
            (texto) => {

                const th =
                    document.createElement(
                        "th"
                    );


                th.textContent =
                    texto;


                filaCabecera.appendChild(
                    th
                );
            }
        );


        thead.appendChild(
            filaCabecera
        );


        tabla.appendChild(
            thead
        );


        const tbody =
            document.createElement(
                "tbody"
            );


        pedidos.forEach(
            (pedido) => {

                const fila =
                    document.createElement(
                        "tr"
                    );


                [
                    pedido.idPedido,
                    pedido.estado,
                    pedido.modalidadEntrega,
                    formatearMoneda(
                        pedido.total
                    )
                ].forEach(
                    (valor) => {

                        const td =
                            document.createElement(
                                "td"
                            );


                        td.textContent =
                            valor ?? "";


                        fila.appendChild(
                            td
                        );
                    }
                );


                tbody.appendChild(
                    fila
                );
            }
        );


        tabla.appendChild(
            tbody
        );


        tablaContenedor.appendChild(
            tabla
        );


        contenedor.appendChild(
            tablaContenedor
        );


    } catch (error) {

        contenedor.replaceChildren();


        mostrarMensaje(
            contenedor,
            `Error al cargar pedidos: ${error.message}`,
            false
        );
    }
}


/* =========================================================
   COMPROBANTE DE PAGO
   ========================================================= */

async function subirComprobante(evento) {

    evento.preventDefault();


    const resultado =
        document.getElementById(
            "resultadoComprobante"
        );


    if (!clienteAutenticado) {

        mostrarMensaje(
            resultado,
            "Debes iniciar sesión para adjuntar un comprobante.",
            false
        );

        return;
    }


    const idPedido =
        Number(
            document
                .getElementById(
                    "pedidoComprobante"
                )
                .value
        );


    const archivo =
        document
            .getElementById(
                "archivoComprobante"
            )
            .files[0];


    if (
        !Number.isInteger(idPedido)
        || idPedido < 1
        || !archivo
    ) {

        mostrarMensaje(
            resultado,
            "Indica un pedido válido y selecciona un archivo.",
            false
        );

        return;
    }


    const datos =
        new FormData();


    datos.append(
        "archivo",
        archivo
    );


    try {

        const headers =
            await obtenerHeadersCsrf();


        const respuesta = await fetch(
            `${API_BASE}/pedidos/${idPedido}/comprobante`,
            {
                method:
                    "POST",

                headers:
                    headers,

                credentials:
                    "same-origin",

                body:
                    datos
            }
        );


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );
        }


        mostrarMensaje(
            resultado,
            "Comprobante adjuntado correctamente.",
            true
        );


        document
            .getElementById(
                "formComprobante"
            )
            .reset();


        await cargarPedidos();


    } catch (error) {

        mostrarMensaje(
            resultado,
            `Error al adjuntar comprobante: ${error.message}`,
            false
        );
    }
}


/* =========================================================
   FUNCIONES AUXILIARES
   ========================================================= */

function agregarParrafo(
    contenedor,
    texto
) {

    const parrafo =
        document.createElement(
            "p"
        );


    parrafo.textContent =
        texto;


    contenedor.appendChild(
        parrafo
    );
}


function mostrarMensaje(
    contenedor,
    mensaje,
    exito
) {

    contenedor.textContent =
        mensaje;


    contenedor.className =
        exito
            ? "mensaje-exito"
            : "mensaje-error";
}


async function obtenerMensajeError(
    respuesta
) {

    try {

        const datos =
            await respuesta.json();


        return datos.message
            || datos.mensaje
            || datos.error
            || `Error HTTP ${respuesta.status}`;


    } catch (error) {

        return `Error HTTP ${respuesta.status}`;
    }
}


function formatearMoneda(valor) {

    const numero =
        Number(valor);


    if (!Number.isFinite(numero)) {

        return "$0";
    }


    return new Intl.NumberFormat(
        "es-CL",
        {
            style:
                "currency",

            currency:
                "CLP",

            maximumFractionDigits:
                0
        }
    ).format(
        numero
    );
}