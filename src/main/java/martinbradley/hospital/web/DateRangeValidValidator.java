package martinbradley.hospital.web;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

public class DateRangeValidValidator implements ConstraintValidator<DateRangeValid, LocalDateRange>
{
    private static final Logger logger = LoggerFactory.getLogger(DateRangeValidValidator.class);

    @Override
    public void initialize(final DateRangeValid constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(final LocalDateRange range, final ConstraintValidatorContext context)
    {
        logger.info("Running the validation");
        LocalDate startDate = range.getStartDate();
        LocalDate endDate = range.getEndDate();

        if (startDate == null || endDate == null)
        {
            logger.warn("a date is null");
            return false;
        }

        boolean isBefore  = startDate.isBefore(endDate);
        logger.info(startDate + " isBefore " + endDate + " = " + isBefore);
        return isBefore;
    }
}
