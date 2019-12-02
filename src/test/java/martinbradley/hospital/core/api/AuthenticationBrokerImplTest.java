package martinbradley.hospital.core.api;

import martinbradley.hospital.core.domain.password.AuthGroup;
import martinbradley.hospital.core.domain.password.Salt;
import martinbradley.hospital.persistence.repository.AuthUserGroupRepo;
import martinbradley.security.AuthenticationConstants;
import martinbradley.security.JsonWebToken;
import martinbradley.security.KeyStoreLoader;
import mockit.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class AuthenticationBrokerImplTest
{
    @Injectable AuthUserGroupRepo mockRepo;
    /*KeyStoreLoader keyStore = new KeyStoreLoader(AuthenticationConstants.AUTH_ISSUER.getValue(),
            AuthenticationConstants.AUTH_DOMAIN.getValue());*/

    @Tested AuthenticationBrokerImpl impl;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationBrokerImplTest.class);

    private void failedGetSalt() throws AuthenticationException {
        new Expectations(){{
            mockRepo.getUserSalt(anyString);
            result = new AuthenticationException();
        }};
    }

    public void foundUserSalt(final String aSalt) throws AuthenticationException {
        new Expectations() {{
            mockRepo.getUserSalt(anyString);
            result = new Salt("BAD change this");
        }};
    }

    private void passwordCheckFails() throws AuthenticationException {
        new Expectations() {{
            mockRepo.authenticate(anyString, anyString);
            result = new AuthenticationException();
        }};
    }

    private void passwordCheckPasses() throws AuthenticationException {
        new Expectations() {{
            mockRepo.authenticate(anyString, anyString);
            Set<AuthGroup> groups = new HashSet<>();
            AuthGroup group = new AuthGroup();
            group.setName("goodies");
            groups.add(group);
            result = groups;
        }};
    }

    @Test
    public void userSaltNotThrowsAuthenticationException() throws AuthenticationException {
        failedGetSalt();

        try {
            impl.authenticate("badUsername", "anything");
            fail("Should not reach here.");
        }catch (AuthenticationException e) {
        }

        new Verifications() {{
            mockRepo.authenticate(anyString, anyString);
            times = 0;
        }};
    }

  @Test
  public void userPasswordIncorrect() throws AuthenticationException {
      foundUserSalt("ABC");
      passwordCheckFails();
      try {
          impl.authenticate("badUsername", "anything");
          fail("Should not reach here.");
      } catch (AuthenticationException e) { }
  }

      @Test
      public void userPasswordCorrect() throws AuthenticationException {
          foundUserSalt("ABC");
          passwordCheckPasses();
          try {
              JsonWebToken str = impl.authenticate("badUsername", "anything");
              logger.info(str.toString());
          } catch (AuthenticationException e) {
              fail("Should not reach here.");
        }
    }


}
