package onlinebanknew;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel="loanStatus", path="loanStatus")
public interface LoanStatusRepository extends CrudRepository<LoanStatus, Long> {


}