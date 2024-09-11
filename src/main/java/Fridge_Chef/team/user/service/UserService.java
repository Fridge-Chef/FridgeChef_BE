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
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<User> findByUserId(AuthenticatedUser userId) {
        return userRepository.findByUserId_Value(userId.userId().getValue());
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUserId(String uudid) {
        return userRepository.findByUserId_Value(UUID.fromString(uudid));
    }

    @Transactional
    public User signup(String email, String password, String name) {
        return signup(email,password,name,Role.USER);
    }

    @Transactional
    public User signup(String email, String password, String name, Role role) {
        String encodePassword = passwordEncoder.encode(password);

        User user = User.create(email, encodePassword, name,role);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public void validateMemberRegistration(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ApiException(ErrorCode.SIGNUP_EMAIL_DUPLICATE);
        }
    }

    @Transactional(readOnly = true)
    public void checkEmailValidAndUnique(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(ErrorCode.USER_EMAIL_UNIQUE);
        }
    }

    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_EMAIL));
    }

    public void authenticate(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(ErrorCode.LOGIN_PASSWORD_INCORRECT);
        }
    }

    @Transactional
    public void accountDelete(UserId userId, String username) {
        User user = userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (user.getIsDelete() != null && user.getIsDelete().bool()) {
            throw new ApiException(ErrorCode.USER_ACCOUNT_DELETE);
        }
        if (user.getProfile().getUsername().equals(username)) {
            throw new ApiException(ErrorCode.USER_ACCOUNT_DELETE_NAME_INCORRECT);
        }
        user.accountDelete(true);
    }

    @Transactional
    public void updatePassword(UserId userId, String password, String newPassword) {
        User user = userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(ErrorCode.USER_PASSWORD_INPUT_FAIL);
        }
        if (password.equals(newPassword)) {
            throw new ApiException(ErrorCode.USER_NEW_PASSWORD_SAME_AS_OLD);
        }
        user.updatePassword(passwordEncoder.encode(newPassword));
    }
}