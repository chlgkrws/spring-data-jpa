package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findHelloBy();

    List<Member> findTop3HeeloBy();

    @Query(name = "Member.findByUsername")
        //생략 가능 JPA -> 1. JpaRepository<?,!> 시 ?.method명으로 먼저 네임드쿼리가 있는지 확인 후 메서드 명을 쿼리로 바꾸는 작업을 진행함
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

//    List<Member> findByAge(int age, Pageable pageable);  // 요청한 페이지의 +1을 Limit으로 설정한다 = 하나의 요소를 더 가져옴
    Slice<Member> findByAge(int age, Pageable pageable);  // 요청한 페이지의 +1을 Limit으로 설정한다 = 하나의 요소를 더 가져옴
//    Page<Member> findByAge(int age, Pageable pageable);

    //카운트 쿼리 분리하기
//    @Query(value = "select m from Member m left join m.team t"
//    ,  countQuery = "select count(m) from Member m")

    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age =:age")
    int bulkAgePlus(@Param("age") int age);


    @Query("select m from Member m left join fetch m.team")
    List<Member> findMembersByFetch();

    @Override
    @EntityGraph(attributePaths = "team")
    List<Member> findAll();

    // 커스터마이징 엔티티 그래프
    @EntityGraph(attributePaths = "team")
    @Query("select m from Member m")
    List<Member> findAllCustom();

    // 네임드 엔티티 그래프
    @EntityGraph("Member.All")
    List<Member> findEntityGraphByUsername(@Param("username") String username);
}
