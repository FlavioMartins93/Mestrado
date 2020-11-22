import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import static com.mongodb.client.model.Filters.*;

public class DAO {
    private JSONObject jobj;

    public DAO(JSONObject jobj) {
        this.jobj = jobj;
    }

    public Document getInternment(MongoCollection<Document> internments) {
        JSONObject patient = (JSONObject) this.jobj.get("patient");

        Document internment = internments.find(and(and(
                eq("patient",
                        (new Document("id", (long) patient.get("patientid")))
                                .append("name", patient.get("patientname").toString())
                                .append("birthdate", patient.get("patientbirthdate").toString())),
                (eq("admdate", jobj.get("admdate").toString()))),
                (eq("sensor",
                        (new Document("id", (long) jobj.get("sensorid")))
                                .append("num", (long) jobj.get("sensornum"))
                                .append("type", jobj.get("type_of_sensor").toString()))
                ))).first();
        return internment;
    }

    public ArrayList<Document> getCareTeam() {
        ArrayList<Document> careTeamList = new ArrayList<Document>();
        JSONArray careTeam = (JSONArray) this.jobj.get("careteam");
        careTeam.forEach(doc -> {
            JSONObject doctor = (JSONObject) doc;
            careTeamList.add(new Document(
                    "id", doctor.get("id"))
                    .append("name",doctor.get("nome").toString()));
        });
        return careTeamList;
    }

    public ArrayList<Document> getNurseTeam() {
        ArrayList<Document> nurseTeamList = new ArrayList<Document>();
        JSONArray nurseTeam = (JSONArray) jobj.get("nurseteam");
        if(nurseTeam == null) return nurseTeamList;
        nurseTeam.forEach(doc -> {
            JSONObject doctor = (JSONObject) doc;
            nurseTeamList.add(new Document(
                    "id", doctor.get("id"))
                    .append("name",doctor.get("nome").toString()));
        });
        return nurseTeamList;
    }

    public Document addInternment(MongoCollection<Document> internments) {
        JSONObject patient = (JSONObject) this.jobj.get("patient");

        ArrayList<Document> careTeamList = getCareTeam();
        ArrayList<Document> nurseTeamList = getNurseTeam();

        Document internment = new Document(
                "patient", (new Document(
                "id", (long) patient.get("patientid")))
                .append("name", patient.get("patientname").toString())
                .append("birthdate", patient.get("patientbirthdate").toString()))
                .append("nurseTeam", nurseTeamList)
                .append("careTeam", careTeamList)
                .append("service", new Document(
                        "code", this.jobj.get("servicecod").toString())
                        .append("description", this.jobj.get("servicedesc").toString()))
                .append("admdate", this.jobj.get("admdate").toString())
                .append("bed", this.jobj.get("bed").toString())
                .append("sensor", new Document(
                        "id", (long) this.jobj.get("sensorid"))
                        .append("num", (long) this.jobj.get("sensornum"))
                        .append("type", this.jobj.get("type_of_sensor").toString()))
                .append("reads", new ArrayList<ObjectId>());

        internments.insertOne(internment);
        return internment;

    }

    public ObjectId addRead(MongoCollection<Document> reads) {
        JSONObject bloodpress = (JSONObject) this.jobj.get("bloodpress");
        JSONObject blood =  (JSONObject) this.jobj.get("blood");

        Document read = new Document(
                "bodytemp", jobj.get("bodytemp"))
                .append("bloodpress", new Document(
                        "systolic", (long) bloodpress.get("systolic"))
                        .append("diastolic", (long) bloodpress.get("diastolic")))
                .append("bpm", (long) this.jobj.get("bpm"))
                .append("birthdate", this.jobj.get("sato2"))
                .append("timestamp", this.jobj.get("timestamp"));

        if(blood != null)
            read.append("blood", new Document(
                    "ferritine", (long) blood.get("ferritine"))
                    .append("hemoglobine", (long) blood.get("hemoglobine")));
        reads.insertOne(read);
        return (ObjectId) read.get("_id");
    }

    public void parseData() {
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));

        MongoDatabase database = mongoClient.getDatabase("sensor");

        MongoCollection<Document> internments = database.getCollection("internments");
        MongoCollection<Document> reads = database.getCollection("reads");

        Document internment = getInternment(internments);

        if(internment == null) internment = addInternment(internments);

        ObjectId readId = addRead(reads);

        internments.updateOne(
                new Document("_id",internment.get("_id")),
                new Document("$addToSet",new Document("readId",readId))
                );

    }
}
