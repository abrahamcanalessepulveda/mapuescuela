
"use strict";

const API_INVENTARIO = "/api/admin/inventario";

document.addEventListener("DOMContentLoaded", () => {
    const btnMostrarPedidos = document.getElementById("btnMostrarPedidos");
    const btnMostrarInventario = document.getElementById("btnMostrarInventario");
    const btnActualizarInventario = document.getElementById("btnActualizarInventario");
    const btnNuevoProducto = document.getElementById("btnNuevoProducto");
    const btnCancelarProducto = document.getElementById("btnCancelarProducto");
    const formProducto = document.getElementById("formProducto");

    btnMostrarPedidos.addEventListener("click", () => {
        document.getElementById("panelPedidos").style.display = "block";
        document.getElementById("panelInventario").style.display = "none";
    });

    btnMostrarInventario.addEventListener("click", async () => {
        document.getElementById("panelPedidos").style.display = "none";
        document.getElementById("panelInventario").style.display = "block";

        await cargarInventario();
    });

    btnActualizarInventario.addEventListener("click", cargarInventario);

    btnNuevoProducto.addEventListener("click", () => {
        prepararFormularioNuevoProducto();
    });

    btnCancelarProducto.addEventListener("click", () => {
        cerrarFormularioProducto();
    });

    formProducto.addEventListener("submit", guardarProducto);
});

async function cargarInventario() {
    const resultado = document.getElementById("resultadoInventario");
    const listado = document.getElementById("listadoInventario");

    resultado.textContent = "Cargando inventario...";
    listado.replaceChildren();

    try {
        const respuesta = await fetch(API_INVENTARIO, {
            credentials: "same-origin"
        });

        if (respuesta.status === 401) {
            resultado.textContent =
                "La sesión administrativa no está activa. Inicie sesión nuevamente.";
            return;
        }

        if (!respuesta.ok) {
            throw new Error("No fue posible consultar el inventario.");
        }

        const productos = await respuesta.json();

        if (!Array.isArray(productos)) {
            throw new Error("El servidor no devolvió un listado válido.");
        }

        resultado.textContent =
            `Productos registrados: ${productos.length}`;

        if (productos.length === 0) {
            listado.textContent = "No hay productos registrados.";
            return;
        }

        const tabla = document.createElement("table");
        const encabezado = document.createElement("thead");
        const filaEncabezado = document.createElement("tr");

        [
            "ID",
            "Producto",
            "Categoría",
            "Precio",
            "Stock",
            "Estado",
            "Acción"
        ].forEach(titulo => {
            const celda = document.createElement("th");
            celda.textContent = titulo;
            filaEncabezado.appendChild(celda);
        });

        encabezado.appendChild(filaEncabezado);
        tabla.appendChild(encabezado);

        const cuerpo = document.createElement("tbody");

        productos.forEach(producto => {
            const fila = document.createElement("tr");

            const valores = [
                producto.idProducto,
                producto.nombre,
                producto.categoria || "Sin categoría",
                formatearPrecio(producto.precio),
                producto.stock,
                producto.estado
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
                prepararFormularioEditarProducto(producto);
            });

            celdaAccion.appendChild(botonEditar);
            fila.appendChild(celdaAccion);
            cuerpo.appendChild(fila);
        });

        tabla.appendChild(cuerpo);
        listado.appendChild(tabla);

    } catch (error) {
        resultado.textContent =
            error.message || "Ocurrió un error al cargar el inventario.";
    }
}

function prepararFormularioNuevoProducto() {
    const formulario = document.getElementById("formProducto");

    formulario.reset();

    document.getElementById("inventarioIdProducto").value = "";
    document.getElementById("tituloFormularioProducto").textContent =
        "Agregar producto";

    document.getElementById("inventarioEstado").value = "DISPONIBLE";

    document.getElementById("panelFormularioProducto").style.display =
        "block";

    document.getElementById("inventarioNombre").focus();
}

