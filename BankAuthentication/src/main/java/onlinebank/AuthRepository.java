package onlinebank;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="auths", path="auths")
public interface AuthRepository extends PagingAndSortingRepository<Auth, Long>{


}
