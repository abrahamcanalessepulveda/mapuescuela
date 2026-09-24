
"use strict";

const API_CLIENTES_ADMIN = "/api/admin/clientes";

document.addEventListener("DOMContentLoaded", () => {
    const btnMostrarClientes = document.getElementById("btnMostrarClientes");
    const btnMostrarPedidos = document.getElementById("btnMostrarPedidos");
    const btnMostrarInventario = document.getElementById("btnMostrarInventario");
    const btnActualizarClientes = document.getElementById("btnActualizarClientes");
    const btnNuevoCliente = document.getElementById("btnNuevoCliente");
    const btnCancelarCliente = document.getElementById("btnCancelarCliente");
    const formCliente = document.getElementById("formCliente");

    if (btnMostrarClientes) {
        btnMostrarClientes.addEventListener("click", async () => {
            mostrarPanelClientes();
            await cargarClientes();
        });
    }

    if (btnMostrarPedidos) {
        btnMostrarPedidos.addEventListener("click", () => {
            ocultarPanelClientes();
        });
    }

    if (btnMostrarInventario) {
        btnMostrarInventario.addEventListener("click", () => {
            ocultarPanelClientes();
        });
    }

    if (btnActualizarClientes) {
        btnActualizarClientes.addEventListener("click", cargarClientes);
    }

    if (btnNuevoCliente) {
        btnNuevoCliente.addEventListener("click", prepararFormularioNuevoCliente);
    }

    if (btnCancelarCliente) {
        btnCancelarCliente.addEventListener("click", cerrarFormularioCliente);
    }

    if (formCliente) {
        formCliente.addEventListener("submit", guardarCliente);
    }
});

function mostrarPanelClientes() {
    const panelPedidos = document.getElementById("panelPedidos");
    const panelInventario = document.getElementById("panelInventario");
    const panelClientes = document.getElementById("panelClientes");

    if (panelPedidos) {
        panelPedidos.style.display = "none";
    }

    if (panelInventario) {
        panelInventario.style.display = "none";
    }

    if (panelClientes) {
        panelClientes.style.display = "block";
    }
}

function ocultarPanelClientes() {
    const panelClientes = document.getElementById("panelClientes");

    if (panelClientes) {
        panelClientes.style.display = "none";
    }
}

async function cargarClientes() {
    const resultado = document.getElementById("resultadoClientes");
    const listado = document.getElementById("listadoClientes");

    if (!resultado || !listado) {
        return;
    }

    resultado.textContent = "Cargando clientes...";
    listado.replaceChildren();

    try {
        const respuesta = await fetch(API_CLIENTES_ADMIN, {
            credentials: "same-origin"
        });

        if (respuesta.status === 401) {
            throw new Error(
                "La sesión administrativa no está activa. Inicie sesión nuevamente."
            );
        }

        if (!respuesta.ok) {
            throw new Error(
                `No fue posible consultar los clientes. Error ${respuesta.status}.`
            );
        }

        const clientes = await respuesta.json();

        if (!Array.isArray(clientes)) {
            throw new Error(
                "El servidor no devolvió un listado válido de clientes."
            );
        }

        resultado.textContent =
            `Clientes registrados: ${clientes.length}`;

        if (clientes.length === 0) {
            listado.textContent = "No hay clientes registrados.";
            return;
        }

        const tabla = document.createElement("table");
        const encabezado = document.createElement("thead");
        const filaEncabezado = document.createElement("tr");

        [
            "ID",
            "RUT",
            "Razón social",
            "Contacto",
            "Correo electrónico",
            "Teléfono",
            "Dirección",
            "Acción"
        ].forEach(titulo => {
            const celda = document.createElement("th");
            celda.textContent = titulo;
            filaEncabezado.appendChild(celda);
        });

        encabezado.appendChild(filaEncabezado);
        tabla.appendChild(encabezado);

        const cuerpo = document.createElement("tbody");

        clientes.forEach(cliente => {
            const fila = document.createElement("tr");

            const valores = [
                cliente.idCliente,
                cliente.rut,
                cliente.razonSocial,
                cliente.nombreContacto,
                cliente.email,
                cliente.telefono,
                cliente.direccion
            ];

            valores.forEach(valor => {
                const celda = document.createElement("td");
                celda.textContent = valor ?? "";
                fila.appendChild(celda);
            });

            const celdaAccion = document.createElement("td");
            const botonEditar = document.createElement("button");

            botonEditar.type = "button";
            botonEditar.textContent = "Editar";

            botonEditar.addEventListener("click", () => {
                prepararFormularioEditarCliente(cliente);
            });

            celdaAccion.appendChild(botonEditar);
            fila.appendChild(celdaAccion);
            cuerpo.appendChild(fila);
        });

        tabla.appendChild(cuerpo);
        listado.appendChild(tabla);

    } catch (error) {
        resultado.textContent =
            error.message || "Ocurrió un error al cargar los clientes.";
    }
}

