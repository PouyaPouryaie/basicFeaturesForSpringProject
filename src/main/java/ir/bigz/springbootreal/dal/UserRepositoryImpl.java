package ir.bigz.springbootreal.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UserRepositoryImpl extends DaoRepositoryImpl<User, Long> implements UserRepository {

    @Override
    public User getUserWithNationalCode(String nationalCode) {

        List<User> resultList = new ArrayList<>();
        String query = "select u from User u where u.nationalCode like '" + nationalCode + "'";

        resultList = genericSearch(query);

        if(resultList.size() > 0){
            return resultList.get(0);
        }
        return null;
    }

    @Override
    public Page<User> getUserSearchResult(UserSearchDto userSearchDto, Pageable pageable) {
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        Map<String, String> map = new ObjectMapper().convertValue(userSearchDto, Map.class);
        List<Predicate> collect = map.keySet().stream().filter(s -> (Objects.nonNull(map.get(s)) && !(map.get(s).equals(""))))
                .map(s -> criteriaBuilder.like(userRoot.get(s), "%"+map.get(s)+"%"))
                .collect(Collectors.toList());
        Predicate[] predicates = collect.stream().toArray(Predicate[]::new);
        Predicate and = criteriaBuilder.and(predicates);
        criteriaQuery.where(and);
        Page<User> users = genericSearch(criteriaQuery, pageable);
        return users;
    }
}
