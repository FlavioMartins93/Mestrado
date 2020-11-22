import org.json.simple.JSONObject;

public class Main {

    public static void main(String[] args) {

        while (true) {
            int sensorId = 3001;
            while (sensorId < 4006) {
                JSONObject jobj = APIGetter.getJson(sensorId);

                if (jobj != null) {
                    DAO dao = new DAO(jobj);
                    dao.parseData();
                } else {
                    System.out.println("Couldn't get the sensor data");
                }
                if (sensorId == 3005)
                    sensorId = 4001;
                else
                    sensorId++;
            }
            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
