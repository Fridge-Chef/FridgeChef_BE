package Fridge_Chef.team.user.service;


import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public void accountDelete(UserId userId, String username) {
        User user = findByUserId(userId);
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
        return findByUserId(userId);
    }

    @Transactional
    public void updateUserProfilePicture(UserId userId, Image picture) {
        findByUserId(userId).updatePicture(picture);
    }

    @Transactional(readOnly = true)
    public User findByUserId(UserId userId) {
        return findByUserId(userId.getValue().toString())
                .filter(user -> !user.getIsDelete().bool())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}