package onlinebank;

import onlinebank.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired RequestRepository requestRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCancelled_CancelBankAuthentication(@Payload AuthCancelled authCancelled){

        if(!authCancelled.validate()) return;    
        requestRepository.deleteById( authCancelled.getBankRequestId() );
        System.out.println("\nBankRequest.PolicyHandler.20\n##############################################" + 
                           "\n" + authCancelled.getRequestName() + " Request Cancelled" + 
                           "\nAuthentication Failed" + 
                           "\n##############################################\n" );
        System.out.println("BankRequest.PolicyHandler.24\n##### listener CancelBankAuthentication : " + authCancelled.toJson() + "\n");
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAccountRequestCompleted_CompleteBankRequest(@Payload AccountRequestCompleted accountRequestCompleted){
        if(!accountRequestCompleted.validate()) return;
        System.out.println("\nBankRequest.PolicyHandler.30\n##############################################" + 
                           "\n" + accountRequestCompleted.getRequestName() + " Request Completed" + 
                           "\nbalance : " + accountRequestCompleted.getAmountOfMoney() + " Won" +
                           "\n##############################################\n" );
        System.out.println("BankRequest.PolicyHandler.34\n##### listener CompleteBankRequest : " + accountRequestCompleted.toJson() + "\n");

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAccountRequestCancelled_CancelBankRequest(@Payload AccountRequestCancelled accountRequestCancelled){

        if(!accountRequestCancelled.validate()) return;
        requestRepository.deleteById( accountRequestCancelled.getBankRequestId() );
        System.out.println("\nBankRequest.PolicyHandler.43\n##############################################" + 
                           "\n" + accountRequestCancelled.getRequestName() + " Request Cancelled" + 
                           "\nmessage : " + accountRequestCancelled.getMessage() + 
                           "\n##############################################\n" );
        System.out.println("BankRequest.PolicyHandler.47\n##### listener CancelBankRequest : " + accountRequestCancelled.toJson() + "\n");
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverHistoryShown_CompleteTransactionRequest(@Payload HistoryShown historyShown){

        if(!historyShown.validate()) return;
        System.out.println("\nBankRequest.PolicyHandler.54\n##############################################" + 
                           "\nTransaction History Shown " + 
                           "\n##############################################\n" );
        System.out.println("BankRequest.PolicyHandler.57\n##### listener CompleteTransactionRequest : " + historyShown.toJson() + "\n");
        
    }
}
