package onlinebanknew;

import onlinebanknew.config.kafka.KafkaProcessor;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
//import java.util.Optional;

@Service
public class PolicyHandler{
    @Autowired LoanRequestRepository loanRequestRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCancelled_CancelAuthentication(@Payload AuthCancelled authCancelled){
        if(!authCancelled.validate()) return;
        System.out.println("\n\n##### listener CancelAuthentication : " + authCancelled.toJson() + "\n\n");
        
        LoanRequest loanRequest = new LoanRequest(); 
        loanRequest.setId(authCancelled.getLoanRequestId());
        loanRequest.setRequestId(authCancelled.getRequestId());
        loanRequest.setRequestName(authCancelled.getRequestName());
        loanRequest.setRequestDate(authCancelled.getRequestDate());
        loanRequest.setUserId(authCancelled.getUserId());
        loanRequest.setUserName(authCancelled.getUserName());
        loanRequest.setUserPassword(authCancelled.getUserPassword());
        loanRequest.setUserMobile(authCancelled.getUserMobile());
        loanRequest.setAmountOfMoney(authCancelled.getAmountOfMoney());
        loanRequest.setRequestStatus("인증실패");
        loanRequest.setLoanRequestId(authCancelled.getLoanRequestId());
        loanRequestRepository.save(loanRequest);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverLoanRequestRecieved_CompleteRequest(@Payload LoanRequestRecieved loanRequestRecieved){
        if(!loanRequestRecieved.validate()) return;
        System.out.println("\n\n##### listener CompleteRequest : " + loanRequestRecieved.toJson() + "\n\n");

        LoanRequest loanRequest = new LoanRequest(); 
        loanRequest.setId(loanRequestRecieved.getLoanRequestId());
        loanRequest.setRequestId(loanRequestRecieved.getRequestId());
        loanRequest.setRequestName(loanRequestRecieved.getRequestName());
        loanRequest.setRequestDate(loanRequestRecieved.getRequestDate());
        loanRequest.setUserId(loanRequestRecieved.getUserId());
        loanRequest.setUserName(loanRequestRecieved.getUserName());
        loanRequest.setUserPassword(loanRequestRecieved.getUserPassword());
        loanRequest.setUserMobile(loanRequestRecieved.getUserMobile());
        loanRequest.setAmountOfMoney(loanRequestRecieved.getAmountOfMoney());
        loanRequest.setRequestStatus("요청완료");
        loanRequest.setLoanRequestId(loanRequestRecieved.getLoanRequestId());
        loanRequestRepository.save(loanRequest);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
