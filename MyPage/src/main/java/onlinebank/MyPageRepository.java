package onlinebank;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel="mypages", path="mypages")
public interface MyPageRepository extends CrudRepository<MyPage, Long> {

    MyPage findByAccountNo(String accountNo);

}