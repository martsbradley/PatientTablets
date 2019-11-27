package martinbradley.hospital.core.api;

import java.util.List;
import java.util.ArrayList;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import martinbradley.hospital.core.domain.*;
import martinbradley.hospital.persistence.repository.PatientDBRepo;
import martinbradley.hospital.persistence.repository.UploadedImageRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import martinbradley.hospital.core.api.dto.*;
import martinbradley.hospital.core.domain.SavePatientResponse;
import martinbradley.hospital.core.beans.PageInfo;
import martinbradley.aws.s3.Uploader;
import java.io.IOException;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.net.URI;
import martinbradley.aws.s3.URLSigner;
import java.io.UnsupportedEncodingException;

@Model
public class PatientBrokerImpl implements PatientBroker
{
    private static final String IMAGE_S3_BUCKET = "gorticrumboxone";// TODO from environment
    private static final Region AWS_REGION = Region.EU_WEST_1;

    @Inject PatientDBRepo repo;
    @Inject UploadedImageRepo imageRepo;

    private static final Logger logger = LoggerFactory.getLogger(PatientBrokerImpl.class);

    @Override
    public List<Patient> getPatientsPaged(PageInfo aPageInfo)
    {
        Patient.SortOrder ordering = Patient.SortOrder.find(aPageInfo.getSortField(), 
                                                            aPageInfo.isAscending());
  
        List<Patient> patients = repo.pagePatients(aPageInfo.getStartingAt(), 
                                                   aPageInfo.getMaxPerPage(), 
                                                   ordering);
        return patients;
    }

    public int getPatientCount()
    {
        return repo.getTotalPatients();
    }

    @Override
    public long savePatient(Patient aPatient, 
                            MessageCollection aMessages)
    {
        logger.debug("savePatient" + aPatient);


        SavePatientResponse repoResponse = repo.savePatient(aPatient);
        logger.info("repoResponse hasMessages?" + repoResponse.hasMessages());
        if (repoResponse.hasMessages())
        {
            logger.info("Repo hit problems when saving");
            aMessages.addAll(repoResponse.getMessages());
            return -1;
        }
        return repoResponse.getPatient().getId();
    }


    @Override
    public Patient deletePatient(Patient aPatient)
    {
        logger.debug("deletePatient" + aPatient);

        repo.deletePatient(aPatient);
        //TODO result should come from repo?
        return aPatient;
    }

    @Override
    public Patient loadById(long id)
    {
        Patient patient = repo.loadById(id);

        if (patient == null) {
            logger.info("returning null for patient " + id);
        }

        return patient;
    }

    public List<String> listImages(long patientId) {

        List<UploadedImage> images = imageRepo.getImages(patientId);
     // List<String> urls = images.stream() 
     //                           .map(UploadedImage::getName)
     //                           .collect(Collectors.toList());

        EnvironmentVariableCredentialsProvider provider = 
                   EnvironmentVariableCredentialsProvider.create();
        
        List<String> urls = new ArrayList<>();

        for (UploadedImage image: images) {
            URI url = null;
            try {
                url = URLSigner.generateUrl(provider, 
                                           AWS_REGION, 
                                           IMAGE_S3_BUCKET,
                                           image.getName());
                logger.info("Download URL:'" + url + "'");
                urls.add(url.toString());
            } catch (UnsupportedEncodingException e) {
                logger.warn("Encoding issue with " + url);
                logger.warn("UnsupportedEncodingException ", e);
            }
        }

        return urls;
    }

    public UploadedImage saveImage(UploadedImage image) {
        logger.warn("Trying the image mapping");

        image.setBucket(IMAGE_S3_BUCKET);

        logger.warn("imageDTO length: " + image.getData().length);

        try {
            Uploader s3 = new Uploader();

            //  Save the image on Amazon S3.
            s3.upload(image.getData(),
                      image.getBucket(),
                      image.getName());

            // Write into the local database details of the image.
            imageRepo.saveUpload(image);
        }catch(IOException e) {
            logger.warn("failed", e);
        }


        logger.warn("Created... "+ image);
        logger.warn("Created with ... "+ image.getPatient());

        return image;
    }
}
