package ru.practicum.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import ru.practicum.dto.ViewStats;
import ru.practicum.model.ServiceHit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HitListElementRepositoryImpl implements HitListElementRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ViewStats> getHitListElementDtos(LocalDateTime start,
                                                 LocalDateTime end,
                                                 String[] uris,
                                                 boolean unique) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ViewStats> cq = cb.createQuery(ViewStats.class);
        Root<ServiceHit> root = cq.from(ServiceHit.class);
        List<Predicate> predicates = new ArrayList<>();
        Predicate datePredicate = cb.between(root.get("created"), start, end);
        predicates.add(datePredicate);
        if (uris != null && uris.length > 0) {
            Predicate uriPredicate = root.get("uri").in(Arrays.asList(uris));
            predicates.add(uriPredicate);
        }
        Expression<Long> hitCountExpression = unique
                ? cb.countDistinct(root.get("ip"))
                : cb.count(root.get("ip"));
        cq.select(cb.construct(
                ViewStats.class,
                root.get("app"),
                root.get("uri"),
                hitCountExpression
        ));
        cq.where(predicates.toArray(new Predicate[0]))
                .groupBy(root.get("app"), root.get("uri"))
                .orderBy(cb.desc(hitCountExpression));
        return em.createQuery(cq).getResultList();
    }
}
