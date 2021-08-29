package onlinebanknew;

import onlinebanknew.config.kafka.KafkaProcessor;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
//import org.springframework.beans.BeanUtils;

@Service
public class PolicyHandler{
    @Autowired LoanManagerRepository loanManagerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCertified_RecieveLoanRequest(@Payload AuthCertified authCertified){

        if(!authCertified.validate()) return;
        System.out.println("\n\n##### listener RecieveLoanRequest : " + authCertified.toJson() + "\n\n");

        LoanManager loanManager = new LoanManager();

        loanManager.setId(authCertified.getLoanRequestId());
        loanManager.setRequestId(authCertified.getRequestId());
        loanManager.setRequestName(authCertified.getRequestName());
        loanManager.setRequestDate(authCertified.getRequestDate());
        loanManager.setUserId(authCertified.getUserId());
        loanManager.setUserName(authCertified.getUserName());
        loanManager.setUserPassword(authCertified.getUserPassword());
        loanManager.setUserMobile(authCertified.getUserMobile());
        loanManager.setAmountOfMoney(authCertified.getAmountOfMoney());
        loanManager.setLoanRequestId(authCertified.getLoanRequestId());

        //BeanUtils.copyProperties(loanManager, authCertified);
        loanManagerRepository.save(loanManager);
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCancelled_CancelAuthentication(@Payload AuthCancelled authCancelled){

        if(!authCancelled.validate()) return;
        System.out.println("\n\n##### listener CancelAuthentication : " + authCancelled.toJson() + "\n\n");

        // Sample Logic //
        // LoanManager loanManager = new LoanManager();
        // loanManagerRepository.save(loanManager);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAccountRequestCompleted_CompleteRequest(@Payload AccountRequestCompleted accountRequestCompleted){

        if(!accountRequestCompleted.validate()) return;
        System.out.println("\n\n##### listener CompleteRequest : " + accountRequestCompleted.toJson() + "\n\n");

        // Sample Logic //
        // LoanManager loanManager = new LoanManager();
        // loanManagerRepository.save(loanManager);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCancelled_CancelRequest(@Payload AuthCancelled authCancelled){

        if(!authCancelled.validate()) return;
        System.out.println("\n\n##### listener CancelRequest : " + authCancelled.toJson() + "\n\n");

        // Sample Logic //
        // LoanManager loanManager = new LoanManager();
        // loanManagerRepository.save(loanManager);

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
