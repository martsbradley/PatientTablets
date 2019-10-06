package martinbradley.hospital.web.api;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import martinbradley.hospital.core.api.MedicineBroker;
import martinbradley.hospital.core.domain.Medicine;
import martinbradley.hospital.core.beans.MedicineBean;
import martinbradley.hospital.mappers.MedicineBeanMapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import martinbradley.hospital.core.beans.PageInfo;

@ApplicationScoped
@Named
public class MedicineHandler
{
    private static final Logger logger = LoggerFactory.getLogger(MedicineHandler.class);
    final MedicineBeanMapper mapper = Mappers.getMapper(MedicineBeanMapper.class);

    @Inject MedicineBroker medBroker;

    public List<MedicineBean> pageMedicines(PageInfo aPageInfo)
    {
        List<Medicine> list = medBroker.getMedicinesPaged(aPageInfo);
        List<MedicineBean> beans  = convert(list);

        return beans;
    }

    public int getTotalMedicines(String filter)
    {
        return medBroker.getTotalMedicines(filter);
    }

    private List<MedicineBean> convert(List<Medicine> list)
    {
        ArrayList<MedicineBean> beans  = new ArrayList<>();
        for (Medicine p: list)
        {
            MedicineBean bean = mapper.domainToBean(p);
            beans.add(bean);
        }
        return beans;
    }
    public MedicineBean loadById(long id)
    {
        Medicine medicine = medBroker.loadById(id);
        logger.info(String.format("loadById(%d) returned %s",id, medicine));
        MedicineBean bean = mapper.domainToBean(medicine);
        return bean;
    }
}
