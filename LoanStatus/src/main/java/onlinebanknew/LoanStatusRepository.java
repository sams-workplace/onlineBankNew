package onlinebanknew;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanStatusRepository extends CrudRepository<LoanStatus, Long> {


}