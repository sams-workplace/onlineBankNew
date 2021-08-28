package onlinebank;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="accounts", path="accounts")
public interface AccountRepository extends PagingAndSortingRepository<Account, Long>{

    //@Query("SELECT * FROM ACCOUNT_TABLE WHERE ACCOUNTNO = ?!")
    Account findByAccountNo(String accountNo);
    //void deleteByAccountNo(String accountNo);
}
