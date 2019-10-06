package martinbradley.hospital.core.message;

public interface MessageKey
{
    /**
     * This method is needed by the front end to match this 
     * message to a properties file message to present to the user.
     * @returns String
     */
    public String getKey();
}
