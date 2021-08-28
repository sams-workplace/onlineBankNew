package onlinebanknew;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="loanRequests", path="loanRequests")
public interface LoanRequestRepository extends PagingAndSortingRepository<LoanRequest, Long>{


}
