package martinbradley.hospital.persistence.repository;

import martinbradley.hospital.core.domain.password.AuthGroup;
import martinbradley.hospital.core.domain.password.AuthUser;
import martinbradley.hospital.core.domain.password.AuthUserGroup;
import martinbradley.hospital.core.domain.password.Salt;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

public class UserGroupTestIT
{
    private static final Logger logger = LoggerFactory.getLogger(UserGroupTestIT.class);
    private static String protocol     = "jdbc:derby:"; 
    private static String driver       = "org.apache.derby.jdbc.EmbeddedDriver"; 
    private static String databaseName = "myDerbyDB;create=true"; 

    private static void deleteOldDBFiles() //throws Exception
    {
        logger.info("DELETE old files");
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
        catch (IOException e) {
            logger.warn("Unable to delete", e);
        }
    }

    private static Connection getConnection() throws SQLException
    {
        Connection conn = DriverManager.getConnection(protocol + databaseName, new Properties());
        return conn;
    }
    private static void loadDriver() throws Exception {
        Class.forName(driver).newInstance();
    }

    private static void createTables(Connection conn) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append( "create table auth_user (                                  ");
        sb.append( "id INTEGER PRIMARY KEY,                              ");
        sb.append( "username varchar(20) NOT NULL,                       ");
        sb.append( "salt varchar(64) NOT NULL,                           ");
        sb.append( "passwordHash varchar(64) NOT NULL)                   ");

        String query = sb.toString();

        runStatement(conn, query);

        sb = new StringBuilder();
        sb.append( "create table auth_group (                                  ");
        sb.append( "id INTEGER PRIMARY KEY,                               ");
        sb.append( "name varchar(50) NOT NULL,                            ");
        sb.append( "description varchar(50) NOT NULL)                     ");

        query = sb.toString();
        runStatement(conn, query);

        sb = new StringBuilder();
        sb.append("create table user_group (                          ");
        sb.append("id INTEGER PRIMARY KEY,                            ");
        sb.append("user_id INTEGER NOT NULL REFERENCES auth_user (id),     ");
        sb.append("group_id INTEGER NOT NULL REFERENCES auth_group (id),   ");
        sb.append("created TIMESTAMP NOT NULL)                        ");

        query = sb.toString();
        runStatement(conn, query);


        runStatement(conn, "CREATE SEQUENCE auth_user_id_seq  start with 10");
        runStatement(conn, "CREATE SEQUENCE auth_group_id_seq start with 200");
        runStatement(conn, "CREATE SEQUENCE auth_user_group_id_seq start with 400");
    }

    static EntityManager getEntityManager() {
        EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("testingUnit");
        EntityManager em = emFactory.createEntityManager();
        return em;
    }

    public static void populate() throws Exception
    {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();

        AuthUser martin = createUser(em, "Martin", "abc","pass1");
        AuthUser paul = createUser(em, "Paul", "def", "pass2");

        AuthGroup normal = createGroup(em, "normal", "Normal Folks");
        AuthGroup admin = createGroup(em,   "admin",   "Super Users");

        AuthUserGroup p  = createUserGroup(em, martin,normal);
        AuthUserGroup p2 = createUserGroup(em, martin,admin);
        AuthUserGroup p3 = createUserGroup(em, paul,normal);

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
        logger.info("___________Setup Starting_____________");
        deleteOldDBFiles();
        loadDriver();
        Connection conn = getConnection(); 
        createTables(conn);
        populate();
        logger.info("___________Setup Finished_____________");
    }

    @AfterAll
    public static void tearDown() throws Exception
    {
          //deleteOldDBFiles();
    }

    private static AuthUser newUser(String username,
                                    String salt,
                                    String passwordHash)
    {
        AuthUser u = new AuthUser();
        u.setUsername(username);
        u.setSalt(salt);
        u.setPasswordHash(passwordHash);
        return u;
    }

    private static AuthUser createUser(EntityManager em,
                                       String username,
                                       String salt,
                                       String passwordHash)
    {
        AuthUser u = newUser(username, salt, passwordHash);
        em.persist(u);

        String msg = String.format("User %d %s persisted",
                                   u.getId(),
                                   username);
        logger.info(msg);
        return u;
    }

    private static AuthGroup createGroup(EntityManager em,
                                         String name,
                                         String description)
    {
        AuthGroup g = new AuthGroup();
        g.setName(name);
        g.setDescription(description);
        em.persist(g);

        String msg = String.format("Group %d %s persisted",
                                   g.getId(),
                                   name);
        logger.info(msg);
        return g;
    }

    private static AuthUserGroup createUserGroup(EntityManager em,
                                                 AuthUser user,
                                                 AuthGroup group)
    {
        AuthUserGroup ug = new AuthUserGroup();
        ug.setUser(user);
        ug.setGroup(group);
        ug.setCreatedDate(LocalDate.now());

        em.persist(ug);

        return ug;
    }

    @Test
    public void canLoadUserSalt() throws Exception
    {
        EntityManager em = getEntityManager();

        AuthUserGroupRepo repo = new AuthUserGroupRepo();
        repo.setEntityManager(em);
        repo.tx = new UserTransactionAdapter(em.getTransaction());

        Salt martinSalt = repo.getUserSalt("Martin");
        Salt paulSalt = repo.getUserSalt("Paul");

        assertThat(martinSalt, is(notNullValue()));
        assertThat(paulSalt, is(notNullValue()));

        assertThat(martinSalt.getSaltValue(), is("abc"));
        assertThat(paulSalt.getSaltValue(), is("def"));


        Set<AuthGroup> groups = repo.authenticate("Martin", "pass1");

        for(AuthGroup group: groups) {
            logger.info("Martin in " + group);
        }

        try {
            repo.authenticate("UnknownUser", "pass1");
            fail("Should throw an exception");
        }
        catch (AuthenticationException e) { }

        try {
            repo.authenticate("Martin", "pass2");// wrong password
            fail("Should throw an exception");
        }
        catch (AuthenticationException e) { }

        em.close();
    }




}

