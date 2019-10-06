package martinbradley.hospital.web.converter;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import mockit.*;
import martinbradley.hospital.persistence.repository.MedicineDBRepo;
import martinbradley.hospital.core.domain.Medicine;
import java.util.Arrays;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocalDateConverterTest
{
    private static final Logger logger = LoggerFactory.getLogger(LocalDateConverterTest.class);
    @Tested LocalDateConverter impl;
    @Injectable FacesContext context;
    @Injectable UIComponent component;

    @Test
    public void getAsObject()
    {
        LocalDate today = LocalDate.of(2018,6,9);

        String result = impl.getAsString(context, component,today);
        logger.debug("Result " + result);
        assertThat("2018-06-09", is(result));
    }
}
