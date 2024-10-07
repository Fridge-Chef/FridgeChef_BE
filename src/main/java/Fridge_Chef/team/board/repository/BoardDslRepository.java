package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardType;
import Fridge_Chef.team.board.repository.model.IssueType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import static Fridge_Chef.team.board.domain.QBoard.board;
import static Fridge_Chef.team.board.domain.QBoardIssue.boardIssue;

@Repository
@RequiredArgsConstructor
public class BoardDslRepository {
    private final JPAQueryFactory factory;

    public Page<BoardMyRecipePageResponse> findByPageUsers(PageRequest pageable, BoardPageRequest pageRequest) {
        JPAQuery<Board> query = createBaseQuery(pageable, pageRequest);

        applySort(query, pageRequest.getSortType());

        List<BoardMyRecipePageResponse> content = query.fetch().stream()
                .map(entity -> BoardMyRecipePageResponse.ofEntity(pageRequest.getSortType(), entity))
                .collect(Collectors.toList());

        return PageableExecutionUtils.getPage(content , pageable, () -> query.fetch().size());
    }

    private JPAQuery<Board> createBaseQuery(PageRequest pageable, BoardPageRequest request) {
        if (request.getIssueType() == null || request.getIssueType().equals(IssueType.ALL)) {
            return factory.selectFrom(board)
                    .where(board.type.eq(BoardType.USER))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize());
        }
        JPAQuery<Board> query = factory.selectFrom(board)
                .innerJoin(boardIssue).on(boardIssue.board.eq(board))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        LocalDateTime now = LocalDateTime.now();
        if (request.getIssueType().equals(IssueType.THIS_WEEK)) {
            LocalDateTime startOfWeek = now.with(DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
            query.where(boardIssue.createTime.between(startOfWeek, now));
        }

        if (request.getIssueType().equals(IssueType.THIS_MONTH)) {
            LocalDateTime startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
            query.where(boardIssue.createTime.between(startOfMonth, now));
        }
        return query.distinct();
    }

    private void applySort(JPAQuery<Board> query, SortType sortType) {
        switch (sortType) {
            case RATING -> query.orderBy(board.totalStar.desc());
            case HIT -> query.orderBy(board.hit.desc());
            case CLICKS -> query.orderBy(board.count.desc());
            default -> query.orderBy(board.createTime.desc());
        }
    }
}