function prepararFormularioEditarProducto(producto) {
    document.getElementById("inventarioIdProducto").value =
        producto.idProducto;

    document.getElementById("inventarioNombre").value =
        producto.nombre || "";

    document.getElementById("inventarioDescripcion").value =
        producto.descripcion || "";

    document.getElementById("inventarioCategoria").value =
        producto.categoria || "";

    document.getElementById("inventarioPrecio").value =
        producto.precio ?? "";

    document.getElementById("inventarioStock").value =
        producto.stock ?? "";

    document.getElementById("inventarioEstado").value =
        producto.estado || "DISPONIBLE";

    document.getElementById("inventarioImagen").value =
        producto.imagen || "";

    document.getElementById("tituloFormularioProducto").textContent =
        `Editar producto N.º ${producto.idProducto}`;

    document.getElementById("panelFormularioProducto").style.display =
        "block";

    document.getElementById("panelFormularioProducto").scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

function cerrarFormularioProducto() {
    document.getElementById("formProducto").reset();

    document.getElementById("inventarioIdProducto").value = "";

    document.getElementById("panelFormularioProducto").style.display =
        "none";
}

async function guardarProducto(evento) {
    evento.preventDefault();

    const resultado = document.getElementById("resultadoInventario");
    const formulario = document.getElementById("formProducto");
    const botonGuardar = formulario.querySelector('button[type="submit"]');

    const idProducto =
        document.getElementById("inventarioIdProducto").value;

    const producto = {
        nombre: document.getElementById("inventarioNombre").value.trim(),
        descripcion: document.getElementById("inventarioDescripcion").value.trim(),
        categoria: document.getElementById("inventarioCategoria").value.trim(),
        precio: Number(document.getElementById("inventarioPrecio").value),
        stock: Number(document.getElementById("inventarioStock").value),
        estado: document.getElementById("inventarioEstado").value,
        imagen: document.getElementById("inventarioImagen").value.trim()
    };

    if (!producto.nombre) {
        resultado.textContent = "Ingrese el nombre del producto.";
        return;
    }

    if (!Number.isFinite(producto.precio) || producto.precio < 0) {
        resultado.textContent = "Ingrese un precio válido.";
        return;
    }

    if (!Number.isInteger(producto.stock) || producto.stock < 0) {
        resultado.textContent = "Ingrese una cantidad de stock válida.";
        return;
    }

    const esEdicion = idProducto !== "";

    const url = esEdicion
        ? `${API_INVENTARIO}/${encodeURIComponent(idProducto)}`
        : API_INVENTARIO;

    botonGuardar.disabled = true;

    resultado.textContent = esEdicion
        ? "Actualizando producto..."
        : "Registrando producto...";

    try {
        const respuesta = await fetchSeguro(url, {
            method: esEdicion ? "PUT" : "POST",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(producto)
        });

        const datos = await respuesta.json().catch(() => ({}));

        if (!respuesta.ok) {
            if (respuesta.status === 401) {
                throw new Error(
                    "La sesión administrativa no está activa. Inicie sesión nuevamente."
                );
            }

            throw new Error(
                datos.mensaje || "No fue posible guardar el producto."
            );
        }

        cerrarFormularioProducto();

        await cargarInventario();

        resultado.textContent = esEdicion
            ? `Producto N.º ${datos.idProducto} actualizado correctamente.`
            : `Producto N.º ${datos.idProducto} registrado correctamente.`;

    } catch (error) {
        resultado.textContent =
            error.message || "Ocurrió un error al guardar el producto.";
    } finally {
        botonGuardar.disabled = false;
    }
}

function formatearPrecio(precio) {
    const numero = Number(precio);

    if (!Number.isFinite(numero)) {
        return "Precio no disponible";
    }

    return new Intl.NumberFormat("es-CL", {
        style: "currency",
        currency: "CLP",
        maximumFractionDigits: 0
    }).format(numero);
}