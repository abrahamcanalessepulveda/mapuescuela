
const API_BASE = "/api";

let clienteAutenticado = false;

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

    cargarProductos();
    consultarSesion();
});

async function consultarSesion() {
    const estado = document.getElementById("estadoSesion");

    try {
        const respuesta = await fetch(
            `${API_BASE}/clientes/sesion`,
            { credentials: "same-origin" }
        );

        if (!respuesta.ok) {
            throw new Error("No fue posible consultar la sesión.");
        }

        const datos = await respuesta.json();

        if (datos.autenticado === true) {
            actualizarInterfazSesion(true, datos);
            await cargarPedidos();
        } else {
            actualizarInterfazSesion(false);
        }
    } catch (error) {
        actualizarInterfazSesion(false);

        mostrarMensaje(
            estado,
            `Error al consultar la sesión: ${error.message}`,
            false
        );
    }
}

function actualizarInterfazSesion(autenticado, datos = {}) {
    clienteAutenticado = autenticado;

    const estado = document.getElementById("estadoSesion");

    document.getElementById("formulariosAcceso").hidden =
        autenticado;

    document.getElementById("accionesSesion").hidden =
        !autenticado;

    document.getElementById("seccionPedido").hidden =
        !autenticado;

    document.getElementById("seccionPedidos").hidden =
        !autenticado;

    document.getElementById("seccionComprobante").hidden =
        !autenticado;

    if (autenticado) {
        estado.className = "mensaje-exito";
        estado.textContent =
            `Sesión iniciada: ${datos.razonSocial || datos.email || "Cliente"}.`;
    } else {
        estado.className = "";
        estado.textContent =
            "Inicia sesión o registra una cuenta para generar pedidos.";

        document.getElementById("pedidos").replaceChildren();
        document.getElementById("resultadoPedido").textContent = "";
        document.getElementById("resultadoComprobante").textContent = "";
    }
}

async function iniciarSesion(evento) {
    evento.preventDefault();

    const resultado = document.getElementById("resultadoLogin");

    const datos = {
        email: document.getElementById("emailLogin").value.trim(),
        password: document.getElementById("passwordLogin").value
    };

    try {
        const respuesta = await fetchSeguro(
            `${API_BASE}/clientes/login`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(datos)
            }
        );

        if (!respuesta.ok) {
            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible iniciar sesión."
                )
            );
        }

        document.getElementById("formLogin").reset();
        resultado.textContent = "";

        await consultarSesion();
    } catch (error) {
        mostrarMensaje(
            resultado,
            `Error al iniciar sesión: ${error.message}`,
            false
        );
    }
}

async function registrarCliente(evento) {
    evento.preventDefault();

    const resultado = document.getElementById("resultadoRegistro");

    const datos = {
        rut: document.getElementById("rutRegistro").value.trim(),
        razonSocial: document
            .getElementById("razonSocialRegistro").value.trim(),
        nombreContacto: document
            .getElementById("nombreContactoRegistro").value.trim(),
        email: document.getElementById("emailRegistro").value.trim(),
        telefono: document.getElementById("telefonoRegistro").value.trim(),
        direccion: document.getElementById("direccionRegistro").value.trim(),
        password: document.getElementById("passwordRegistro").value
    };

    try {
        const respuesta = await fetchSeguro(
            `${API_BASE}/clientes/registro`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(datos)
            }
        );

        if (!respuesta.ok) {
            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible registrar al cliente."
                )
            );
        }

        document.getElementById("formRegistro").reset();

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

async function cerrarSesion() {
    const estado = document.getElementById("estadoSesion");

    try {
        const respuesta = await fetchSeguro(
            `${API_BASE}/clientes/logout`,
            { method: "POST" }
        );

        if (!respuesta.ok) {
            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible cerrar la sesión."
                )
            );
        }

        actualizarInterfazSesion(false);

        document.getElementById("formPedido").reset();
        document.getElementById("formComprobante").reset();
        document.getElementById("formLogin").reset();

        // El cierre de sesión invalida la sesión anterior.
        // Se solicitará un token CSRF nuevo en el siguiente POST.
        tokenCsrf = null;
        nombreCabeceraCsrf = null;
    } catch (error) {
        mostrarMensaje(
            estado,
            `Error al cerrar sesión: ${error.message}`,
            false
        );
    }
}

