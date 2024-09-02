package Fridge_Chef.team.user.service;


import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void signup(String password, String id, String name, String email) {
        String encodePassword = passwordEncoder.encode(password);

        User user = new User(id, name, encodePassword, email, Role.USER);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean isIdCheck(String id) {
        if (userRepository.findByUserId(id).isPresent()) {
            throw new ApiException(ErrorCode.USER_ID_DUPLICATE);
        }
        return true;
    }

    @Transactional(readOnly = true)
    public void validateMemberRegistration(String userid, String email) {
        if (userRepository.findByUserId(userid).isPresent()) {
            throw new ApiException(ErrorCode.USER_ID_DUPLICATE);
        }
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
    public User validEmail(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (user.getEmail() == null) {
            throw new ApiException(ErrorCode.USER_NOT_EMAIL);
        }
        return user;
    }


    @Transactional
    public void passwordChange(String userId, String password) {
        String encodePassword = passwordEncoder.encode(password);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.updatePassword(encodePassword);
    }

    @Transactional
    public void updateEmail(String userId, String send) {
        userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND))
                .updateEmail(send);
    }
}