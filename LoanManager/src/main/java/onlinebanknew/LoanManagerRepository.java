package onlinebanknew;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="loanManagers", path="loanManagers")
public interface LoanManagerRepository extends PagingAndSortingRepository<LoanManager, Long>{
    LoanManager findByLoanRequestId(Long loanRequestId);

}
