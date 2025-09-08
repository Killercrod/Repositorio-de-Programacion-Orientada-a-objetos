package Modelo;
//se crea la clase ciudadanos donde contiene los datos
//es publica para que pueda ser usada en otras clases
//las variables son privadas para que no puedan ser modificadas en otras clases
public  class Ciudadanos {
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String curp;
    private long tel;
    private String mail;
    private String direccion;
//se crea el constructor de la clase ciudadanos
//un constructor es un metodo especial que se llama igual que la clase
//el constructor se usa para inicializar la clase
    public Ciudadanos(String nombre, String apellidoPaterno, String apellidoMaterno, String curp, long tel, String mail, String direccion) {
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.curp = curp;
        this.tel = tel;
        this.mail = mail;
        this.direccion = direccion;
    }
    //se crean los metodos get y set para la clase ciudadanos
    //get significa obtener y set significa colorcar
    //get se usa para obtener el valor de un atributo
    //set se usa para colocar un valor a un atributo
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellidoPaterno() {
        return apellidoPaterno;
    }
    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }
    public String getApellidoMaterno() {
        return apellidoMaterno;
    }
    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }
    public String getCurp() {
        return curp;
    }
    public void setCurp(String curp) {
        this.curp = curp;
    }
    public long  getTel() {
        return tel;
    }
    public void setTel(long  tel) {
        this.tel = tel;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    //se crea el metodo toString para la clase ciudadanos
    //toString se usa para imprimir los atributos de la clase
     public String toString() {
        return "ciudadanos nombre: \n" + nombre + "\n apellidoPaterno: " + apellidoPaterno + "\n apellidoMaterno: "
                + apellidoMaterno + "\n curp: " + curp + "\n tel: " + tel + "\n mail: " + mail + "\n direccion: " + direccion;
    }

    public boolean validarEdad(int edad) {
        return edad > 18;
    }
}
