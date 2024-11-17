package Fridge_Chef.team.comment.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.comment.domain.CommentUserEvent;
import Fridge_Chef.team.comment.repository.CommentRepository;
import Fridge_Chef.team.comment.repository.CommentUserEventRepository;
import Fridge_Chef.team.comment.rest.request.CommentCreateRequest;
import Fridge_Chef.team.comment.rest.request.CommentUpdateRequest;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final CommentUserEventRepository commentUserEventRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;


    @Transactional
    public Comment addComment(Long boardId, UserId userId, CommentCreateRequest request) {
        log.info("댓글 등록 board:" + boardId + " , userid:" + userId + ", message :" + request.comment());

        Board board = findByBoard(boardId);
        User user = findByUser(userId);
        List<Image> images = request.images() != null ? imageService.imageUploads(userId, request.images()) : new ArrayList<>();

        Optional<Comment> existingComment = board.getComments()
                .stream()
                .filter(comment -> comment.getUsers().getUserId().equals(userId))
                .findFirst();

        if (existingComment.isPresent()) {
            Comment commentToUpdate = existingComment.get();
            commentToUpdate.updateImage(images);
            commentToUpdate.updateComment(request.comment());
            commentToUpdate.updateStar(request.star());
            return commentRepository.save(commentToUpdate);
        }

        Comment newComment = new Comment(board, user, images, request.comment(), request.star());
        board.updateStar(calculateNewTotalStar(board, request.star()));
        return commentRepository.save(newComment);

    }

    @Transactional
    public Comment updateComment(Long boardId, Long commentId, UserId userId, CommentUpdateRequest request) {
        log.info("댓글 수정    user :"+userId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));

        validCommentAuthor(comment, boardId);
        validCommentUserAuthor(comment, userId);

        comment.updateStar(request.star());
        comment.updateComment(request.comment());

        if(!request.isImage()){
            return comment;
        }

        for(Image image : comment.getCommentImage()){
            imageService.imageRemove(userId,image.getId());
            imageRepository.delete(image);
        }

        comment.removeImage();

        if(request.images() == null){
            return comment;
        }

        List<Image> images = new ArrayList<>();
        request.images().forEach(image -> images.add(imageService.imageUpload(userId, image)));
        comment.updateComments(images);
        log.info("댓글 수정 성공     user: "+userId);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long boardId, Long commentId, UserId userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
        Board board = comment.getBoard();

        validCommentAuthor(comment, boardId);
        validCommentUserAuthor(comment, userId);

        board.updateStar(calculateNewTotalStar(board, -comment.getStar()));
        commentRepository.delete(comment);
        log.info("댓글 삭제 성공 - board id :" + boardId +" , comment id :"+commentId +" , user id :"+userId);
    }


    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByBoards(Long boardId, int page, int size, Optional<UserId> user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        PageRequest pageable = PageRequest.of(page, size);
        List<Comment> comments = commentRepository.findAllByBoard(board);

        user.flatMap(userId -> comments.stream()
                        .filter(comment -> comment.getUsers().getUserId().equals(userId))
                        .findFirst())
                .ifPresent(userComment -> {
                    comments.remove(userComment);
                    comments.add(0, userComment);
                });

        List<CommentResponse> responses = comments.stream()
                .map(entity -> CommentResponse.fromEntity(entity, user))
                .toList();

        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentsByBoard(Long boardId, Long commentId, Optional<UserId> user) {
        boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
        return CommentResponse.fromEntity(comment, user);
    }

    @Transactional
    public int updateHit(Long boardId, Long commentId, UserId userId) {
        Board board = findByBoard(boardId);
        Comment comment = findComment(commentId);

        Optional<CommentUserEvent> event = commentUserEventRepository.findByBoardIdAndCommentsIdAndUserUserId(boardId, commentId, userId);
        event.ifPresent(CommentUserEvent::updateHit);

        if (event.isEmpty()) {
            var userEvent = new CommentUserEvent(board, comment, findByUser(userId));
            userEvent.updateHit();

            var commentUserEvent = commentUserEventRepository.save(userEvent);
            comment.addUserEvent(commentUserEvent);
            comment.updateHit(filterTotalHit((commentUserEvent.getComments())));
            log.info("댓글 처음 좋아요 " + commentId + " , 카운트 " + commentUserEvent.getHit());
        } else {
            comment.updateHit(filterTotalHit(event.get().getComments()));
            log.info("댓글 좋아요 " + commentId + " ,카운트 " + event.get().getHit());
        }

        return comment.getTotalHit();
    }

    private int filterTotalHit(Comment comment) {
        return comment.getCommentUserEvent()
                .stream()
                .filter(CommentUserEvent::isHitOn)
                .toList()
                .size();
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
    }

    private double calculateNewTotalStar(Board board, double newStar) {
        List<Comment> comments = commentRepository.findAllByBoard(board);
        if (comments.size() == 0) {
            return newStar;
        }
        double totalStar = comments.stream().mapToDouble(Comment::getStar).sum();
        return (totalStar + newStar) / (comments.size() + 1);
    }

    private Board findByBoard(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));
    }

    private User findByUser(UserId userId) {
        return userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    private void validCommentAuthor(Comment comment, Long boardId) {
        if (!comment.getBoard().getId().equals(boardId)) {
            throw new ApiException(ErrorCode.COMMENT_NOT_BOARD);
        }
    }

    private void validCommentUserAuthor(Comment comment, UserId userId) {
        if (!comment.getUsers().getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.COMMENT_NOT_USER_AUTHOR);
        }
    }
}
