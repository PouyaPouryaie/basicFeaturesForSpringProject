package ir.bigz.springbootreal.dal;

import ir.bigz.springbootreal.dto.entity.User;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DaoRepository<User,Long> {

    User getUserWithNationalCode(String nationalCode);

    Page<User> getUserSearchResult(UserSearchDto userSearchDto, Sort.Order order, Pageable pageable);
}
