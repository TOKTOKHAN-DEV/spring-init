package com.spring.spring_init.admin.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.spring.spring_init.admin.user.dto.response.UserAdminInfo;
import com.spring.spring_init.user.entity.QUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserAdminQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final QUser user = QUser.user;

    public Page<UserAdminInfo> getUsers(Pageable pageable) {

        List<UserAdminInfo> content = queryFactory
            .select(Projections.constructor(
                UserAdminInfo.class,
                user.userId,
                user.email,
                user.dateJoined,
                user.userRole
                ))
            .from(user)
            .orderBy(user.userId.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(content, pageable, 10);
    }
}
