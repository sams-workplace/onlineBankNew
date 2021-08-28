package onlinebank;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Request_table")
public class Request {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String accountNo;
    private String requestId;
    private String requestName;
    private Date requestDate;
    private Integer amountOfMoney;
    private String userId;
    private String userName;
    private String userPassword;

    @PostPersist
    public void onPostPersist(){

        System.out.println("\nBankRequest.Request.27\n#########################################################################");
        System.out.println("requestId : " + getRequestId() ) ;    
        System.out.println("\nrequestName : " + getRequestName() ) ;      
        System.out.println("#########################################################################\n");
        
        // requestId
        // "01" : Deposit
        // "02" : Withdraw
        // "03" : Balance
        // "04" : Transaction  
        // "05" : addAccount
        // "06" : deleteAccount
        if( "01".equals( getRequestId() ) || // Deposit
            "02".equals( getRequestId() ) || // Withdraw
            "05".equals( getRequestId() ) || // addAccount
            "06".equals( getRequestId() ) ){ // deleteAccount 
            onlinebank.external.Auth auth = new onlinebank.external.Auth();
            BeanUtils.copyProperties(this, auth);
            auth.setBankRequestId( getId() );
            BankRequestApplication.applicationContext.getBean(onlinebank.external.AuthService.class).requestAuth(auth);
        }
        
        // Balance
        if( "03".equals( getRequestId() ) ){ 
            BalanceRequested balanceRequested = new BalanceRequested();
            BeanUtils.copyProperties(this, balanceRequested);
            balanceRequested.setBankRequestId( getId() );
            balanceRequested.publish();
        }

        // Transaction
        if( "04".equals( getRequestId() ) ){ 
            TransactionRequested transactionRequested = new TransactionRequested();
            BeanUtils.copyProperties(this, transactionRequested);
            transactionRequested.publishAfterCommit();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }
    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
    public Integer getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Integer amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}