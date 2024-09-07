package Fridge_Chef.team.user.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDslRepository {
    private final JPAQueryFactory factory;

}
