package martinbradley.hospital.core.api.dto;

import martinbradley.hospital.core.domain.Patient;
import martinbradley.hospital.rest.ImageUploadedBean;
import martinbradley.hospital.mappers.UploadBeanMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import martinbradley.hospital.core.domain.UploadedImage;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class UploadedBeanMapperTest
{
    private static final Logger logger = LoggerFactory.getLogger(UploadedBeanMapperTest.class);
    @Test
    public void dto_maps_to_patient()
    {
        final UploadBeanMapper mapper = Mappers.getMapper(UploadBeanMapper.class);
        UploadedImage image = new UploadedImage();

	    LocalDateTime myDate = LocalDateTime.now();
        image.setName("martin");
        image.setBucket("bradley");
        image.setPatient(new Patient());
        image.setDescription("m");
	    image.setDateUploaded(myDate);
        byte[] array = new byte[1];
        image.setData(array);

        ImageUploadedBean bean = mapper.domainToBean(image);

        assertEquals("m",       bean.getDescription());
        assertEquals(1,      bean.getData().length);
    }
}