package martinbradley.hospital.core.api.dto;

import martinbradley.hospital.core.domain.Sex;

public class SexMapper {

    public String asString(Sex sex) {
        String result = "M";
        if (sex == Sex.Female) result = "F";

	return result;
    }

    public Sex asSex(String sex) 
    {
        Sex result = null;
	if (sex.equalsIgnoreCase("M"))
	{
	    result = Sex.Male;
	}
        else if (sex.equalsIgnoreCase("F"))
	{
	    result = Sex.Female;
	}
	return result;
    }
}
