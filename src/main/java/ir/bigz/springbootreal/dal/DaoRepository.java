package ir.bigz.springbootreal.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface DaoRepository<T, K extends Serializable> {

    Object getORMapper();
    <E> E getORMapper(Class<E> type);
    <S extends T> S insert(S entity);
    <S extends T> Iterable<S> insert(Iterable<S> entities);
    <S extends T> S update(S entity);
    <S extends T> Iterable<S> update(Iterable<S> entities);
    <S extends T> void delete(S entity);
    void delete(K id);
    void deleteAll();
    Stream<T> getAll();
    Optional<T> find(K id);
    <S extends T> List<S> find(List<K> entityIds);
    <S extends T> List<S> find(String entityName);
    List<T> genericSearch(String query);
    void flush();
    void clear();

    Page<T> genericSearch(String query, Pageable pageable);
}
