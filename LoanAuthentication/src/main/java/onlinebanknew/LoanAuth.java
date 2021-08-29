package onlinebanknew;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.Date;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Entity
@Table(name="LoanAuth_table")
public class LoanAuth {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String requestId;
    private String requestName;
    private Date requestDate;
    private String userId;
    private String userName;
    private String userMobile;
    private String userPassword;
    private Long amountOfMoney;
    private Long loanRequestId;

    @PostPersist
    public void onPostPersist(){

        String chkUserId = "1@sk.com";
        String chkUserName = "유은상";
        String chkUserPassword = "1234";
        Boolean loginFlag = false;

        if( userId.equals( chkUserId ) && userName.equals( chkUserName ) && userPassword.equals( chkUserPassword ) ){
            loginFlag = true;
        } 

        if( loginFlag == false ){
            AuthCancelled authCancelled = new AuthCancelled();
            BeanUtils.copyProperties(this, authCancelled);
            authCancelled.publish();
            
        }else{
            AuthCertified authCertified = new AuthCertified();
            BeanUtils.copyProperties(this, authCertified);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
                    authCertified.publish();
                }
            });
        }
        
		//try {
		//	Thread.currentThread().sleep((long) (420 + Math.random() * 200));
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }
    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public Long getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Long amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }
    public Long getLoanRequestId() {
        return loanRequestId;
    }

    public void setLoanRequestId(Long loanRequestId) {
        this.loanRequestId = loanRequestId;
    }




}