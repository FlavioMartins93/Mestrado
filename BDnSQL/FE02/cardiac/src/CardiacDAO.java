import java.sql.*;

public class CardiacDAO {
    private static Connection connection;

    public CardiacDAO() {
        try {
            this.connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/orclpdb1.localdomain", "cardiac_sensor", "cardiac_sensor");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void close() throws SQLException {
        this.connection.close();
    }

    /* Checks if sensor already exists in the database, insert if not */
    public void addSensor(long id, long num, String type) throws SQLException {
        Statement existsStatement = connection.createStatement();
        ResultSet rs = existsStatement.executeQuery("SELECT * FROM cardiac_sensor.sensor WHERE id = " + id);
        if(!rs.next()) {
            Statement insStatement = connection.createStatement();
            insStatement.executeUpdate("INSERT INTO cardiac_sensor.sensor VALUES (" + id + ", " + num + ", '" + type + "')");
        }
    }

    /* Checks if patient already exists in the database, insert if not */
    public void addPatient(long id, String name, String birthdate) throws SQLException {
        Statement existsStatement = connection.createStatement();
        ResultSet rs = existsStatement.executeQuery("SELECT * FROM cardiac_sensor.patient WHERE id = " + id);
        if(!rs.next()) {
            Statement insStatement = connection.createStatement();
            insStatement.executeUpdate("INSERT INTO cardiac_sensor.patient VALUES (" + id + ", '" + name + "', to_date('" + birthdate + "', 'yyyy-mm-dd'))");
        }
    }

    /* Checks if service already exists in the database, insert if not */
    public void addService(String codigo, String descricao) throws SQLException {
        Statement existsStatement = connection.createStatement();
        ResultSet rs = existsStatement.executeQuery("SELECT * FROM cardiac_sensor.service WHERE codigo = '" + codigo + "'");
        if(!rs.next()) {
            Statement insStatement = connection.createStatement();
            insStatement.executeUpdate("INSERT INTO cardiac_sensor.service VALUES ('" + codigo + "', '" + descricao + "')");
        }
    }

    /* Gets the internment Id */
    public long getInternmentId(long patientId, String serviceCod, String admdate, String bed) throws SQLException {
        Statement existsStatement = connection.createStatement();
        ResultSet rs = existsStatement.executeQuery("SELECT id FROM cardiac_sensor.internment WHERE patient = " + patientId
                + " AND service = '" + serviceCod
                + "' AND admdate = to_date('" + admdate + "', 'yyyy-mm-dd')"
                + " AND bed = " + bed);
        rs.next();
        return rs.getLong("id");
    }

    /* Checks if patient already exists in the database, insert if not */
    public void addInternment(long patientId, String serviceCod, String admdate, String bed) throws SQLException {
        Statement existsStatement = connection.createStatement();
        ResultSet rs = existsStatement.executeQuery("SELECT * FROM cardiac_sensor.internment WHERE patient = " + patientId
                                                                                                    + " AND service = '" + serviceCod
                                                                                                    + "' AND admdate = to_date('"
                                                                                                    + admdate + "', 'yyyy-mm-dd')"
                                                                                                    + " AND bed = " + bed);
        if(!rs.next()) {
            Statement higherId = connection.createStatement();
            ResultSet hrs = higherId.executeQuery(  "SELECT id FROM cardiac_sensor.internment " +
                                                        "ORDER BY id DESC " +
                                                        "FETCH FIRST 1 ROWS ONLY");
            long id;
            if(!hrs.next()) {
                id = 1;
            } else {
                id = hrs.getLong("id") + 1;
            }
            Statement insStatement = connection.createStatement();
            insStatement.executeUpdate("INSERT INTO cardiac_sensor.internment VALUES (" + id + ", " + patientId + ", '" + serviceCod
                                                                                            + "', to_date('" + admdate + "', 'yyyy-mm-dd'), "
                                                                                            + bed + ")");
        }
    }

    /* Checks if doctor already exists in the database, insert if not */
    public void addDoctor(long id, String name) throws SQLException {
        Statement existsStatement = connection.createStatement();
        ResultSet rs = existsStatement.executeQuery("SELECT * FROM cardiac_sensor.doctor WHERE id = " + id);
        if(!rs.next()) {
            Statement insStatement = connection.createStatement();
            insStatement.executeUpdate("INSERT INTO cardiac_sensor.doctor VALUES (" + id + ", '" + name + "')");
        }
    }

    /* Checks if relation patient-doctor already exists in the database, insert if not */
    public void addInternmentDoctor(long internment, long doctorid, String nome) throws SQLException {
        addDoctor(doctorid, nome);
        Statement existsStatement = connection.createStatement();
        ResultSet rs = existsStatement.executeQuery("SELECT * FROM cardiac_sensor.internment_doctor " +
                                                        " WHERE internment = " + internment + " AND doctor = " + doctorid);
        if(!rs.next()) {
            Statement insStatement = connection.createStatement();
            insStatement.executeUpdate("INSERT INTO cardiac_sensor.internment_doctor VALUES (" + internment + ", " + doctorid + ")");
        }
    }

    public void addRead(long internmentId, long sensorId, long bodytemp, long systolic, long diastolic, long bpm, long sato2, String timestamp) throws SQLException {
        Statement insStatement = connection.createStatement();
        insStatement.executeUpdate("INSERT INTO cardiac_sensor.sensor_reads VALUES ("   + internmentId + ", " + sensorId + ", "
                                                                                            + bodytemp + ", " + systolic + ", "
                                                                                            + diastolic + ", " + bpm + ", "
                                                                                            + sato2 + ", '" + timestamp + "')");
    }
}
