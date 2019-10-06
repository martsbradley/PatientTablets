package martinbradley.hospital.web.beans;

import martinbradley.hospital.core.beans.PageInfo;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static martinbradley.hospital.core.beans.PageInfo.PageInfoBuilder;

public class PageInfoTest
{
    @Test
    public void start_at()
    {
        PageInfoBuilder builder = new PageInfoBuilder();
        builder.setStartAt(10);
        PageInfo info = builder.build();
        assertThat(info.getStartingAt(), is(10));
    }
    @Test
    public void max_per_page()
    {
        PageInfoBuilder builder = new PageInfoBuilder();
        builder.setMaxPerPage(20);
        PageInfo info = builder.build();
        assertThat(info.getMaxPerPage(), is(20));
    }
    @Test
    public void sortField()
    {
        PageInfoBuilder builder = new PageInfoBuilder();
        builder.setSortField("forename");
        PageInfo info = builder.build();
        assertThat(info.getSortField(), is("forename"));
    }
    @Test
    public void isAscending()
    {
        PageInfoBuilder builder = new PageInfoBuilder();

        builder.setIsAscending(true);
        builder.setIsAscending(false);// builder change his mind prior to building.
        builder.setIsAscending(true); 
        PageInfo info = builder.build();
        assertThat(info.isAscending(), is(true));
    }
    @Test
    public void filter_correct()
    {
        PageInfoBuilder builder = new PageInfoBuilder();

        builder.setFilter("marty");

        PageInfo info = builder.build();
        assertThat(info.getFilter(), is("marty"));
    }
    @Test
    public void complete_check()
    {
        PageInfoBuilder builder = new PageInfoBuilder();

        builder.setStartAt(10);
        builder.setMaxPerPage(20);
        builder.setSortField("forename");
        builder.setIsAscending(true);
        builder.setFilter("marty");
        builder.setSortField("forename");

        PageInfo info = builder.build();
        assertThat(info.getStartingAt(), is(10));
        assertThat(info.getMaxPerPage(), is(20));
        assertThat(info.getFilter(), is("marty"));
        assertThat(info.isAscending(), is(true));
    }
}
