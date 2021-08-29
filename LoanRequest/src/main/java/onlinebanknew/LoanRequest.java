package onlinebanknew;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.Date;

@Entity
@Table(name="LoanRequest_table")
public class LoanRequest {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String requestId;
    private String requestName;
    private Date requestDate;
    private String userId;
    private String userName;
    private String userPassword;
    private String userMobile;
    private Long amountOfMoney;
    private String requestStatus;
    private Long loanRequestId;

    @PostPersist
    public void onPostPersist(){

        if( "01".equals( getRequestId() ) ){
            onlinebanknew.external.LoanAuth loanAuth = new onlinebanknew.external.LoanAuth();
            BeanUtils.copyProperties(this, loanAuth);
            loanAuth.setLoanRequestId( getId() );
            loanAuth.setRequestDate( new Date() );
            LoanRequestApplication.applicationContext.getBean(onlinebanknew.external.LoanAuthService.class).requestAuth(loanAuth);
        }

        else if( "02".equals( getRequestId() ) ){
            onlinebanknew.external.LoanAuth loanAuth = new onlinebanknew.external.LoanAuth();
            BeanUtils.copyProperties(this, loanAuth);
            loanAuth.setLoanRequestId( getId() );
            loanAuth.setRequestDate( new Date() );
            LoanRequestApplication.applicationContext.getBean(onlinebanknew.external.LoanAuthService.class).requestAuth(loanAuth);
        }
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
    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }
    public Long getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Long amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }
    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }
    public Long getLoanRequestId() {
        return loanRequestId;
    }

    public void setLoanRequestId(Long loanRequestId) {
        this.loanRequestId = loanRequestId;
    }




}