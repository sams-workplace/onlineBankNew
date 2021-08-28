package onlinebank;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="histories", path="histories")
public interface HistoryRepository extends PagingAndSortingRepository<History, Long>{

    List<History> findByAccountNo(String accountNo);
}
