package onlinebanknew;
import java.util.Date;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="LoanStatus_table")
public class LoanStatus {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long loanRequestId;
        private String requestId;
        private String requestName;
        private String requestDate;
        private String procId;
        private String procName;
        private Date procDate;
        private String loanStatus;
        private Long amountOfMoney;


        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
        public Long getLoanRequestId() {
            return loanRequestId;
        }

        public void setLoanRequestId(Long loanRequestId) {
            this.loanRequestId = loanRequestId;
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
        public String getRequestDate() {
            return requestDate;
        }

        public void setRequestDate(String requestDate) {
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

}
