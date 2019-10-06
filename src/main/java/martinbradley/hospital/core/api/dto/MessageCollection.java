package martinbradley.hospital.core.api.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class MessageCollection implements Iterable<Message>
{
    private List<Message> list = new ArrayList<>();

    public MessageCollection()
    {
    }

    public void add(Message message) {
        if (message == null) {
            throw new NullPointerException("");
        }

        list.add(message);
    }

    public Iterator<Message> getIterator()
    {
        return list.iterator();
    }

    public boolean hasMessages()
    {
        return !list.isEmpty();
    }
    public Iterator<Message> iterator() {
        return list.iterator();
    }

    public void addAll(MessageCollection aMessages)
    {
        this.list.addAll(aMessages.list);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Message message : list) {
            sb.append("Message :");
            sb.append(message);
            sb.append("\n");
        }
        return sb.toString();
    }
}
