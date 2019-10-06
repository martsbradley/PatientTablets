package martinbradley.hospital.web.api;
import javax.inject.Named;
import javax.inject.Inject;
import java.util.List;
import java.util.ArrayList;

import martinbradley.hospital.core.domain.Patient;
import martinbradley.hospital.core.domain.UploadedImage;
import martinbradley.hospital.rest.ImageUploadedBean;
import martinbradley.hospital.core.beans.PatientBean;
import martinbradley.hospital.mappers.PatientBeanMapper;
import martinbradley.hospital.mappers.UploadBeanMapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import martinbradley.hospital.core.api.PatientBroker;
import martinbradley.hospital.core.api.dto.*;
import martinbradley.hospital.core.beans.PageInfo;

import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
@Named
public class PatientHandler
{
    private static final Logger logger = LoggerFactory.getLogger(PatientHandler.class);
    final PatientBeanMapper patientMapper = Mappers.getMapper(PatientBeanMapper.class);

    @Inject PatientBroker patientBroker;

    public List<PatientBean> pagePatients(PageInfo aPageInfo)
    {
        List<Patient> patients = patientBroker.getPatientsPaged(aPageInfo);

        ArrayList<PatientBean> beans = new ArrayList<>();
        for (Patient p: patients) {
            PatientBean bean = patientMapper.domainToBean(p);
            beans.add(bean);
        }
        return beans;
    }
    public int getTotalPatients()
    {
        return patientBroker.getPatientCount();
    }

    public long savePatient(PatientBean patientBean, MessageCollection aMessages)
    {
        logger.info("Save Patient: " + patientBean);
        Patient patient = patientMapper.beanToDomain(patientBean);

        long patientId = patientBroker.savePatient(patient, aMessages);

        logger.info("Save Patient: has messages? " + aMessages.hasMessages());

        return patientId;
    }

    public void deletePatient(PatientBean patientBean)
    {
        logger.info("Delete Patient: " + patientBean);
        Patient patient = patientMapper.beanToDomain(patientBean);
        patientBroker.deletePatient(patient);
    }

    public PatientBean loadById(long id)
    {
        Patient patient = patientBroker.loadById(id);
        logger.info(String.format("loadById(%d) returned %s",id, patient));
        PatientBean bean = patientMapper.domainToBean(patient);
        return bean;
    }

    public List<String> listImages(long patientId) {
        logger.debug("listImages");
        List<String> urls = patientBroker.listImages(patientId);
        return urls;
    }

    public void saveImage(long patientId, ImageUploadedBean uploadBean) {

        logger.debug("saveImage");
        final UploadBeanMapper uploadMapper = Mappers.getMapper(UploadBeanMapper.class);
        UploadedImage image = uploadMapper.beanToDomain(uploadBean);

        Patient patient = patientBroker.loadById(patientId);
        if (patient != null)
        {
            image.setPatient(patient);
    
            String name = UUID.randomUUID().toString();
            logger.debug("image allocated name '" + name + "'");
    
            image.setName(name);
            image.setDateUploaded(LocalDateTime.now());
    
    
            logger.info("saveImage calling broker.");
            patientBroker.saveImage(image);
            logger.info("saveImage finished.");
        }
        else {
            logger.warn("Patient not found " + patientId);
        }
    }

}
