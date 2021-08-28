package onlinebank;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;
import java.time.LocalDateTime;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="Account_table")
public class Account {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String accountNo;
    private String requestId;
    private String requestName;
    //@LastModifiedDate
    private Date requestDate;
    private Integer amountOfMoney;
    //@CreatedDate
    private Date createDate;
    private String accountStatus;
    private Long bankRequestId;
    private Integer requestMoney;

    // 계좌생성
    @PostPersist
    public void onPostPersist(){
        AccountRequestCompleted accountRequestCompleted = new AccountRequestCompleted();
        BeanUtils.copyProperties(this, accountRequestCompleted);
        accountRequestCompleted.publishAfterCommit();
    }

    // 계좌삭제
    @PostRemove
    public void onPostRemove(){
        AccountRequestCompleted accountRequestCompleted = new AccountRequestCompleted();
        BeanUtils.copyProperties(this, accountRequestCompleted);
        accountRequestCompleted.publishAfterCommit();
    }

    // 계좌정보 업데이트 
    @PostUpdate
    public void eventPublish(){
        AccountRequestCompleted accountRequestCompleted = new AccountRequestCompleted();
        BeanUtils.copyProperties(this, accountRequestCompleted);
        accountRequestCompleted.publishAfterCommit();
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
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Long getBankRequestId() {
        return bankRequestId;
    }

    public void setBankRequestId(Long bankRequestId) {
        this.bankRequestId = bankRequestId;
    }

	public Integer getRequestMoney() {
		return this.requestMoney;
	}

	public void setRequestMoney(Integer requestMoney) {
		this.requestMoney = requestMoney;
	}
}