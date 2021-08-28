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
    @Autowired LoanMessengerRepository loanMessengerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverLoanJudged_SendMessage(@Payload LoanJudged loanJudged){

        if(!loanJudged.validate()) return;

        System.out.println("\n\n##### listener SendMessage : " + loanJudged.toJson() + "\n\n");



        // Sample Logic //
        // LoanMessenger loanMessenger = new LoanMessenger();
        // loanMessengerRepository.save(loanMessenger);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverLoanRequestRecieved_SendMessage(@Payload LoanRequestRecieved loanRequestRecieved){

        if(!loanRequestRecieved.validate()) return;

        System.out.println("\n\n##### listener SendMessage : " + loanRequestRecieved.toJson() + "\n\n");



        // Sample Logic //
        // LoanMessenger loanMessenger = new LoanMessenger();
        // loanMessengerRepository.save(loanMessenger);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverExcecuteLoanRequested_SendMessage(@Payload ExcecuteLoanRequested excecuteLoanRequested){

        if(!excecuteLoanRequested.validate()) return;

        System.out.println("\n\n##### listener SendMessage : " + excecuteLoanRequested.toJson() + "\n\n");



        // Sample Logic //
        // LoanMessenger loanMessenger = new LoanMessenger();
        // loanMessengerRepository.save(loanMessenger);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRepayLoanRequested_SendMessage(@Payload RepayLoanRequested repayLoanRequested){

        if(!repayLoanRequested.validate()) return;

        System.out.println("\n\n##### listener SendMessage : " + repayLoanRequested.toJson() + "\n\n");



        // Sample Logic //
        // LoanMessenger loanMessenger = new LoanMessenger();
        // loanMessengerRepository.save(loanMessenger);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
