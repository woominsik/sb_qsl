package com.ll.exam.qsl.user.repository;

import com.ll.exam.qsl.interestKeyword.entity.QInterestKeyword;
import com.ll.exam.qsl.user.entity.SiteUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.ll.exam.qsl.interestKeyword.entity.QInterestKeyword.interestKeyword;
import static com.ll.exam.qsl.user.entity.QSiteUser.siteUser;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public SiteUser getQslUser(Long id) {
        return jpaQueryFactory
                .select(siteUser)
                .from(siteUser)
                .where(siteUser.id.eq(id))
                .fetchOne();
    }

    @Override
    public long getQslCount() {
        return jpaQueryFactory
                .select(siteUser.count())
                .from(siteUser)
                .fetchOne();
    }

    @Override
    public SiteUser getQslUserOrderByIdAscOne() {
        return jpaQueryFactory
                .select(siteUser)
                .from(siteUser)
                .orderBy(siteUser.id.asc())
                .fetchFirst();
    }

    @Override
    public List<SiteUser> getQslUsersOrderByIdAsc() {
        return jpaQueryFactory
                .select(siteUser)
                .from(siteUser)
                .orderBy(siteUser.id.asc())
                .fetch();
    }

    @Override
    public List<SiteUser> searchQsl(String kw) {
        return jpaQueryFactory
                .select(siteUser)
                .from(siteUser)
                .where(
                        siteUser.username.contains(kw)
                                .or(siteUser.email.contains(kw))
                )
                .orderBy(siteUser.id.desc())
                .fetch();
    }

    @Override
    public Page<SiteUser> searchQsl(String kw, Pageable pageable) {
        JPAQuery<SiteUser> usersQuery = jpaQueryFactory
                .select(siteUser)
                .from(siteUser)
                .where(
                        siteUser.username.contains(kw)
                                .or(siteUser.email.contains(kw))
                )
                .offset(pageable.getOffset()) // 몇개를 건너 띄어야 하는지 LIMIT {1}, ?
                .limit(pageable.getPageSize()); // 한페이지에 몇개가 보여야 하는지 LIMIT ?, {1}

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(siteUser.getType(), siteUser.getMetadata());
            usersQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
        }

        List<SiteUser> users = usersQuery.fetch();

        JPAQuery<Long> usersCountQuery = jpaQueryFactory
                .select(siteUser.count())
                .from(siteUser)
                .where(
                        siteUser.username.contains(kw)
                                .or(siteUser.email.contains(kw))
                );

        return PageableExecutionUtils.getPage(users, pageable, usersCountQuery::fetchOne);
    }

    @Override
    public List<SiteUser> getQslUsersByInterestKeyword(String keywordContent) {
        /*
       SELECT SU.*
       FROM site_user AS SU
       INNER JOIN site_user_interest_keywords AS SUIK
       ON SU.id = SUIK.site_user_id
       INNER JOIN interest_keyword AS IK
       ON IK.content = SUIK.interest_keywords_content
       WHERE IK.content = "축구";
       */
        return jpaQueryFactory
                .selectFrom(siteUser)
                .innerJoin(siteUser.interestKeywords, interestKeyword)
                .where(
                        interestKeyword.content.eq(keywordContent)
                )
                .fetch();
    }
}