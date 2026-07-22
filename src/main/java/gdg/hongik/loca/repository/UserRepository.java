package gdg.hongik.loca.repository;

import gdg.hongik.loca.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // 활성 사용자(soft-delete 안 됨) 이메일 조회 - 로그인/중복 체크용
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
