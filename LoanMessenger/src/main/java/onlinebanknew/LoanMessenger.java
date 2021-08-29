package onlinebanknew;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
//import java.util.List;
import java.util.Date;

@Entity
@Table(name="LoanMessenger_table")
public class LoanMessenger {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String userId;
    private String userName;
    private Date procDate;
    private String loanStatus;
    private Long amountOfMoney;
    private String userMobile;
    private String text;

    @PostPersist
    public void onPostPersist(){
        MessegeSent messegeSent = new MessegeSent();
        BeanUtils.copyProperties(this, messegeSent);
        messegeSent.publishAfterCommit();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public Date getProcDate() {
        return procDate;
    }

    public void setProcDate(Date procDate) {
        this.procDate = procDate;
    }
    public String getLoanStatus() {
        return loanStatus;
    }

    public void setLoanStatus(String loanStatus) {
        this.loanStatus = loanStatus;
    }
    public Long getAmountOfMoney() {
        return amountOfMoney;
    }

    public void setAmountOfMoney(Long amountOfMoney) {
        this.amountOfMoney = amountOfMoney;
    }
    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }


}