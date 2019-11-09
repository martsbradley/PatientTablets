package martinbradley.hospital.web.api;

import martinbradley.hospital.core.api.AuthenticationBroker;
import martinbradley.hospital.core.api.MedicineBroker;
import martinbradley.hospital.core.beans.MedicineBean;
import martinbradley.hospital.core.beans.PageInfo;
import martinbradley.hospital.core.beans.UserPassword;
import martinbradley.hospital.core.domain.Medicine;
import martinbradley.hospital.mappers.MedicineBeanMapper;
import martinbradley.security.JWTString;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Named
public class AuthenticationHandler
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    @Inject AuthenticationBroker authBroker;

    public JWTString authenticate(UserPassword userPassword) throws AuthenticationException
    {
        return authBroker.authenticate(userPassword.getUsername(),
                                       userPassword.getPassword());
    }
}