async function cargarProductos() {
    const contenedor = document.getElementById("productos");
    const selectorProducto = document.getElementById("producto");

    try {
        const respuesta = await fetch(
            `${API_BASE}/productos`,
            { credentials: "same-origin" }
        );

        if (!respuesta.ok) {
            throw new Error("No fue posible obtener los productos.");
        }

        const productos = await respuesta.json();

        contenedor.replaceChildren();
        selectorProducto.replaceChildren();

        const opcionInicial = document.createElement("option");
        opcionInicial.value = "";
        opcionInicial.textContent = "Seleccione un producto";
        selectorProducto.appendChild(opcionInicial);

        if (!Array.isArray(productos) || productos.length === 0) {
            agregarParrafo(
                contenedor,
                "No hay productos registrados."
            );
            return;
        }

        productos.forEach((producto) => {
            const tarjeta = document.createElement("div");
            tarjeta.className = "producto-card";

            const titulo = document.createElement("h3");
            titulo.textContent = producto.nombre;
            tarjeta.appendChild(titulo);

            agregarParrafo(
                tarjeta,
                `Precio: $${formatearNumero(producto.precio)}`
            );

            agregarParrafo(
                tarjeta,
                `Stock: ${producto.stock}`
            );

            agregarParrafo(
                tarjeta,
                `Estado: ${producto.estado}`
            );

            contenedor.appendChild(tarjeta);

            if (
                producto.stock > 0
                && producto.estado === "DISPONIBLE"
            ) {
                const opcion = document.createElement("option");
                opcion.value = producto.idProducto;
                opcion.textContent =
                    `${producto.nombre} - Stock: ${producto.stock}`;

                selectorProducto.appendChild(opcion);
            }
        });
    } catch (error) {
        mostrarMensaje(
            contenedor,
            `Error al cargar productos: ${error.message}`,
            false
        );
    }
}

async function crearPedido(evento) {
    evento.preventDefault();

    const resultado = document.getElementById("resultadoPedido");

    if (!clienteAutenticado) {
        mostrarMensaje(
            resultado,
            "Debes iniciar sesión para generar un pedido.",
            false
        );
        return;
    }

    const idProducto = Number(
        document.getElementById("producto").value
    );

    const cantidad = Number(
        document.getElementById("cantidad").value
    );

    const modalidadEntrega =
        document.getElementById("modalidad").value;

    if (
        !Number.isInteger(idProducto)
        || idProducto < 1
        || !Number.isInteger(cantidad)
        || cantidad < 1
    ) {
        mostrarMensaje(
            resultado,
            "Debe completar correctamente los datos del pedido.",
            false
        );
        return;
    }

    const pedido = {
        modalidadEntrega: modalidadEntrega,
        productos: [
            {
                idProducto: idProducto,
                cantidad: cantidad
            }
        ]
    };

    try {
        const respuesta = await fetchSeguro(
            `${API_BASE}/pedidos`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(pedido)
            }
        );

        if (!respuesta.ok) {
            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible generar el pedido."
                )
            );
        }

        const pedidoCreado = await respuesta.json();

        mostrarMensaje(
            resultado,
            `Pedido N.º ${pedidoCreado.idPedido} generado correctamente. Estado: ${pedidoCreado.estado}.`,
            true
        );

        document.getElementById("formPedido").reset();
        document.getElementById("cantidad").value = 1;

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

