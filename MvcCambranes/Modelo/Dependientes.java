package Modelo;
public class Dependientes extends Ciudadanos {
    private String curpParentesco;
    private String genero;
    private int edad;

    public Dependientes(String nombre, String apellidoPaterno, String apellidoMaterno, String curp, long tel, String mail, String direccion, String curpParentesco, String genero, int edad) {
        super(nombre, apellidoPaterno, apellidoMaterno, curp, tel, mail, direccion);
        this.curpParentesco = curpParentesco;
        this.genero = genero;
        this.edad = edad;
    }

    public String getCurpParentesco() {
        return curpParentesco;
    }

    public void setCurpParentesco(String curpParentesco) {
        this.curpParentesco = curpParentesco;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return super.toString() + 
                "Curp parentesco:'" + curpParentesco + '\'' +
                " genero:" + genero + '\'' +
                " edad:" + edad;
    }
    public boolean validarEdad(int edad) {
        return edad > 12 && edad < 17;
    }
}
