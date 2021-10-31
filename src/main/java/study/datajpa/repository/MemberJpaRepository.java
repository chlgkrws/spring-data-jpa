package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.Controller;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        //JPQL
        List<Member> memberList = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        return memberList;
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        Long count = em.createQuery("select count(m) from Member m", Long.class)
                .getSingleResult();

        return count;
    }

    public Member find(Long id) {
        Member member = em.find(Member.class, id);
        return member;
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByUsername(String username) {
        return em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();

    }

    public List<Member> findByPage(int age, int offset, int limit) {
        var memberList = em.createQuery("select m from Member m where m.age = :age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
        return memberList;
    }

    public Long totalCount(int age) {
            return em.createQuery("select count(m) from Member m where m.age = :age", Long.class)
                .setParameter("age",age)
                .getSingleResult();

    }

    public int bulkAgePlus(int age) {
        int resultCount = em.createQuery("update Member m set m.age = m.age+1" +
                        "where m.age =:age")
                .setParameter("age", age)
                .executeUpdate();

        return resultCount;
    }


}
