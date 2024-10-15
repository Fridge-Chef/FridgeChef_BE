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
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentUserEventRepository commentUserEventRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;


    @Transactional
    public Comment addComment(Long boardId, UserId userId, CommentCreateRequest request) {
        Board board = findByBoard(boardId);
        User user = findByUser(userId);
        List<Image> images = imageService.imageUploads(userId,request.images());

        Optional<Comment> existingComment = board.getComments()
                .stream()
                .filter(comment -> comment.getUser().getUserId().equals(userId))
                .findFirst();

        if (existingComment.isPresent()) {
            Comment commentToUpdate = existingComment.get();
            commentToUpdate.updateComment(request.comment());
            commentToUpdate.updateStar(request.star());
            return commentRepository.save(commentToUpdate);
        } else {
            Comment newComment = new Comment(board, user, images, request.comment(), request.star());
            board.updateStar(calculateNewTotalStar(board, request.star()));
            return commentRepository.save(newComment);
        }
    }

    @Transactional
    public Comment updateComment(Long boardId, Long commentId, UserId userId, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
        Board board = comment.getBoard();

        validCommentBoardAuthor(boardId, comment, board);
        validCommentUserAuthor(comment, userId);

        comment.updateStar(request.star());
        comment.updateComment(request.comment());
        if(request.isImage()){
            List<Image> images = new ArrayList<>();
            request.image().forEach(image -> images.add( imageService.imageUpload(userId,image)));
            comment.updateComments(images);
        }
        commentRepository.save(comment);

        return comment;
    }

    @Transactional
    public void deleteComment(Long boardId, Long commentId, UserId userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
        Board board = comment.getBoard();

        validCommentBoardAuthor(boardId, comment, board);
        validCommentUserAuthor(comment, userId);

        board.updateStar(calculateNewTotalStar(board, -comment.getStar()));
        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));
        return commentRepository.findAllByBoard(board)
                .stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByBoard(Long boardId, Optional<AuthenticatedUser> user) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        List<Comment> comments = commentRepository.findAllByBoard(board);

        user.map(AuthenticatedUser::userId).flatMap(userId -> comments.stream()
                .filter(comment -> comment.getUser().getUserId().equals(userId))
                .findFirst()).ifPresent(userComment -> {
            comments.remove(userComment);
            comments.add(0, userComment);
        });

        return comments.stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CommentResponse getCommentsByBoard(Long boardId, Long commentId) {
        Comment comment =  commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_FOUND));
        if(!comment.getBoard().getId().equals(boardId)){
            throw new ApiException(ErrorCode.COMMENT_NOT_BOARD);
        }
        return CommentResponse.fromEntity(comment);
    }


    @Transactional
    public void updateHit(Long boardId, Long commentId, UserId userId) {
        Optional<CommentUserEvent> event = commentUserEventRepository.findByBoardIdAndCommentsIdAndUserUserId(boardId,commentId,userId);
        event.ifPresent(CommentUserEvent::updateHit);
        if(event.isEmpty()){
            Board board =findByBoard(boardId);
            Comment comment = findComment(commentId);
            validCommentBoardAuthor(boardId,comment,board);
            User user = findByUser(userId);
            var userEvent = new CommentUserEvent(board,comment,user);
            userEvent.updateHit();
            int sum = comment.getCommentUserEvent()
                    .stream()
                    .mapToInt(CommentUserEvent::getHit)
                    .sum();
            comment.updateHit(sum);
        }
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

    private void validCommentBoardAuthor(Long boardId, Comment comment, Board board) {
        if (!boardId.equals(comment.getBoard().getId())) {
            throw new ApiException(ErrorCode.COMMENT_NOT_BOARD);
        }
        if (!comment.getBoard().getId().equals(board.getId())) {
            throw new ApiException(ErrorCode.COMMENT_NOT_BOARD);
        }
    }

    private void validCommentUserAuthor(Comment comment, UserId userId) {
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.COMMENT_NOT_USER_AUTHOR);
        }
    }

}
