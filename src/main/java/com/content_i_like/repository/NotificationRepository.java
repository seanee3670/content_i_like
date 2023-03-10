package com.content_i_like.repository;

import com.content_i_like.domain.entity.Likes;
import com.content_i_like.domain.entity.Member;
import com.content_i_like.domain.entity.Notification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findNotificationByMemberAndFromMemberNoAndRecommendNoAndCommentNo(Member member, Long fromMemberNo, Long recommendNo, Long commentNo); // like 는 commentNo 가 null 로 저장됨.
    Page<Notification> findPageAllByMember(Member member, Pageable pageable);

    Slice<Notification> findSliceAllByMember(Member member, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Notification n SET n.isRead = :isRead WHERE n.notificationNo = :notificationNo")
    int updateIsRead(@Param("isRead") boolean isRead, @Param("notificationNo") Long notificationNo);

}