async function cargarPedidos() {
    if (!clienteAutenticado) {
        return;
    }

    const contenedor = document.getElementById("pedidos");

    try {
        const respuesta = await fetch(
            `${API_BASE}/pedidos`,
            { credentials: "same-origin" }
        );

        if (!respuesta.ok) {
            throw new Error("No fue posible obtener los pedidos.");
        }

        const pedidos = await respuesta.json();

        contenedor.replaceChildren();

        if (!Array.isArray(pedidos) || pedidos.length === 0) {
            agregarParrafo(
                contenedor,
                "No tienes pedidos registrados."
            );
            return;
        }

        const tablaContenedor = document.createElement("div");
        tablaContenedor.className = "tabla-contenedor";

        const tabla = document.createElement("table");
        const encabezado = document.createElement("thead");
        const filaEncabezado = document.createElement("tr");

        ["Pedido", "Estado", "Modalidad", "Total"].forEach(
            (nombreColumna) => {
                const celda = document.createElement("th");
                celda.textContent = nombreColumna;
                filaEncabezado.appendChild(celda);
            }
        );

        encabezado.appendChild(filaEncabezado);
        tabla.appendChild(encabezado);

        const cuerpo = document.createElement("tbody");

        pedidos.forEach((pedido) => {
            const fila = document.createElement("tr");

            [
                pedido.idPedido,
                pedido.estado,
                pedido.modalidadEntrega,
                `$${formatearNumero(pedido.total)}`
            ].forEach((valor) => {
                const celda = document.createElement("td");
                celda.textContent = String(valor ?? "");
                fila.appendChild(celda);
            });

            cuerpo.appendChild(fila);
        });

        tabla.appendChild(cuerpo);
        tablaContenedor.appendChild(tabla);
        contenedor.appendChild(tablaContenedor);
    } catch (error) {
        mostrarMensaje(
            contenedor,
            `Error al cargar pedidos: ${error.message}`,
            false
        );
    }
}

async function subirComprobante(evento) {
    evento.preventDefault();

    const resultado =
        document.getElementById("resultadoComprobante");

    if (!clienteAutenticado) {
        mostrarMensaje(
            resultado,
            "Debes iniciar sesión para adjuntar un comprobante.",
            false
        );
        return;
    }

    const idPedido = Number(
        document.getElementById("pedidoComprobante").value
    );

    const campoArchivo =
        document.getElementById("archivoComprobante");

    if (
        !Number.isInteger(idPedido)
        || idPedido < 1
        || campoArchivo.files.length === 0
    ) {
        mostrarMensaje(
            resultado,
            "Debe indicar el número de pedido y seleccionar un archivo.",
            false
        );
        return;
    }

    const formulario = new FormData();
    formulario.append("archivo", campoArchivo.files[0]);

    try {
        const respuesta = await fetchSeguro(
            `${API_BASE}/comprobantes?idPedido=${idPedido}`,
            {
                method: "POST",
                body: formulario
            }
        );

        if (!respuesta.ok) {
            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible subir el comprobante."
                )
            );
        }

        const comprobante = await respuesta.json();

        mostrarMensaje(
            resultado,
            `Comprobante registrado correctamente para el pedido N.º ${comprobante.idPedido}. Estado del pedido: ${comprobante.estadoPedido}.`,
            true
        );

        document.getElementById("formComprobante").reset();

        await cargarPedidos();
    } catch (error) {
        mostrarMensaje(
            resultado,
            `Error al subir comprobante: ${error.message}`,
            false
        );
    }
}

async function obtenerMensajeError(respuesta, mensajePredeterminado) {
    const texto = await respuesta.text();

    if (!texto) {
        return mensajePredeterminado;
    }

    try {
        const datos = JSON.parse(texto);

        if (typeof datos.mensaje === "string") {
            return datos.mensaje;
        }

        if (typeof datos.error === "string") {
            return datos.error;
        }
    } catch (error) {
        // La respuesta no contiene JSON.
    }

    return mensajePredeterminado;
}

function agregarParrafo(contenedor, contenido) {
    const parrafo = document.createElement("p");
    parrafo.textContent = contenido;
    contenedor.appendChild(parrafo);
}

function mostrarMensaje(contenedor, mensaje, exito) {
    contenedor.className =
        exito ? "mensaje-exito" : "mensaje-error";

    contenedor.textContent = mensaje;
}

function formatearNumero(valor) {
    if (valor === null || valor === undefined) {
        return "0";
    }

    return Number(valor).toLocaleString("es-CL");
}
