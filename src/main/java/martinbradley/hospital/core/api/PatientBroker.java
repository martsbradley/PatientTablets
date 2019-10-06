package martinbradley.hospital.core.api;

import java.util.List;
import martinbradley.hospital.core.api.dto.MessageCollection;
import martinbradley.hospital.core.domain.Patient;
import martinbradley.hospital.core.domain.UploadedImage;
import martinbradley.hospital.core.beans.PageInfo;

public interface PatientBroker
{
    public List<Patient> getPatientsPaged(PageInfo aPageInfo);

    public int getPatientCount();

    public long savePatient(Patient aPatient, MessageCollection aMessages);

    public Patient deletePatient(Patient aPatient);

    public Patient loadById(long id);

    public List<String> listImages(long patientId);

    public UploadedImage saveImage(UploadedImage imageDTO);
}
