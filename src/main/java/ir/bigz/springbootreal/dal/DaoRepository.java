package ir.bigz.springbootreal.dal;

import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.dto.PagedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface DaoRepository<T, K extends Serializable> {

    Object getORMapper();
    <E> E getORMapper(Class<E> type);
    <S extends T> S save(S entity);
    <S extends T> Iterable<S> saveAll(Iterable<S> entities);

    <S extends T> S update(S entity);
    <S extends T> Iterable<S> updateAll(Iterable<S> entities);

    void deleteById(K id);
    <S extends T> void delete(S entity);
    void deleteAll();
    void deleteAllById(Iterable<? extends K> entities);
    void deleteAll(Iterable<? extends T> entities);

    Optional<T> findById(K id);
    Stream<T> findAll();
    List<T> findAll(Sort sort);
    Page<T> findAll(Pageable pageable);
    <S extends T> List<S> find(String entityName);

    List<T> genericSearch(String query);
    List<T> genericSearch(CriteriaQuery<T> criteriaQuery);
    Page<T> genericSearch(CriteriaQuery<T> criteriaQuery, Pageable pageable);
    Page<T> genericSearch(String query, Pageable pageable);

    List<T> nativeQuery(String query, Map<String, Object> parameters);
    PageResult<T> pageCreateQuery(String nativeQuery, PagedQuery pagedQuery, Map<String, Object> parameterMap, boolean getTotalCount);

    void flush();
    void clear();
}
