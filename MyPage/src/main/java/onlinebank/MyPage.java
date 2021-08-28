package onlinebank;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="MyPage_table")
public class MyPage {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String accountNo;
    private Date lastModifiedDate;
    private String lastRequestId;
    private String lastRequestName;
    private Integer requestMoney;
    private Integer amountOfMoney;
    private String requestStatus;

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

	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getLastRequestId() {
		return this.lastRequestId;
	}

	public void setLastRequestId(String lastRequestId) {
		this.lastRequestId = lastRequestId;
	}

	public String getLastRequestName() {
		return this.lastRequestName;
	}

	public void setLastRequestName(String lastRequestName) {
		this.lastRequestName = lastRequestName;
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

	public String getRequestStatus() {
		return this.requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}



}
