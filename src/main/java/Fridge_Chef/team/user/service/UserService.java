package Fridge_Chef.team.user.service;


import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import Fridge_Chef.team.user.rest.request.UserProfileNameUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByUserId(AuthenticatedUser userId) {
        return userRepository.findByUserId(userId.userId());
    }

    @Transactional
    public void accountDelete(UserId userId, String username) {
        User user = findByUserId(userId);
        if (user.getDelete() != null && user.getDelete().bool()) {
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


    @Transactional
    public void updateUserProfileUsername(UserId userId, UserProfileNameUpdateRequest request) {
        findByUserId(userId).updateUsername(request.username());
    }

    private User findByUserId(UserId userId) {
        return userRepository.findByUserId(userId)
                .filter(user -> !user.getDelete().bool())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}