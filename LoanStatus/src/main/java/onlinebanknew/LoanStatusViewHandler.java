package onlinebanknew;

import onlinebanknew.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class LoanStatusViewHandler {


    @Autowired
    private LoanStatusRepository loanStatusRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenLoanRequestRecieved_then_CREATE_1 (@Payload LoanRequestRecieved loanRequestRecieved) {
        try {

            if (!loanRequestRecieved.validate()) return;

            // view 객체 생성
            LoanStatus loanStatus = new LoanStatus();
            // view 객체에 이벤트의 Value 를 set 함
            loanStatus.setLoanRequestId(loanRequestRecieved.getLoanRequestId());
            loanStatus.setRequestId(loanRequestRecieved.getRequestId());
            loanStatus.setRequestName(loanRequestRecieved.getRequestName());
            loanStatus.setProcId(loanRequestRecieved.getProcId());
            loanStatus.setProcName(loanRequestRecieved.getProcName());
            loanStatus.setProcDate(loanRequestRecieved.getProcDate());
            loanStatus.setLoanStatus(loanRequestRecieved.getLoanStatus());
            loanStatus.setAmountOfMoney(loanRequestRecieved.getAmountOfMoney());
            loanStatus.setRequestDate(loanRequestRecieved.getRequestDate());
            // view 레파지 토리에 save
            loanStatusRepository.save(loanStatus);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



}

