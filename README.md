# 온라인 대출 시스템

## 서비스 시나리오

### 기능적 요구 사항

1. 고객이 대출 관련 업무를 신청한다. 
   - 신규 대출 신청, 대출 상환  
2. 고객 대출업무 신청시 개인정보를 인증한다. 
3. 대출 담당자가 신청정보를 확인한다. 
4. 대출 담당자가 신청건에 대해 심사, 심사결과 전달, 대출실행, 대출상환 처리한다.
5. 업무 처리현황을 고객에게 메세지로 전송한다.
6. 업무 처리현황을 실시간으로 확인한다.     

*****

### 비기능적 요구 사항
1. 트랜잭션 
   - 고객 요청이 대출관리시스템에 정상적으로 전송되는 경우 요청 상태를 업데이트 한다.(Correlation)
   - 개인정보 인증 실패시 서비스 이용이 불가하다 (Sync)

2. 장애격리 
   - 대출 관리 시스템은 24시간 이용이 가능하다 (event-driven 방식 Async 호출)
   - 대출신청 / 대출상환 서비스에 트레픽이 몰리는 경우 잠시 후에 처리하도록 유도한다. (Circuit breaker)

3. 성능
   - 고객이 대출 처리현황을 계속 확인할 수 있어야 한다.(CQRS)
*****

