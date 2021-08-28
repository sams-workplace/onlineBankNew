package onlinebank;

import onlinebank.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class PolicyHandler{
    @Autowired HistoryRepository historyRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverTransactionRequested_ShowHistory(@Payload TransactionRequested transactionRequested){

        if(!transactionRequested.validate()) return;
        HistoryShown historyShown = new HistoryShown();

        System.out.println("\nBankAccountHistory.PolicyHandler.23\n##############################################");
        System.out.println("ShowHistory");
        System.out.println("##############################################\n");
        System.out.println("\nBankAccountHistory.PolicyHandler.26\n##### transactionRequested ShowHistory : " + transactionRequested.toJson() + "\n");

        historyShown.setListHistory(historyRepository.findByAccountNo(transactionRequested.getAccountNo()));
        historyShown.publish();
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAccountRequestCompleted_AddHistory(@Payload AccountRequestCompleted accountRequestCompleted){

        if(!accountRequestCompleted.validate()) return;

        System.out.println("\nBankAccountHistory.PolicyHandler.36\n##############################################");
        System.out.println("AddHistory");
        System.out.println("##############################################\n");
        System.out.println("\nBankAccount.PolicyHandler.39\n##### listener AddHistory : " + accountRequestCompleted.toJson() + "\n");

        if( "01".equals( accountRequestCompleted.getRequestId() ) ||  // Deposit
            "02".equals( accountRequestCompleted.getRequestId() ) ){  // Withdraw
        
            History history = new History();
            history.setAccountNo(accountRequestCompleted.getAccountNo());
            history.setRequestDate(accountRequestCompleted.getRequestDate());
            history.setRequestId(accountRequestCompleted.getRequestId());
            history.setRequestName(accountRequestCompleted.getRequestName());
            history.setRequestDate(accountRequestCompleted.getRequestDate());
            history.setRequestMoney(accountRequestCompleted.getRequestMoney());
            history.setAmountOfMoney(accountRequestCompleted.getAmountOfMoney());   
            historyRepository.save(history);
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}
}
