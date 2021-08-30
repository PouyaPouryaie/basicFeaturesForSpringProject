package ir.bigz.springbootreal.dal;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.management.openmbean.InvalidOpenTypeException;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public abstract class DaoRepositoryImpl<T, K extends Serializable> implements DaoRepository<T, K> {

    @PersistenceContext
    private EntityManager entityManager;

    protected Class<T> daoType;

    protected CriteriaBuilder criteriaBuilder;

    @SuppressWarnings("unchecked")
    protected DaoRepositoryImpl() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        daoType = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @PostConstruct
    public void init() {
        criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Override
    public Object getORMapper() {
        return entityManager;
    }

    @Override
    public <E> E getORMapper(Class<E> type) {
        if(type == null)
            return null;

        if(!type.isInstance(entityManager)) {
            throw new InvalidOpenTypeException("DaoRepositoryImpl support only the EntityManager type . . .");
        }

        return type.cast(entityManager);
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public <S extends T> S insert(S entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> insert(Iterable<S> entities) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public <S extends T> S update(S entity) {
        return entityManager.merge(entity);
    }

    @Override
    public <S extends T> Iterable<S> update(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends T> void delete(S entity) {

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void delete(K id) {
        entityManager.remove(find(id).get());
    }

    @Override
    public void deleteAll() {

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Optional<T> find(K id) throws IllegalArgumentException {
        return Optional.of(entityManager.find(daoType, id));
    }

    @Override
    public <S extends T> List<S> find(List<K> entityIds) {
        return null;
    }

    @Override
    public <S extends T> List<S> find(String entityName) {
        return null;
    }

    @Override
    public List<T> genericSearch(String query) {

        return entityManager.createQuery(query, daoType).getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void flush() {
        entityManager.flush();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public void clear() {
        entityManager.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Stream<T> getAll() {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery("from " + daoType.getName()).stream();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public List<T> getAll(Sort sort) {
        CriteriaQuery<T> query = criteriaBuilder.createQuery(daoType);
        Root<T> root = query.from(daoType);
        query.orderBy(orderByClauseBuilder(root, sort));
        query.select(root);
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Page<T> getAll(Pageable pageable) {

        long totalCount = totalCountOfEntities();

        CriteriaQuery<T> query = criteriaBuilder.createQuery(daoType);
        Root<T> root = query.from(daoType);

        if(!pageable.getSort().isEmpty())
            query.orderBy(orderByClauseBuilder(root, pageable.getSort()));

        query.select(root);

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((pageable.getPageNumber()-1) * pageable.getPageSize());
        typedQuery.setMaxResults(pageable.getPageSize());

         return new PageImpl<>(typedQuery.getResultList(), pageable, totalCount);
         
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public List<T> genericSearch(CriteriaQuery<T> criteriaQuery) {
        TypedQuery<T> tq = entityManager.createQuery(criteriaQuery);
        return tq.getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Page<T> genericSearch(CriteriaQuery<T> query, Pageable pageable){

        long totalCount = totalCountOfEntities();

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        List<T> resultList = typedQuery
            .setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Page<T> genericSearch(String query, Pageable pageable){

        TypedQuery<T> countQuery = entityManager.createQuery(query, daoType);
        int totalCount = countQuery.getResultList().size();

        TypedQuery<T> typedQuery = entityManager.createQuery(query, daoType);
        List<T> resultList = typedQuery
            .setFirstResult((pageable.getPageNumber() - 1) * pageable.getPageSize())
            .setMaxResults(pageable.getPageSize()).getResultList();

        return new PageImpl<>(resultList, pageable, totalCount);
    }


    protected long totalCountOfEntities() {
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        Root<T> root = query.from(daoType);
        Expression<Long> exp1 = criteriaBuilder.count(root);
        query.select(exp1);
        TypedQuery<Long> typedQuery = entityManager.createQuery(query);
        return typedQuery.getSingleResult();
    }

    protected List<Order> orderByClauseBuilder(Root<T> root, Sort sort){

        List<Order> orders = new ArrayList<>();
        Iterator<Sort.Order> iterator = sort.iterator();
        while(iterator.hasNext()){
            Sort.Order order = iterator.next();
            if(order.getDirection() == Sort.Direction.ASC){
                orders.add(criteriaBuilder.asc(root.get(order.getProperty())));
            }
            else{
                orders.add(criteriaBuilder.desc(root.get(order.getProperty())));
            }
        }
        return orders;
    }
}
