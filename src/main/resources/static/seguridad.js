let tokenCsrf = null;
let nombreCabeceraCsrf = null;

async function obtenerTokenCsrf() {
    const respuesta = await fetch("/api/csrf", {
        credentials: "same-origin"
    });

    if (!respuesta.ok) {
        throw new Error(
            "No fue posible obtener el token de seguridad."
        );
    }

    const datos = await respuesta.json();

    tokenCsrf = datos.token;
    nombreCabeceraCsrf = datos.headerName;

    if (!tokenCsrf || !nombreCabeceraCsrf) {
        throw new Error(
            "El servidor no entregó un token de seguridad válido."
        );
    }
}

async function fetchSeguro(url, opciones = {}) {
    if (!tokenCsrf || !nombreCabeceraCsrf) {
        await obtenerTokenCsrf();
    }

    const cabeceras = new Headers(opciones.headers || {});
    cabeceras.set(nombreCabeceraCsrf, tokenCsrf);

    return fetch(url, {
        ...opciones,
        headers: cabeceras,
        credentials: "same-origin"
    });
}