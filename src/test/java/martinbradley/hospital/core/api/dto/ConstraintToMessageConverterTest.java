package martinbradley.hospital.core.api.dto;

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
import java.util.ArrayList;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import javax.validation.ConstraintViolation;
import java.util.Set;
import javax.validation.Path;

public class ConstraintToMessageConverterTest
{
    private ConstraintToMessageConverter impl;

    @BeforeEach
    public void setMeUp()
    {
        impl = new ConstraintToMessageConverter();
    }


    @Test
    public void getMessage_returns(@Mocked ConstraintViolation constraint,
                                   @Mocked Path path,
                                   @Mocked Path.Node node)
    {
        new Expectations(){{
            // Path is tricky because it only exposes an iterator
            // And the elemnts iterated over are also interfaces.
            // so Mocked the item in the list also.

            List<Path.Node> nodes = new ArrayList<>();
            nodes.add(node);

            node.getName(); result = "surname";

            path.iterator(); result = nodes.iterator();

            constraint.getPropertyPath(); result = path;
            constraint.getRootBeanClass(); result = Patient.class;
            constraint.getMessageTemplate(); result = "javax.validation.constraints.Size.message";
        }};

        Message message = impl.getMessage(constraint);
        assertThat(message, is(notNullValue()));
    }
}
