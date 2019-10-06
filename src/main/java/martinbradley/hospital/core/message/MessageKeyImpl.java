package martinbradley.hospital.core.message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import martinbradley.hospital.core.message.MessageKey;

public enum MessageKeyImpl implements MessageKey
{
    PATIENT_NAME_DUPLICATE("PatientNameDuplicate"),
    OPTIMISTIC_LOCK_EXCEPTION("OptimisticLockException");

    private final String propertyPath;

    private MessageKeyImpl(String propertyPath)
    {
        this.propertyPath = propertyPath;
    }

    @Override
    public String getKey()
    {
        return propertyPath;
    }
}
