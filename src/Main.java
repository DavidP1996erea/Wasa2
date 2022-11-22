import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static String usuarioBaseDatos = "ad2223_dperea";
    public static void main(String[] args) {


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String conexionURL = "jdbc:mysql://dns11036.phdns11.es?"
                    + "user="+usuarioBaseDatos+"&password=1234";
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



           anadirContacto(con);
           enviarMensaje(con, "dperea");



           // Scanner sc = new Scanner(System.in);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void anadirContacto(Connection con) {
        Scanner sc = new Scanner(System.in);
        String nombre;
        System.out.println("¿Cual es el nombre del usuario?");
        nombre = sc.nextLine();

        try {

            if(!comprobarNombreContacto(con,nombre)) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO Contactos (nombre_Contacto, esBloqueado) VALUES (?,?)");

                ps.setString(1, nombre);
                ps.setInt(2, 0);
                ps.executeUpdate();
            }else {
                System.out.println("El usuario " + nombre + " ya existe");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static boolean comprobarNombreContacto(Connection con, String nombreABuscar){

        boolean existeUsuario=false;
        String nombreTabla="";
        try {

            PreparedStatement ps = con.prepareStatement("SELECT nombre_Contacto FROM Contactos WHERE nombre_Contacto=?");
            ps.setString(1,nombreABuscar);

            ResultSet guardarNombre = ps.executeQuery();

            while (guardarNombre.next()) {
                nombreTabla = guardarNombre.getString(1);
            }

            if(!nombreTabla.equals("")){
                existeUsuario=true;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return existeUsuario;
    }

    private static int devolverIdPorNombre(Connection con, String nombreContacto){

        int idContacto=0;

        try{

            PreparedStatement ps = con.prepareStatement("SELECT id_Contacto FROM Contactos WHERE nombre_Contacto=?");
            ps.setString(1,nombreContacto);

            ResultSet guardarNombre = ps.executeQuery();

            while (guardarNombre.next()) {
                idContacto = Integer.parseInt(guardarNombre.getString(1)) ;
            }

        }catch (SQLException e){
            throw new RuntimeException();
        }

    return idContacto;
    }

    private static void enviarMensaje(Connection con, String nombreContacto) {
        Scanner sc = new Scanner(System.in);
        String texto;
        try {

            java.util.Date date = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            java.sql.Timestamp sqlTime = new java.sql.Timestamp(date.getTime());

            mostrarMensajesUsuarios(con, nombreContacto, usuarioBaseDatos, devolverIdPorNombre(con, nombreContacto));

            System.out.println("Que mensaje quieres enviarle");
            texto = sc.nextLine();
            PreparedStatement ps = con.prepareStatement("INSERT INTO Mensajes (texto, hora, fecha, leido, id_Destino) VALUES (?,?,?,?,?)");
            PreparedStatement ps2;


            ps.setString(1, texto);
            ps.setTimestamp(2, sqlTime);
            ps.setDate(3, sqlDate);
            ps.setInt(4, 0);
            ps.setInt(5, devolverIdPorNombre(con, nombreContacto));


            ps.executeUpdate();
            ps2 = con.prepareStatement("INSERT INTO ad2223_"+nombreContacto+".Mensajes (texto, hora, fecha, leido, nombre_Origen) VALUES (?,?,?,?,?)");

            ps2.setString(1, texto);
            ps2.setTimestamp(2, sqlTime);
            ps2.setDate(3, sqlDate);
            ps2.setInt(4, 0);
            ps2.setString(5, usuarioBaseDatos);

            //ps2 = con.prepareStatement("INSERT INTO ad2223_"+nombreContacto+".Mensajes (texto, hora, fecha, leido ) SELECT texto, hora, fecha, leido FROM "+usuarioBaseDatos+".Mensajes order by id_Mensaje desc limit 1");

            ps2.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    private static void mostrarMensajesUsuarios(Connection con, String nombreContacto, String nombreOrigen, int idDestino){

      /*  List<String> listaMensajesOrigen = new ArrayList<>();
        List<String> listaMensajeDestino = new ArrayList<>();*/
        try{

            PreparedStatement mensajesOrigen  = con.prepareStatement("SELECT texto, id_Destino, nombre_Origen FROM Mensajes WHERE id_Destino = ?  OR nombre_Origen =?   ORDER BY fecha ASC, hora ASC ");
            mensajesOrigen.setInt(1, idDestino);
            mensajesOrigen.setString(2, "ad2223_"+nombreContacto);


            ResultSet resultSetMensajesOrigen = mensajesOrigen.executeQuery();
            while (resultSetMensajesOrigen.next()){
               /* listaMensajesOrigen.add( resultSetMensajesOrigen.getString(1));*/

                if(resultSetMensajesOrigen.getInt(2)==idDestino) {
                    System.out.println(nombreOrigen + ": " + resultSetMensajesOrigen.getString(1));
                }

                if(resultSetMensajesOrigen.getString(3) != null){
                    if(resultSetMensajesOrigen.getString(3).equals("ad2223_"+nombreContacto)){
                        System.out.println(nombreContacto + ": " + resultSetMensajesOrigen.getString(1));
                    }
                }


            }
/*
            PreparedStatement mensajesDestino  = con.prepareStatement("SELECT texto FROM Mensajes where id_Destino = "+idDestino+" ORDER BY fecha,hora ASC ");
            ResultSet resultSetMensajesDestino = mensajesDestino.executeQuery();
            while (resultSetMensajesDestino.next()){
                listaMensajeDestino.add(resultSetMensajesDestino.getString(1));

            }
*/
/*
            for(int i =0; i<listaMensajeDestino.size()+listaMensajesOrigen.size();i++){

                if (i < listaMensajesOrigen.size()){
                    System.out.println( nombreContacto+ ": "+listaMensajesOrigen.get(i));
                }
                if (i < listaMensajeDestino.size()){
                    System.out.println( usuarioBaseDatos + ": "+  listaMensajeDestino.get(i));
                }


            }
*/

        }catch (SQLException e){
            throw new RuntimeException(e);
        }


    }

    private static void crearTablas(Connection con, String consulta) {

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


            String usarTabla = "USE "+usuarioBaseDatos+"";

            smtl.executeUpdate(usarTabla);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
