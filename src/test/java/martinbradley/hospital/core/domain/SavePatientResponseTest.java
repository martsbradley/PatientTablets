package martinbradley.hospital.core.domain;
import martinbradley.hospital.core.api.dto.MessageCollection;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import martinbradley.hospital.core.domain.Patient;
import mockit.*;
import martinbradley.hospital.persistence.repository.PatientDBRepo;
import martinbradley.hospital.core.domain.Patient;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.MatcherAssert.assertThat;
import javax.validation.ConstraintViolation;
import java.util.Set;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SavePatientResponseTest
{
    @Test
    public void response_never_returns_null()
    {
        Patient p = new Patient();
        MessageCollection msg = new MessageCollection();
        SavePatientResponse impl = new SavePatientResponse(p, msg);

        assertThat(impl.getPatient(), is(notNullValue()));
        assertThat(impl.getMessages(), is(notNullValue()));
    }

    @Test
    public void bad_response_throws() throws NullPointerException
    {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new SavePatientResponse(null, null);
        });
    }
}
