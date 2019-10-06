package martinbradley.hospital.core.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import mockit.*;
import martinbradley.hospital.persistence.repository.MedicineDBRepo;
import martinbradley.hospital.core.domain.Medicine;
import martinbradley.hospital.core.beans.PageInfo;
import static martinbradley.hospital.core.beans.PageInfo.PageInfoBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.CoreMatchers.not;

public class MedicineBrokerImplTest
{
    @Mocked MedicineDBRepo mockRepo;
    private MedicineBrokerImpl impl= new MedicineBrokerImpl();

    @BeforeEach
    public void setMeUp()
    {
        impl.repo = mockRepo;
    }

    @Test
    public void medicinesPagedNotNull()
    {
        PageInfoBuilder builder = new PageInfoBuilder();
        builder.setStartAt(10);
        PageInfo info = builder.build();
        assertNotNull(impl.getMedicinesPaged(info));
    }

    private void setupLoadById(final Medicine med) {
        new Expectations(){{
            mockRepo.findById(anyLong); 
            result = med;
        }};
    }

    @Test
    public void loadByIdNotFound() {
        setupLoadById(null);

        Medicine dto = impl.loadById(10);

        assertThat(dto, is(nullValue()));
    }

    @Test
    public void loadByIdFound() {
        setupLoadById(new Medicine());

        Medicine dto = impl.loadById(10);

        assertThat(dto, is(notNullValue()));
    }

  //@Test
  //public void medicines_search_calls_repo()
  //{
  //    impl.searchMedicine("");
  //    new Verifications(){{
  //        mockRepo.searchMedicine(anyString); times = 1;
  //    }};
  //}
  //
}
