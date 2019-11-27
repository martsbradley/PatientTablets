package martinbradley.hospital.rest;

import martinbradley.hospital.core.beans.PatientBean;
import martinbradley.hospital.core.beans.MedicineBean;
import martinbradley.hospital.core.beans.PrescriptionBean;
import martinbradley.hospital.core.beans.ValidationErrors;
import martinbradley.hospital.core.beans.ValidationError;



import martinbradley.hospital.core.beans.IdentifierBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.Response;
import martinbradley.hospital.web.api.PatientHandler;
import martinbradley.hospital.web.api.MedicineHandler;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.*;
import javax.ws.rs.NotFoundException;
import static javax.ws.rs.core.Response.Status;
import martinbradley.hospital.core.beans.PageInfo;
import static martinbradley.hospital.core.beans.PageInfo.PageInfoBuilder;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import martinbradley.hospital.core.api.dto.MessageCollection;
import martinbradley.hospital.core.api.dto.Message;
import martinbradley.security.SecuredRestfulMethod;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

@Path("/hospital")
public class HospitalResourceImpl 
{
    @Inject PatientHandler patientHandler;
    @Inject MedicineHandler medicineHandler;
    private static Logger logger = LoggerFactory.getLogger(HospitalResourceImpl.class);

    @GET
    @Path("patient/{id}")
    @Produces("application/json")
    public Response getPatient(@PathParam("id") long patientId)
    {
        logger.info("getPatient byId " + patientId);
        PatientBean patient = patientHandler.loadById(patientId);

        if (patient == null)
        {
            throw new NotFoundException();
        }

        return Response.accepted(patient)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    @GET
    @Path("patients/")
    @Produces("application/json")
    public Response pagePatients(@QueryParam("start")  int aStart,
                                 @QueryParam("max")    int aMax,
                                 @QueryParam("sortby") String aSortBy)
    {
        PageInfo pageInfo  = new PageInfoBuilder()
                                 .setStartAt(aStart)
                                 .setMaxPerPage(aMax)
                                 .setSortField(aSortBy)
                                 .build();
        List<PatientBean> patients = patientHandler.pagePatients(pageInfo);

        logger.info("pagePatients called returning :" + patients.size());
        GenericEntity<List<PatientBean>> entity = new GenericEntity<List<PatientBean>>(patients) {};

        return Response.accepted(entity)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    @POST
    @Path("patient")
    @Produces("application/json")
    @SecuredRestfulMethod(groups={"admin"})
    public Response savePatient(PatientBean patientBean,
                                @Context SecurityContext securityContext)
    {
        String userName = securityContext.getUserPrincipal().getName();
        logger.info(userName + " called savePatient " + patientBean);

        ValidationErrors validationResult = validate(patientBean);

        if (validationResult.hasErrors()) {
            logger.info("savePatient returning validation message");
                return Response.status(Status.BAD_REQUEST)
                               .type(MediaType.APPLICATION_JSON)
                               .entity(validationResult)
                               .build();
        }


        MessageCollection messages = new MessageCollection();
        Long id = patientHandler.savePatient(patientBean, messages);
        IdentifierBean ident = new IdentifierBean();
        ident.setId(id);
        
        if (id == null || messages.hasMessages())
        {
            ValidationErrors errors = asValidationErrors(messages);

            logger.info("Failed to save" + messages);
            return Response.status(Status.BAD_REQUEST)
                           .type(MediaType.APPLICATION_JSON)
                           .entity(errors)
                           .build();
        }

        logger.info("savePatient successful");

        return Response.accepted(ident)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    @GET
    @Path("patients/total")
    @Produces("application/json")
    public Response totalPatients()
    {
        logger.debug("totalPatients called");
        int total = patientHandler.getTotalPatients();

        return Response.accepted(total)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    @GET
    @Path("medicines/")
    @Produces("application/json")
    public Response pageMedicines(@QueryParam("start")  int aStart,
                                  @QueryParam("max")    int aMax,
                                  @QueryParam("sortby") String aSortBy,
                                  @QueryParam("filter") String aFilter) {
        PageInfo pageInfo  = new PageInfoBuilder()
                                 .setStartAt(aStart)
                                 .setMaxPerPage(aMax)
                                 .setSortField(aSortBy)
                                 .setFilter(aFilter)
                                 .build();

        logger.info("pageMedicines " + pageInfo);

        List<MedicineBean> medicines = medicineHandler.pageMedicines(pageInfo);

        GenericEntity<List<MedicineBean>> entity = new GenericEntity<List<MedicineBean>>(medicines) {};

        return Response.accepted(entity)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
    @POST
    @Path("patient/{patientId}/medicine/{medicineId}")
    @Produces("application/json")
    @SecuredRestfulMethod(groups={"admin"})
    public Response savePrescription(@PathParam("patientId") long patientId,  
                                     @PathParam("medicineId") long medicineId,  
                                     PrescriptionBean prescriptionBean,
                                     @Context SecurityContext securityContext)
    {
        PatientBean patient = patientHandler.loadById(patientId);
        MedicineBean medicine = medicineHandler.loadById(medicineId);
        prescriptionBean.setPatient(patient);
        prescriptionBean.setMedicine(medicine);

        logger.debug("savePrescription called ");
        logger.debug("medicineId " + medicineId);
        logger.debug("prescriptionBean " + prescriptionBean);
        logger.debug("patientId " + patientId);
        logger.debug(patient.getForename() + " " + patient.getSurname());

        logger.debug("Loaded up the " + prescriptionBean);


        patient.addPrescription(prescriptionBean);

        Response result = savePatient(patient, securityContext);

        //return Response.status(Response.Status.OK).build();
        return result;
    }

    @GET
    @Path("medicines/total")
    @Produces("application/json")
    public Response totalMedicines(@QueryParam("filter") String filter)
    {
        logger.debug("totalMedicines called");
        int total = medicineHandler.getTotalMedicines(filter);

        return Response.accepted(total)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }

    @GET
    @Path("patient/{patientId}/images")
    @Produces("application/json")
    @SecuredRestfulMethod(groups={"admin"})
    public Response listImages(@PathParam("patientId") long patientId) {
        logger.debug("listImages");
        List<String> urls = patientHandler.listImages(patientId);
        logger.debug("listImages returning " + urls.size());

        return Response.accepted(urls)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("patient/{patientId}/image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/json")
    @SecuredRestfulMethod(groups={"admin"})
    @ContentLengthMethod
    public Response addImage(@PathParam("patientId") long patientId,
                             @MultipartForm ImageUploadedBean myForm)
    {
        logger.debug("ImageUploaded called " + myForm);

        int total = 10;

        patientHandler.saveImage(patientId, myForm);



        return Response.accepted(total)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }


    private ValidationErrors validate(PatientBean patientBean) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<PatientBean>> violations = validator.validate(patientBean);
        logger.debug("violations has " + violations.size());

	ValidationErrors errors = new ValidationErrors();

	for (ConstraintViolation<PatientBean> violation: violations)
	{
	    String invalidValue = (String) violation.getInvalidValue();
	    String message = violation.getMessage();
	    String path = violation.getPropertyPath().toString();

	    logger.warn("Found constraint violation. Value: '" + invalidValue + "' Message: " + message);
	    logger.warn("path." + path);

	    ValidationError error = new ValidationError();
	    error.setField(path);
	    error.setMessage(message);
	    errors.add(error);
	}
	return errors;
    }

    private ValidationErrors asValidationErrors(MessageCollection aMessages){

	ValidationErrors errors = new ValidationErrors();

        for (Message message: aMessages) {
	    ValidationError error = new ValidationError();
	    error.setField(message.getKey());
	    error.setMessage(message.toString());
	    errors.add(error);
        }
        return errors;
    }



  //@GET
  //@Path("userDetail")
  //@Produces("application/text")
  //public Response userDetails(@Context SecurityContext sc) {

  //    String username = "";

  //    if (isAdminLoggedIn(sc)) {
  //        username = sc.getUserPrincipal().getName();
  //    }
  //    logger.info("userDetails :'"+ username + "'");

  //    return Response.accepted(username)
  //                   .type(MediaType.TEXT_PLAIN)
  //                   .build();
  //}

  //@GET
  //@Path("isAdmin")
  //@Produces("application/text")
  //public Response isAdmin(@Context SecurityContext sc) {

  //    boolean isAdmin = isAdminLoggedIn(sc);

  //    logger.debug("isAdmin :"+ isAdmin);
  //    return Response.accepted(isAdmin)
  //                   .type(MediaType.TEXT_PLAIN)
  //                   .build();
  //}


  //@POST
  //@Path("logOff")
  //@Produces("application/text")
  //public Response isAdmin(@Context HttpServletRequest req) {

  //    req.getSession().invalidate();

  //    return Response.status(Status.UNAUTHORIZED)
  //                   .type(MediaType.TEXT_PLAIN)
  //                   .build();
  //}

  //private boolean isAdminLoggedIn(SecurityContext sc) {
  //    boolean isAdmin = sc.isUserInRole(ADMIN_ROLE);
  //    return isAdmin;
  //}
}
