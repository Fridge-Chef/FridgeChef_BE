package Fridge_Chef.team.common;

import Fridge_Chef.team.user.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ServiceLayerTest {
    @Mock
    protected UserRepository userRepository;
}
