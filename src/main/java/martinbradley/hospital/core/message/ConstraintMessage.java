package martinbradley.hospital.message;
import martinbradley.hospital.core.domain.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import martinbradley.hospital.core.message.MessageKey;
import javax.validation.ConstraintViolation;
import javax.validation.Path;

public enum ConstraintMessage implements MessageKey
{
    PATIENT_SURNAME_SIZE(Patient.class, "surname", "javax.validation.constraints.Size.message"),
    PATIENT_DOB_NOT_NULL(Patient.class, "dob", "javax.validation.constraints.NotNull.message");

    private static final Logger logger = LoggerFactory.getLogger(ConstraintMessage.class);
    private final Class clazz;
    private final String propertyPath;
    private final String messageTemplate;

    private ConstraintMessage(Class  clazz,
                              String propertyPath, 
                              String messageTemplate)
    {
        if (clazz           == null ||
            propertyPath    == null ||
            messageTemplate == null)
        {
            throw new IllegalArgumentException();
        }
        this.clazz = clazz;
        this.propertyPath = propertyPath;
        this.messageTemplate = messageTemplate;
    }

    /**
     * getRootBeanClass/getPropertyPath/getMessageTemplate
     *
     * These methods are needed to convert from JSR303 Validation objects
     * to the key needed in a messages property file for presentation to the user.
     */
    public Class getRootBeanClass()
    {
        return clazz;
    }
    public String getPropertyPath()
    {
        return this.propertyPath;
    }
    public String getMessageTemplate()
    {
        return this.messageTemplate;
    }

    @Override
    public String getKey()
    {
        String fullKey = this.propertyPath;
        if (!this.messageTemplate.isEmpty())
        {
            fullKey = this.propertyPath + "." + this.messageTemplate;
        }
        return fullKey;
    }

    private void logConstraint(ConstraintViolation constraint)
    {
        final String path = stringFromPath(constraint.getPropertyPath());

        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintViolation [\n\t");
        sb.append("class (");
        sb.append(constraint.getRootBeanClass().toString()); sb.append(") \n\t");
        sb.append("path (");
        sb.append(path); sb.append(") \n\t");
        sb.append("template (");
        sb.append(constraint.getMessageTemplate()); sb.append(")]");
        logger.debug(sb.toString());
    }

    public boolean matches(ConstraintViolation constraint)
    {
        final String path = stringFromPath(constraint.getPropertyPath());

        boolean rootMatches     = clazz.equals(constraint.getRootBeanClass());
        boolean pathMatches     = propertyPath.equals(path);
        boolean templateMatches = constraint.getMessageTemplate().contains(messageTemplate);
        
        logConstraint(constraint);
        logger.debug(this.toString());

        logger.debug(String.format("matches?\n\troot %s\n\tpath %s\n\ttemplate %s)", 
                                                       rootMatches,
                                                       pathMatches,
                                                       templateMatches));

        //logger.debug(String.format("%s %s", messageTemplate, constraint.getMessageTemplate()));

        boolean matches = rootMatches && pathMatches && templateMatches;

        return matches;
    }

    private String stringFromPath(Path pathToTraversableObject) 
    {
        StringBuilder path = new StringBuilder();
        for (Path.Node node : pathToTraversableObject) 
        {
            if (node.getName() != null) 
            {
                path.append(node.getName());
            }
        }
        return path.toString();
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("ConstraintMessage [");
        sb.append("class (");
        sb.append(clazz.toString()); sb.append(") ");
        sb.append("path (");
        sb.append(propertyPath); sb.append(") ");
        sb.append("template (");
        sb.append(messageTemplate); sb.append(")]");
        return sb.toString();
    }
}
