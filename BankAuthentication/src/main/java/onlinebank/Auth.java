package onlinebank;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Entity
@Table(name="Auth_table")
public class Auth {

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
    private Long bankRequestId ;

    @PrePersist
    public void onPrePersist(){

        String userId = "1@sk.com";
        String userName = "sam";
        String userPassword = "1234";
        boolean authResult = false ;

        if( userId.equals( getUserId() ) && userName.equals( getUserName() ) && userPassword.equals( getUserPassword() ) ){
            authResult = true ;
        }

        System.out.println("\nBankAuthentication.Auth.39\n###############################################");
        System.out.println("authResult : " + authResult) ;
        System.out.println("requestId : " + getRequestId()) ;
        System.out.println("requestName : " + getRequestName()) ;
        System.out.println("###############################################\n");

        if( authResult == false ){
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
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNo() {
		return this.accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getRequestId() {
		return this.requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestName() {
		return this.requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public Date getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Integer getAmountOfMoney() {
		return this.amountOfMoney;
	}

	public void setAmountOfMoney(Integer amountOfMoney) {
		this.amountOfMoney = amountOfMoney;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return this.userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public Long getBankRequestId() {
		return this.bankRequestId;
	}

	public void setBankRequestId(Long bankRequestId) {
		this.bankRequestId = bankRequestId;
	}

}