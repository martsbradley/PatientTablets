package martinbradley.hospital.rest;

import org.junit.jupiter.api.Test;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import martinbradley.hospital.core.beans.PatientBean;
import martinbradley.hospital.core.beans.IdentifierBean;
import javax.ws.rs.core.GenericType;
import java.util.List;
import java.time.LocalDate;

public class RestClientTestIT
{
    private static final Logger logger = LoggerFactory.getLogger(RestClientTestIT.class);

    @Test
    public void patient_load_not_found_404()
    {
        logger.info("*************Starting patient_load_not_found_404 **************");
        Response response = call_load_patient(1991999);

        assertThat(response.getStatusInfo(), is(Response.Status.NOT_FOUND));
    }

    @Test
    public void load_patients_paged()
    {
        logger.info("*************Starting load_patients_paged **************");
        Response resp = call_load_patient_paged(1, 8);

        assertThat(resp.getStatusInfo(), is(Response.Status.ACCEPTED));

        List<PatientBean> patients = resp.readEntity(new GenericType<List<PatientBean>>() { });
        //
        //assertThat(patients, is(notN

        System.out.println("patients.size() " +patients.size());
        for (PatientBean pat : patients)
        {
            System.out.println(pat);
        }
    }

    @Test
    public void successful_save_call() {
        logger.info("starting successful_save_call test");
        PatientBean patientBean = new PatientBean();
        patientBean.setId(null);
        patientBean.setForename("Frank");
        patientBean.setSurname(getRandomString());
        patientBean.setDob(LocalDate.now());
        patientBean.setRowVersion(2);


        Response response = callSavePatientEndpoint(patientBean);

        assertThat(response.getStatusInfo(), is(Response.Status.ACCEPTED));

        logger.info("post response " + response);

        IdentifierBean idBean = response.readEntity(IdentifierBean.class);

        logger.info("Save returned id " + idBean.getId());
    }

    /** Load up the same object twice
     *  save updated version of the first one.
     *  Then try to save the second one with a change and it not allow it*/
    @Test
    public void test_optimistic_locking() {

        for (int x = 0; x < 10; x++) {
            tryOptimistic();
        }
    }
    private void tryOptimistic(){
        Response response = call_load_patient(1);
        PatientBean patientOne = response.readEntity(PatientBean.class);
        logger.info("Loaded patientOne version "+ patientOne.getRowVersion() + " " +
                                                  patientOne.getSurname());

        Response anotherResponse = call_load_patient(1);
        PatientBean anotherPatientOne = anotherResponse.readEntity(PatientBean.class);
        logger.info("Loaded anotherResponse version "+ anotherPatientOne.getRowVersion()+ " " + 
                                                       anotherPatientOne.getSurname());

        assertThat(patientOne, not(anotherPatientOne));

        String name = getRandomString();
        logger.info("Changing PatientOne to name "+ name);
        patientOne.setSurname(name);
        response = callSavePatientEndpoint(patientOne);

        assertThat(response.getStatusInfo(), is(Response.Status.ACCEPTED));

        name = getRandomString();
        logger.info("Changing anotherPatientOne to name " + name);
        anotherPatientOne.setSurname(name);
        response = callSavePatientEndpoint(anotherPatientOne);
        assertThat(response.getStatusInfo(), is(Response.Status.BAD_REQUEST));
    }

    private String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);

        for (int i = 0; i < targetStringLength; i++) 
        {
            int randomLimitedInt = leftLimit + (int) 
              (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();
        return generatedString;
    }

    //private Response call_post

    private Response call_load_patient(int id)
    {
        String url = String.format("http://localhost:8080/firstcup/rest/hospital/patient/%d",id);
        
        Response response = callGetRequest(url);

        return response;
    }

    private Response call_load_patient_paged(int aStart, int aMax)
    {
        String url = String.format("http://localhost:8080/firstcup/rest/hospital/patients?" + 
                                   "start=%d&" +
                                   "max=%d&sortby=forename", 
                                   aStart,
                                   aMax);

        Response response = callGetRequest(url);

        return response;
    }

    private Response callGetRequest(String url)
    {
        Client client = ClientBuilder.newClient();
        Response response = client.target(url)
                                  .request()
                                  .get(Response.class);
        return response;
    }

    private Response callSavePatientEndpoint(PatientBean patientBean) {
        
        Client client = ClientBuilder.newClient();

        Entity entity = Entity.entity(patientBean, MediaType.APPLICATION_JSON);

        String url = "http://localhost:8080/firstcup/rest/hospital/patient/";
        Response response = client.target(url)
                                  .request()
                                  .post(entity, Response.class);
        return response;
    }
}
