package Fridge_Chef.team.user.service;


import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByUserId(AuthenticatedUser userId) {
        return userRepository.findByUserId_Value(userId.userId().getValue());
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUserId(String uudid) {
        return userRepository.findByUserId_Value(UUID.fromString(uudid));
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EMAIL));
    }

    @Transactional
    public void accountDelete(UserId userId, String username) {
        User user = userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (user.getIsDelete() != null && user.getIsDelete().bool()) {
            throw new ApiException(ErrorCode.USER_ACCOUNT_DELETE);
        }
        if (!user.getProfile().getUsername().equals(username)) {
            throw new ApiException(ErrorCode.USER_ACCOUNT_DELETE_NAME_INCORRECT);
        }
        user.accountDelete(true);
    }

    @Transactional(readOnly = true)
    public User findByUser(UserId userId) {
        return findByUserId(userId.getValue().toString())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}