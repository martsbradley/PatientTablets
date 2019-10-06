package martinbradley.hospital.persistence.repository;

import javax.transaction.UserTransaction;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Properties;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import martinbradley.hospital.core.domain.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import javax.persistence.EntityTransaction;
import java.nio.file.NoSuchFileException;
import java.io.IOException;
import mockit.*;
import static org.hamcrest.core.Is.is;
import org.hamcrest.core.IsCollectionContaining;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatabaseTestIT
{
    private static final Logger log = LoggerFactory.getLogger(DatabaseTestIT.class);
    private static String protocol     = "jdbc:derby:"; 
    private static String driver       = "org.apache.derby.jdbc.EmbeddedDriver"; 
    private static String databaseName = "myDerbyDB;create=true"; 

    private static void deleteOldDBFiles() //throws Exception
    {
        log.info("DELETE old files");
        try {
            Path path = new File("myDerbyDB").toPath();

            final List<Path> pathsToDelete = Files.walk(path)
                                             .sorted(Comparator.reverseOrder())
                                             .collect(Collectors.toList());
            for(Path aPath : pathsToDelete) 
            {
                Files.deleteIfExists(aPath);
            }
        }
        catch (IOException e)
        {
            // No problem
        }
    }

    private static Connection getConnection() throws SQLException
    {
        Connection conn = DriverManager.getConnection(protocol + databaseName, new Properties());
        return conn;
    }
    private static void loadDriver() throws Exception
    {
        Class.forName(driver).newInstance();
    }

    private static void createTables(Connection conn) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "create table patient (                                    ");
        sb.append( "id INTEGER PRIMARY KEY,                                   ");
        sb.append( "forename varchar(20) NOT NULL,                            ");
        sb.append( "surname varchar(50) NOT NULL,                             ");
        sb.append( "sex varchar(6) CHECK (sex IN ('Male','Female')) NOT NULL, ");
        sb.append( "dateofbirth date NOT NULL,                                "); 
        sb.append( "version integer NOT NULL)                                 "); 

        String query = sb.toString();

        runStatement(conn, query);

        sb = new StringBuilder();
        sb.append( "create table medicine (                                   ");
        sb.append( "id INTEGER PRIMARY KEY,                                   ");
        sb.append( "name varchar(50) NOT NULL,                                ");
        sb.append( "manufacturer varchar(50) NOT NULL,                        ");
        sb.append( "delivery_method varchar(12) CHECK (delivery_method IN     ");
        sb.append( "('Intravenous','Tablet','Liquid','Suppository')))         ");

        query = sb.toString();
        runStatement(conn, query);

        sb = new StringBuilder();
        sb.append( "create table prescription (                                ");
        sb.append( "id INTEGER PRIMARY KEY,                                     ");
        sb.append( "patient_id INTEGER NOT NULL REFERENCES patient (id),       ");
        sb.append( "medicine_id INTEGER NOT NULL REFERENCES medicine (id),     ");
        sb.append( "start_date date NOT NULL,                                  ");
        sb.append( "end_date date NOT NULL,                                    ");
        sb.append( "amount VARCHAR(40) NOT NULL)                               ");

        query = sb.toString();

        runStatement(conn, query);


        sb = new StringBuilder();
        sb.append("create table uploadedimage (                              ");
        sb.append("id INTEGER PRIMARY KEY,                                   ");
        sb.append("patient_id INTEGER NOT NULL REFERENCES patient (id),       ");
        sb.append("name   VARCHAR(250) NOT NULL,                             ");
        sb.append("bucket VARCHAR(40) NOT NULL,                              ");
        sb.append("description VARCHAR(250),                                 ");
        sb.append("date_uploaded TIMESTAMP NOT NULL)                         ");

        query = sb.toString();
        runStatement(conn, query);


        runStatement(conn, "CREATE SEQUENCE patient_id_seq  start with 10");
        runStatement(conn, "CREATE SEQUENCE medicine_id_seq start with 200");
        runStatement(conn, "CREATE SEQUENCE prescription_id_seq start with 400");
        runStatement(conn, "CREATE SEQUENCE uploadedimage_id_seq start with 500");
    }

    public static void populate() throws Exception
    {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();

        Patient martin = createPatient(em, "Martin", "Bradley");
        Patient paul = createPatient(em, "Paul", "Bradley");

        Medicine fizzy = createMedicine(em, "fizzy", "Derry");
        Medicine mix = createMedicine(em,   "mix",   "Dublin");

        Prescription p  = createPrescription(em, martin,fizzy,"lots");
        Prescription p2 = createPrescription(em, martin,mix,"justsome");
        Prescription p3 = createPrescription(em, paul,mix,"lots");

        UploadedImage image   = createUpload(em, martin,"bucket", "marty1");
        UploadedImage image2  = createUpload(em, martin,"bucket", "marty2");
        UploadedImage imager3 = createUpload(em, paul,  "bucket", "paul1");

        em.getTransaction().commit();
        em.close();
    }

    private static void runStatement(Connection conn, String query) throws Exception
    {
        PreparedStatement pstm = conn.prepareStatement(query);
        pstm.execute();
        pstm.close();
    }
    @BeforeAll
    public static void setupAll() throws Exception
    {
        log.info("___________Setup Starting_____________");
        deleteOldDBFiles();
        loadDriver();
        Connection conn = getConnection(); 
        createTables(conn);
        populate();
        log.info("___________Setup Finished_____________");
    }

    @AfterAll
    public static void tearDown() throws Exception
    {
          //deleteOldDBFiles();
    }

    private static Patient newPatient(String forename, 
                                      String surname)
    {
        Patient p = new Patient();
        p.setForename(forename);
        p.setSurname(surname);
        p.setDob(LocalDate.now());
        p.setSex(Sex.Male);
        return p;
    }

    private static Patient createPatient(EntityManager em,
                                  String forename, 
                                  String surname)
    {
        Patient p = newPatient(forename, surname);
        em.persist(p);

        String msg = String.format("Patient %d %s %s persisted",
                                   p.getId(),
                                   forename,
                                   surname);
        log.info(msg);
        return p;
    }

    private static UploadedImage createUpload(EntityManager em,
                                              Patient aPatient,
                                              String bucket, 
                                              String name) {
        UploadedImage u = new UploadedImage();
        u.setPatient(aPatient);
        u.setBucket(bucket);
        u.setName(name);
        u.setDescription("Some image");
        u.setDateUploaded(LocalDateTime.now());

        em.persist(u);

        String msg = String.format("UploadedImage %d %s persisted",
                                   u.getId(),
                                   name);
        log.info(msg);
        return u;
    }


    private static Medicine createMedicine(EntityManager em,
                                    String name, 
                                    String manufacturer)
    {
        Medicine m = new Medicine();
        m.setName(name);
        m.setManufacturer(manufacturer);
        em.persist(m);

        String msg = String.format("Medicine %d %s %s persisted",
                                   m.getId(),
                                   name,
                                   manufacturer);
        log.info(msg);
        return m;
    }

    private static Prescription createPrescription(EntityManager em,
                                            Patient patient,
                                            Medicine medicine,
                                            String amount)
    {
        Prescription p = new Prescription();
        p.setPatient(patient);
        p.setMedicine(medicine);
        p.setStartDate(LocalDate.now());
        p.setEndDate(LocalDate.now());
        p.setAmount("heaps");

        em.persist(p);

      //String msg = String.format("Medicine %d %s %s persisted",
      //                           p.getMessage.getId(),
      //                           name);
      //log.info(msg);
        return p;
    }


    @Test
    public void canLoad() throws Exception
    {
        Connection conn = getConnection(); 

        Statement stmt = null;
        String query = "select * from Patient";

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println("Got one..");
            }
        }
        catch (SQLException e ) 
        {
            System.out.println("Problem... " + e.getMessage());
        } 
        finally 
        {
            if (stmt != null) { stmt.close(); }
        }

        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        TypedQuery<Patient> typedQuery = em.createQuery("SELECT e FROM Patient e", Patient.class);
        List<Patient> patients=  typedQuery.getResultList();
        System.out.println("patients has " + patients.size() + " elements.");
        for (Patient pat: patients)
        {
            System.out.println("Loaded up " + pat);
        }
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void ensure_loadPatient_works() throws Exception
    {
        log.info("______________starttest....ensure_loadPatient_works");
        EntityManager em = getEntityManager();
        PatientDBRepo repo = new PatientDBRepo();
        repo.tx = new UserTransactionAdapter(em.getTransaction());
        repo.entityManager = em;

        Patient p = repo.loadById(10);

        log.info("Got patient p"  + p);

        em.close();// Important as no more loading of SQL 
        
        List<Prescription> tablets = p.getPrescription();

        log.info("tablets are " + tablets);
        log.info(String.format("loaded patient and it has %d prescriptions",
                                    tablets.size()));
        log.info("Name... [" + p.getForename() + "  " + p.getSurname() + "]");

        for (Prescription pres: tablets)
        {
            Medicine med = pres.getMedicine();
            log.info(String.format("%d  %s",med.getId(),med.getName()));
        }
        log.info("______________endtest....ensure_loadPatient_works");
    }

    @Test
    public void add_another_prescription() throws Exception
    {
        log.info("______________starttest...add_another_prescription");
        EntityManager em = getEntityManager();

        PatientDBRepo repo = new PatientDBRepo();
        repo.tx = new UserTransactionAdapter(em.getTransaction());
        repo.entityManager = em;

        em.getTransaction().begin();
        Patient patient = repo.loadById(10);


        List<Prescription> tablets = patient.getPrescription();

        log.info(patient.toString());

        Medicine med = null;

        for (Prescription pres: tablets)
        {
            med = pres.getMedicine();
            log.info(String.format("%d  %s",med.getId(),med.getName()));
        }
        em.detach(patient);

        em.getTransaction().commit();

        log.info("Adding another prescription");

        em.getTransaction().begin();

        Prescription tab = new Prescription();
        tab.setPatient(patient);
        tab.setMedicine(med);
        tab.setStartDate(LocalDate.now());
        tab.setEndDate(LocalDate.now());
        tab.setAmount("good lot");

        patient.getPrescription().add(tab);

        log.info("********merge***************");

        em.merge(patient);

        em.getTransaction().commit();

        em.getTransaction().begin();

        log.info("After the merge");

        patient = repo.loadById(10);

        log.info(patient.toString());

        em.getTransaction().commit();
        em.close();


        log.info("Finished add_another_prescription");
    }

    @Test
    public void deleteAllPatientPrescriptions() throws Exception
    {
        showPrescriptionIds();

        EntityManager em = getEntityManager();

        PatientDBRepo repo = new PatientDBRepo();
        repo.tx = new UserTransactionAdapter(em.getTransaction());
        repo.entityManager = em;


        //Removing this because the database is only populated once
        //this test was messing up the earlier data

        // Delete images that were created for Martin
      //UploadedImage image1 = em.find(UploadedImage.class, 500L);
      //UploadedImage image2 = em.find(UploadedImage.class, 501L);
      //em.remove(image1);
      //em.remove(image2);

      //Patient p = repo.loadById(10);
      //log.info("Got patient p"  + p);
      //repo.deletePatient(p);

        em.close();// Important as no more loading of SQL 
     // Set<Long> ids = showPrescriptionIds();

     // log.info("The 400 and 401 should not be in below list...");
     // assertThat(1, is(ids.size()));
     // // Only Prescription 402 remains
     // assertThat(ids,  IsCollectionContaining.hasItems(402L));
    }

    @Test
    public void uploadedImage() throws Exception
    {
        showUploadedImages();
        EntityManager em  = getEntityManager();

        UploadedImageRepo repo = new UploadedImageRepo();
        repo.tx = new UserTransactionAdapter(em.getTransaction());
        repo.entityManager = em;

        List<UploadedImage> list = repo.getImages(10L);// Martin

        log.info("CreateUploadedImage list has " + list.size());
        assertThat(2, is(list.size()));

        list = repo.getImages(11L);// Paul

        log.info("CreateUploadedImage list has " + list.size());
        assertThat(1, is(list.size()));


        PatientDBRepo patientRepo = new PatientDBRepo();
        patientRepo.tx = new UserTransactionAdapter(em.getTransaction());
        patientRepo.entityManager = em;

        em.getTransaction().begin();
        Patient paul = patientRepo.loadById(11);
        UploadedImage imager4 = createUpload(em, paul,  "bucket", "paul2");
        UploadedImage imager5 = createUpload(em, paul,  "bucket", "paul2");


        em.close();
        log.info("Finished createUploadedImage");
    }

    static EntityManager getEntityManager() {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testingUnit");
        EntityManager em = emFactory.createEntityManager();
        return em;
    }

    private void showUploadedImages() throws Exception
    {
        // Verify...
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("Select id from uploadedimage");
        Set<Long> ids = new HashSet<>();
        while (rs.next()) 
        {
            long id = rs.getInt("id");
            log.info("Found uploadedimage "+ id);
            ids.add(id);
        }
        rs.close();
        conn.close();

        log.info("________________________");
    }

    private Set<Long> showPrescriptionIds() throws Exception
    {
        // Verify...
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("Select id from prescription");
        Set<Long> ids = new HashSet<>();
        while (rs.next()) 
        {
            long id = rs.getInt("id");
            log.info("Found Prescription "+ id);
            ids.add(id);
        }
        rs.close();
        conn.close();

        log.info("________________________");
        return ids;
    }
}

class UserTransactionAdapter implements UserTransaction
{
    final EntityTransaction entityTransaction;
    private static final Logger log = LoggerFactory.getLogger(UserTransactionAdapter.class);

    public UserTransactionAdapter(EntityTransaction entityTransaction)
    {
	this.entityTransaction = entityTransaction;
    }

    public void begin()
    {
        log.info("begin has been CALLED...");
        entityTransaction.begin();
    }

    public void commit()
    {
        log.info("commit has been CALLED...");
        entityTransaction.commit();
    }

    public int  getStatus(){ return 0;}

    public void rollback()
    {
        notSupported();
    }

    public void setRollbackOnly()
    {
        notSupported();
    }

    public void setTransactionTimeout(int seconds)
    {
        notSupported();
    }

    private void notSupported()
    {
        throw new RuntimeException();
    }
}
