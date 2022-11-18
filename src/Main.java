import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {



        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String conexionURL = "jdbc:mysql://dns11036.phdns11.es?"
                    + "user=ad2223_rlindes&password=1234";
            Connection con = DriverManager.getConnection(conexionURL);
            System.out.println(con.toString());


            usarDatabase(con);
            // Se crea la tabla Contactos
            crearTablas(con, "CREATE TABLE IF NOT EXISTS Contactos" + "(id_Contacto INTEGER AUTO_INCREMENT," + "nombre_Contacto VARCHAR(45)," +
                    "esBloqueado TINYINT," + "PRIMARY KEY (id_Contacto))");

            // Se crea la tabla Mensajes
            crearTablas(con,
             "CREATE TABLE IF NOT EXISTS Mensajes"+
                    "(id_Mensaje INTEGER AUTO_INCREMENT,"+
                    "texto VARCHAR(500), id_Destino INTEGER,"+
                    "leido TINYINT, fecha DATE,"+
                    "hora TIME, PRIMARY KEY (id_Mensaje),"+
                    "FOREIGN KEY (id_Destino) REFERENCES Contactos(id_Contacto))"
            );



            enviarMensaje(con);

            Scanner sc = new Scanner(System.in);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void enviarMensaje(Connection con) {

        Statement smtl = null;

        try {
            smtl = con.createStatement();
            smtl.execute("INSERT INTO Mensajes (texto) VALUES ('Mensaje de prueba')");
            smtl.execute("INSERT INTO ad2223_dperea.Mensajes (texto) Values ('asdasda')");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void crearTablas(Connection con, String consulta ) {

        Statement smtl = null;
        try {
            smtl = con.createStatement();

            smtl.executeUpdate(consulta);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public static void usarDatabase(Connection con) {

        Statement smtl = null;
        try {
            smtl = con.createStatement();


            String usarTabla = "USE ad2223_rlindes";

            smtl.executeUpdate(usarTabla);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
