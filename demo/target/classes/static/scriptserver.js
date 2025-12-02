//Esto es el JavaScript para ejecutar la funcion enviar()
async function enviar() {
    //Se usa el try para capturar el bloque de codigo que 
    //podria contener errores, en este caso lo que se le envia al servidor
    const nombre = document.getElementById("nombre").value;
    //Lo de arriba crea la variable nombre y ahí guarda el valor que se escribio en el box 
    //que tiene el id "nombre"
    const apellidoPaterno = document.getElementById("apePater").value;
    const apellidoMaterno = document.getElementById("apeMater").value; 
    const curp = document.getElementById("curp").value;
    const direccion = document.getElementById("direccion").value;
    const cruzamientos = document.getElementById("cruzamientos").value; 
    const codigopostal = document.getElementById("cp").value;
    const correo = document.getElementById("correo").value;
    const telefono = document.getElementById("telefono").value;

    const response = await fetch("/saludo", {
    //fetch es la funcion que hace la peticion aal backend (JAVA) 
    //await hace que el codigo espere hasta que llegue la respuesta del servidor 
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },

    body: "nombre=" + encodeURIComponent(nombre) +
            "&apellidoPaterno=" + encodeURIComponent(apellidoPaterno) +
            "&apellidoMaterno=" + encodeURIComponent(apellidoMaterno) +
            "&curp=" + encodeURIComponent(curp) + 
            "&direccion=" + encodeURIComponent(direccion) +
            "&cruzamientos=" + encodeURIComponent (cruzamientos) + 
            "&codigopostal=" + encodeURIComponent(codigopostal) +
            "&correo=" + encodeURIComponent(correo) +
            "&telefono=" + encodeURIComponent(telefono)});//Manda todo lo de arriba al JAVA mediante el metodo enviar()
    const texto = await response.text();
    document.getElementById("respuesta").textContent = texto;
    //Recibe el texto de usuario registrado correctamente
    alert("Usuario registrado con éxito");
//if (texto === "CORRECTO"){}
}