package onlinebank;

import java.util.Date;

public class AccountRequestCancelled extends AbstractEvent {

    private Long id;
    private String accountNo;
    private String requestId;
    private String requestName;
    private Date requestDate;
    private Integer requestMoney;
    private Integer amountOfMoney;
    private Date createDate;
    private String accountStatus;
    private String message;
    private Long bankRequestId;

    public AccountRequestCancelled(){
        super();
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

	public Integer getRequestMoney() {
		return this.requestMoney;
	}

	public void setRequestMoney(Integer requestMoney) {
		this.requestMoney = requestMoney;
	}

	public Integer getAmountOfMoney() {
		return this.amountOfMoney;
	}

	public void setAmountOfMoney(Integer amountOfMoney) {
		this.amountOfMoney = amountOfMoney;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getAccountStatus() {
		return this.accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getBankRequestId() {
		return this.bankRequestId;
	}

	public void setBankRequestId(Long bankRequestId) {
		this.bankRequestId = bankRequestId;
	}
}
