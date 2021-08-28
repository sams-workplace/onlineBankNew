package onlinebank;

import onlinebank.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import java.util.Optional;
import java.util.Date;

@Service
public class PolicyHandler{
    @Autowired AccountRepository accountRepository;

    // 조회( RequestName : Balance )
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBalanceRequested_RecieveAccountRequest(@Payload BalanceRequested balanceRequested){

        if(!balanceRequested.validate()) return;
        System.out.println("\nBankAccount.PolicyHandler.23\n##### listener RecieveAccountRequest : " + balanceRequested.toJson());
        
        // 조회계좌가 없을 경우 
        if( accountRepository.findByAccountNo( balanceRequested.getAccountNo() ) == null ){
            System.out.println("\nBankAccount.PolicyHandler.27\n##############################################");
            System.out.println("Balance : Account Not Exist");
            System.out.println("accountRequestCancelled");
            System.out.println("##############################################\n");

            AccountRequestCancelled accountRequestCancelled = new AccountRequestCancelled();
            
            accountRequestCancelled.setAccountNo(balanceRequested.getAccountNo());
            accountRequestCancelled.setMessage("Account Not Exist");
            accountRequestCancelled.setRequestName(balanceRequested.getRequestName());
            accountRequestCancelled.setBankRequestId(balanceRequested.getBankRequestId());
            accountRequestCancelled.setRequestDate(new Date());
            
            accountRequestCancelled.publish();
        }
        // 조회계좌가 있을 경우
        else{
            System.out.println("\nBankAccount.PolicyHandler.44\n##############################################");
            System.out.println("Balance : Account Exist");
            System.out.println("accountRequestCompleted");
            System.out.println("##############################################\n");

            Account account = accountRepository.findByAccountNo( balanceRequested.getAccountNo() );
            AccountRequestCompleted accountRequestCompleted = new AccountRequestCompleted();
               
            accountRequestCompleted.setAccountNo(balanceRequested.getAccountNo());
            accountRequestCompleted.setAmountOfMoney(account.getAmountOfMoney());
            accountRequestCompleted.setRequestId(balanceRequested.getRequestId());
            accountRequestCompleted.setRequestName(balanceRequested.getRequestName());
            accountRequestCompleted.setRequestDate(new Date());
            
            accountRequestCompleted.publish();
        }   

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCertified_RecieveAccountRequest(@Payload AuthCertified authCertified){

        if(!authCertified.validate()) return;
        
        // 입금처리( RequestId : Deposit )
        if( "01".equals( authCertified.getRequestId() ) ){

           // 입금계좌가 없을 경우 
           if( accountRepository.findByAccountNo( authCertified.getAccountNo() ) == null ){
               System.out.println("\nBankAccount.PolicyHandler.73\n##############################################");
               System.out.println("Deposit : Account Not Exist");
               System.out.println("accountRequestCancelled");
               System.out.println("##############################################\n");

               AccountRequestCancelled accountRequestCancelled = new AccountRequestCancelled();
               
               accountRequestCancelled.setAccountNo(authCertified.getAccountNo());
               accountRequestCancelled.setBankRequestId(authCertified.getBankRequestId());
               accountRequestCancelled.setMessage("Account Not Exist");
               accountRequestCancelled.setRequestId(authCertified.getRequestId());
               accountRequestCancelled.setRequestName(authCertified.getRequestName());
               accountRequestCancelled.setRequestMoney(authCertified.getAmountOfMoney());
               accountRequestCancelled.setRequestDate(new Date());
               
               accountRequestCancelled.publish();
           }
           // 입금계좌가 있을 경우
           else{
               System.out.println("\nBankAccount.PolicyHandler.92\n##############################################");
               System.out.println("Deposit : Account Exist");
               System.out.println("accountRequestCompleted");
               System.out.println("##############################################\n");

               Account account = accountRepository.findByAccountNo( authCertified.getAccountNo() );
               account.setAccountNo(authCertified.getAccountNo());
               account.setRequestId(authCertified.getRequestId());
               account.setRequestName(authCertified.getRequestName());
               account.setRequestMoney(authCertified.getAmountOfMoney());
               account.setAmountOfMoney(account.getAmountOfMoney() + authCertified.getAmountOfMoney() );
               account.setRequestDate(new Date());
               accountRepository.save(account);
           }
        }
        // 출금처리( RequestId : Withdraw )
        if( "02".equals( authCertified.getRequestId() ) ){
            // 출금계좌가 없을 경우 
            if( accountRepository.findByAccountNo( authCertified.getAccountNo() ) == null ){
                System.out.println("\nBankAccount.PolicyHandler.111\n##############################################");
                System.out.println("Withdraw : Account Not Exist");
                System.out.println("accountRequestCancelled");
                System.out.println("##############################################\n");
    
                AccountRequestCancelled accountRequestCancelled = new AccountRequestCancelled();
                
                accountRequestCancelled.setAccountNo(authCertified.getAccountNo());
                accountRequestCancelled.setBankRequestId(authCertified.getBankRequestId());
                accountRequestCancelled.setMessage("Account Not Exist");
                accountRequestCancelled.setRequestId(authCertified.getRequestId());
                accountRequestCancelled.setRequestName(authCertified.getRequestName());
                accountRequestCancelled.setRequestDate(new Date());
                
                accountRequestCancelled.publish();
            }
            // 출금계좌가 있을 경우
            else{
                System.out.println("\nBankAccount.PolicyHandler.129\n##############################################");
                System.out.println("Withdraw : Account Exist");
                System.out.println("accountRequestCompleted");
                System.out.println("##############################################\n");
    
                Account account = accountRepository.findByAccountNo( authCertified.getAccountNo() );

                if( account.getAmountOfMoney() - authCertified.getAmountOfMoney() >= 0 ){
                    account.setRequestMoney(authCertified.getAmountOfMoney());
                    account.setAmountOfMoney(account.getAmountOfMoney() - authCertified.getAmountOfMoney() );
                    account.setAccountNo(authCertified.getAccountNo());
                    account.setRequestId(authCertified.getRequestId());
                    account.setRequestName(authCertified.getRequestName());
                    account.setRequestDate(new Date());

                    accountRepository.save(account);
                }
                else{
                    System.out.println("\nBankAccount.PolicyHandler.147\n##############################################");
                    System.out.println("Withdraw : Account Exist But Not Sufficient Money");
                    System.out.println("accountRequestCancelled");
                    System.out.println("##############################################\n");

                    AccountRequestCancelled accountRequestCancelled = new AccountRequestCancelled();

                    accountRequestCancelled.setAccountNo(authCertified.getAccountNo());
                    accountRequestCancelled.setBankRequestId(authCertified.getBankRequestId());
                    accountRequestCancelled.setMessage("Not Sufficient");
                    accountRequestCancelled.setRequestId(authCertified.getRequestId());
                    accountRequestCancelled.setRequestName(authCertified.getRequestName());
                    accountRequestCancelled.setRequestDate(new Date());
                    
                    accountRequestCancelled.publish();                    
                }
            }
        }
        // 계좌생성( RequestId : addAccount )
        if( "05".equals( authCertified.getRequestId() ) ){
            // 계좌가 존재하지 않을 경우
            if( accountRepository.findByAccountNo( authCertified.getAccountNo() ) == null ){
                System.out.println("\nBankAccount.PolicyHandler.169\n##############################################");
                System.out.println("Add Acount : Account Not Exist");
                System.out.println("accountRequestCompleted");
                System.out.println("##############################################\n");
 
                Account account = new Account();
                account.setAccountNo(authCertified.getAccountNo());
                account.setAccountStatus("Use");
                account.setAmountOfMoney(authCertified.getAmountOfMoney());
                account.setRequestId(authCertified.getRequestId());
                account.setRequestName(authCertified.getRequestName());
                account.setRequestDate(new Date());
                account.setCreateDate(new Date());
 
                accountRepository.save(account);
            }
            // 계좌가 존재하는 경우
            else{
                System.out.println("\nBankAccount.PolicyHandler.187\n##############################################");
                System.out.println("Add Account : Account Exist");
                System.out.println("accountRequestCancelled");
                System.out.println("##############################################\n");
 
                AccountRequestCancelled accountRequestCancelled = new AccountRequestCancelled();
                accountRequestCancelled.setAccountNo(authCertified.getAccountNo());
                accountRequestCancelled.setBankRequestId(authCertified.getBankRequestId());
                accountRequestCancelled.setMessage("Account Exist");
                accountRequestCancelled.setRequestId(authCertified.getRequestId());
                accountRequestCancelled.setRequestName(authCertified.getRequestName());
                accountRequestCancelled.setRequestDate(new Date());
                
                accountRequestCancelled.publish();
            }        
        }
        // 계좌삭제( RequestId : deleteAccount )
        if( "06".equals( authCertified.getRequestId() ) ){
            // 삭제 대상 계좌가 없을 경우
            if( accountRepository.findByAccountNo( authCertified.getAccountNo() ) == null ){
                System.out.println("\nBankAccount.PolicyHandler.207\n##############################################");
                System.out.println("Delete Account : Account Not Exist");
                System.out.println("accountRequestCancelled");
                System.out.println("##############################################\n");

                AccountRequestCancelled accountRequestCancelled = new AccountRequestCancelled();
                accountRequestCancelled.setAccountNo(authCertified.getAccountNo());
                accountRequestCancelled.setBankRequestId(authCertified.getBankRequestId());
                accountRequestCancelled.setMessage("Account Not Exist");
                accountRequestCancelled.setRequestId(authCertified.getRequestId());
                accountRequestCancelled.setRequestName(authCertified.getRequestName());
                accountRequestCancelled.setRequestDate(new Date());
                
                accountRequestCancelled.publish();
            }
            // 삭제 대상 계좌가 있을 경우
            else{
                System.out.println("\nBankAccount.PolicyHandler.224\n##############################################");
                System.out.println("Delete Account : Account Exist");
                System.out.println("accountRequestCompleted");
                System.out.println("##############################################\n");
 
                Account account = accountRepository.findByAccountNo( authCertified.getAccountNo() );
                accountRepository.deleteById(account.getId());
            }
            System.out.println("BankAccount.PolicyHandler.232\n##### listener RecieveAccountRequest : " + authCertified.toJson() + "\n");   
        }  
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
