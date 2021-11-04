package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() throws Exception {
        // given
        Member member = new Member("A");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).orElseThrow();

        // then
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);

        memberRepository.flush();
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() throws Exception {
        // given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        // 카운트
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> findMembers = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(findMembers.get(0).getUsername()).isEqualTo("AAA");
        assertThat(findMembers.size()).isEqualTo(1);
    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = memberRepository.findHelloBy();
        memberRepository.findTop3HeeloBy();
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findByUsername("AAA");

        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void testNameQuery2() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);

        memberRepository.save(member1);
        memberRepository.save(member2);
        List<Member> result = memberRepository.findUser("AAA",10);

        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);


        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> byNames = memberRepository.findByNames(List.of(m1.getUsername(), m2.getUsername()));
        System.out.println("byNames = " + byNames);
    }


    @Test
    public void findByReturnType() {
        Member m1 = new Member("aaa", 10);
        Member m2 = new Member("bbb", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> aaa = memberRepository.findListByUsername("aaa");
        System.out.println("aaa = " + aaa);

        Member m3 = new Member("ZHZH", 10);
        memberRepository.save(m3);

        Optional<Member> optionalByUsername = memberRepository.findOptionalByUsername(m3.getUsername());
        System.out.println("optionalByUsername = " + optionalByUsername.get());

        //중요! 컬렉션 조회는 값이 없으면 빈 컬렉션을 반환한다.
        List<Member> adsadasd = memberRepository.findListByUsername("ADSADASD");
        System.out.println("adsadasd = " + adsadasd.size());

        //단건일 경우는 NULL을 반환한다.
        Member qweqwewqewq = memberRepository.findMemberByUsername("qweqwewqewq");
        System.out.println("qweqwewqewq = " + qweqwewqewq);


    }


    @Test
    public void paging() {
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        memberRepository.save(new Member("member6", 10));

        int age = 10;

        PageRequest page = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username","age"));
        Slice<Member> result = memberRepository.findByAge(age, page);

        //map사용으로 DTO 반환하기 예시
//        result.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        System.out.println("result = " + result.getContent());
        //System.out.println("result = " + result.getTotalElements()); Page 인터페이스 영역

        assertThat(result.getContent().size()).isEqualTo(3);
        //assertThat(result.getTotalElements()).isEqualTo(6);  Page 인터페이스 영역
        assertThat(result.getNumber()).isEqualTo(0);
        //assertThat(result.getTotalPages()).isEqualTo(2);   Page 인터페이스 영역
        assertThat(result.isFirst()).isTrue();
        assertThat(result.hasNext()).isTrue();

    }

    @Test
    public void bulkUpdate() throws Exception {
        memberRepository.save(new Member("AA", 10, null));
        memberRepository.save(new Member("AA1", 10, null));
        memberRepository.save(new Member("AA2", 10, null));
        memberRepository.save(new Member("AA3", 10, null));
        memberRepository.save(new Member("AA4", 10, null));
        memberRepository.save(new Member("AA5", 10, null));
        memberRepository.save(new Member("AA7", 10, null));
        memberRepository.save(new Member("AA6", 10, null));

        int resultCount = memberRepository.bulkAgePlus(10);

        List<Member> aa6 = memberRepository.findByUsername("AA6");
        System.out.println("aa6 = " + aa6.get(0).getAge());

        assertThat(resultCount).isEqualTo(8);
    }

    @Test
    public void N_1_Problem() {
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));
        entityManager.flush();
        entityManager.clear();

        memberRepository.save(new Member("A", 10, teamA));
        memberRepository.save(new Member("B", 10, teamB));
        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void fetchJoin() {
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));
        entityManager.flush();
        entityManager.clear();

        memberRepository.save(new Member("A", 10, teamA));
        memberRepository.save(new Member("B", 10, teamB));
        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findMembersByFetch();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void fetchJoin_EntityGraph() {
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));
        entityManager.flush();
        entityManager.clear();

        memberRepository.save(new Member("A", 10, teamA));
        memberRepository.save(new Member("B", 10, teamB));
        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void fetchJoin_NamedEntityGraph() {
        Team teamA = teamRepository.save(new Team("teamA"));
        Team teamB = teamRepository.save(new Team("teamB"));
        entityManager.flush();
        entityManager.clear();

        memberRepository.save(new Member("A", 10, teamA));
        memberRepository.save(new Member("A", 10, teamB));
        entityManager.flush();
        entityManager.clear();

        List<Member> members = memberRepository.findEntityGraphByUsername("A");

        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }
    }

    @Test
    public void queryHint() {
        //given
        Member member = new Member("user", 10);
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("user");
        findMember.setUsername("member2");

        entityManager.flush();

    }

    @Test
    public void lock() {
        //given
        Member member = new Member("user", 10);
        memberRepository.save(member);
        entityManager.flush();
        entityManager.clear();

        //when
        List<Member> lockByUsername = memberRepository.findLockByUsername("user");

    }

}
