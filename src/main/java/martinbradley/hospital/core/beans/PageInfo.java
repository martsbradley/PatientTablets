package martinbradley.hospital.core.beans;

public class PageInfo
{
    private PageInfo(int startingAt,
                     int maxPerPage,
                     String sortField,
                     boolean isAscending,
                     String filter)
    {
        this.startingAt = startingAt;
        this.maxPerPage = maxPerPage;
        this.sortField = sortField;
        this.isAscending = isAscending;
        this.filter = filter;
    }

    private final int startingAt;
    private final int maxPerPage;
    private final String sortField;
    private final boolean isAscending;
    private final String filter;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("PageInfo( start:");
        sb.append(startingAt);
        sb.append(" maxPerPage: ");
        sb.append(maxPerPage);
        sb.append(" sortField: ");
        sb.append(sortField);
        sb.append(" isAscending: ");
        sb.append(isAscending);
        sb.append(" filter: ");
        sb.append(filter);
        return sb.toString();
    }

    public int getStartingAt()
    {
        return startingAt;
    }
    public int getMaxPerPage()
    {
        return maxPerPage;
    }
    public String getSortField()
    {
        return sortField;
    }

    public boolean isAscending()
    {
        return isAscending;
    }

    public String getFilter()
    {
        return filter;
    }

    public static class PageInfoBuilder{
        private int startingAt = 0;
        private int maxPerPage = 5;
        private String sortField = "";
        private boolean isAscending = false;
        private String filter = "";

        public PageInfo build()
        {
            PageInfo pg = new PageInfo(startingAt,
                                       maxPerPage,
                                       sortField,
                                       isAscending,
                                       filter);
            return pg;
        }

        public PageInfoBuilder setStartAt(int startingAt)
        {
            this.startingAt = startingAt;
            return this;
        }
        public PageInfoBuilder setMaxPerPage(int maxPerPage)
        {
            this.maxPerPage = maxPerPage;
            return this;
        }
        public PageInfoBuilder setSortField(String sortField)
        {
            this.sortField = sortField;
            return this;
        }
        public PageInfoBuilder setIsAscending(boolean isAscending)
        {
            this.isAscending = isAscending;
            return this;
        }
        public PageInfoBuilder setFilter(String filter)
        {
            this.filter = filter;
            return this;
        }
    }
}
