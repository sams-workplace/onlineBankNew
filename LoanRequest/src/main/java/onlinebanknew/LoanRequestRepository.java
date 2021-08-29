package onlinebanknew;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel="loanRequests", path="loanRequests")
public interface LoanRequestRepository extends PagingAndSortingRepository<LoanRequest, Long>{
    LoanRequest findByLoanRequestId(Long id);
}
