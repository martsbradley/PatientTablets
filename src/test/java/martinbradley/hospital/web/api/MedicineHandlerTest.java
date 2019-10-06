package martinbradley.hospital.web.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import martinbradley.hospital.core.domain.Medicine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import mockit.*;
import java.util.Arrays;
import java.util.List;
import martinbradley.hospital.core.api.MedicineBroker;
import martinbradley.hospital.core.beans.MedicineBean;
import martinbradley.hospital.core.beans.PageInfo;
import static martinbradley.hospital.core.beans.PageInfo.PageInfoBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.CoreMatchers.not;

public class MedicineHandlerTest
{
    @Mocked MedicineBroker mockBroker;
    private MedicineHandler impl;

    @BeforeEach
    public void setMeUp() {
        impl = new MedicineHandler();
        impl.medBroker = mockBroker;
    }

    @Test
    public void notNull() {

        PageInfo pageInfo  = pageInfo = getPageInfo();
        assertNotNull(impl.pageMedicines(pageInfo));
    }

    private PageInfo getPageInfo() {
        PageInfo pageInfo;
        pageInfo = new PageInfoBuilder()
                         .setStartAt(0)
                         .setMaxPerPage(1)
                         .setSortField("")
                         .setIsAscending(false)
                         .build();
        return pageInfo;
    }

    @Test
    public void oneMedFound()
    {
        initMedicinesPagedMock(new Medicine());
        PageInfo pageInfo  = pageInfo = getPageInfo();
        assertEquals(1, impl.pageMedicines(pageInfo).size());
    }
    @Test
    public void threeMedsFound()
    {
        initMedicinesPagedMock(new Medicine(),
                               new Medicine(),
                               new Medicine());
        PageInfo pageInfo  = pageInfo = getPageInfo();
        assertEquals(3, impl.pageMedicines(pageInfo).size());
    }

    private void initMedicinesPagedMock(Medicine... meds)
    {
        final List<Medicine> myList = Arrays.asList(meds);
        new Expectations(){{
            mockBroker.getMedicinesPaged((PageInfo)any); 
            result = myList;
        }};
    }
    @Test
    public void correctDetails()
    {
        Medicine med = new Medicine();
        med.setId(44);
        med.setName("Socks");
        med.setManufacturer("Adria");
        initMedicinesPagedMock(med);

        PageInfo pageInfo  = pageInfo = getPageInfo();
        List<MedicineBean> beans = impl.pageMedicines(pageInfo);

        MedicineBean medBean = beans.get(0);
        assertEquals("Socks", medBean.getName());
        assertEquals("Adria", medBean.getManufacturer());
    }
  //public void searchMedicine_calls_broker()
  //{

  //    new Verifications(){{
  //        mockBroker.searchMedicine(anyString); times = 1;
  //    }};
  //}
  //
    private void setupLoadById(final Medicine dto) {
        new Expectations(){{
            mockBroker.loadById(anyLong); 
            result = dto;
        }};
    }
    @Test
    public void loadByNegativeIsNull() {

        setupLoadById(null);

        MedicineBean bean = impl.loadById(-1);

        assertThat(bean, is(nullValue()));
    }

    @Test
    public void loadByIdSuccess() {
        setupLoadById(new Medicine());
        MedicineBean bean = impl.loadById(10);
        assertThat(bean, is(notNullValue()));
    }

}
