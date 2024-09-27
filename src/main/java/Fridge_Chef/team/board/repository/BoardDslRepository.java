package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static Fridge_Chef.team.board.domain.QBoard.board;

@Repository
@RequiredArgsConstructor

public class BoardDslRepository {
    private final JPAQueryFactory factory;

    public Page<BoardMyRecipePageResponse> findByPageUsers(PageRequest pageable, BoardPageRequest pageRequest) {
        JPAQuery<Board> query = createBaseQuery(pageRequest, pageable);
        applySort(query, pageRequest.getSortType());

        List<BoardMyRecipePageResponse> content = query.fetch().stream()
                .map(entity -> BoardMyRecipePageResponse.ofEntity(pageRequest.getSortType(), entity))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, content.size());
    }

    private JPAQuery<Board> createBaseQuery(BoardPageRequest pageRequest, PageRequest pageable) {
        JPAQuery<Board> query = factory.selectFrom(board)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (pageRequest.getSortType() == SortType.WEEKLY_RECIPE) {
            applyWeeklyFilter(query);
        } else if (pageRequest.getSortType() == SortType.MONTHLY_RECIPE) {
            applyMonthlyFilter(query);
        }

        return query;
    }

    private void applySort(JPAQuery<Board> query, SortType sortType) {
        switch (sortType) {
            case RATING -> query.orderBy(board.totalStar.desc());
            case HIT -> query.orderBy(board.hit.desc());
            case CLICKS -> query.orderBy(board.count.desc());
            default -> query.orderBy(board.createTime.desc());
        }
    }

    private void applyWeeklyFilter(JPAQuery<Board> query) {
        LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
        LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SATURDAY);
        query.where(board.createTime.between(startOfWeek.atStartOfDay(), endOfWeek.atTime(LocalTime.MAX)));
    }

    private void applyMonthlyFilter(JPAQuery<Board> query) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        query.where(board.createTime.between(startOfMonth.atStartOfDay(), endOfMonth.atTime(LocalTime.MAX)));
    }
}
