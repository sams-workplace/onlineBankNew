package onlinebanknew;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="loanMessengers", path="loanMessengers")
public interface LoanMessengerRepository extends PagingAndSortingRepository<LoanMessenger, Long>{


}
