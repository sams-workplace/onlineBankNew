package onlinebank;
import java.util.Date;

public class HistoryAdded extends AbstractEvent {

    private Long id;
    private String accountNo;
    private Date requestDate;
    private String requestId;
    private String requestName;
    private Integer requestMoney;
    private Integer amountOfMoney;

    public HistoryAdded(){
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

	public Date getRequestDate() {
		return this.requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
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
}
