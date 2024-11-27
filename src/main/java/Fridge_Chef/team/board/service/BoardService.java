package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.*;
import Fridge_Chef.team.board.repository.BoardDslRepository;
import Fridge_Chef.team.board.repository.BoardHistoryRepository;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.domain.ImageType;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardDslRepository boardDslRepository;
    private final BoardRepository boardRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final BoardUserEventRepository boardUserEventRepository;
    private final BoardHistoryRepository boardHistoryRepository;

    @Transactional(readOnly = true)
    public BoardMyRecipeResponse findMyRecipeId(Long boardId) {
        Board board = findById(boardId);

        var todayStart = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        var todayEnd = todayStart.plusDays(1).minusNanos(1);
        BoardHistory todayHistory = board.getHistorys()
                .stream()
                .filter(history -> {
                    var createTime = history.getCreateTime();
                    return createTime.isAfter(todayStart) && createTime.isBefore(todayEnd);
                })
                .findFirst()
                .orElse(boardHistoryRepository.save(new BoardHistory(board, 0)));

        todayHistory.countUp();
        board.updateCount();
        return BoardMyRecipeResponse.of(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardMyRecipePageResponse> findMyRecipes(UserId userId, BoardPageRequest request) {
        log.info("레시피 페이징 조회  size :"+request.getSize() +", page :"+request.getPage()+", sort : "+request.getSortType()+" issue: "+request.getIssueType());
        if (request.getSize() > 50) {
            throw new ApiException(ErrorCode.VALID_SIZE_50);
        }
        var page = PageRequest.of(request.getPage(), request.getSize());
        return boardDslRepository.findByPageUsers(page, request, userId);
    }

    @Transactional
    public void delete(UserId userId, Long boardId) {
        Board board = findByUserIdAndBoardId(userId, boardId);
        Context context = board.getContext();

        List<Description> descriptions = context.getDescriptions();

        if (!board.isMainImageEmpty() && board.getMainImage().getType().equals(ImageType.ORACLE_CLOUD)) {
            imageService.imageRemove(userId, board.getMainImage().getId());
        }

        descriptions.stream()
                .map(Description::getImage)
                .filter(this::isOracleCloudImage)
                .forEach(imageService::imageRemove);

        if (!board.getComments().isEmpty()) {
            board.getComments().forEach(comment -> {
                comment.getCommentImage().stream()
                        .filter(this::isOracleCloudImage)
                        .forEach(imageService::imageRemove);
            });
            board.commentClear();
        }
        boardRepository.delete(board);
        log.info("삭제");
    }


    @Transactional
    public void counting(Long boardId) {
        Board board = findById(boardId);
        board.updateCount();
    }

    @Transactional
    public int updateUserHit(UserId userId, Long boardId) {
        Board board = findById(boardId);
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        var event = getUserEvent(user, board);
        event.hitUp();

        int total = filterTotalHit(board);

        board.updateHit(total);
        log.info("게시글 좋아요 :" + event.getHit() + ",총함 :" + total);
        return total;
    }

    private int filterTotalHit(Board board) {
        return board.getBoardUserEvent()
                .stream()
                .filter(v -> v.getHit() == 1)
                .toList()
                .size();
    }

    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));
    }

    private boolean isOracleCloudImage(Image image) {
        return image != null && image.getType() != null && ImageType.ORACLE_CLOUD.equals(image.getType());
    }

    private Board findByUserIdAndBoardId(UserId userId, Long boardId) {
        Board board = findById(boardId);
        if (!board.getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.BOARD_NOT_USER_CREATE);
        }
        return board;
    }

    private BoardUserEvent getUserEvent(User user, Board board) {
        Optional<BoardUserEvent> event = board.getBoardUserEvent().stream()
                .filter(events -> events.getUser() != null && events.getUser().getUserId().equals(user.getUserId()))
                .findAny();

        if (event.isEmpty()) {
            event = Optional.of(boardUserEventRepository.save(new BoardUserEvent(board, user)));
            board.addUserEvent(event.get());
        }
        return event.get();
    }
}
