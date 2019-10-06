package martinbradley.hospital.core.api.dto;
import martinbradley.hospital.core.message.MessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Collections;

public class Message
{
    private final MessageKey message;
    private final List<String> args;

    public Message(MessageKey message)
    {
        this.message = message;
        this.args = Collections.emptyList();
    }

    public Message(MessageKey message, List<String> args)
    {
        this.message = message;
        this.args = args;
    }

    public String getKey()
    {
        return message.getKey();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(" MessageKey ");
        sb.append(message);

        for (String arg : args) {
            sb.append("Arg :");
            sb.append(arg);
            sb.append(": ");
        }
        return sb.toString();
    }

}
