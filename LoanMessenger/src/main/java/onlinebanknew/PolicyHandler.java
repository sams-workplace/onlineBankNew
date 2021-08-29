package onlinebanknew;

import onlinebanknew.config.kafka.KafkaProcessor;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class PolicyHandler{
    @Autowired LoanMessengerRepository loanMessengerRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverLoanRequestRecieved_SendMessage(@Payload LoanRequestRecieved loanRequestRecieved){

        if(!loanRequestRecieved.validate()) return;
        System.out.println("\n\n##### listener SendMessage : " + loanRequestRecieved.toJson() + "\n\n");

        String text = "";
        LoanMessenger loanMessenger = new LoanMessenger();
        loanMessenger.setUserId(loanRequestRecieved.getUserId());
        loanMessenger.setUserName(loanRequestRecieved.getUserName());
        loanMessenger.setUserMobile(loanRequestRecieved.getUserMobile());
        loanMessenger.setProcDate(new Date());
        loanMessenger.setLoanStatus(loanRequestRecieved.getRequestName());
        loanMessenger.setAmountOfMoney(loanRequestRecieved.getAmountOfMoney());
        text = loanRequestRecieved.getUserName() +"(" + loanRequestRecieved.getUserId() + ")님 " + loanRequestRecieved.getRequestName() + " 완료 하였습니다.";
        loanMessenger.setText(text);
        loanMessengerRepository.save(loanMessenger);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverLoanJudged_SendMessage(@Payload LoanJudged loanJudged){

        if(!loanJudged.validate()) return;
        System.out.println("\n\n##### listener SendMessage : " + loanJudged.toJson() + "\n\n");

        String loanStatus = "";
        String text = "";
 
        LoanMessenger loanMessenger = new LoanMessenger();
        loanMessenger.setUserId(loanJudged.getUserId());
        loanMessenger.setUserName(loanJudged.getUserName());
        loanMessenger.setUserMobile(loanJudged.getUserMobile());
        loanMessenger.setProcDate(new Date());
        if( "01".equals(loanJudged.getLoanStatus() ) ){
            loanStatus = "심사진행";
        }
        else if( "02".equals(loanJudged.getLoanStatus() ) ){
            loanStatus = "대출가능";
        }
        else if( "03".equals(loanJudged.getLoanStatus() ) ){
            loanStatus = "대출불가";
        }
        loanMessenger.setLoanStatus(loanStatus);
        loanMessenger.setAmountOfMoney(loanJudged.getAmountOfMoney());
        text = loanJudged.getUserName() +"(" + loanJudged.getUserId() + ")님은 " + loanStatus + " 상태입니다. " ;

        System.out.println("###############################################################");
        System.out.println("###############################################################");
        System.out.println("###############################################################");
        if( !"".equals(loanJudged.getAdmComment()) ){
            text += loanJudged.getAdmComment() ;
            System.out.println("text : " + text);
        }
        System.out.println("###############################################################");
        System.out.println("###############################################################");
        System.out.println("###############################################################");
        loanMessenger.setText(text);
        loanMessengerRepository.save(loanMessenger);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverExcecuteLoanRequested_SendMessage(@Payload ExcecuteLoanRequested excecuteLoanRequested){

        if(!excecuteLoanRequested.validate()) return;
        System.out.println("\n\n##### listener SendMessage : " + excecuteLoanRequested.toJson() + "\n\n");

        String text = "";
        String loanStatusName = "대출실행";
        LoanMessenger loanMessenger = new LoanMessenger();
        loanMessenger.setUserId(excecuteLoanRequested.getUserId());
        loanMessenger.setUserName(excecuteLoanRequested.getUserName());
        loanMessenger.setUserMobile(excecuteLoanRequested.getUserMobile());
        loanMessenger.setProcDate(new Date());
        loanMessenger.setLoanStatus(loanStatusName);
        loanMessenger.setAmountOfMoney(excecuteLoanRequested.getAmountOfMoney());
        text = excecuteLoanRequested.getUserName() +"(" + excecuteLoanRequested.getUserId() + ")님 " + excecuteLoanRequested.getAmountOfMoney() + "원 대출 되었습니다. ";
        if( !"".equals(excecuteLoanRequested.getAdmComment() ) ){
            text += excecuteLoanRequested.getAdmComment() ;
        }
        loanMessenger.setText(text);
        loanMessengerRepository.save(loanMessenger);
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRepayLoanRequested_SendMessage(@Payload RepayLoanRequested repayLoanRequested){

        if(!repayLoanRequested.validate()) return;
        System.out.println("\n\n##### listener SendMessage : " + repayLoanRequested.toJson() + "\n\n");

        String text = "";
        String loanStatusName = "대출상환";
        LoanMessenger loanMessenger = new LoanMessenger();
        loanMessenger.setUserId(repayLoanRequested.getUserId());
        loanMessenger.setUserName(repayLoanRequested.getUserName());
        loanMessenger.setUserMobile(repayLoanRequested.getUserMobile());
        loanMessenger.setProcDate(new Date());
        loanMessenger.setLoanStatus(loanStatusName);
        loanMessenger.setAmountOfMoney(repayLoanRequested.getAmountOfMoney());
        text = repayLoanRequested.getUserName() +"(" + repayLoanRequested.getUserId() + ")님 " + repayLoanRequested.getAmountOfMoney() + "원 대출상환 되었습니다. ";
        if( !"".equals(repayLoanRequested.getAdmComment() ) ){
            text += repayLoanRequested.getAdmComment() ;
        }
        loanMessenger.setText(text);
        loanMessengerRepository.save(loanMessenger);
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
