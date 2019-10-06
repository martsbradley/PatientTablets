package martinbradley.hospital.mappers;

import martinbradley.hospital.core.domain.Medicine;
import martinbradley.hospital.core.beans.MedicineBean;
import org.mapstruct.Mapper;

@Mapper
public interface MedicineBeanMapper
{
    MedicineBean domainToBean(Medicine medicine);

    Medicine beanToDomain(MedicineBean bean);
}
