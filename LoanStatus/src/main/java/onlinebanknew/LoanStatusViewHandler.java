package onlinebanknew;

import onlinebanknew.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class LoanStatusViewHandler {


    @Autowired
    private LoanStatusRepository loanStatusRepository;

    @StreamListener(KafkaProcessor.INPUT)
    // 대출신청, 대출상환신청
    public void whenLoanRequestRecieved_then_CREATE_1 (@Payload LoanRequestRecieved loanRequestRecieved) {
        try {

            if (!loanRequestRecieved.validate()) return;

            LoanStatus loanStatus = new LoanStatus();
            loanStatus.setLoanRequestId(loanRequestRecieved.getLoanRequestId());
            loanStatus.setRequestId(loanRequestRecieved.getRequestId());
            loanStatus.setRequestName(loanRequestRecieved.getRequestName());
            loanStatus.setProcId(loanRequestRecieved.getProcId());
            loanStatus.setProcName(loanRequestRecieved.getProcName());
            loanStatus.setProcDate(new Date());
            loanStatus.setLoanStatus(loanRequestRecieved.getLoanStatus());
            loanStatus.setAmountOfMoney(loanRequestRecieved.getAmountOfMoney());
            loanStatus.setRequestDate(loanRequestRecieved.getRequestDate());
            loanStatusRepository.save(loanStatus);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 대출심사
    @StreamListener(KafkaProcessor.INPUT)
    public void whenLoanJudged_then_CREATE_2 (@Payload LoanJudged loanJudged) {
        try {

            if (!loanJudged.validate()) return;

            String loanStatusName = "";
            if( "01".equals(loanJudged.getLoanStatus() ) ){
                loanStatusName = "심사진행";
            }
            else if( "02".equals(loanJudged.getLoanStatus() ) ){
                loanStatusName = "대출가능";
            }
            else if( "03".equals(loanJudged.getLoanStatus() ) ){
                loanStatusName = "대출불가";
            }
            
            LoanStatus loanStatus = new LoanStatus();
            loanStatus.setLoanRequestId(loanJudged.getLoanRequestId());
            loanStatus.setRequestId(loanJudged.getRequestId());
            loanStatus.setRequestName(loanJudged.getRequestName());
            loanStatus.setProcId(loanJudged.getProcId());
            loanStatus.setProcName(loanJudged.getProcName());
            loanStatus.setProcDate(new Date());
            loanStatus.setLoanStatus(loanStatusName);
            loanStatus.setAmountOfMoney(loanJudged.getAmountOfMoney());
            loanStatus.setRequestDate(loanJudged.getRequestDate());
            loanStatusRepository.save(loanStatus);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 대출실행
    @StreamListener(KafkaProcessor.INPUT)
    public void whenExcecuteLoanRequested_then_CREATE_3 (@Payload ExcecuteLoanRequested excecuteLoanRequested) {
        try {

            if (!excecuteLoanRequested.validate()) return;

            String loanStatusName = "대출실행";
            LoanStatus loanStatus = new LoanStatus();
            loanStatus.setLoanRequestId(excecuteLoanRequested.getLoanRequestId());
            loanStatus.setRequestId(excecuteLoanRequested.getRequestId());
            loanStatus.setRequestName(excecuteLoanRequested.getRequestName());
            loanStatus.setProcId(excecuteLoanRequested.getProcId());
            loanStatus.setProcName(excecuteLoanRequested.getProcName());
            loanStatus.setProcDate(new Date());
            loanStatus.setLoanStatus(loanStatusName);
            loanStatus.setAmountOfMoney(excecuteLoanRequested.getAmountOfMoney());
            loanStatus.setRequestDate(excecuteLoanRequested.getRequestDate());
            loanStatusRepository.save(loanStatus);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenRepayLoanRequested_then_CREATE_4 (@Payload RepayLoanRequested repayLoanRequested) {
        try {

            if (!repayLoanRequested.validate()) return;

            String loanStatusName = "대출상환";
            LoanStatus loanStatus = new LoanStatus();
            loanStatus.setLoanRequestId(repayLoanRequested.getLoanRequestId());
            loanStatus.setRequestId(repayLoanRequested.getRequestId());
            loanStatus.setRequestName(repayLoanRequested.getRequestName());
            loanStatus.setProcId(repayLoanRequested.getProcId());
            loanStatus.setProcName(repayLoanRequested.getProcName());
            loanStatus.setProcDate(new Date());
            loanStatus.setLoanStatus(loanStatusName);
            loanStatus.setAmountOfMoney(repayLoanRequested.getAmountOfMoney());
            loanStatus.setRequestDate(repayLoanRequested.getRequestDate());
            loanStatusRepository.save(loanStatus);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

