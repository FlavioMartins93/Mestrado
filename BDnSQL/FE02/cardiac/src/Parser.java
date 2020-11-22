import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.SQLException;

public class Parser {

    public static void process(int apiIndice) throws SQLException {
        JSONObject jobj = APIGetter.getJson(apiIndice);

        if (jobj != null) {
            CardiacDAO cardiacDAO = new CardiacDAO();

            cardiacDAO.addSensor((long) jobj.get("sensorid"), (long) jobj.get("sensornum"), jobj.get("type_of_sensor").toString());

            JSONObject patient = (JSONObject) jobj.get("patient");
            long patientId = (long) patient.get("patientid");
            cardiacDAO.addPatient(patientId, patient.get("patientname").toString(), patient.get("patientbirthdate").toString());

            cardiacDAO.addService(jobj.get("servicecod").toString(), jobj.get("servicedesc").toString());

            cardiacDAO.addInternment(patientId, jobj.get("servicecod").toString(), jobj.get("admdate").toString(), jobj.get("bed").toString());
            long internmentid = cardiacDAO.getInternmentId(patientId, jobj.get("servicecod").toString(), jobj.get("admdate").toString(), jobj.get("bed").toString());

            if (internmentid != 0) {
                JSONArray careTeam = (JSONArray) jobj.get("careteam");
                careTeam.forEach(doc -> {
                    JSONObject doctor = (JSONObject) doc;
                    try {
                        cardiacDAO.addInternmentDoctor(internmentid, (long) doctor.get("id"), doctor.get("nome").toString());
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                });
            }

            JSONObject blood = (JSONObject) jobj.get("bloodpress");
            cardiacDAO.addRead(
                                internmentid,
                                (long) jobj.get("sensorid"),
                                (long) jobj.get("bodytemp"),
                                (long) blood.get("systolic"),
                                (long) blood.get("diastolic"),
                                (long) jobj.get("bpm"),
                                (long) jobj.get("sato2"),
                                jobj.get("timestamp").toString());

            cardiacDAO.close();
            System.out.println("Fetched data for sensor " + jobj.get("sensorid").toString());
        }
    }

    public static void main(String [] args) throws SQLException, InterruptedException {
        while(true) {
            for(int i = 3001; i<3006; i++) {
                process(i);
            }
            /* Sleeping 5 minutes before fetching new reads*/
            Thread.sleep(300000);
        }
    }
}

