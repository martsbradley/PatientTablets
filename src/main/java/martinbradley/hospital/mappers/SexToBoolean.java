package martinbradley.hospital.mappers;

import martinbradley.hospital.core.domain.Sex;

@SexTranslator
public class SexToBoolean
{
    public boolean isMaleFromSex(Sex sex)
    {
        return Sex.Male == sex;
    }

    public Sex toSexFromMale(boolean sex)
    {
        return sex ? Sex.Male: Sex.Female;
    }
}
