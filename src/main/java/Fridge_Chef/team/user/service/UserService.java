package Fridge_Chef.team.user.service;


import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.comment.repository.CommentRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import Fridge_Chef.team.user.rest.request.UserProfileNameUpdateRequest;
import Fridge_Chef.team.user.rest.response.UserProfileMyPageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public Optional<User> findByUserId(AuthenticatedUser userId) {
        return userRepository.findByUserId(userId.userId());
    }

    @Transactional
    public void accountDelete(UserId userId, String username) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (user.getDeleteStatus() != null && user.getDeleteStatus().bool()) {
            throw new ApiException(ErrorCode.USER_ACCOUNT_DELETE);
        }

        if (!user.getProfile().getUsername().equals(username)) {
            throw new ApiException(ErrorCode.USER_ACCOUNT_DELETE_NAME_INCORRECT);
        }
        user.accountDelete(true);
    }

    @Transactional(readOnly = true)
    public User findByUser(UserId userId) {
        User user =  findByUserId(userId);
        user.getImageLink();
        user.getProfile().getUsername();
        return user;
    }

    @Transactional
    public void updateUserProfilePicture(UserId userId, Image picture) {
        findByUserId(userId).updatePicture(picture);
    }

    @Transactional
    public void updateUserProfileUsername(UserId userId, UserProfileNameUpdateRequest request) {
        findByUserId(userId).updateUsername(request.username());
    }

    @Transactional(readOnly = true)
    public UserProfileMyPageResponse findByMyPage(UserId userId) {
        List<Board> boards = boardRepository.findByUserId(userId).orElse(List.of());
        List<Comment> comments = commentRepository.findByUsers(findByUserId(userId)).orElse(List.of());
        return new UserProfileMyPageResponse(boards.size(),comments.size());
    }

    private User findByUserId(UserId userId) {
        return userRepository.findByUserId(userId)
                .filter(user -> !user.getDeleteStatus().bool())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}