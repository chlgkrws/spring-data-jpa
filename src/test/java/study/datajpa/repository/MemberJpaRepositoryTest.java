package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("A");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        // when

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);
        long deletedCount = memberJpaRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> findMembers = memberJpaRepository.findByUsernameAndAgeGreaterThen("AAA", 15);

        assertThat(findMembers.get(0).getUsername()).isEqualTo("AAA");
        assertThat(findMembers.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);
        List<Member> result = memberJpaRepository.findByUsername("AAA");

        assertThat(result.get(0)).isEqualTo(member1);
    }


    @Test
    public void paging() {
        //given - 이런 데이터가 있을 떄
        // when - 이렇게 하면
        // then - 이렇게 된다.
        memberJpaRepository.save(new Member("member1", 10));
        memberJpaRepository.save(new Member("member2", 10));
        memberJpaRepository.save(new Member("member3", 10));
        memberJpaRepository.save(new Member("member4", 10));
        memberJpaRepository.save(new Member("member5", 10));
        memberJpaRepository.save(new Member("member6", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        //when
        List<Member> members = memberJpaRepository.findByPage(10, 0, 3);
        Long aLong = memberJpaRepository.totalCount(10);

        assertThat(members.size()).isEqualTo(3);
        assertThat(aLong).isEqualTo(6L);
    }

    @Test
    public void bulkUpdate() throws Exception {
        // given
        memberJpaRepository.save(new Member("AA", 10, null));
        memberJpaRepository.save(new Member("AA1", 10, null));
        memberJpaRepository.save(new Member("AA2", 10, null));
        memberJpaRepository.save(new Member("AA3", 10, null));
        memberJpaRepository.save(new Member("AA4", 10, null));
        memberJpaRepository.save(new Member("AA5", 10, null));
        memberJpaRepository.save(new Member("AA7", 10, null));
        memberJpaRepository.save(new Member("AA6", 10, null));

        int resultCount = memberJpaRepository.bulkAgePlus(10);
        entityManager.flush();
        entityManager.clear();

        List<Member> aa6 = memberJpaRepository.findByUsername("AA6");
        System.out.println("aa6 = " + aa6.get(0).getAge());

        assertThat(resultCount).isEqualTo(8);

    }

    @Test
    public void emGetClass() throws Exception {
        System.out.println(entityManager);
        memberJpaRepository.save(new Member("user"));
        entityManager.flush();
        entityManager.clear();
        System.out.println(entityManager);
    }

    @Test
    public void queryHint() {
        //given
        Member member = new Member("user", 10);
        memberJpaRepository.save(member);
        entityManager.flush();
        entityManager.clear();

    }


}