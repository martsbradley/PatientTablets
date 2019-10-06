package martinbradley.hospital.core.domain;
import martinbradley.hospital.core.domain.Patient;
import martinbradley.hospital.core.api.dto.MessageCollection; 

public class SavePatientResponse
{
    private Patient patient;
    private MessageCollection messages;

    public SavePatientResponse(Patient patient,
                                MessageCollection messages)
    {
        if (patient == null || messages == null)
        {
            throw new NullPointerException();
        }

        this.patient  =  patient;
        this.messages =  messages;
    }

    public Patient getPatient()
    {
        return this.patient;
    }

    public MessageCollection getMessages()
    {
        return this.messages;
    }

    public boolean hasMessages()
    {
        if (messages == null || !messages.hasMessages())
        {
            return false;
        }
        return true;
    }
}

