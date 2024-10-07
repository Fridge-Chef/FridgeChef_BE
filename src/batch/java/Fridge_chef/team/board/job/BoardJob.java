package Fridge_chef.team.board.job;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardHistory;
import Fridge_Chef.team.board.domain.BoardIssue;
import Fridge_Chef.team.board.repository.BoardIssueRepository;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BoardJob {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardIssueRepository boardIssueRepository;

    public BoardJob(BoardRepository boardRepository, UserRepository userRepository, BoardIssueRepository boardIssueRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.boardIssueRepository = boardIssueRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    void recipeIssueUp() {
        List<Board> boards = boardRepository.findAll();
        int userSize = userRepository.findAll().size();
        var dateTime = getStartOfWeek(LocalDateTime.now());

        if (boardIssueRepository.existsByCreateTimeBetween(dateTime, LocalDateTime.now())) {
            return;
        }

        for (Board board : boards) {
            if (!(board.getCount() >= userSize * 2 && board.getTotalStar() >= 3.5)){
                return;
            }

            var historys = board.getHistorys();
            var yesterdayStart = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS);
            var yesterdayEnd = yesterdayStart.plusDays(1).minusNanos(1);

            int count = historys.stream()
                    .filter(history -> {
                        var createTime = history.getCreateTime();
                        return createTime.isAfter(yesterdayStart) && createTime.isBefore(yesterdayEnd);
                    })
                    .mapToInt(BoardHistory::getCount)
                    .max().orElse(0);

            if(board.getCount() - count >= userSize / 2) {
                boardIssueRepository.save(new BoardIssue(board));
            }
        }
    }

    private LocalDateTime getStartOfWeek(LocalDateTime now) {
        return now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
    }
}
