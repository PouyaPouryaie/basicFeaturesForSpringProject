package ir.bigz.springbootreal.dal;

import ir.bigz.springbootreal.dto.SqlOperation;
import ir.bigz.springbootreal.dto.ValueCondition;
import ir.bigz.springbootreal.dto.entity.User;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.javatuples.Quartet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface UserRepository extends DaoRepository<User,Long> {

    User getUserWithNationalCode(String nationalCode);

    Page<User> getUserSearchResult(UserSearchDto userSearchDto, Sort.Order order, Pageable pageable);

    Page<User> getUserQueryWithCriteriaBuilder(Map<String, String> queryString, List<Quartet<String, String, SqlOperation, ValueCondition>> rules, Pageable pageable);
}
