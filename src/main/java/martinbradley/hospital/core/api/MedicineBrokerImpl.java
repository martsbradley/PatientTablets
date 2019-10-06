package martinbradley.hospital.core.api;

import java.util.List;
import javax.inject.Inject;
import martinbradley.hospital.core.domain.Medicine;
import martinbradley.hospital.persistence.repository.MedicineDBRepo;

import javax.enterprise.inject.Model;

import martinbradley.hospital.core.beans.PageInfo;

@Model
public class MedicineBrokerImpl implements MedicineBroker
{
    @Inject MedicineDBRepo repo;

    @Override
    public List<Medicine> getMedicinesPaged(PageInfo pageInfo)
    {
        Medicine.SortOrder ordering = Medicine.SortOrder.find(pageInfo.getSortField(), 
                                                              pageInfo.isAscending());

        List<Medicine> meds = repo.pageMedicines(pageInfo.getStartingAt(), 
                                                 pageInfo.getMaxPerPage(), 
                                                 ordering,
                                                  pageInfo.getFilter());
        return meds;
    }

    public int getTotalMedicines(String filter)
    {
        return repo.getTotalMedicines(filter);
    }

    @Override
    public Medicine loadById(long id) {
        Medicine med = repo.findById(id);
        return med;
    }
}
