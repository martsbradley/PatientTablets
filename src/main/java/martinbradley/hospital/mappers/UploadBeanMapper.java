
package martinbradley.hospital.mappers;

import martinbradley.hospital.core.domain.UploadedImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import martinbradley.hospital.rest.ImageUploadedBean;

@Mapper
public interface UploadBeanMapper
{
    ImageUploadedBean domainToBean(UploadedImage dto);

    UploadedImage beanToDomain(ImageUploadedBean bean);
}
