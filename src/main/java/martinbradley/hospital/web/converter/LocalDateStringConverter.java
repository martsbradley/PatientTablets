package martinbradley.hospital.web.converter;

import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.ConverterException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.faces.application.FacesMessage;

@FacesConverter("stringDateConverter")
public class LocalDateStringConverter implements Converter
{
    private static final Logger logger = LoggerFactory.getLogger(LocalDateConverter.class);
    @Override
    public Object getAsObject(FacesContext context, 
                              UIComponent component, 
                              String value)
    {
        LocalDate date = null;
        return date;
    }
    
    private DateTimeFormatter getFormatter()
    {
        return DateTimeFormatter.ofPattern("E d MMM yyyy");
    }

    @Override
    public String getAsString(FacesContext context,
                              UIComponent component, 
                              Object value)
    {
        String result = "undefined date.";
        try{
            LocalDate date = (LocalDate)value;
            DateTimeFormatter dft = getFormatter();

            result = date.format(dft);
            logger.warn("Sending date as " + result);
        }
        catch(Exception e)
        {
            logger.warn("getAsString failed to convert " + value);
        }
        return result;
    }
}
