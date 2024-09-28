package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardDslRepository;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.ContextRepository;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardDslRepository boardDslRepository;
    private final BoardRepository boardRepository;
    private final ContextRepository contextRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public BoardMyRecipeResponse findMyRecipeId(Long boardId) {
        Board board = findById(boardId);
        return BoardMyRecipeResponse.of(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardMyRecipePageResponse> findMyRecipes(BoardPageRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize());

        return boardDslRepository.findByPageUsers(pageRequest, request);
    }

    @Transactional
    public void delete(UserId userId, Long boardId) {
        Board board = findByUserIdAndBoardId(userId, boardId);

        Context context = board.getContext();
        List<Description> descriptions = context.getDescriptions();

        if (!board.isMainImageEmpty()) {
            imageService.imageRemove(userId, board.getMainImage().getId());
        }
        descriptions.forEach(description -> {
            if (!description.isImageEmpty()) {
                imageService.imageRemove(userId, description.getImage().getId());
            }
        });
        contextRepository.delete(context);
        boardRepository.delete(board);
    }


    @Transactional
    public void counting(Long boardId) {
        Board board = findById(boardId);
        board.updateCount();
    }

    @Transactional
    public void updateUserHit(UserId userId, Long boardId) {
        Board board = findById(boardId);
        User user = userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        getUserEvent(user, board).hitUp();

        int hit = board.getBoardUserEvent()
                .stream()
                .mapToInt(BoardUserEvent::getHit)
                .sum();

        board.updateHit(hit);
    }

    private Board findByUserIdAndBoardId(UserId userId, Long boardId) {
        Board board = findById(boardId);
        if (!board.getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.BOARD_NOT_USER_CREATE);
        }
        return board;
    }

    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));
    }

    private BoardUserEvent getUserEvent(User user, Board board) {
        return board.getBoardUserEvent().stream()
                .filter(events -> events.getUser() != null && events.getUser().getUserId().equals(user.getUserId()))
                .findAny()
                .orElse(new BoardUserEvent(board, user));
    }
}
