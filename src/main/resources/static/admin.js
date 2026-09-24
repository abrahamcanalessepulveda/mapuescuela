const API_ADMIN = "/api/admin";

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("formLogin")
        .addEventListener("submit", iniciarSesion);

    document.getElementById("btnCerrarSesion")
        .addEventListener("click", cerrarSesion);

    document.getElementById("formConsultarPedido")
        .addEventListener("submit", consultarPedido);

    consultarSesion();
});

async function consultarSesion() {
    try {
        const respuesta = await fetch(`${API_ADMIN}/sesion`);
        const datos = await respuesta.json();

        if (datos.autenticado) {
            mostrarAdministracion();
        } else {
            mostrarLogin();
        }

    } catch (error) {
        mostrarLogin();

        mostrarMensaje(
            document.getElementById("resultadoLogin"),
            "No fue posible comprobar la sesión administrativa.",
            false
        );
    }
}

async function iniciarSesion(evento) {
    evento.preventDefault();

    const resultado =
        document.getElementById("resultadoLogin");

    const username =
        document.getElementById("adminUsuario").value.trim();

    const password =
        document.getElementById("adminPassword").value;

    if (!username || !password) {
        mostrarMensaje(
            resultado,
            "Debe ingresar usuario y contraseña.",
            false
        );

        return;
    }

    try {
        const respuesta = await fetchSeguro(`${API_ADMIN}/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const datos = await respuesta.json();

        if (!respuesta.ok) {
            throw new Error(
                datos.mensaje ||
                "No fue posible iniciar sesión"
            );
        }

        document.getElementById("formLogin").reset();
        mostrarAdministracion();

    } catch (error) {
        mostrarMensaje(
            resultado,
            error.message,
            false
        );
    }
}

async function cerrarSesion() {
    try {
        await fetchSeguro(`${API_ADMIN}/logout`, {
            method: "POST"
        });

    } catch (error) {
        console.error(
            "Error al cerrar sesión:",
            error
        );
    }

    document.getElementById("formConsultarPedido").reset();
    document.getElementById("resultadoAdmin").innerHTML = "";
    document.getElementById("accionesPedido").style.display = "none";

    mostrarLogin();
}

async function consultarPedido(evento) {
    evento.preventDefault();

    const idPedido =
        Number(
            document.getElementById("adminPedido").value
        );

    await cargarPedido(idPedido);
}

async function cargarPedido(idPedido) {
    const resultado =
        document.getElementById("resultadoAdmin");

    if (!idPedido || idPedido < 1) {
        mostrarMensaje(
            resultado,
            "Debe indicar un número de pedido válido.",
            false
        );

        return;
    }

    try {
        const respuesta =
            await fetch(
                `${API_ADMIN}/pedidos/${idPedido}/tareas`
            );

        const datos = await respuesta.json();

        if (respuesta.status === 401) {
            mostrarLogin();

            throw new Error(
                "La sesión administrativa ha finalizado."
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                datos.mensaje ||
                "No fue posible consultar el pedido."
            );
        }

        mostrarPedido(datos);

    } catch (error) {
        document.getElementById("accionesPedido")
            .style.display = "none";

        mostrarMensaje(
            resultado,
            error.message,
            false
        );
    }
}

function mostrarPedido(datos) {
    const resultado =
        document.getElementById("resultadoAdmin");

    const acciones =
        document.getElementById("accionesPedido");

    const detalle =
        document.getElementById("detalleTarea");

    const botones =
        document.getElementById("botonesAcciones");

    mostrarMensaje(
        resultado,
        `Pedido N.º ${datos.idPedido} consultado correctamente.`,
        true
    );

    let datosRetiro = "";

    if (datos.nombrePersonaRetira) {
        datosRetiro = `
            <p>
                <strong>Retirado por:</strong>
                ${escaparHtml(datos.nombrePersonaRetira)}
            </p>

            <p>
                <strong>RUT:</strong>
                ${escaparHtml(datos.rutPersonaRetira || "")}
            </p>

            <p>
                <strong>Fecha de retiro:</strong>
                ${formatearFecha(datos.fechaRetiro)}
            </p>
        `;
    }

    let datosComprobante = "";

    if (datos.hayComprobante) {
        datosComprobante = `
            <div class="comprobante-admin">
                <h3>Comprobante de pago</h3>

                <p>
                    <strong>Fecha de carga:</strong>
                    ${formatearFecha(
                        datos.fechaCargaComprobante
                    )}
                </p>

                <p>
                    <strong>Estado de validación:</strong>
                    ${formatearEstadoComprobante(
                        datos.estadoValidacionComprobante
                    )}
                </p>

                ${
                    datos.observacionComprobante
                        ? `
                            <p>
                                <strong>Observación:</strong>
                                ${escaparHtml(
                                    datos.observacionComprobante
                                )}
                            </p>
                        `
                        : ""
                }

                <p>
                    <a
                        href="${API_ADMIN}/comprobantes/${datos.idComprobante}/archivo"
                        target="_blank"
                        rel="noopener noreferrer"
                    >
                        Ver comprobante
                    </a>
                </p>
            </div>
        `;
    }

    let datosDespacho = "";

    if (datos.empresaTransporte
            || datos.numeroSeguimiento
            || datos.fechaEnvio) {

        datosDespacho = `
            <p>
                <strong>Empresa de transporte:</strong>
                ${escaparHtml(datos.empresaTransporte || "")}
            </p>

            <p>
                <strong>Número de seguimiento:</strong>
                ${escaparHtml(datos.numeroSeguimiento || "")}
            </p>

            <p>
                <strong>Fecha de envío:</strong>
                ${formatearFecha(datos.fechaEnvio)}
            </p>
        `;

        if (datos.fechaEntrega) {
            datosDespacho += `
                <p>
                    <strong>Fecha de entrega:</strong>
                    ${formatearFecha(datos.fechaEntrega)}
                </p>
            `;
        }
    }

    detalle.innerHTML = `
        <p>
            <strong>Pedido:</strong>
            N.º ${datos.idPedido}
        </p>

        <p>
            <strong>Estado actual:</strong>
            ${formatearEstado(datos.estado)}
        </p>

        <p>
            <strong>Modalidad de entrega:</strong>
            ${formatearModalidad(datos.modalidadEntrega)}
        </p>

        <p>
            ${obtenerDescripcionEstado(datos)}
        </p>

        ${datosComprobante}
        ${datosRetiro}
        ${datosDespacho}
    `;

    botones.innerHTML = "";

    crearAccionesPedido(datos, botones);

    acciones.style.display = "block";
}

function crearAccionesPedido(datos, contenedor) {
    const accion = datos.accionPendiente;

    if (accion === "Validar comprobante de pago") {
        if (!datos.hayComprobante) {
            const mensaje =
                document.createElement("p");

            mensaje.textContent =
                "No se encontró un comprobante asociado al pedido.";

            contenedor.appendChild(mensaje);
            return;
        }

        agregarBoton(
            contenedor,
            "Aprobar pago",
            () => validarPago(
                datos.idPedido,
                "APROBADO"
            )
        );

        agregarBoton(
            contenedor,
            "Rechazar pago",
            () => validarPago(
                datos.idPedido,
                "RECHAZADO"
            )
        );

        return;
    }

    if (accion === "Empacar y preparar pedido") {
        agregarBoton(
            contenedor,
            "Confirmar pedido preparado",
            () => completarAccion(
                datos.idPedido,
                "Empacar y preparar pedido",
                {},
                "El pedido fue marcado como preparado."
            )
        );

        return;
    }

    if (accion ===
            "Registrar pedido disponible para retiro en local") {

        agregarBoton(
            contenedor,
            "Confirmar pedido listo para retiro",
            () => completarAccion(
                datos.idPedido,
                "Registrar pedido disponible para retiro en local",
                {},
                "El pedido quedó disponible para retiro."
            )
        );

        return;
    }

    if (accion === "Registrar retiro en el local") {
        crearFormularioRetiro(
            datos.idPedido,
            contenedor
        );

        return;
    }

    if (accion ===
            "Registrar envío y número de seguimiento") {

        crearFormularioDespacho(
            datos.idPedido,
            contenedor
        );

        return;
    }

    if (accion === "Confirmar entrega del pedido") {
        agregarBoton(
            contenedor,
            "Confirmar entrega",
            () => completarAccion(
                datos.idPedido,
                "Confirmar entrega del pedido",
                {},
                "La entrega del pedido fue confirmada."
            )
        );

        return;
    }

    if (accion === "Informar rechazo a cliente") {
        agregarBoton(
            contenedor,
            "Confirmar aviso de rechazo",
            () => completarAccion(
                datos.idPedido,
                "Informar rechazo a cliente",
                {},
                "Se confirmó el aviso de rechazo."
            )
        );

        return;
    }

    const mensaje = document.createElement("p");

    if (datos.estado === "FINALIZADO") {
        mensaje.textContent =
            "El pedido se encuentra finalizado.";

    } else if (datos.estado === "CANCELADO") {
        mensaje.textContent =
            "El pedido se encuentra cancelado.";

    } else {
        mensaje.textContent =
            "No existen acciones pendientes para este pedido en este momento.";
    }

    contenedor.appendChild(mensaje);
}

function crearFormularioRetiro(
        idPedido,
        contenedor) {

    const titulo =
        document.createElement("h3");

    titulo.textContent =
        "Registrar retiro del pedido";

    contenedor.appendChild(titulo);

    const descripcion =
        document.createElement("p");

    descripcion.textContent =
        "Ingrese los datos de la persona que retira el pedido.";

    contenedor.appendChild(descripcion);

    const labelNombre =
        document.createElement("label");

    labelNombre.setAttribute(
        "for",
        "nombrePersonaRetira"
    );

    labelNombre.textContent =
        "Nombre de la persona que retira";

    contenedor.appendChild(labelNombre);

    contenedor.appendChild(
        document.createElement("br")
    );

    const inputNombre =
        document.createElement("input");

    inputNombre.type = "text";
    inputNombre.id = "nombrePersonaRetira";
    inputNombre.maxLength = 150;
    inputNombre.autocomplete = "off";

    contenedor.appendChild(inputNombre);

    contenedor.appendChild(
        document.createElement("br")
    );

    contenedor.appendChild(
        document.createElement("br")
    );

    const labelRut =
        document.createElement("label");

    labelRut.setAttribute(
        "for",
        "rutPersonaRetira"
    );

    labelRut.textContent =
        "RUT de la persona que retira";

    contenedor.appendChild(labelRut);

    contenedor.appendChild(
        document.createElement("br")
    );

    const inputRut =
        document.createElement("input");

    inputRut.type = "text";
    inputRut.id = "rutPersonaRetira";
    inputRut.maxLength = 20;
    inputRut.placeholder = "Ej.: 12.345.678-9";
    inputRut.autocomplete = "off";

    contenedor.appendChild(inputRut);

    contenedor.appendChild(
        document.createElement("br")
    );

    contenedor.appendChild(
        document.createElement("br")
    );

    agregarBoton(
        contenedor,
        "Marcar pedido como retirado",
        () => registrarRetiroDesdePanel(
            idPedido,
            inputNombre,
            inputRut
        )
    );
}

function crearFormularioDespacho(
        idPedido,
        contenedor) {

    const titulo =
        document.createElement("h3");

    titulo.textContent =
        "Registrar despacho del pedido";

    contenedor.appendChild(titulo);

    const descripcion =
        document.createElement("p");

    descripcion.textContent =
        "Ingrese los antecedentes del envío antes de despachar el pedido.";

    contenedor.appendChild(descripcion);

    const labelEmpresa =
        document.createElement("label");

    labelEmpresa.setAttribute(
        "for",
        "empresaTransporte"
    );

    labelEmpresa.textContent =
        "Empresa de transporte";

    contenedor.appendChild(labelEmpresa);

    contenedor.appendChild(
        document.createElement("br")
    );

    const inputEmpresa =
        document.createElement("input");

    inputEmpresa.type = "text";
    inputEmpresa.id = "empresaTransporte";
    inputEmpresa.maxLength = 100;
    inputEmpresa.placeholder = "Ej.: Chilexpress";
    inputEmpresa.autocomplete = "off";

    contenedor.appendChild(inputEmpresa);

    contenedor.appendChild(
        document.createElement("br")
    );

    contenedor.appendChild(
        document.createElement("br")
    );

    const labelSeguimiento =
        document.createElement("label");

    labelSeguimiento.setAttribute(
        "for",
        "numeroSeguimiento"
    );

    labelSeguimiento.textContent =
        "Número de envío / seguimiento";

    contenedor.appendChild(labelSeguimiento);

    contenedor.appendChild(
        document.createElement("br")
    );

    const inputSeguimiento =
        document.createElement("input");

    inputSeguimiento.type = "text";
    inputSeguimiento.id = "numeroSeguimiento";
    inputSeguimiento.maxLength = 100;
    inputSeguimiento.autocomplete = "off";

    contenedor.appendChild(inputSeguimiento);

    contenedor.appendChild(
        document.createElement("br")
    );

    contenedor.appendChild(
        document.createElement("br")
    );

    const labelFecha =
        document.createElement("label");

    labelFecha.setAttribute(
        "for",
        "fechaEnvio"
    );

    labelFecha.textContent =
        "Fecha y hora de envío";

    contenedor.appendChild(labelFecha);

    contenedor.appendChild(
        document.createElement("br")
    );

    const inputFecha =
        document.createElement("input");

    inputFecha.type = "datetime-local";
    inputFecha.id = "fechaEnvio";
    inputFecha.value =
        obtenerFechaHoraLocalActual();

    contenedor.appendChild(inputFecha);

    contenedor.appendChild(
        document.createElement("br")
    );

    contenedor.appendChild(
        document.createElement("br")
    );

    agregarBoton(
        contenedor,
        "Marcar pedido como enviado",
        () => registrarDespachoDesdePanel(
            idPedido,
            inputEmpresa,
            inputSeguimiento,
            inputFecha
        )
    );
}

async function registrarRetiroDesdePanel(
        idPedido,
        inputNombre,
        inputRut) {

    const resultado =
        document.getElementById("resultadoAdmin");

    const nombrePersonaRetira =
        inputNombre.value.trim();

    const rutPersonaRetira =
        inputRut.value.trim();

    if (!nombrePersonaRetira) {
        mostrarMensaje(
            resultado,
            "Debe ingresar el nombre de la persona que retira.",
            false
        );

        inputNombre.focus();
        return;
    }

    if (!rutPersonaRetira) {
        mostrarMensaje(
            resultado,
            "Debe ingresar el RUT de la persona que retira.",
            false
        );

        inputRut.focus();
        return;
    }

    const confirmado =
        window.confirm(
            `¿Confirma el retiro del pedido N.º ${idPedido} por ${nombrePersonaRetira}?`
        );

    if (!confirmado) {
        return;
    }

    try {
        const respuesta =
            await fetchSeguro(
                `${API_ADMIN}/pedidos/${idPedido}/registrar-retiro`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        nombrePersonaRetira:
                            nombrePersonaRetira,
                        rutPersonaRetira:
                            rutPersonaRetira
                    })
                }
            );

        const datos = await respuesta.json();

        if (respuesta.status === 401) {
            mostrarLogin();

            throw new Error(
                "La sesión administrativa ha finalizado."
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                datos.mensaje ||
                "No fue posible registrar el retiro."
            );
        }

        mostrarMensaje(
            resultado,
            "El retiro del pedido fue registrado correctamente.",
            true
        );

        await esperar(1500);

        await cargarPedido(idPedido);

    } catch (error) {
        mostrarMensaje(
            resultado,
            error.message,
            false
        );
    }
}

async function registrarDespachoDesdePanel(
        idPedido,
        inputEmpresa,
        inputSeguimiento,
        inputFecha) {

    const resultado =
        document.getElementById("resultadoAdmin");

    const empresaTransporte =
        inputEmpresa.value.trim();

    const numeroSeguimiento =
        inputSeguimiento.value.trim();

    const fechaEnvio =
        inputFecha.value;

    if (!empresaTransporte) {
        mostrarMensaje(
            resultado,
            "Debe ingresar la empresa de transporte.",
            false
        );

        inputEmpresa.focus();
        return;
    }

    if (!numeroSeguimiento) {
        mostrarMensaje(
            resultado,
            "Debe ingresar el número de envío o seguimiento.",
            false
        );

        inputSeguimiento.focus();
        return;
    }

    if (!fechaEnvio) {
        mostrarMensaje(
            resultado,
            "Debe ingresar la fecha y hora de envío.",
            false
        );

        inputFecha.focus();
        return;
    }

    const confirmado =
        window.confirm(
            `¿Confirma el despacho del pedido N.º ${idPedido} mediante ${empresaTransporte}?`
        );

    if (!confirmado) {
        return;
    }

    try {
        const respuesta =
            await fetchSeguro(
                `${API_ADMIN}/pedidos/${idPedido}/registrar-despacho`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        empresaTransporte:
                            empresaTransporte,
                        numeroSeguimiento:
                            numeroSeguimiento,
                        fechaEnvio:
                            fechaEnvio
                    })
                }
            );

        const datos = await respuesta.json();

        if (respuesta.status === 401) {
            mostrarLogin();

            throw new Error(
                "La sesión administrativa ha finalizado."
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                datos.mensaje ||
                "No fue posible registrar el despacho."
            );
        }

        mostrarMensaje(
            resultado,
            "El despacho del pedido fue registrado correctamente.",
            true
        );

        await esperar(1500);

        await cargarPedido(idPedido);

    } catch (error) {
        mostrarMensaje(
            resultado,
            error.message,
            false
        );
    }
}

async function validarPago(
        idPedido,
        resultadoValidacion) {

    const aprobar =
        resultadoValidacion === "APROBADO";

    let motivoRechazo = "";

    if (!aprobar) {
        motivoRechazo = window.prompt(
            "Ingrese el motivo por el cual se rechaza el comprobante de pago:"
        );

        if (motivoRechazo === null) {
            return;
        }

        motivoRechazo =
            motivoRechazo.trim();

        if (!motivoRechazo) {
            window.alert(
                "Debe ingresar un motivo para rechazar el comprobante."
            );

            return;
        }
    }

    const texto =
        aprobar ? "aprobar" : "rechazar";

    const confirmado =
        window.confirm(
            `¿Confirma que desea ${texto} el pago del pedido N.º ${idPedido}?`
        );

    if (!confirmado) {
        return;
    }

    const variables = {
        resultadoValidacion:
            resultadoValidacion
    };

    if (!aprobar) {
        variables.motivoRechazo =
            motivoRechazo;
    }

    await completarAccion(
        idPedido,
        "Validar comprobante de pago",
        variables,
        aprobar
            ? "El pago fue aprobado correctamente."
            : "El pago fue rechazado."
    );
}

async function completarAccion(
        idPedido,
        nombreTarea,
        variables,
        mensajeExito) {

    const resultado =
        document.getElementById("resultadoAdmin");

    try {
        const respuesta =
            await fetchSeguro(
                `${API_ADMIN}/pedidos/${idPedido}/completar`,
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        nombreTarea: nombreTarea,
                        variables: variables
                    })
                }
            );

        const datos = await respuesta.json();

        if (respuesta.status === 401) {
            mostrarLogin();

            throw new Error(
                "La sesión administrativa ha finalizado."
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                datos.mensaje ||
                "No fue posible realizar la acción."
            );
        }

        mostrarMensaje(
            resultado,
            mensajeExito,
            true
        );

        await esperarActualizacion(nombreTarea);

        await cargarPedido(idPedido);

    } catch (error) {
        mostrarMensaje(
            resultado,
            error.message,
            false
        );
    }
}

async function esperarActualizacion(nombreTarea) {
    let milisegundos = 1000;

    if (nombreTarea ===
            "Validar comprobante de pago") {

        milisegundos = 6500;
    }

    await esperar(milisegundos);
}

function agregarBoton(
        contenedor,
        texto,
        accion) {

    const boton =
        document.createElement("button");

    boton.type = "button";
    boton.textContent = texto;

    boton.addEventListener(
        "click",
        accion
    );

    contenedor.appendChild(boton);

    contenedor.appendChild(
        document.createTextNode(" ")
    );
}

function obtenerDescripcionEstado(datos) {
    switch (datos.estado) {
        case "PENDIENTE_PAGO":
            return "El pedido está a la espera del comprobante de pago.";

        case "PAGO_EN_REVISION":
            return "El comprobante de pago fue recibido y debe ser revisado.";

        case "PAGO_RECHAZADO":
            return "El comprobante de pago fue rechazado.";

        case "PAGO_APROBADO":
            return "El pago fue aprobado correctamente.";

        case "EN_PREPARACION":
            return "El pago fue aprobado y el pedido se encuentra en preparación.";

        case "LISTO_RETIRO":
            return "El pedido está preparado y disponible para retiro.";

        case "ENVIADO":
            return "El pedido fue despachado y está pendiente de confirmación de entrega.";

        case "FINALIZADO":
            if (datos.modalidadEntrega === "RETIRO") {
                return "El pedido fue retirado correctamente y el proceso se encuentra finalizado.";
            }

            return "El pedido fue entregado y el proceso se encuentra finalizado.";

        case "CANCELADO":
            return "El pedido fue cancelado.";

        default:
            return "Consulte las acciones disponibles para continuar con el pedido.";
    }
}

function formatearEstadoComprobante(estado) {
    const estados = {
        PENDIENTE: "Pendiente de revisión",
        APROBADO: "Aprobado",
        RECHAZADO: "Rechazado"
    };

    return estados[estado] || estado || "";
}

function formatearEstado(estado) {
    const estados = {
        PENDIENTE_PAGO: "Pendiente de pago",
        PAGO_EN_REVISION: "Pago en revisión",
        PAGO_RECHAZADO: "Pago rechazado",
        PAGO_APROBADO: "Pago aprobado",
        EN_PREPARACION: "En preparación",
        LISTO_RETIRO: "Listo para retiro",
        ENVIADO: "Enviado",
        FINALIZADO: "Finalizado",
        CANCELADO: "Cancelado"
    };

    return estados[estado] || estado;
}

function formatearModalidad(modalidad) {
    if (modalidad === "RETIRO") {
        return "Retiro en local";
    }

    if (modalidad === "DESPACHO") {
        return "Despacho";
    }

    return modalidad;
}

function formatearFecha(fecha) {
    if (!fecha) {
        return "";
    }

    const valor = new Date(fecha);

    if (Number.isNaN(valor.getTime())) {
        return fecha;
    }

    return valor.toLocaleString("es-CL");
}

function obtenerFechaHoraLocalActual() {
    const ahora = new Date();

    const anio =
        ahora.getFullYear();

    const mes =
        String(ahora.getMonth() + 1)
            .padStart(2, "0");

    const dia =
        String(ahora.getDate())
            .padStart(2, "0");

    const hora =
        String(ahora.getHours())
            .padStart(2, "0");

    const minuto =
        String(ahora.getMinutes())
            .padStart(2, "0");

    return `${anio}-${mes}-${dia}T${hora}:${minuto}`;
}

function escaparHtml(valor) {
    const elemento =
        document.createElement("div");

    elemento.textContent =
        valor == null ? "" : String(valor);

    return elemento.innerHTML;
}

function esperar(milisegundos) {
    return new Promise(
        resolve =>
            setTimeout(
                resolve,
                milisegundos
            )
    );
}

function mostrarAdministracion() {
    document.getElementById("seccionLogin")
        .style.display = "none";

    document.getElementById("seccionAdministracion")
        .style.display = "block";

    cargarListadoPedidos();
}

function mostrarLogin() {
    document.getElementById("seccionLogin")
        .style.display = "block";

    document.getElementById("seccionAdministracion")
        .style.display = "none";
}

function mostrarMensaje(
        contenedor,
        mensaje,
        exito) {

    contenedor.className =
        exito
            ? "mensaje-exito"
            : "mensaje-error";

    contenedor.textContent = mensaje;
}

/* Listado y filtro de pedidos del panel administrativo */

let pedidosAdministrativos = [];

document.addEventListener("DOMContentLoaded", () => {
    const filtro = document.getElementById("filtroEstadoPedidos");
    const actualizar = document.getElementById("btnActualizarPedidos");
    const mostrarPedidos = document.getElementById("btnMostrarPedidos");

    filtro.addEventListener("change", mostrarListadoPedidos);
    actualizar.addEventListener("click", cargarListadoPedidos);

    mostrarPedidos.addEventListener("click", () => {
        marcarSeccionAdministrativa("btnMostrarPedidos");
        cargarListadoPedidos();
    });

    document.getElementById("btnMostrarInventario")
        .addEventListener("click", () => {
            marcarSeccionAdministrativa("btnMostrarInventario");
        });

    document.getElementById("btnMostrarClientes")
        .addEventListener("click", () => {
            marcarSeccionAdministrativa("btnMostrarClientes");
        });
});

function marcarSeccionAdministrativa(idBotonActivo) {
    [
        "btnMostrarPedidos",
        "btnMostrarInventario",
        "btnMostrarClientes"
    ].forEach(id => {
        const boton = document.getElementById(id);
        const activo = id === idBotonActivo;

        boton.classList.toggle("admin-nav-active", activo);

        if (activo) {
            boton.setAttribute("aria-current", "page");
        } else {
            boton.removeAttribute("aria-current");
        }
    });
}

async function cargarListadoPedidos() {
    const resultado = document.getElementById("resultadoListadoPedidos");
    const listado = document.getElementById("listadoPedidos");

    resultado.textContent = "Cargando pedidos...";
    listado.replaceChildren();

    try {
        const respuesta = await fetch("/api/pedidos", {
            credentials: "same-origin"
        });

        if (respuesta.status === 401 || respuesta.status === 403) {
            throw new Error(
                "La sesión administrativa no está activa. Inicie sesión nuevamente."
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                "No fue posible obtener el listado de pedidos."
            );
        }

        const datos = await respuesta.json();

        if (!Array.isArray(datos)) {
            throw new Error(
                "El servidor no devolvió un listado válido de pedidos."
            );
        }

        pedidosAdministrativos = datos;
        mostrarListadoPedidos();

    } catch (error) {
        pedidosAdministrativos = [];
        listado.replaceChildren();
        resultado.textContent =
            error.message || "Ocurrió un error al consultar los pedidos.";
    }
}

function mostrarListadoPedidos() {
    const resultado = document.getElementById("resultadoListadoPedidos");
    const listado = document.getElementById("listadoPedidos");
    const estadoSeleccionado =
        document.getElementById("filtroEstadoPedidos").value;

    listado.replaceChildren();

    const pedidosFiltrados = pedidosAdministrativos.filter(pedido =>
        !estadoSeleccionado || pedido.estado === estadoSeleccionado
    );

    resultado.textContent =
        `Pedidos encontrados: ${pedidosFiltrados.length}`;

    if (pedidosFiltrados.length === 0) {
        listado.textContent = "No hay pedidos para el estado seleccionado.";
        return;
    }

    const tabla = document.createElement("table");
    const encabezado = document.createElement("thead");
    const filaEncabezado = document.createElement("tr");

    ["Pedido", "Estado", "Modalidad", "Acción"].forEach(titulo => {
        const celda = document.createElement("th");
        celda.textContent = titulo;
        filaEncabezado.appendChild(celda);
    });

    encabezado.appendChild(filaEncabezado);
    tabla.appendChild(encabezado);

    const cuerpo = document.createElement("tbody");

    pedidosFiltrados
        .slice()
        .sort((a, b) => b.idPedido - a.idPedido)
        .forEach(pedido => {
            const fila = document.createElement("tr");

            [
                `N.º ${pedido.idPedido}`,
                formatearEstado(pedido.estado),
                formatearModalidad(pedido.modalidadEntrega)
            ].forEach(valor => {
                const celda = document.createElement("td");
                celda.textContent = valor ?? "";
                fila.appendChild(celda);
            });

            const celdaAccion = document.createElement("td");
            const boton = document.createElement("button");

            boton.type = "button";
            boton.textContent = "Ver detalle";

            boton.addEventListener("click", async () => {
                document.getElementById("adminPedido").value =
                    pedido.idPedido;

                await cargarPedido(pedido.idPedido);

                document.getElementById("accionesPedido")
                    .scrollIntoView({
                        behavior: "smooth",
                        block: "start"
                    });
            });

            celdaAccion.appendChild(boton);
            fila.appendChild(celdaAccion);
            cuerpo.appendChild(fila);
        });

    tabla.appendChild(cuerpo);
    listado.appendChild(tabla);
}


