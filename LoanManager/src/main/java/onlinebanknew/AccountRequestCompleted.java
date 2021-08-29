package onlinebanknew;
import java.util.Date;

public class AccountRequestCompleted extends AbstractEvent {

    private Long id;
    private String accountNo;
    private String requestId;
    private String requestName;
    private Date requestDate;
    private Integer amountOfMoney;
    private Date createDate;
    private String accountStatus;
    private Long bankRequestId;
    private Integer requestMoney;

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
        return requestMoney;
    }

    public void setRequestMoney(Integer requestMoney) {
        this.requestMoney = requestMoney;
    }
}