package Fridge_Chef.team.common;

import Fridge_Chef.team.cert.repository.CertRepository;
import Fridge_Chef.team.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class ServiceLayerTest {
    @Mock
    protected UserRepository userRepository;
    @Mock
    protected CertRepository repository;
    @Mock
    protected PasswordEncoder passwordEncoder;

}
