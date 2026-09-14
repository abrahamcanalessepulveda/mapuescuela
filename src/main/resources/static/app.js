const API_BASE = "http://localhost:8080/api";

document.addEventListener("DOMContentLoaded", () => {
    cargarProductos();
    cargarClientes();
    cargarPedidos();

    document
        .getElementById("formPedido")
        .addEventListener("submit", crearPedido);

    document
        .getElementById("formComprobante")
        .addEventListener("submit", subirComprobante);

    document
        .getElementById("btnActualizarPedidos")
        .addEventListener("click", cargarPedidos);
});

async function cargarProductos() {
    const contenedor = document.getElementById("productos");
    const selectorProducto = document.getElementById("producto");

    try {
        const respuesta = await fetch(`${API_BASE}/productos`);

        if (!respuesta.ok) {
            throw new Error("No fue posible obtener los productos");
        }

        const productos = await respuesta.json();

        contenedor.innerHTML = "";
        selectorProducto.innerHTML =
            '<option value="">Seleccione un producto</option>';

        if (!Array.isArray(productos) || productos.length === 0) {
            contenedor.innerHTML = "<p>No hay productos registrados.</p>";
            return;
        }

        productos.forEach((producto) => {
            const tarjeta = document.createElement("div");
            tarjeta.className = "producto-card";

            tarjeta.innerHTML = `
                <h3>${producto.nombre}</h3>
                <p><strong>Precio:</strong> $${formatearNumero(producto.precio)}</p>
                <p><strong>Stock:</strong> ${producto.stock}</p>
                <p><strong>Estado:</strong> ${producto.estado}</p>
            `;

            contenedor.appendChild(tarjeta);

            if (producto.stock > 0 && producto.estado === "DISPONIBLE") {
                const opcion = document.createElement("option");
                opcion.value = producto.idProducto;
                opcion.textContent =
                    `${producto.nombre} - Stock: ${producto.stock}`;

                selectorProducto.appendChild(opcion);
            }
        });
    } catch (error) {
        contenedor.innerHTML = `
            <div class="mensaje-error">
                Error al cargar productos: ${error.message}
            </div>
        `;
    }
}

async function cargarClientes() {
    const selectorCliente = document.getElementById("cliente");

    try {
        const respuesta = await fetch(`${API_BASE}/clientes`);

        if (!respuesta.ok) {
            throw new Error("No fue posible obtener los clientes");
        }

        const clientes = await respuesta.json();

        selectorCliente.innerHTML =
            '<option value="">Seleccione un cliente</option>';

        clientes.forEach((cliente) => {
            const opcion = document.createElement("option");
            opcion.value = cliente.idCliente;
            opcion.textContent =
                `${cliente.razonSocial} - ${cliente.rut}`;

            selectorCliente.appendChild(opcion);
        });
    } catch (error) {
        selectorCliente.innerHTML =
            '<option value="">Error al cargar clientes</option>';
    }
}

async function crearPedido(evento) {
    evento.preventDefault();

    const resultado = document.getElementById("resultadoPedido");

    const idCliente = Number(
        document.getElementById("cliente").value
    );

    const idProducto = Number(
        document.getElementById("producto").value
    );

    const cantidad = Number(
        document.getElementById("cantidad").value
    );

    const modalidadEntrega =
        document.getElementById("modalidad").value;

    if (!idCliente || !idProducto || cantidad < 1) {
        mostrarMensaje(
            resultado,
            "Debe completar correctamente los datos del pedido.",
            false
        );
        return;
    }

    const pedido = {
        idCliente: idCliente,
        modalidadEntrega: modalidadEntrega,
        productos: [
            {
                idProducto: idProducto,
                cantidad: cantidad
            }
        ]
    };

    try {
        const respuesta = await fetch(`${API_BASE}/pedidos`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(pedido)
        });

        if (!respuesta.ok) {
            const textoError = await respuesta.text();
            throw new Error(
                textoError || "No fue posible generar el pedido"
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
    const contenedor = document.getElementById("pedidos");

    try {
        const respuesta = await fetch(`${API_BASE}/pedidos`);

        if (!respuesta.ok) {
            throw new Error("No fue posible obtener los pedidos");
        }

        const pedidos = await respuesta.json();

        if (!Array.isArray(pedidos) || pedidos.length === 0) {
            contenedor.innerHTML =
                "<p>No existen pedidos registrados.</p>";
            return;
        }

        let filas = "";

        pedidos.forEach((pedido) => {
            filas += `
                <tr>
                    <td>${pedido.idPedido}</td>
                    <td>${pedido.estado}</td>
                    <td>${pedido.modalidadEntrega}</td>
                    <td>$${formatearNumero(pedido.total)}</td>
                </tr>
            `;
        });

        contenedor.innerHTML = `
            <div class="tabla-contenedor">
                <table>
                    <thead>
                        <tr>
                            <th>Pedido</th>
                            <th>Estado</th>
                            <th>Modalidad</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${filas}
                    </tbody>
                </table>
            </div>
        `;
    } catch (error) {
        contenedor.innerHTML = `
            <div class="mensaje-error">
                Error al cargar pedidos: ${error.message}
            </div>
        `;
    }
}

async function subirComprobante(evento) {
    evento.preventDefault();

    const resultado =
        document.getElementById("resultadoComprobante");

    const idPedido = Number(
        document.getElementById("pedidoComprobante").value
    );

    const campoArchivo =
        document.getElementById("archivoComprobante");

    if (!idPedido || campoArchivo.files.length === 0) {
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
        const respuesta = await fetch(
            `${API_BASE}/comprobantes?idPedido=${idPedido}`,
            {
                method: "POST",
                body: formulario
            }
        );

        if (!respuesta.ok) {
            const textoError = await respuesta.text();
            throw new Error(
                textoError || "No fue posible subir el comprobante"
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