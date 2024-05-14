package pentasecurity.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pentasecurity.task.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
}