package ir.bigz.springbootreal.dal;
import ir.bigz.springbootreal.commons.util.Utils;
import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.dto.PagedQuery;
import ir.bigz.springbootreal.exception.AppException;
import ir.bigz.springbootreal.exception.HttpErrorCode;
import org.hibernate.Session;
import org.hibernate.jpa.QueryHints;
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
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Stream;

@Component
public abstract class DaoRepositoryImpl<T, K extends Serializable> implements DaoRepository<T, K> {

    @PersistenceContext
    private EntityManager entityManager;

    protected Class<T> daoType;

    protected CriteriaBuilder criteriaBuilder;

    private static int maxPageSize = 1000;

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
    public <S extends T> S save(S entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public <S extends T> S update(S entity) {
        return entityManager.merge(entity);
    }

    @Override
    public <S extends T> Iterable<S> updateAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends T> void delete(S entity) {

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteById(K id) {
        entityManager.remove(findById(id).get());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteAll() {
        findAll().forEach(t -> entityManager.remove(t));
    }

    @Override
    public void deleteAllById(Iterable<? extends K> entities) {

    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Optional<T> findById(K id) throws IllegalArgumentException {
        return Optional.of(entityManager.find(daoType, id));
    }

    @Override
    public <S extends T> List<S> find(String entityName) {
        return null;
    }

    @Override
    public List<T> genericSearch(String query) {

        return entityManager.createQuery(query, daoType).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public List<T> nativeQuery(String query, Map<String, Object> parameters) {

        Query nativeQuery = entityManager.createNativeQuery(query, daoType);
        parameters.keySet().forEach(s -> nativeQuery.setParameter(s, parameters.get(s)));
        return nativeQuery.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public PageResult<T> pageCreateQuery(String nativeQuery, PagedQuery pagedQuery, Map<String, Object> parameterMap, boolean getTotalCount) {

        StringBuilder orderString = new StringBuilder();
        String queryString = nativeQuery;
        pagedQuery.getOrdering().forEach(
                orderParam -> {
                    try {
                        orderParam = orderParam.trim();
                        String[] orderField = orderParam.split("_");
                        Field field = Utils.getDeclaredField(daoType, orderField[0]);
                        if(field == null){
                            return;
                        }
                        String orderColumn = field.getName().replaceAll("([A-Z])", "_$1").toLowerCase();
                        if (orderString.length() > 0) {
                            orderString.append(", ");
                        }
                        String order = PagedQuery.ORDER_ASC;
                        if (orderField.length > 1 && orderField[1].equalsIgnoreCase(PagedQuery.ORDER_DESC)) {
                            order = PagedQuery.ORDER_DESC;
                        }
                        orderString.append(orderColumn + " " + order);
                    }catch (Exception e){
                        throw AppException.newInstance(
                                HttpErrorCode.ERR_10705, String.format("field %s ordering is wrong", orderParam)
                        );
                    }
                }
        );

        if (orderString.length() > 0) {
            queryString = "SELECT * FROM (" + removeDefaultOrderBy(queryString) + ") q ORDER BY " + orderString;
        }

        Query query = entityManager.createNativeQuery(queryString, daoType);
        parameterMap.keySet().forEach(s -> query.setParameter(s, parameterMap.get(s)));

        //todo paginationWindow must be improve
        if (pagedQuery.getPageSize() > 0 && pagedQuery.getPageSize() < maxPageSize && pagedQuery.getPageNumber() > 0) {
            int start = ((pagedQuery.getPageNumber() - 1) * pagedQuery.getPageSize());
            int end = (pagedQuery.getPageNumber() * pagedQuery.getPageSize());
            query.setFirstResult(start);
            query.setMaxResults(end);
        }

        List<T> resultQuery = query.getResultList();

        Long total = 0L;
        if (getTotalCount && pagedQuery.getPageSize() > -1) {
            total = totalCountOfSearch(queryString, parameterMap);
        }

        return new PageResult<>(
                resultQuery
                , (pagedQuery.getPageSize() > -1 ? pagedQuery.getPageSize() : resultQuery.size())
                , pagedQuery.getPageNumber()
                , pagedQuery.getOffset()
                , total != 0 ? total : resultQuery.size()
        );
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
    public Stream<T> findAll() {
        Session session = entityManager.unwrap(Session.class);
        return session.createQuery("from " + daoType.getName())
                .setHint(QueryHints.HINT_FETCH_SIZE, 50)
                .stream();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public List<T> findAll(Sort sort) {
        CriteriaQuery<T> query = criteriaBuilder.createQuery(daoType);
        Root<T> root = query.from(daoType);
        query.orderBy(orderByClauseBuilder(root, sort));
        query.select(root);
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        return typedQuery.getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public Page<T> findAll(Pageable pageable) {

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

        TypedQuery<T> countQuery = entityManager.createQuery(query.toString(), daoType);
        int totalCount = countQuery.getResultList().size();

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

    protected Long totalCountOfSearch(String queryString, Map<String, Object> parameterMap) {
        queryString = MessageFormat.format("SELECT count(*) count FROM (SELECT * FROM ({0}) query) counter", queryString);
        Query query = entityManager.createNativeQuery(queryString);
        parameterMap.keySet().forEach(s -> query.setParameter(s, parameterMap.get(s)));
        List resultList = query.getResultList();
        return ((BigInteger)resultList.get(0)).longValue();
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

    protected static String removeDefaultOrderBy(String query) {
        int defaultOrderIndex = query.toLowerCase().lastIndexOf("order by");
        int lastParenthesisIndex = query.lastIndexOf(")");
        if (defaultOrderIndex > 0 && defaultOrderIndex > lastParenthesisIndex) {
            return query.substring(0, defaultOrderIndex);
        }
        return query;
    }
}
