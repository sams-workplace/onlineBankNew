package onlinebanknew;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="loanAuths", path="loanAuths")
public interface LoanAuthRepository extends PagingAndSortingRepository<LoanAuth, Long>{


}
