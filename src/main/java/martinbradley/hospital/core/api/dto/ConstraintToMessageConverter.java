package martinbradley.hospital.core.api.dto;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import javax.validation.constraints.*;
import java.util.Set;
import javax.validation.constraints.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.List;
import java.util.ArrayList;
import martinbradley.hospital.message.ConstraintMessage;

public class ConstraintToMessageConverter
{
    private static final Logger logger = LoggerFactory.getLogger(ConstraintToMessageConverter.class);
    List<ConstraintMessage> messageInfos = new ArrayList<>();

    public ConstraintToMessageConverter()
    {
        messageInfos.add(ConstraintMessage.PATIENT_SURNAME_SIZE);
        messageInfos.add(ConstraintMessage.PATIENT_DOB_NOT_NULL);
    }

    public Message getMessage(ConstraintViolation constraint)
    {
        for (ConstraintMessage message: messageInfos)
        {
            if (message.matches(constraint))
            {
                return new Message(message, new ArrayList<String>());
            }
        }
        
        return null;
    }
}
