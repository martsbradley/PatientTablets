package martinbradley.hospital.core.api;

import java.util.List;
import martinbradley.hospital.core.domain.Medicine;
import martinbradley.hospital.core.beans.PageInfo;

public interface MedicineBroker
{
    List<Medicine> getMedicinesPaged(PageInfo pageInfo);
    int getTotalMedicines(String filter);

    Medicine loadById(long id);
}
