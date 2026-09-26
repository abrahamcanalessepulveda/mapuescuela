let tokenCsrf = null;
let nombreCabeceraCsrf = null;


/* =========================================================
   OBTENER TOKEN CSRF
   ========================================================= */

async function obtenerTokenCsrf() {

    const respuesta = await fetch(
        "/api/csrf",
        {
            credentials: "same-origin"
        }
    );


    if (!respuesta.ok) {

        throw new Error(
            "No fue posible obtener el token de seguridad."
        );
    }


    const datos =
        await respuesta.json();


    tokenCsrf =
        datos.token;


    nombreCabeceraCsrf =
        datos.headerName;


    if (
        !tokenCsrf
        || !nombreCabeceraCsrf
    ) {

        throw new Error(
            "El servidor no entregó un token de seguridad válido."
        );
    }
}


/* =========================================================
   CABECERAS CON TOKEN CSRF
   ========================================================= */

async function obtenerHeadersCsrf() {

    if (
        !tokenCsrf
        || !nombreCabeceraCsrf
    ) {

        await obtenerTokenCsrf();
    }


    const headers =
        new Headers();


    headers.set(
        nombreCabeceraCsrf,
        tokenCsrf
    );


    return headers;
}


/* =========================================================
   CABECERAS JSON + TOKEN CSRF
   ========================================================= */

async function obtenerHeadersJson() {

    const headers =
        await obtenerHeadersCsrf();


    headers.set(
        "Content-Type",
        "application/json"
    );


    return headers;
}


/* =========================================================
   FETCH SEGURO
   ========================================================= */

async function fetchSeguro(
    url,
    opciones = {}
) {

    if (
        !tokenCsrf
        || !nombreCabeceraCsrf
    ) {

        await obtenerTokenCsrf();
    }


    const cabeceras =
        new Headers(
            opciones.headers || {}
        );


    cabeceras.set(
        nombreCabeceraCsrf,
        tokenCsrf
    );


    return fetch(
        url,
        {
            ...opciones,

            headers:
                cabeceras,

            credentials:
                "same-origin"
        }
    );
}