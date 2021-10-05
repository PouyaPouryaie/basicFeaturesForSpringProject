package ir.bigz.springbootreal.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.bigz.springbootreal.dto.entity.User;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;
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
    public Page<User> getUserSearchResult(UserSearchDto userSearchDto, Sort.Order order, Pageable pageable) {

        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = criteriaQuery.from(User.class);
        List<Predicate> searchConditions = getSqlCondition(userSearchDto, criteriaBuilder, userRoot);
        Predicate concatConditions = criteriaBuilder.and(searchConditions.stream().toArray(Predicate[]::new));
        criteriaQuery.where(concatConditions);
        if(order.getDirection().name().equals("ASC"))
            criteriaQuery.orderBy(criteriaBuilder.asc(userRoot.get(order.getProperty())));
        else
            criteriaQuery.orderBy(criteriaBuilder.desc(userRoot.get(order.getProperty())));
        Page<User> users = genericSearch(criteriaQuery, pageable);
        return users;
    }

    private <T,K> List<Predicate> getSqlCondition(T searchModel, CriteriaBuilder criteriaBuilder, Root<K> root){
        List<Field> collect = Arrays.stream(searchModel.getClass().getDeclaredFields()).collect(Collectors.toList());
        Map<String, String> map = new ObjectMapper().convertValue(searchModel, Map.class);

        return map.keySet().stream()
                .filter(s -> (Objects.nonNull(map.get(s)) && !(map.get(s).equals(""))))
                .flatMap(s -> collect.stream().filter(field -> field.getName().equals(s)))
                .map(field -> {
                    if (field.getType().getName().contains("String"))
                        return criteriaBuilder.like(root.get(field.getName()), "%" + map.get(field.getName()) + "%");
                    else if (field.getType().getName().contains("Timestamp")){
                        if(field.getName().contains("from") || field.getName().contains("start")){
                            return criteriaBuilder.greaterThanOrEqualTo(root.get(field.getName()), map.get(field.getName()));
                        }
                        else
                            return criteriaBuilder.lessThanOrEqualTo(root.get(field.getName()), map.get(field.getName()));
                    }
                    else
                        return criteriaBuilder.equal(root.get(field.getName()), map.get(field.getName()));
                })
                .collect(Collectors.toList());
    }
}
