package org.beep.sbpp.users.repository;

import org.beep.sbpp.users.dto.UserMyPageResDTO;
import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {


    Optional<UserEntity> findByUserId(Long userId);

    Optional<UserEntity> findByEmail(String email);

    // tbl_user + tbl_user_profile
    @Query("""
        select new org.beep.sbpp.users.dto.UserMyPageResDTO(
                    u.email, p.nickname, p.birthDate, p.nationality, p.gender, p.profileImgUrl)
            from UserEntity u JOIN UserProfileEntity p
                where u.userId = p.userId and u.userId = :userId
    """)
    UserMyPageResDTO findMyPageBasic(@Param("userId") Long userId);

//    // 닉네임으로 userId 값 받아오기
//    @Query("""
//        select u.userId
//            From UserEntity u join UserProfileEntity p
//                where p.nickname = :nickname
//    """)
//    Optional<Long> findUserIdByNickname(@Param("nickname") String nickname);

}
