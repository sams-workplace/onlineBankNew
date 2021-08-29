package onlinebanknew;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="LoanManager_table")
public class LoanManager {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String requestId;
    private String requestName;
    private Date requestDate;
    private String procId;
    private String procName;
    private Date procDate;
    private String userId;
    private String userName;
    private String userPassword;
    private String userMobile;
    private Long amountOfMoney;
    private String loanStatus;
    private Long loanRequestId;

    @PostPersist
    public void onPostPersist(){
      
        LoanRequestRecieved loanRequestRecieved = new LoanRequestRecieved();
        BeanUtils.copyProperties(this, loanRequestRecieved);
        loanRequestRecieved.publishAfterCommit();

        // RequestSearched requestSearched = new RequestSearched();
        // BeanUtils.copyProperties(this, requestSearched);
        // requestSearched.publishAfterCommit();

        // onlinebanknew.external.LoanAuth loanAuth = new onlinebanknew.external.LoanAuth();
        // // mappings goes here
        // LoanManagerApplication.applicationContext.getBean(onlinebanknew.external.LoanAuthService.class).requestAuth(loanAuth);

        // LoanJudged loanJudged = new LoanJudged();
        // BeanUtils.copyProperties(this, loanJudged);
        // loanJudged.publishAfterCommit();

        // ExcecuteLoanRequested excecuteLoanRequested = new ExcecuteLoanRequested();
        // BeanUtils.copyProperties(this, excecuteLoanRequested);
        // excecuteLoanRequested.publishAfterCommit();

        // onlinebanknew.external.LoanAuth loanAuth = new onlinebanknew.external.LoanAuth();
        // // mappings goes here
        // LoanManagerApplication.applicationContext.getBean(onlinebanknew.external.LoanAuthService.class).requestAuth(loanAuth);

        // RepayLoanRequested repayLoanRequested = new RepayLoanRequested();
        // BeanUtils.copyProperties(this, repayLoanRequested);
        // repayLoanRequested.publishAfterCommit();

        // onlinebanknew.external.LoanAuth loanAuth = new onlinebanknew.external.LoanAuth();
        // // mappings goes here
        // LoanManagerApplication.applicationContext.getBean(onlinebanknew.external.LoanAuthService.class).requestAuth(loanAuth);

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
    public String getProcId() {
        return procId;
    }

    public void setProcId(String procId) {
        this.procId = procId;
    }
    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }
    public Date getProcDate() {
        return procDate;
    }

    public void setProcDate(Date procDate) {
        this.procDate = procDate;
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
    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }
    public Long getLoanRequestId() {
        return loanRequestId;
    }

    public void setLoanRequestId(Long loanRequestId) {
        this.loanRequestId = loanRequestId;
    }

}