## 분석/설계
![image](https://user-images.githubusercontent.com/87048587/131761942-7296c2f7-1feb-4480-ba83-e1f5f0eb50a4.png)

![image](https://user-images.githubusercontent.com/87048587/131328237-b44498d0-d4cc-4bf0-9fbf-1af78627c631.png)

![image](https://user-images.githubusercontent.com/87048587/131338375-af21a798-a081-4d84-8d8a-316b85df35a2.png)

![image](https://user-images.githubusercontent.com/87048587/131338416-77971fdb-9239-4edc-b372-bee9571db9f8.png)

![image](https://user-images.githubusercontent.com/87048587/131338454-abcae784-26e6-4c20-a88d-da1668b419ff.png)

![image](https://user-images.githubusercontent.com/87048587/131338488-0dafe033-76d4-4beb-962d-b2ac2affa529.png)

![image](https://user-images.githubusercontent.com/87048587/131338524-7bb278a4-c0d7-4ae4-93d4-79e73dc7fdcc.png)

![image](https://user-images.githubusercontent.com/87048587/131338573-ed694a03-4518-4b86-9331-32c0c4d9cc89.png)

![image](https://user-images.githubusercontent.com/87048587/131338624-c9be6c6a-1540-4252-9524-da867fd6fd76.png)

![image](https://user-images.githubusercontent.com/87048587/131338662-b4674cea-f94d-44fc-8bd3-778429fa8ad4.png)

![image](https://user-images.githubusercontent.com/87048587/131338925-3a78322b-42ea-4777-87b8-0be2dc19fcc6.png)

![image](https://user-images.githubusercontent.com/87048587/131338978-1dc09ea4-f7fe-4e82-8391-7a4a0bf43ba0.png)

![image](https://user-images.githubusercontent.com/87048587/131339271-cbf3b54d-f1ce-428b-97d3-a4ae8078cf2a.png)

## 구현

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 
구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (서비스 포트는 8290, 8291, 8292, 8293, 8294, 8295 이다)

```
cd LoanRequest
mvn spring-boot:run

cd LoanAuthentication
mvn spring-boot:run

cd LoanManager
mvn spring-boot:run

cd LoanMessenger
mvn spring-boot:run

cd LoanStatus
mvn spring-boot:run

cd gateway
mvn spring-boot:run

```
*****

### DDD의 적용

1. 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. 
(예시는 LoanRequest 마이크로 서비스 )

#### LoanRequest.java

```
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
    ...
```

2. Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 
데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 
자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

#### LoanRequestRepository.java

```
package onlinebanknew;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="loanRequests", path="loanRequests")
public interface LoanRequestRepository extends PagingAndSortingRepository<LoanRequest, Long>{
    LoanRequest findByLoanRequestId(Long id);
}
```

3. 적용 후 REST API 의 테스트

#### request 서비스의 요청처리

```
http request:8080/requests accountNo="1111" requestId="01" requestName="Deposit" amountOfMoney=10000 userId="1@sk.com" userName="sam" userPassword="1234"  
```

#### 요청상태 확인

```
http http://request:8080/requests/1
```
*****

### 동기식 호출 (구현)

분석단계에서의 조건 중 하나로 요청(Loanrequest)->인증(LoanAuthentication) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 

처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 

이용하여 호출하도록 한다. 

1. 결제서비스를 호출하기 위하여 FeignClient 를 이용하여 Service 대행 인터페이스 구현

#### AuthService.java

```
@FeignClient(name="LoanAuthentication", url="http://localhost:8291")
public interface LoanAuthService {
    @RequestMapping(method= RequestMethod.GET, path="/loanAuths")
    public void requestAuth(@RequestBody LoanAuth loanAuth);

}
```

2. 요청을 받은 직후(@PostPersist) 인증을 요청하도록 처리

#### Request.java

```
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
```
*****

### 비동기식 호출 

인증성공 후 대출 관리시스템으로 데이터를 전송할 경우 동기식이 아니라 비동기식으로 처리하여 

인증 기능이 블로킹 되지 않도록 처리한다.

1. 이를 위하여 인증이력에 기록을 남긴 후에 곧바로 인증완료 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)

#### LoanAuth.java

```
    @PostPersist
    public void onPostPersist(){

        String chkUserId = "1@sk.com";
        String chkUserName = "유은상";
        String chkUserPassword = "1234";
        Boolean loginFlag = false;

        if( userId.equals( chkUserId ) && userName.equals( chkUserName ) && userPassword.equals( chkUserPassword ) ){
            loginFlag = true;
        } 

        if( loginFlag == false ){
            AuthCancelled authCancelled = new AuthCancelled();
            BeanUtils.copyProperties(this, authCancelled);
            authCancelled.publish();
            
        }else{
            AuthCertified authCertified = new AuthCertified();
            BeanUtils.copyProperties(this, authCertified);
            authCertified.publish();
        }
    }
```

2. 대출 관리 시스템에서는 인증 승인 이벤트를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다

#### PolicyHandler.java

```
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCertified_RecieveLoanRequest(@Payload AuthCertified authCertified){

        if(!authCertified.validate()) return;
        System.out.println("\n\n##### listener RecieveLoanRequest : " + authCertified.toJson() + "\n\n");

        LoanManager loanManager = new LoanManager();

        loanManager.setId(authCertified.getLoanRequestId());
        loanManager.setRequestId(authCertified.getRequestId());
        loanManager.setRequestName(authCertified.getRequestName());
        loanManager.setRequestDate(authCertified.getRequestDate());
        loanManager.setUserId(authCertified.getUserId());
        loanManager.setUserName(authCertified.getUserName());
        loanManager.setUserPassword(authCertified.getUserPassword());
        loanManager.setUserMobile(authCertified.getUserMobile());
        loanManager.setAmountOfMoney(authCertified.getAmountOfMoney());
        loanManager.setLoanRequestId(authCertified.getLoanRequestId());

        loanManagerRepository.save(loanManager);
    }
    ...
    
```
*****

### CQRS 

대출신청, 대출상환신청, 대출심사, 대출실행, 대출상환 총 5개의 이벤트가 발생하면 LoanStatus 서비스에서
카프카로 송출된(Publish) 이벤트를 수신(subscribe) 후 데이터를 Insert 한다. 

#### LoanStatusViewHandler.java

```
    ...
    // 대출심사
    @StreamListener(KafkaProcessor.INPUT)
    public void whenLoanJudged_then_CREATE_2 (@Payload LoanJudged loanJudged) {
        try {

            if (!loanJudged.validate()) return;

            String loanStatusName = "";
            if( "01".equals(loanJudged.getLoanStatus() ) ){
                loanStatusName = "심사진행";
            }
            else if( "02".equals(loanJudged.getLoanStatus() ) ){
                loanStatusName = "대출가능";
            }
            else if( "03".equals(loanJudged.getLoanStatus() ) ){
                loanStatusName = "대출불가";
            }
            
            LoanStatus loanStatus = new LoanStatus();
            loanStatus.setLoanRequestId(loanJudged.getLoanRequestId());
            loanStatus.setRequestId(loanJudged.getRequestId());
            loanStatus.setRequestName(loanJudged.getRequestName());
            loanStatus.setProcId(loanJudged.getProcId());
            loanStatus.setProcName(loanJudged.getProcName());
            loanStatus.setProcDate(new Date());
            loanStatus.setLoanStatus(loanStatusName);
            loanStatus.setAmountOfMoney(loanJudged.getAmountOfMoney());
            loanStatus.setRequestDate(loanJudged.getRequestDate());
            loanStatusRepository.save(loanStatus);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    ...
```
### 기능적 요구사항 검증 

#### 테스트 시나리오

1. 고객이 대출 시스템에서 신규 대출 신청을 한다.   
   ```
   root@siege:/# http http://request:8080/loanRequests requestId="01" requestName="대출신청" userId="1@sk.com" userName="유은상" userMobile="010-000-0000" userPassword="1234"  amountOfMoney="100000"
	HTTP/1.1 201 
	Content-Type: application/json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:40:14 GMT
	Location: http://request:8080/loanRequests/1
	Transfer-Encoding: chunked

	{
	    "_links": {
		"loanRequest": {
		    "href": "http://request:8080/loanRequests/1"
		},
		"self": {
		    "href": "http://request:8080/loanRequests/1"
		}
	    },
	    "amountOfMoney": 100000,
	    "loanRequestId": null,
	    "requestDate": null,
	    "requestId": "01",
	    "requestName": "대출신청",
	    "requestStatus": null,
	    "userId": "1@sk.com",
	    "userMobile": "010-000-0000",
	    "userName": "유은상",
	    "userPassword": "1234"
	}
   ```
   - 신청 후 신청정보 확인( loanRequestId, requestDate, requestStatus 상태 업데이트 여부 확인 )
   ``` 
	root@siege:/# http http://request:8080/loanRequests
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:45:59 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanRequests": [
		    {
			"_links": {
			    "loanRequest": {
				"href": "http://request:8080/loanRequests/1"
			    },
			    "self": {
				"href": "http://request:8080/loanRequests/1"
			    }
			},
			"amountOfMoney": 100000,
			"loanRequestId": 1,
			"requestDate": "2021-09-02T05:40:13.526+0000",
			"requestId": "01",
			"requestName": "대출신청",
			"requestStatus": "요청완료",
			"userId": "1@sk.com",
			"userMobile": "010-000-0000",
			"userName": "유은상",
			"userPassword": "1234"
		    }
   ```
   - 메세지 전송내역 확인
   ```
	root@siege:/# http http://messenger:8080/loanMessengers
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:48:15 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanMessengers": [
		    {
			"_links": {
			    "loanMessenger": {
				"href": "http://messenger:8080/loanMessengers/1"
			    },
			    "self": {
				"href": "http://messenger:8080/loanMessengers/1"
			    }
			},
			"amountOfMoney": 100000,
			"loanStatus": "대출신청",
			"procDate": "2021-09-02T05:40:14.696+0000",
			"text": "유은상(1@sk.com)님 대출신청 완료 하였습니다.",
			"userId": "1@sk.com",
			"userMobile": "010-000-0000",
			"userName": "유은상"
		    }
		]
   ```
   - 대출 진행상태 확인
   ```
	root@siege:/# http http://status:8080/loanStatus
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:48:55 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanStatus": [
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/1"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/1"
			    }
			},
			"amountOfMoney": 100000,
			"loanRequestId": 1,
			"loanStatus": null,
			"procDate": "2021-09-02T05:40:14.678+0000",
			"procId": null,
			"procName": null,
			"requestDate": "1630561213526",
			"requestId": "01",
			"requestName": "대출신청"
		    }
		]
   ```

2. 신청 정보에 대해 인증 시스템에서 개인정보를 인증한다. 
   ```
	root@siege:/# http http://auth:8080/loanAuths
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:52:27 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanAuths": [
		    {
			"_links": {
			    "loanAuth": {
				"href": "http://auth:8080/loanAuths/1"
			    },
			    "self": {
				"href": "http://auth:8080/loanAuths/1"
			    }
			},
			"amountOfMoney": 100000,
			"loanRequestId": 1,
			"requestDate": "2021-09-02T05:40:13.526+0000",
			"requestId": "01",
			"requestName": "대출신청",
			"userId": "1@sk.com",
			"userMobile": "010-000-0000",
			"userName": "유은상",
			"userPassword": "1234"
		    }
		]
   ```

3. 대출 담당자가 신청정보를 확인한다.
   ```
	root@siege:/# http http://manager:8080/loanManagers
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:53:10 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanManagers": [
		    {
			"_links": {
			    "loanManager": {
				"href": "http://manager:8080/loanManagers/1"
			    },
			    "self": {
				"href": "http://manager:8080/loanManagers/1"
			    }
			},
			"admComment": null,
			"amountOfMoney": 100000,
			"loanRequestId": 1,
			"loanStatus": null,
			"procDate": null,
			"procId": null,
			"procName": null,
			"requestDate": "2021-09-02T05:40:13.526+0000",
			"requestId": "01",
			"requestName": "대출신청",
			"userId": "1@sk.com",
			"userMobile": "010-000-0000",
			"userName": "유은상",
			"userPassword": "1234"
		    }
		]
   ```
 
4. 대출 담당자가 신청건에 대해 심사를 시작한다. 
   - loanStatus 필드가 "01" 으로 심사진행중임을 확인
   ```
	root@siege:/# http PATCH http://manager:8080/loanManagers/1 procId="adm@sk.com" procName="관리자" loanStatus="01" admComment=""
	HTTP/1.1 200 
	Content-Type: application/json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:54:57 GMT
	Transfer-Encoding: chunked

	{
	    "_links": {
		"loanManager": {
		    "href": "http://manager:8080/loanManagers/1"
		},
		"self": {
		    "href": "http://manager:8080/loanManagers/1"
		}
	    },
	    "admComment": "",
	    "amountOfMoney": 100000,
	    "loanRequestId": 1,
	    "loanStatus": "01",
	    "procDate": null,
	    "procId": "adm@sk.com",
	    "procName": "관리자",
	    "requestDate": "2021-09-02T05:40:13.526+0000",
	    "requestId": "01",
	    "requestName": "대출신청",
	    "userId": "1@sk.com",
	    "userMobile": "010-000-0000",
	    "userName": "유은상",
	    "userPassword": "1234"
	}
   ```

   - 메세지 전송내역 확인
   ```
	root@siege:/# http http://messenger:8080/loanMessengers
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:55:54 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanMessengers": [
		    {
			"_links": {
			    "loanMessenger": {
				"href": "http://messenger:8080/loanMessengers/2"
			    },
			    "self": {
				"href": "http://messenger:8080/loanMessengers/2"
			    }
			},
			"amountOfMoney": 100000,
			"loanStatus": "심사진행",
			"procDate": "2021-09-02T05:54:57.534+0000",
			"text": "유은상(1@sk.com)님은 심사진행 상태입니다. ",
			"userId": "1@sk.com",
			"userMobile": "010-000-0000",
			"userName": "유은상"
		    }
		]
   ```
   - 대출 진행상태 확인
   ```
	root@siege:/# http http://status:8080/loanStatus
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 05:56:55 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanStatus": [
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/2"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/2"
			    }
			},
			"amountOfMoney": 100000,
			"loanRequestId": 1,
			"loanStatus": "심사진행",
			"procDate": "2021-09-02T05:54:57.527+0000",
			"procId": "adm@sk.com",
			"procName": "관리자",
			"requestDate": "1630561213526",
			"requestId": "01",
			"requestName": "대출신청"
		    }
		]
   ```

5. 대출 담당자가 신청건에 대해 심사 완료 후 심사결과를 전송한다. 
   - loanStatus 필드가 "02" 으로 심사신청 완료되었음을 확인
   ```
	root@siege:/# http PATCH http://manager:8080/loanManagers/1 procId="adm@sk.com" procName="관리자" loanStatus="02" admComment=""
	HTTP/1.1 200 
	Content-Type: application/json;charset=UTF-8
	Date: Thu, 02 Sep 2021 06:06:39 GMT
	Transfer-Encoding: chunked

	{
	    "_links": {
		"loanManager": {
		    "href": "http://manager:8080/loanManagers/1"
		},
		"self": {
		    "href": "http://manager:8080/loanManagers/1"
		}
	    },
	    "admComment": "",
	    "amountOfMoney": 1000000,
	    "loanRequestId": 1,
	    "loanStatus": "02",
	    "procDate": null,
	    "procId": "adm@sk.com",
	    "procName": "관리자",
	    "requestDate": "2021-09-02T05:40:13.526+0000",
	    "requestId": "01",
	    "requestName": "대출신청",
	    "userId": "1@sk.com",
	    "userMobile": "010-000-0000",
	    "userName": "유은상",
	    "userPassword": "1234"
	}
   ```

   - 메세지 전송내역 확인
   ```
	root@siege:/# http http://messenger:8080/loanMessengers
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 06:07:25 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanMessengers": [
		    {
			"_links": {
			    "loanMessenger": {
				"href": "http://messenger:8080/loanMessengers/4"
			    },
			    "self": {
				"href": "http://messenger:8080/loanMessengers/4"
			    }
			},
			"amountOfMoney": 1000000,
			"loanStatus": "대출가능",
			"procDate": "2021-09-02T06:06:39.096+0000",
			"text": "유은상(1@sk.com)님은 대출가능 상태입니다. ",
			"userId": "1@sk.com",
			"userMobile": "010-000-0000",
			"userName": "유은상"
		    }
		]
   ```
   - 대출 진행상태 확인
   ```
	root@siege:/# http http://status:8080/loanStatus
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 06:09:02 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanStatus": [
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/4"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/4"
			    }
			},
			"amountOfMoney": 1000000,
			"loanRequestId": 1,
			"loanStatus": "대출가능",
			"procDate": "2021-09-02T06:06:39.078+0000",
			"procId": "adm@sk.com",
			"procName": "관리자",
			"requestDate": "1630561213526",
			"requestId": "01",
			"requestName": "대출신청"
		    }
		]
	    },
	    "_links": {
		"profile": {
		    "href": "http://status:8080/profile/loanStatus"
		},
		"self": {
		    "href": "http://status:8080/loanStatus"
		}
	    }
	}
   ```

6. 대출 담당자가 대출을 실행한다. 
   - loanStatus 필드가 "04" 로 대출 실행되었음을 확인
   ```
	root@siege:/# http PATCH http://manager:8080/loanManagers/1 procId="adm@sk.com" procName="관리자" loanStatus="04" amountOfMoney="1000000" admComment=""
	HTTP/1.1 200 
	Content-Type: application/json;charset=UTF-8
	Date: Thu, 02 Sep 2021 06:09:53 GMT
	Transfer-Encoding: chunked

	{
	    "_links": {
		"loanManager": {
		    "href": "http://manager:8080/loanManagers/1"
		},
		"self": {
		    "href": "http://manager:8080/loanManagers/1"
		}
	    },
	    "admComment": "",
	    "amountOfMoney": 1000000,
	    "loanRequestId": 1,
	    "loanStatus": "04",
	    "procDate": null,
	    "procId": "adm@sk.com",
	    "procName": "관리자",
	    "requestDate": "2021-09-02T05:40:13.526+0000",
	    "requestId": "01",
	    "requestName": "대출신청",
	    "userId": "1@sk.com",
	    "userMobile": "010-000-0000",
	    "userName": "유은상",
	    "userPassword": "1234"
	}

   ```
   - 메세지 전송내역 확인
   ```
	root@siege:/# http http://messenger:8080/loanMessengers
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 06:10:37 GMT
	Transfer-Encoding: chunked
	{
	    "_embedded": {
		"loanMessengers": [
			"_links": {
			    "loanMessenger": {
				"href": "http://messenger:8080/loanMessengers/5"
			    },
			    "self": {
				"href": "http://messenger:8080/loanMessengers/5"
			    }
			},
			"amountOfMoney": 1000000,
			"loanStatus": "대출실행",
			"procDate": "2021-09-02T06:09:53.702+0000",
			"text": "유은상(1@sk.com)님 1000000원 대출 되었습니다. ",
			"userId": "1@sk.com",
			"userMobile": "010-000-0000",
			"userName": "유은상"
		    }
		]
   ```
   - 대출 진행상태 확인
   ```
	root@siege:/# http http://status:8080/loanStatus
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 06:11:33 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanStatus": [
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/5"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/5"
			    }
			},
			"amountOfMoney": 1000000,
			"loanRequestId": 1,
			"loanStatus": "대출실행",
			"procDate": "2021-09-02T06:09:53.698+0000",
			"procId": "adm@sk.com",
			"procName": "관리자",
			"requestDate": null,
			"requestId": "01",
			"requestName": "대출신청"
		    }
		]
   ```

7. 고객이 대출 실행 여부를 최종 확인한다. 
   ```
	root@siege:/# http http://status:8080/loanStatus
	HTTP/1.1 200 
	Content-Type: application/hal+json;charset=UTF-8
	Date: Thu, 02 Sep 2021 06:17:34 GMT
	Transfer-Encoding: chunked

	{
	    "_embedded": {
		"loanStatus": [
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/1"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/1"
			    }
			},
			"amountOfMoney": 100000,
			"loanRequestId": 1,
			"loanStatus": null,
			"procDate": "2021-09-02T05:40:14.678+0000",
			"procId": null,
			"procName": null,
			"requestDate": "1630561213526",
			"requestId": "01",
			"requestName": "대출신청"
		    },
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/2"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/2"
			    }
			},
			"amountOfMoney": 100000,
			"loanRequestId": 1,
			"loanStatus": "심사진행",
			"procDate": "2021-09-02T05:54:57.527+0000",
			"procId": "adm@sk.com",
			"procName": "관리자",
			"requestDate": "1630561213526",
			"requestId": "01",
			"requestName": "대출신청"
		    },
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/4"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/4"
			    }
			},
			"amountOfMoney": 1000000,
			"loanRequestId": 1,
			"loanStatus": "대출가능",
			"procDate": "2021-09-02T06:06:39.078+0000",
			"procId": "adm@sk.com",
			"procName": "관리자",
			"requestDate": "1630561213526",
			"requestId": "01",
			"requestName": "대출신청"
		    },
		    {
			"_links": {
			    "loanStatus": {
				"href": "http://status:8080/loanStatus/5"
			    },
			    "self": {
				"href": "http://status:8080/loanStatus/5"
			    }
			},
			"amountOfMoney": 1000000,
			"loanRequestId": 1,
			"loanStatus": "대출실행",
			"procDate": "2021-09-02T06:09:53.698+0000",
			"procId": "adm@sk.com",
			"procName": "관리자",
			"requestDate": null,
			"requestId": "01",
			"requestName": "대출신청"
		    }
		]
	    },
	    "_links": {
		"profile": {
		    "href": "http://status:8080/profile/loanStatus"
		},
		"self": {
		    "href": "http://status:8080/loanStatus"
		}
	    }
	}

   ```

## 운영
*****

### 서킷 브레이킹

1. Spring FeignClient + Hystrix 옵션을 사용하여 구현

2. 요청-인증시 Request/Response 로 연동하여 구현이 되어있으며 요청이 과도할 경우 CB를 통하여 장애격리 

3. Hystrix 를 설정: 요청처리 쓰레드에서 처리시간이 610 밀리가 넘어서기 시작하여 어느정도 유지되면 

CB 회로가 닫히도록 (요청을 빠르게 실패처리, 차단) 설정

#### application.yml

```
feign:
  hystrix:
    enabled: true

hystrix:
  command:
    default:
      execution.isolation.thread.timeoutInMilliseconds: 610
```

4. 인증 서비스의 임의 부하 처리 

#### LoanAuth.java (Entity)

```
    @PrePersist
    public void onPrePersist(){  

        ...
        
        try {
	    // 인증 데이터 저장 전 처리 시간을 400ms ~ 620ms 강제 지연
            Thread.currentThread().sleep((long) (400 + Math.random() * 220));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

5. 부하테스터 seige 툴을 통한 서킷 브레이커 동작 확인

```
root@siege:/# siege -v -c100 -t90S -r10 --content-type "application/json" 'http://request:8080/loanRequests POST 
{"requestId":"01","requestName":"대출신청","userId":"1@sk.com","userName":"유은상","userMobile":"010-000-0000","userPassword":"1234","amountOfMoney":"100000"}'
( 동시사용자 100명, 90초간 진행 )

HTTP/1.1 500     2.79 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.79 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.62 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.80 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.81 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.81 secs:     254 bytes ==> POST http://request:8080/loanRequests

HTTP/1.1 201     2.85 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.86 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.92 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.94 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.98 secs:     455 bytes ==> POST http://request:8080/loanRequests

HTTP/1.1 500     3.07 secs:     254 bytes ==> POST http://request:8080/loanRequests

HTTP/1.1 201     3.08 secs:     455 bytes ==> POST http://request:8080/loanRequests

HTTP/1.1 500     2.31 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.28 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.26 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.26 secs:     254 bytes ==> POST http://request:8080/loanRequests

HTTP/1.1 201     3.09 secs:     455 bytes ==> POST http://request:8080/loanRequests

HTTP/1.1 500     1.68 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.68 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.81 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.69 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.80 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     3.33 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.88 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.04 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.72 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.79 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     1.79 secs:     254 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 500     2.07 secs:     254 bytes ==> POST http://request:8080/loanRequests

HTTP/1.1 201     3.22 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.81 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.70 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.79 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     3.01 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.79 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.77 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.46 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     2.81 secs:     455 bytes ==> POST http://request:8080/loanRequests

siege aborted due to excessive socket failure; you
can change the failure threshold in $HOME/.siegerc

Transactions:                   1213 hits
Availability:                  52.72 %
Elapsed time:                  31.50 secs
Data transferred:               0.79 MB
Response time:                  2.51 secs
Transaction rate:              38.51 trans/sec
Throughput:                     0.03 MB/sec
Concurrency:                   96.69
Successful transactions:        1213
Failed transactions:            1088
Longest transaction:            9.18
Shortest transaction:           0.02
```
운영시스템은 죽지 않고 지속적으로 CB 에 의하여 적절히 회로가 열림과 닫힘이 벌어지면서 자원을 보호하고 있음을 보여줌. 
동적 Scale out (replica의 자동적 추가,HPA) 을 통하여 시스템을 확장 해주는 후속처리가 필요.
*****

### 오토스케일 아웃

#### 앞서 CB 는 시스템을 안정되게 운영할 수 있게 해줬지만 사용자의 요청을 100% 받아들여주지 못했기 때문에 

이에 대한 보완책으로 자동화된 확장 기능을 적용하고자 한다.

결제서비스에 대한 replica 를 동적으로 늘려주도록 HPA 를 설정한다. 

설정은 CPU 사용량이 50프로를 넘어서면 replica 를 10개까지 늘려준다

```
root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest#  kubectl autoscale deployment request --cpu-percent=50 --min=1 --max=10
horizontalpodautoscaler.autoscaling/request autoscaled
```

#### 부하 테스트 진행

```
root@siege:/# siege -v -c100 -t90S -r10 --content-type "application/json" 'http://request:8080/loanRequests POST 
{"requestId":"01","requestName":"대출신청","userId":"1@sk.com","userName":"유은상","userMobile":"010-000-0000","userPassword":"1234","amountOfMoney":"100000"}'
( 동시사용자 100명, 90초간 진행 )

HTTP/1.1 201     0.31 secs:     453 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.08 secs:     453 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.03 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.08 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.19 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.19 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.78 secs:     453 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.49 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     1.00 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.69 secs:     453 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.40 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.70 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.22 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.49 secs:     453 bytes ==> POST http://request:8080/loanRequests

Lifting the server siege...
Transactions:                   5035 hits
Availability:                 100.00 %
Elapsed time:                  89.92 secs
Data transferred:               2.21 MB
Response time:                  0.67 secs
Transaction rate:              55.99 trans/sec
Throughput:                     0.02 MB/sec
Concurrency:                   37.62
Successful transactions:        5113
Failed transactions:               0
Longest transaction:            8.70
Shortest transaction:           0.01
```

#### Terminal 을 추가하여 오토스케일링 현황을 모니터링 한다. ( watch kubectl get pod )

#### 부하 테스트 진행전 

```
Every 2.0s: kubectl get pod                                                                                    labs--1458334666: Thu Sep  2 10:15:56 2021

NAME                         READY   STATUS    RESTARTS   AGE
auth-76575bb66d-c286z        1/1     Running   0          175m
gateway-867596b974-75dxj     1/1     Running   0          5h24m
manager-57f779bd4f-pn92k     1/1     Running   0          5h24m
messenger-689ff46b85-svwlb   1/1     Running   0          5h24m
request-7bb665d5bf-snvll     1/1     Running   0          29m  
siege                        1/1     Running   0          7h46m   
status-5cd9db6d56-pzj5j      1/1     Running   0          5h24m

```

#### 부하 테스트 진행 후 

```
Every 2.0s: kubectl get pod                                                                                    labs--1458334666: Thu Sep  2 10:25:15 2021

NAME                         READY   STATUS             RESTARTS   AGE
auth-76575bb66d-c286z        1/1     Running            0          3h4m
gateway-867596b974-75dxj     1/1     Running            0          5h33m
manager-57f779bd4f-pn92k     1/1     Running            0          5h33m
messenger-689ff46b85-svwlb   1/1     Running            0          5h33m
request-7bb665d5bf-5tbcr     1/1     Running            0          6m26s
request-7bb665d5bf-7rnhk     1/1     Running            0          6m10s
request-7bb665d5bf-snvll     1/1     Running            0          38m
request-7bb665d5bf-snvzf     1/1     Running            0          6m10s
request-7bb665d5bf-ztmz6     1/1     Running            0          5m56s
siege                        1/1     Running            0          7h55m
status-5cd9db6d56-pzj5j      1/1     Running            0          5h33m
```

#### 부하테스트 결과 Availability 는 100% 를 보이며 성공하였고, 늘어난 pod 개수를 통하여

오토 스케일링이 정상적으로 수행되었음을 확인할 수 있다. 
*****

### 무정지 재배포

#### 무정지 재배포 여부를 확인을 위해서 Autoscaler 와 CB 설정을 제거한다.

```
root@siege:/# siege -v -c100 -t90S -r10 --content-type "application/json" 'http://request:8080/requests POST 
{"requestId":"01","requestName":"대출신청","userId":"1@sk.com","userName":"유은상","userMobile":"010-000-0000","userPassword":"1234","amountOfMoney":"100000"}'
( 동시사용자 100명, 90초간 진행 )
```

#### 부하테스트중 추가 생성한 Terminal 에서 readiness 설정되지 않은 버젼으로 재배포 한다.

```
root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest# kubectl apply -f loanRequest-redeploy.yaml
deployment.apps/request configured
service/request unchanged

HTTP/1.1 201     0.61 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.44 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.92 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.93 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.93 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.91 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     1.30 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.77 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.63 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     3.54 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.78 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.76 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     1.12 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     1.00 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     0.53 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     0.53 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     1.02 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     1.03 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     0.70 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.71 secs:     455 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.70 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     1.66 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     0.73 secs:     455 bytes ==> POST http://request:8080/loanRequests
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[alert] socket: select and discovered it's not ready sock.c:351: Connection timed out
[alert] socket: read check timed out(30) sock.c:240: Connection timed out
[alert] socket: select and discovered it's not ready sock.c:351: Connection timed out
[alert] socket: read check timed out(30) sock.c:240: Connection timed out

siege aborted due to excessive socket failure; you
can change the failure threshold in $HOME/.siegerc

Transactions:                    548 hits
Availability:                  32.79 %
Elapsed time:                  40.59 secs
Data transferred:               0.24 MB
Response time:                  1.33 secs
Transaction rate:              13.50 trans/sec
Throughput:                     0.01 MB/sec
Concurrency:                   17.91
Successful transactions:         548
Failed transactions:            1123
Longest transaction:            9.04
Shortest transaction:           0.02
```

#### 부하테스트중 새로 배포된 서비스를 READY 상태로 인지하여 서비스 중단됨을 확인함.

#### 부하테스트 진행

root@siege:/# siege -v -c100 -t30S -r10 --content-type "application/json" 'http://request:8080/loanRequests POST  
{"requestId":"01","requestName":"대출신청","userId":"1@sk.com","userName":"유은상","userMobile":"010-000-0000","userPassword":"1234","amountOfMoney":"100000"}'
( 동시사용자 100명, 90초간 진행 )

#### 부하테스트중 추가 생성한 Terminal 에서 readiness 설정 되어있는 버젼으로 재배포 한다.

```
root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest# kubectl apply -f loanRequest-deploy.yaml
deployment.apps/request configured
service/request unchanged

HTTP/1.1 201     0.30 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.37 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.30 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.14 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.39 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.15 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.46 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.20 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.46 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.04 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.14 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.19 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.20 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.20 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.35 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.15 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.19 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.15 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.47 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.21 secs:     457 bytes ==> POST http://request:8080/loanRequests
HTTP/1.1 201     0.31 secs:     457 bytes ==> POST http://request:8080/loanRequests

Lifting the server siege...
Transactions:                  11648 hits
Availability:                 100.00 %
Elapsed time:                  29.16 secs
Data transferred:               5.06 MB
Response time:                  0.25 secs
Transaction rate:             399.45 trans/sec
Throughput:                     0.17 MB/sec
Concurrency:                   97.89
Successful transactions:       11648
Failed transactions:               0
Longest transaction:            2.44
Shortest transaction:           0.01
```

#### 배포중 Availability 100%를 보이며 무정지 재배포가 정상적으로 성공하였다.
*****

### Gateway / Corelation

#### Gateway 기능이 정상적으로 수행되는지 확인하기 위하여 Gateway를 통하여 요청서비스를 호출한다.  

```
root@siege:/# http http://gateway:8080/loanRequests requestId="01" requestName="대출신청" userId="1@sk.com" userName="유은상" userMobile="010-000-0000" userPassword="1234" amountOfMoney="100000"

HTTP/1.1 201 Created
Content-Type: application/json;charset=UTF-8
Date: Thu, 02 Sep 2021 13:26:22 GMT
Location: http://request:8080/loanRequests/1
transfer-encoding: chunked

{
    "_links": {
        "loanRequest": {
            "href": "http://request:8080/loanRequests/1"
        },
        "self": {
            "href": "http://request:8080/loanRequests/1"
        }
    },
    "amountOfMoney": 100000,
    "loanRequestId": null,
    "requestDate": null,
    "requestId": "01",
    "requestName": "대출신청",
    "requestStatus": null,
    "userId": "1@sk.com",
    "userMobile": "010-000-0000",
    "userName": "유은상",
    "userPassword": "1234"
}
```

#### 요청 처리결과를 통하여 Gateway 기능이 정상적으로 수행되었음을 확인할 수 있다. 

#### 요청이 정상적으로 접수된 경우 요청 데이터를 업데이트 한다.  

요청시 파라미터로 전송된 id 값을 기준으로 기 저장된 요청 데이터내 처리상태( requestStatus ), 요청번호( loanRequestId ),  
요청일자( requestDate ) 필드를 업데이트 한다.  

```
root@siege:/# http http://request:8080/loanRequests/1

HTTP/1.1 200 
Content-Type: application/hal+json;charset=UTF-8
Date: Thu, 02 Sep 2021 13:26:39 GMT
Transfer-Encoding: chunked

{
    "_links": {
        "loanRequest": {
            "href": "http://request:8080/loanRequests/1"
        },
        "self": {
            "href": "http://request:8080/loanRequests/1"
        }
    },
    "amountOfMoney": 100000,
    "loanRequestId": 1,
    "requestDate": "2021-09-02T13:26:21.492+0000",
    "requestId": "01",
    "requestName": "대출신청",
    "requestStatus": "요청완료",
    "userId": "1@sk.com",
    "userMobile": "010-000-0000",
    "userName": "유은상",
    "userPassword": "1234"
}
```
#### loanRequestId, requestDate, requestStatus 데이터가 정상적으로 업데이트 되었음을 확인할 수 있다. 
*****

### 동기식 호출 (운영)

#### 동기식 호출인 관계로 인증시스템 장애시 서비스를 처리할 수 없다. 

1) 인증 서비스 임시로 삭제한다. 

```
root@labs--1458334666:/home/project/onlineBank2/LoanRequest# kubectl delete service auth
service "auth" deleted
```

2) 요청 처리결과를 확인한다.

```
root@siege:/# http http://request:8080/loanRequests requestId="01" requestName="대출신청" userId="1@sk.com" userName="유은상" userMobile="010-000-0000" userPassword="1234" amountOfMoney="100000"

HTTP/1.1 500 
Connection: close
Content-Type: application/json;charset=UTF-8
Date: Thu, 02 Sep 2021 13:32:15 GMT
Transfer-Encoding: chunked

{
    "error": "Internal Server Error",
    "message": "Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction",
    "path": "/loanRequests",
    "status": 500,
    "timestamp": "2021-09-02T13:32:15.213+0000"
}
```

3) 인증서비스 다시 시작한다.

```
root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest# kubectl expose deploy auth --type="LoadBalancer" --port=8080
service/auth exposed
```

4) 요청처리 결과를 확인한다. 

```
root@siege:/# http http://request:8080/loanRequests requestId="01" requestName="대출신청" userId="1@sk.com" userName="유은상" userMobile="010-000-0000" userPassword="1234" amountOfMoney="100000"

HTTP/1.1 201 
Content-Type: application/json;charset=UTF-8
Date: Thu, 02 Sep 2021 13:33:29 GMT
Location: http://request:8080/loanRequests/3
Transfer-Encoding: chunked

{
    "_links": {
        "loanRequest": {
            "href": "http://request:8080/loanRequests/3"
        },
        "self": {
            "href": "http://request:8080/loanRequests/3"
        }
    },
    "amountOfMoney": 100000,
    "loanRequestId": null,
    "requestDate": null,
    "requestId": "01",
    "requestName": "대출신청",
    "requestStatus": null,
    "userId": "1@sk.com",
    "userMobile": "010-000-0000",
    "userName": "유은상",
    "userPassword": "1234"
}
```

#### 테스트를 통하여 인증 서비스가 기동되지 않은 상태에서는 업무 요청이 실패함을 확인 할 수 있다.
*****

### Persistence Volume

#### Persistence Volume 을 생성한다. 

```
efs-pv.yaml

apiVersion: v1
kind: PersistentVolume
metadata:
  name: task-pv-volume
  labels:
    type: local
spec:
  storageClassName: aws-efs
  capacity:
    storage: 100Mi
  accessModes:
    - ReadWriteOnce
  hostPath:
    path: "/mnt/data"
    
root@labs--1458334666:/home/project/onlineBank2/yaml# kubectl get pv

NAME                                       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                      STORAGECLASS   REASON   AGE
pvc-06cef063-7441-4e78-8cfc-bdc3de9dd873   1Gi        RWO            Delete           Bound    kafka/datadir-my-kafka-0   gp2                     13h
pvc-13dc784b-7bd4-4bcf-9391-ea5747513b44   1Gi        RWO            Delete           Bound    kafka/datadir-my-kafka-2   gp2                     12h
pvc-17a1154a-8326-4e6c-9019-d6203f3be9e8   1Gi        RWO            Delete           Bound    kafka/datadir-my-kafka-1   gp2                     12h
task-pv-volume                             100Mi      RWO            Retain           Bound    default/aws-efs            aws-efs                 3m18s

```

#### Storageclass 를 생성한다. 

```
root@labs--1458334666:/home/project/onlineBank2/yaml# kubectl get sc

NAME            PROVISIONER             RECLAIMPOLICY   VOLUMEBINDINGMODE      ALLOWVOLUMEEXPANSION   AGE
aws-efs         my-aws.com/aws-efs      Delete          Immediate              false                  4m47s
gp2 (default)   kubernetes.io/aws-ebs   Delete          WaitForFirstConsumer   false                  13h
```

#### Persistence Volume Claim 을 생성한다. 

```
root@labs--1458334666:/home/project/onlineBank2/yaml# kubectl get pvc

NAME      STATUS   VOLUME           CAPACITY   ACCESS MODES   STORAGECLASS   AGE
aws-efs   Bound    task-pv-volume   100Mi      RWO            aws-efs        3m55s
```

#### Pod 로 접속하여 파일시스템 정보를 확인한다. 

```
root@labs--1458334666:/home/project/onlineBank2/yaml# kubectl get pod

NAME                               READY   STATUS         RESTARTS   AGE
auth-76575bb66d-4tb9h              1/1     Running        0          75m
efs-provisioner-67d7fbb46f-ghrf6   1/1     Running        0          115s
gateway-867596b974-nzljp           1/1     Running        0          83m
manager-57f779bd4f-75dsb           1/1     Running        0          74m
messenger-689ff46b85-svwlb         1/1     Running        0          9h
request-56c65447c9-pxqnh           1/2     ErrImagePull   0          9m58s
request-69dfc9fc7f-hcrm6           1/1     Running        0          75m
siege                              1/1     Running        0          12h
status-5cd9db6d56-pzj5j            1/1     Running        0          9h

root@labs--1458334666:/home/project/onlineBank2/yaml# kubectl exec -it request-69dfc9fc7f-hcrm6 -- /bin/bash
```

#### 생성된 Persistence Volume 은 Mount 되지 않은 상태임을 확인한다. 

```
root@request-69dfc9fc7f-hcrm6:/# df -h
Filesystem      Size  Used Avail Use% Mounted on
overlay          80G  4.4G   76G   6% /
tmpfs            64M     0   64M   0% /dev
tmpfs           1.9G     0  1.9G   0% /sys/fs/cgroup
/dev/nvme0n1p1   80G  4.4G   76G   6% /etc/hosts
shm              64M     0   64M   0% /dev/shm
tmpfs           1.9G   12K  1.9G   1% /run/secrets/kubernetes.io/serviceaccount
tmpfs           1.9G     0  1.9G   0% /proc/acpi
tmpfs           1.9G     0  1.9G   0% /sys/firmware
```

#### Persistenct Volume 이 Mount 되도록 yaml 설정파일을 변경한다. 

#### request-deploy-vol.yaml

```
    spec:
      containers:
        - name: request
          image: 879772956301.dkr.ecr.ap-northeast-2.amazonaws.com/request
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
...
          volumeMounts:
          - mountPath: "/mnt/aws"
            name: volume
...
      volumes:
        - name: volume
          persistentVolumeClaim:
            claimName: aws-efs
```

#### 변경된 yaml 파일로 서비스 재배포 한다. 

```
root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest# kubectl apply -f loanRequest-deploy-vol.yaml
deployment.apps/request created
```

#### Pod 로 접속하여 파일시스템 정보를 확인한다. 

```
root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest# kubectl get pod
NAME                               READY   STATUS    RESTARTS   AGE
auth-76575bb66d-4tb9h              1/1     Running   0          94m
efs-provisioner-67d7fbb46f-ghrf6   1/1     Running   0          21m
gateway-867596b974-nzljp           1/1     Running   0          102m
manager-57f779bd4f-75dsb           1/1     Running   0          94m
messenger-689ff46b85-svwlb         1/1     Running   0          10h
request-5c5469f649-5t846           0/1     Running   0          25s
siege                              1/1     Running   0          12h
status-5cd9db6d56-pzj5j            1/1     Running   0          10h

root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest# kubectl exec -it request-5c5469f649-5t846 -- /bin/bash

root@request-5c5469f649-5t846:/# df -h
Filesystem      Size  Used Avail Use% Mounted on
overlay          80G  4.6G   76G   6% /
tmpfs            64M     0   64M   0% /dev
tmpfs           1.9G     0  1.9G   0% /sys/fs/cgroup
/dev/nvme0n1p1   80G  4.6G   76G   6% /mnt/aws
shm              64M     0   64M   0% /dev/shm
tmpfs           1.9G   12K  1.9G   1% /run/secrets/kubernetes.io/serviceaccount
tmpfs           1.9G     0  1.9G   0% /proc/acpi
tmpfs           1.9G     0  1.9G   0% /sys/firmware
```

#### 생성된 Persistence Volume 이 pod 내 정상 mount 되었음을 확인할 수 있다. 
```
	root@siege:/# http http://request:8080/loanRequests requestId="01" requestName="대출신청" userId="1@sk.com" userName="유은상" userMobile="010-000-0000" userPassword="1234" amountOfMoney="100000"

	HTTP/1.1 201 
	Content-Type: application/json;charset=UTF-8
	Date: Thu, 02 Sep 2021 15:23:58 GMT
	Location: http://request:8080/loanRequests/1
	Transfer-Encoding: chunked

	{
	    "_links": {
		"loanRequest": {
		    "href": "http://request:8080/loanRequests/1"
		},
		"self": {
		    "href": "http://request:8080/loanRequests/1"
		}
	    },
	    "amountOfMoney": 100000,
	    "loanRequestId": null,
	    "requestDate": null,
	    "requestId": "01",
	    "requestName": "대출신청",
	    "requestStatus": null,
	    "userId": "1@sk.com",
	    "userMobile": "010-000-0000",
	    "userName": "유은상",
	    "userPassword": "1234"
	}
```
#### LoanRequest.java 파일내 할당된 볼륨에 로그를 기록하도록 프로그램 수정
```
@PostPersist
    public void onPostPersist(){

        if( "01".equals( getRequestId() ) ){
            onlinebanknew.external.LoanAuth loanAuth = new onlinebanknew.external.LoanAuth();
            BeanUtils.copyProperties(this, loanAuth);
            loanAuth.setLoanRequestId( getId() );
            loanAuth.setRequestDate( new Date() );
            LoanRequestApplication.applicationContext.getBean(onlinebanknew.external.LoanAuthService.class).requestAuth(loanAuth);

            LoanRequest loanLog = new LoanRequest(); 
            BeanUtils.copyProperties(this, loanLog);

            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime());  
            String dateString = "/mnt/aws/" + ts;
            
            try{
            File file = new File(dateString);
            FileWriter fw = new FileWriter(file,true);

            fw.write(loanLog.toString());
            fw.flush();
            fw.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
```
#### Persist Volume 내 정상 로깅됨을 확인
```
root@request-5c5469f649-wnqwm:/mnt/aws# ls -alrt
total 8
drwxr-xr-x 1 root root 17 Sep  2 15:22 ..
-rw-r--r-- 1 root root 34 Sep  2 15:23 2021-09-02 15:23:57.164
-rw-r--r-- 1 root root 34 Sep  2 15:27 2021-09-02 15:27:31.127
drwxr-xr-x 2 root root 68 Sep  2 15:27 .
root@request-5c5469f649-wnqwm:/mnt/aws# cat "2021-09-02 15:27:31.127"
onlinebanknew.LoanRequest@1199766eroot@request-5c5469f649-wnqwm
```
*****

### Liveness Prove

#### request 서비스 배포시 yaml 파일내 Liveness Prove 설정을 추가한다. 

#### request-deploy.yaml

```
spec:
  replicas: 1
  selector:
    matchLabels:
      app: request
  template:
    metadata:
      labels:
        app: request
    spec:
      containers:
        - name: request
          image: 879772956301.dkr.ecr.ap-northeast-2.amazonaws.com/request
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
...
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
```

#### Liveness Prove 설정 정상 적용여부를 확인하기 위해서 livenessProbe Port 를 강제로 변경한다.

```
  livenessProbe:
    httpGet:
      path: '/actuator/health'
      port: 8081
    initialDelaySeconds: 30
    timeoutSeconds: 2
    periodSeconds: 5
    failureThreshold: 5
    
  root@labs--1458334666:/home/project/onlineBank2/yaml/LoanRequest# kubectl apply -f loanRequest-deploy-vol.yaml
deployment.apps/request created
```

#### request pod 를 조회한다.

```
Every 2.0s: kubectl get pod                                                                         labs--1458334666: Thu Sep  2 15:37:46 2021

NAME                               READY   STATUS    RESTARTS   AGE
auth-76575bb66d-4tb9h              1/1     Running   0          132m
efs-provisioner-67d7fbb46f-ghrf6   1/1     Running   0          59m
gateway-867596b974-nzljp           1/1     Running   0          140m
manager-57f779bd4f-75dsb           1/1     Running   0          132m
messenger-689ff46b85-svwlb         1/1     Running   0          10h
request-75c6688b7d-q4hxf           0/1     Running   1          95s
siege                              1/1     Running   0          13h
status-5cd9db6d56-pzj5j            1/1     Running   0          10h
```

#### 잠시뒤 request pod 정보를 재조회 한다. 

```
Every 2.0s: kubectl get pod                                                                         labs--1458334666: Thu Sep  2 15:40:44 2021

NAME                               READY   STATUS    RESTARTS   AGE
auth-76575bb66d-4tb9h              1/1     Running   0          135m
efs-provisioner-67d7fbb46f-ghrf6   1/1     Running   0          62m
gateway-867596b974-nzljp           1/1     Running   0          143m
manager-57f779bd4f-75dsb           1/1     Running   0          135m
messenger-689ff46b85-svwlb         1/1     Running   0          10h
request-75c6688b7d-q4hxf           0/1     Running   5          4m33s
siege                              1/1     Running   0          13h
status-5cd9db6d56-pzj5j            1/1     Running   0          10h


```

#### request pod Liveness Prove 설정이 적용되어 RESTARTS 횟수가 설정대로 5회로 증가한 것을 확인할 수 있다. 
