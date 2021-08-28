package onlinebanknew;

import onlinebanknew.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired LoanRequestRepository loanRequestRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCancelled_CancelAuthentication(@Payload AuthCancelled authCancelled){

        if(!authCancelled.validate()) return;

        System.out.println("\n\n##### listener CancelAuthentication : " + authCancelled.toJson() + "\n\n");



        // Sample Logic //
        // LoanRequest loanRequest = new LoanRequest();
        // loanRequestRepository.save(loanRequest);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverLoanRequestRecieved_CompleteRequest(@Payload LoanRequestRecieved loanRequestRecieved){

        if(!loanRequestRecieved.validate()) return;

        System.out.println("\n\n##### listener CompleteRequest : " + loanRequestRecieved.toJson() + "\n\n");



        // Sample Logic //
        // LoanRequest loanRequest = new LoanRequest();
        // loanRequestRepository.save(loanRequest);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
