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

@FacesConverter("myLocalDateConverter")
public class LocalDateConverter implements Converter
{
    private static final Logger logger = LoggerFactory.getLogger(LocalDateConverter.class);
    @Override
    public Object getAsObject(FacesContext context, 
                              UIComponent component, 
                              String value)
    {
        logger.debug("getAsObject received " + value);
        LocalDate date = null;
        try {
            DateTimeFormatter dft = getFormatter();

            date = LocalDate.parse(value, dft);
            logger.debug(String.format("getAsObject date as '%s'", date));
        }
        catch (DateTimeParseException exc) {
            logger.warn("Failed to convert " + value);
            sendFacesMessage("Invalid Date");
        }
        return date;
    }
    
    private void sendFacesMessage(String aText)
    {
        FacesMessage message = new FacesMessage("invalid date,","invalid date");
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        logger.warn(aText);
        throw new ConverterException(message);
    }

    private DateTimeFormatter getFormatter()
    {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
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
            logger.debug(String.format("getAsString sending date as '%s'", result));
        }
        catch(Exception e)
        {
            logger.warn("getAsString failed to convert " + value);
        }
        return result;
    }
}