function prepararFormularioNuevoCliente() {
    const formulario = document.getElementById("formCliente");

    if (!formulario) {
        return;
    }

    formulario.reset();

    document.getElementById("clienteId").value = "";

    document.getElementById("tituloFormularioCliente").textContent =
        "Agregar cliente";

    document.getElementById("panelFormularioCliente").style.display =
        "block";

    document.getElementById("clienteRut").focus();
}

function prepararFormularioEditarCliente(cliente) {
    document.getElementById("clienteId").value =
        cliente.idCliente;

    document.getElementById("clienteRut").value =
        cliente.rut || "";

    document.getElementById("clienteRazonSocial").value =
        cliente.razonSocial || "";

    document.getElementById("clienteNombreContacto").value =
        cliente.nombreContacto || "";

    document.getElementById("clienteEmail").value =
        cliente.email || "";

    document.getElementById("clienteTelefono").value =
        cliente.telefono || "";

    document.getElementById("clienteDireccion").value =
        cliente.direccion || "";

    document.getElementById("tituloFormularioCliente").textContent =
        `Editar cliente N.º ${cliente.idCliente}`;

    document.getElementById("panelFormularioCliente").style.display =
        "block";

    document.getElementById("panelFormularioCliente").scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

function cerrarFormularioCliente() {
    const formulario = document.getElementById("formCliente");

    if (formulario) {
        formulario.reset();
    }

    document.getElementById("clienteId").value = "";

    document.getElementById("panelFormularioCliente").style.display =
        "none";
}

async function guardarCliente(evento) {
    evento.preventDefault();

    const resultado = document.getElementById("resultadoClientes");
    const formulario = document.getElementById("formCliente");
    const botonGuardar = formulario.querySelector('button[type="submit"]');

    const idCliente = document.getElementById("clienteId").value;

    const cliente = {
        rut: document.getElementById("clienteRut").value.trim(),
        razonSocial: document.getElementById("clienteRazonSocial").value.trim(),
        nombreContacto: document.getElementById("clienteNombreContacto").value.trim(),
        email: document.getElementById("clienteEmail").value.trim(),
        telefono: document.getElementById("clienteTelefono").value.trim(),
        direccion: document.getElementById("clienteDireccion").value.trim()
    };

    if (!cliente.rut) {
        resultado.textContent = "Ingrese el RUT del cliente.";
        return;
    }

    if (!cliente.razonSocial) {
        resultado.textContent = "Ingrese la razón social del cliente.";
        return;
    }

    if (!cliente.email) {
        resultado.textContent = "Ingrese el correo electrónico del cliente.";
        return;
    }

    const esEdicion = idCliente !== "";

    const url = esEdicion
        ? `${API_CLIENTES_ADMIN}/${encodeURIComponent(idCliente)}`
        : API_CLIENTES_ADMIN;

    botonGuardar.disabled = true;

    resultado.textContent = esEdicion
        ? "Actualizando cliente..."
        : "Registrando cliente...";

    try {
        const respuesta = await fetchSeguro(url, {
            method: esEdicion ? "PUT" : "POST",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(cliente)
        });

        const datos = await respuesta.json().catch(() => ({}));

        if (!respuesta.ok) {
            if (respuesta.status === 401) {
                throw new Error(
                    "La sesión administrativa no está activa. Inicie sesión nuevamente."
                );
            }

            throw new Error(
                datos.mensaje ||
                `No fue posible guardar el cliente. Error ${respuesta.status}.`
            );
        }

        cerrarFormularioCliente();

        await cargarClientes();

        resultado.textContent = esEdicion
            ? `Cliente N.º ${datos.idCliente} actualizado correctamente.`
            : `Cliente N.º ${datos.idCliente} registrado correctamente.`;

    } catch (error) {
        resultado.textContent =
            error.message || "Ocurrió un error al guardar el cliente.";
    } finally {
        botonGuardar.disabled = false;
    }
}