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
![image](https://user-images.githubusercontent.com/87048587/131328109-f7fe4774-4c72-415c-b6e6-af186a90031e.png)

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

![image](https://user-images.githubusercontent.com/87048587/131339077-89662a1b-07ea-4146-82f0-87f7131ad4ba.png)

비기능적 요구사항 coverage 체크
1. 업무 요청이 실패한 경우 요청 내역을 삭제한다 (Correlation)
2. 개인정보 인증 대상 업무를 선택한 경우 인증 실패 시 서비스 이용이 불가하다 (Sync)
3. 잔액 조회, 거래내역 조회 서비스는 24시간 이용이 가능하다 (Async 호출-event-driven)
4. 입/출금, 계좌 개설/폐쇄 서비스가 과중되면 잠시 후에 하도록 유도한다. (Circuit breaker, fallback)
5. 고객이 최종 거래 내역, 계좌 상태를 계속 확인 가능해야 한다 (CQRS)
![image](https://user-images.githubusercontent.com/27180840/130178255-bb142282-4eb6-4487-b6b0-ffab6e990f28.png)


## 구현

분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트로 구현하였다. 
구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (서비스 포트는 8081, 8082, 8083, 8084, 8084, 8088 이다)

```
cd BankRequest
mvn spring-boot:run

cd BankAuthentication
mvn spring-boot:run

cd BankAccount
mvn spring-boot:run

cd BankHistory
mvn spring-boot:run

cd Mypage
mvn spring-boot:run

cd gateway
mvn spring-boot:run

```
*****

### DDD의 적용

1. 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다. 
(예시는 request 마이크로 서비스 )

#### Request.java

```
package onlinebank;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Request_table")
public class Request {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String accountNo;
    private String requestId;
    private String requestName;
    private Date requestDate;
    private Integer amountOfMoney;
    private String userId;
    private String userName;
    private String userPassword;

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
    
    ...
```

2. Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 
데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 
자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용하였다

#### RequestRepository.java

```
package onlinebank;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="requests", path="requests")
public interface RequestRepository extends PagingAndSortingRepository<Request, Long>{

}
```

3. 적용 후 REST API 의 테스트

#### request 서비스의 요청처리

```
http request:8080/requests accountNo="1111" requestId="01" requestName="Deposit" amountOfMoney=10000 userId="1@sk.com" userName="sam" userPassword="1234"  
```

- 모든 요청은 request 에서 처리하는 관계로 다른 마이크로시스템에 접속하지 않는다. 

#### 요청상태 확인

```
http http://request:8080/requests/1
```
*****

### 폴리글랏 퍼시스턴스

고객에게 메세지 전송은 전통적인 RDB로 개발 하기로 하고 구현이 간단한 sqlite로 구현함.

```
pom.xml
sqlite 사용을 위해 sqlite용 jdbc dependency 추가
	<dependency>
      		<groupId>org.xerial</groupId>
      		<artifactId>sqlite-jdbc</artifactId>
	</dependency>
쿼리로 작업하기 위해 mybatis dependency 추가
	<dependency>
    		<groupId>org.hibernate</groupId>
		<artifactId>hibernate-core</artifactId>
	</dependency>
    	<dependency>
        	<groupId>org.mybatis.spring.boot</groupId>
        	<artifactId>mybatis-spring-boot-starter</artifactId>
        	<version>1.3.2</version>
    	</dependency>


application.yml
was 기동시 자동으로 sqlite 연결 설정 프로젝트 폴드에 자동으로 sqlitesample.db 생성됨
  datasource:
    url: jdbc:sqlite:sqlitesample.db 
    driver-class-name: org.sqlite.JDBC
    username: admin 
    password: admin


SendMsgVO.java
mybatis 조회 결과를 VO 형태로 받기 위한 VO 설정
@Data
public class SendMsgVO {
	private Long id; 
	private String phone; 
	private String message; 
	
	public SendMsgVO() {
	}
	
	public SendMsgVO(String pphone, String pmessage) {
		this.phone = pphone;
		this.message = pmessage;
	}
}

KakaoDAO.java
Mapper.xml 파일과 연결하기 위한 용도로 함수 생성
@Mapper
public interface  KakaoDAO {
	void insertmsg(SendMsgVO vo) throws Exception;;
	List<SendMsgVO> selectmsg() throws Exception;;
}

```

### 폴리글랏 프로그래밍
구현의 편의를 위해 Java 버전도 16 을 사용.

```
KakaoTakMapper.xml
KakaoDAO.java 생성된 함수와 동일한 package 경로와 함수명으로 select, insert 함수 생성

<mapper namespace="com.example.demo.table.KakaoDAO">
    <select id="selectmsg"  resultType="com.example.demo.table.SendMsgVO">
    <![CDATA[
        select id, phone, message from send_msg order by 1 desc LIMIT 5
    ]]>
    </select>
    
    <insert id="insertmsg" parameterType="com.example.demo.table.SendMsgVO" >
    <![CDATA[
    	INSERT INTO send_msg VALUES 
		( (select max(id)+1 from send_msg) , 
		#{phone} , #{message}  )
	]]>
	</insert>
</mapper>

KafkaService.java
카프카에서 수신 받은 메세지를 sqlite에 저장

@Service
@Transactional
public class KafkaService {
	
	//저장을 위해 위에서 만든 DAO 파일 선언
	@Autowired
	KakaoDAO kakaoDAO;
	
        //수신 받을 topic 선언
	@KafkaListener(topics = "onlinebank", groupId="kakaotalk")
	public void getKafka(String message) {
		
		System.out.println( "kakaotalk getKafka START " );
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, String> map = (Map<String, String>)objectMapper.readValue( message, Map.class);
			System.out.println( "kafka recerve data : " + map);
			
			//수신 받은 메세지에서 job 에 kakaotalk 이라는 글자가 존재 하면 sqlite 저장
			if ( map.get("job")!= null && map.get("job").indexOf("kakaotalk") >=0 ) {
				
				SendMsgVO vo = new SendMsgVO(); 
				vo.setPhone(  map.get( "phone").toString() );
	    		vo.setMessage( map.get( "message").toString() );
	    		kakaoDAO.insertmsg(vo);
			} else {
				System.out.println( "kafka Skip ");
			}
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}

Dockerfile 
Java 16 버전 사용을 위해 image도 openjdk16 을 사용함.

FROM khipu/openjdk16-alpine
COPY target/*SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Xmx400M","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=docker"]

```

### 동기식 호출 (구현)

분석단계에서의 조건 중 하나로 요청(request)->인증(auth) 간의 호출은 동기식 일관성을 유지하는 트랜잭션으로 

처리하기로 하였다. 호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 

이용하여 호출하도록 한다. 

1. 결제서비스를 호출하기 위하여 FeignClient 를 이용하여 Service 대행 인터페이스 구현

#### AuthService.java

```
@FeignClient(name="auths", url="http://auth:8080")
public interface AuthService {
    @RequestMapping(method= RequestMethod.GET, path="/auths")
    public void requestAuth(@RequestBody Auth auth);

}
```

2. 요청을 받은 직후(@PostPersist) 인증을 요청하도록 처리

#### Request.java

```
@PostPersist
    public void onPostPersist(){

        // requestId
        // "01" : Deposit
        // "02" : Withdraw
        // "03" : Balance
        // "04" : Transaction  
        // "05" : addAccount
        // "06" : deleteAccount
        if( "01".equals( getRequestId() ) || // Deposit
            "02".equals( getRequestId() ) || // Withdraw
            "05".equals( getRequestId() ) || // addAccount
            "06".equals( getRequestId() ) ){ // deleteAccount 
            onlinebank.external.Auth auth = new onlinebank.external.Auth();
            BeanUtils.copyProperties(this, auth);
            auth.setBankRequestId( getId() );
            BankRequestApplication.applicationContext.getBean(onlinebank.external.AuthService.class).requestAuth(auth);
        }
        
        // Balance
        if( "03".equals( getRequestId() ) ){ 
            BalanceRequested balanceRequested = new BalanceRequested();
            BeanUtils.copyProperties(this, balanceRequested);
            balanceRequested.setBankRequestId( getId() );
            balanceRequested.publish();
        }

        // Transaction
        if( "04".equals( getRequestId() ) ){ 
            TransactionRequested transactionRequested = new TransactionRequested();
            BeanUtils.copyProperties(this, transactionRequested);
            transactionRequested.publishAfterCommit();
        }
    }
```
*****

### 비동기식 호출 

인증성공 후 계좌 시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리하여 

계좌 처리를 위하여 인증 기능이 블로킹 되지 않아도록 처리한다.

1. 이를 위하여 인증이력에 기록을 남긴 후에 곧바로 인증완료 되었다는 도메인 이벤트를 카프카로 송출한다(Publish)

#### Auth.java

```
package onlinebank;

@Entity
@Table(name="Auth_table")
public class Auth {

    @PrePersist
    public void onPrePersist(){

        String userId = "1@sk.com";
        String userName = "sam";
        String userPassword = "1234";
        boolean authResult = false ;

        if( userId.equals( getUserId() ) && userName.equals( getUserName() ) && userPassword.equals( getUserPassword() ) ){
            authResult = true ;
        }

        if( authResult == false ){
            AuthCancelled authCancelled = new AuthCancelled();
            BeanUtils.copyProperties(this, authCancelled);
	    // 실패 이벤트 카프카 송출
            authCancelled.publish();

        }else{
            AuthCertified authCertified = new AuthCertified();
            BeanUtils.copyProperties(this, authCertified);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void beforeCommit(boolean readOnly) {
		    // 성공 이벤트 카프카 송출
                    authCertified.publish();
                }
            });
        }
    }
```

2. 계좌 서비스에서는 결제승인 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다

#### PolicyHandler.java

```
package onlinebank;

@Service
public class PolicyHandler{
    @Autowired AccountRepository accountRepository;

    // 인증 성공시 정책 수신
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCertified_RecieveAccountRequest(@Payload AuthCertified authCertified){

        if(!authCertified.validate()) return;
        
        // 입금처리
        if( "01".equals( authCertified.getRequestId() ) ){

           // 입금계좌가 없을 경우 
           if( accountRepository.findByAccountNo( authCertified.getAccountNo() ) == null ){
 
               AccountRequestCancelled accountRequestCancelled = new AccountRequestCancelled();              
               accountRequestCancelled.setAccountNo(authCertified.getAccountNo());
               accountRequestCancelled.setBankRequestId(authCertified.getBankRequestId());
               accountRequestCancelled.setMessage("Account Not Exist");
               accountRequestCancelled.setRequestId(authCertified.getRequestId());
               accountRequestCancelled.setRequestName(authCertified.getRequestName());
               accountRequestCancelled.setRequestMoney(authCertified.getAmountOfMoney());
               accountRequestCancelled.setRequestDate(new Date());
               
               accountRequestCancelled.publish();
           }
           // 입금계좌가 있을 경우
           else{

               Account account = accountRepository.findByAccountNo( authCertified.getAccountNo() );
               account.setAccountNo(authCertified.getAccountNo());
               account.setRequestId(authCertified.getRequestId());
               account.setRequestName(authCertified.getRequestName());
               account.setRequestMoney(authCertified.getAmountOfMoney());
               account.setAmountOfMoney(account.getAmountOfMoney() + authCertified.getAmountOfMoney() );
               account.setRequestDate(new Date());
               accountRepository.save(account);
           }
        }

        ...

    }
}
```
*****

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

#### Auth.java (Entity)

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

root@siege:/# siege -v -c100 -t90S -r10 --content-type "application/json" 'http://request:8080/requests POST {"accountNo":"1111","requestId":"01","requestName":"Deposit","amountOfMoney":10000,"userId":"1@sk.com","userName":"sam","userPassword":"1234"}'
( 동시사용자 100명, 90초간 진행 )

```
HTTP/1.1 500     4.46 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.88 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.69 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.88 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.85 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.60 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.76 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     4.09 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.62 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     4.14 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     3.59 secs:     250 bytes ==> POST http://request:8080/requests

HTTP/1.1 201     4.40 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.33 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.45 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.35 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.38 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.45 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.51 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.57 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.02 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.63 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.05 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.03 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.01 secs:     370 bytes ==> POST http://request:8080/requests

HTTP/1.1 500     4.31 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     4.14 secs:     250 bytes ==> POST http://request:8080/requests

HTTP/1.1 201     4.09 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.15 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.14 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.10 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.15 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.24 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.20 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.24 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.26 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.16 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.30 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.20 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.24 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.29 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 500     4.32 secs:     250 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     4.18 secs:     370 bytes ==> POST http://request:8080/requests

Lifting the server siege...
Transactions:                   1545 hits
Availability:                  71.40 %
Elapsed time:                  89.88 secs
Data transferred:               0.69 MB
Response time:                  5.66 secs
Transaction rate:              17.19 trans/sec
Throughput:                     0.01 MB/sec
Concurrency:                   97.34
Successful transactions:        1545
Failed transactions:             619
Longest transaction:           11.60
Shortest transaction:           0.01
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
root@labs-579721623:/home/project/online-bank/BankAuthentication# kubectl autoscale deployment request --cpu-percent=50 --min=1 --max=10
horizontalpodautoscaler.autoscaling/request autoscaled
```

#### 부하 테스트 진행

root@siege:/# siege -v -c100 -t90S -r10 --content-type "application/json" 'http://request:8080/requests POST {"accountNo":"1111","requestId":"01","requestName":"Deposit","amountOfMoney":10000,"userId":"1@sk.com","userName":"sam","userPassword":"1234"}'
( 동시사용자 100명, 90초간 진행 )

```
HTTP/1.1 201     1.63 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.98 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.80 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.09 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.90 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.89 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.18 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.19 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     1.71 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.10 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.80 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.81 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.82 secs:     370 bytes ==> POST http://request:8080/requests

Lifting the server siege...
Transactions:                   8988 hits
Availability:                 100.00 %
Elapsed time:                  89.51 secs
Data transferred:               3.17 MB
Response time:                  0.99 secs
Transaction rate:             100.41 trans/sec
Throughput:                     0.04 MB/sec
Concurrency:                   99.42
Successful transactions:        8989
Failed transactions:               0
Longest transaction:            8.59
Shortest transaction:           0.01
```

#### Terminal 을 추가하여 오토스케일링 현황을 모니터링 한다. ( watch kubectl get pod )

#### 부하 테스트 진행전 

```
Every 2.0s: kubectl get pod       labs-579721623: Thu Aug 19 08:44:47 2021

NAME                              READY   STATUS    RESTARTS   AGE
account-6b844c4f44-gdsvd          1/1     Running   0          145m
auth-7c55b8b7b9-9r6bb             1/1     Running   0          145m
efs-provisioner-fbcc88cb8-zrlzx   1/1     Running   0          79m
gateway-55bd75dfb9-cwlvg          1/1     Running   0          142m
history-77cc54b895-v5nqm          1/1     Running   0          144m
mypage-7bc648bd4d-5psgz           1/1     Running   0          143m
request-675f455d5c-7txbc          1/1     Running   0          28m
```

#### 부하 테스트 진행 후 

```
Every 2.0s: kubectl get pod       labs-579721623: Thu Aug 19 08:46:34 2021

NAME                              READY   STATUS    RESTARTS   AGE
account-6b844c4f44-gdsvd          1/1     Running   0          147m
auth-7c55b8b7b9-9r6bb             1/1     Running   0          147m
efs-provisioner-fbcc88cb8-zrlzx   1/1     Running   0          81m
gateway-55bd75dfb9-cwlvg          1/1     Running   0          144m
history-77cc54b895-v5nqm          1/1     Running   0          146m
mypage-7bc648bd4d-5psgz           1/1     Running   0          144m
request-675f455d5c-256tz          0/1     Running   0          31s
request-675f455d5c-6s2nz          0/1     Running   0          46s
request-675f455d5c-7txbc          1/1     Running   0          30m
request-675f455d5c-bz4nq          0/1     Running   0          46s
request-675f455d5c-mdbbl          0/1     Running   0          46s
siege                             1/1     Running   0          3h19m
```

#### 부하테스트 결과 Availability 는 100% 를 보이며 성공하였고, 늘어난 pod 개수를 통하여

오토 스케일링이 정상적으로 수행되었음을 확인할 수 있다. 
*****

### 무정지 재배포

#### 무정지 재배포 여부를 확인을 위해서 Autoscaler 와 CB 설정을 제거한다.

root@siege:/# siege -v -c100 -t90S -r10 --content-type "application/json" 'http://request:8080/requests POST {"accountNo":"1111","requestId":"01","requestName":"Deposit","amountOfMoney":10000,"userId":"1@sk.com","userName":"sam","userPassword":"1234"}'
( 동시사용자 100명, 90초간 진행 )

#### 부하테스트중 추가 생성한 Terminal 에서 readiness 설정되지 않은 버젼으로 재배포 한다.

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl apply -f request-redeploy.yaml
deployment.apps/request configured
service/request configured

[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
[error] socket: unable to connect sock.c:249: Connection refused
HTTP/1.1 201     0.83 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.86 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.84 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     1.50 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.94 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.91 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.94 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     1.64 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.81 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     1.43 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.88 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.93 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     1.40 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.89 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     1.42 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.94 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.85 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.85 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.90 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.93 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.80 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.92 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.94 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.94 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.97 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.97 secs:     370 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.91 secs:     370 bytes ==> POST http://request:8080/requests
siege aborted due to excessive socket failure; you
can change the failure threshold in $HOME/.siegerc

Transactions:                   1154 hits
Availability:                  51.38 %
Elapsed time:                  12.57 secs
Data transferred:               0.41 MB
Response time:                  1.06 secs
Transaction rate:              91.81 trans/sec
Throughput:                     0.03 MB/sec
Concurrency:                   97.58
Successful transactions:        1154
Failed transactions:            1092
Longest transaction:            4.69
Shortest transaction:           0.02
```

#### 부하테스트중 새로 배포된 서비스를 READY 상태로 인지하여 서비스 중단됨을 확인함.

#### 부하테스트 진행

root@siege:/# siege -v -c100 -t30S -r10 --content-type "application/json" 'http://request:8080/requests POST {"accountNo":"1111","requestId":"01","requestName":"Deposit","amountOfMoney":10000,"userId":"1@sk.com","userName":"sam","userPassword":"1234"}'
( 동시사용자 100명, 90초간 진행 )

#### 부하테스트중 추가 생성한 Terminal 에서 readiness 설정 되어있는 버젼으로 재배포 한다.

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl apply -f request-deploy.yaml
deployment.apps/request configured
service/request unchanged

HTTP/1.1 201     0.37 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.56 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.38 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.38 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.36 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.35 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.36 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.34 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.36 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.37 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.35 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.05 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.33 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.05 secs:     372 bytes ==> POST http://request:8080/requests
HTTP/1.1 201     0.08 secs:     372 bytes ==> POST http://request:8080/requests

Lifting the server siege...
Transactions:                  24451 hits
Availability:                 100.00 %
Elapsed time:                  89.32 secs
Data transferred:               8.65 MB
Response time:                  0.36 secs
Transaction rate:             273.75 trans/sec
Throughput:                     0.10 MB/sec
Concurrency:                   99.31
Successful transactions:       24451
Failed transactions:               0
Longest transaction:            2.72
Shortest transaction:           0.00
```

#### 배포중 Availability 100%를 보이며 무정지 재배포가 정상적으로 성공하였다.
*****

### Gateway / Corelation

#### Gateway 기능이 정상적으로 수행되는지 확인하기 위하여 Gateway를 통하여 요청서비스를 호출한다.  

```
root@siege:/# http gateway:8080/requests accountNo="1111" requestId="01" requestName="Deposit" amountOfMoney=10000 userId="1@sk.com" userName="sam" userPassword="1234"

HTTP/1.1 201 Created
Content-Type: application/json;charset=UTF-8
Date: Thu, 19 Aug 2021 06:54:01 GMT
Location: http://request:8080/requests/2
transfer-encoding: chunked

{
    "_links": {
        "request": {
            "href": "http://request:8080/requests/2"
        },
        "self": {
            "href": "http://request:8080/requests/2"
        }
    },
    "accountNo": "1111",
    "amountOfMoney": 10000,
    "requestDate": null,
    "requestId": "01",
    "requestName": "Deposit",
    "userId": "1@sk.com",
    "userName": "sam",
    "userPassword": "1234"
}
```

#### 요청 처리결과를 통하여 Gateway 기능이 정상적으로 수행되었음을 확인할 수 있다. 

#### 요청이 정상적으로 처리되지 않는 경우( 예를 들어서 입금 요청을 했으나 계좌가 존재하지 않는 등 )

요청시 파라미터로 전송된 id 값을 기준으로 기 저장된 요청 데이터를 삭제한다. 

Gateway 테스트시 존재하지 않는 계좌에 입금을 시도하였으며 요청이 정상적으로 처리되지 못한 관계로

기 저장된 데이터가 삭제 처리 된다. 

```
root@siege:/# http http://request:8080/requests

HTTP/1.1 200 
Content-Type: application/hal+json;charset=UTF-8
Date: Thu, 19 Aug 2021 06:54:56 GMT
Transfer-Encoding: chunked

{
    "_embedded": {
        "requests": []
    },
    "_links": {
        "profile": {
            "href": "http://request:8080/profile/requests"
        },
        "self": {
            "href": "http://request:8080/requests{?page,size,sort}",
            "templated": true
        }
    },
    "page": {
        "number": 0,
        "size": 20,
        "totalElements": 0,
        "totalPages": 0
    }
}
```
#### request 데이터가 정상적으로 삭제되었음을 확인할 수 있다. 
*****

### 동기식 호출 (운영)

#### 동기식 호출인 관계로 인증시스템 장애시 서비스를 처리할 수 없다. 

1) 인증 서비스 임시로 삭제한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl delete service auth
service "auth" deleted
```

2) 요청 처리결과를 확인한다.

```
root@siege:/# http request:8080/requests accountNo="1111" requestId="01" requestName="Deposit" amountOfMoney=10000 userId="1@sk.com" userName="sam" userPassword="1234"
HTTP/1.1 500 
Connection: close
Content-Type: application/json;charset=UTF-8
Date: Thu, 19 Aug 2021 06:59:08 GMT
Transfer-Encoding: chunked

{
    "error": "Internal Server Error",
    "message": "Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction",
    "path": "/requests",
    "status": 500,
    "timestamp": "2021-08-19T06:59:08.624+0000"
}
```

3) 인증서비스 재기동 한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl expose deploy auth --type="LoadBalancer" --port=8080
service/auth exposed
```

4) 요청처리 결과를 확인한다. 

```
root@siege:/# http http://request:8080/requests accountNo="1111" requestId="01" requestName="Deposit" amountOfMoney=10000 userId="1@sk.com" userName="sam" userPassword="1234"

HTTP/1.1 201 
Content-Type: application/json;charset=UTF-8
Date: Thu, 19 Aug 2021 07:02:31 GMT
Location: http://request:8080/requests/4
Transfer-Encoding: chunked

{
    "_links": {
        "request": {
            "href": "http://request:8080/requests/4"
        },
        "self": {
            "href": "http://request:8080/requests/4"
        }
    },
    "accountNo": "1111",
    "amountOfMoney": 10000,
    "requestDate": null,
    "requestId": "01",
    "requestName": "Deposit",
    "userId": "1@sk.com",
    "userName": "sam",
    "userPassword": "1234"
}
```

#### 테스트를 통하여 인증 서비스가 기동되지 않은 상태에서는 업무 요청이 실패함을 확인 할 수 있음.
*****

### Persistence Volume

#### Persistence Volume 을 생성한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl get pv

NAME                                       CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM                      STORAGECLASS   REASON   AGE
pvc-60c0deaa-241e-443d-a770-2c4890b0d9db   1Gi        RWO            Delete           Bound    kafka/datadir-my-kafka-2   gp2                     174m
pvc-ce2fe4aa-be29-4c82-8637-7d247b243456   1Gi        RWO            Delete           Bound    kafka/datadir-my-kafka-1   gp2                     175m
pvc-f0331c5b-0127-475f-93db-58999bb38980   1Gi        RWO            Delete           Bound    kafka/datadir-my-kafka-0   gp2                     177m
task-pv-volume                             100Mi      RWO            Retain           Bound    labs-579721623/aws-efs     aws-efs                 4m4s
```

#### Persistence Volume Claim 을 생성한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl get pvc

NAME      STATUS   VOLUME           CAPACITY   ACCESS MODES   STORAGECLASS   AGE
aws-efs   Bound    task-pv-volume   100Mi      RWO            aws-efs        101s
```

#### Pod 로 접속하여 파일시스템 정보를 확인한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl get pod

NAME                              READY   STATUS             RESTARTS   AGE
account-6b844c4f44-gdsvd          1/1     Running            0          76m
auth-7c55b8b7b9-9r6bb             1/1     Running            0          76m
efs-provisioner-fbcc88cb8-zrlzx   1/1     Running            0          10m
gateway-55bd75dfb9-cwlvg          1/1     Running            0          73m
history-77cc54b895-v5nqm          1/1     Running            0          75m
mypage-7bc648bd4d-5psgz           1/1     Running            0          73m
request-5cdc6474bf-p76tr          0/1     ImagePullBackOff   0          25m
request-646c4cc7c6-xmk59          1/1     Running            0          28m
siege                             1/1     Running            0          128m

root@labs-579721623:/home/project/online-bank/yaml# kubectl exec -it request-646c4cc7c6-xmk59 -- /bin/bash
```

#### 생성된 Persistence Volume 은 Mount 되지 않은 상태임을 확인한다. 

```
root@request-646c4cc7c6-xmk59:/# df -h
Filesystem      Size  Used Avail Use% Mounted on
overlay          80G  4.2G   76G   6% /
tmpfs            64M     0   64M   0% /dev
tmpfs           1.9G     0  1.9G   0% /sys/fs/cgroup
/dev/nvme0n1p1   80G  4.2G   76G   6% /etc/hosts
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
root@labs-579721623:/home/project/online-bank/yaml# kubectl apply -f request-deploy-vol.yaml
deployment.apps/request created
```

#### Pod 로 접속하여 파일시스템 정보를 확인한다. 

```
root@request-675f455d5c-t8lzd:/# df -h
Filesystem      Size  Used Avail Use% Mounted on
overlay          80G  4.1G   76G   6% /
tmpfs            64M     0   64M   0% /dev
tmpfs           1.9G     0  1.9G   0% /sys/fs/cgroup
/dev/nvme0n1p1   80G  4.1G   76G   6% /mnt/aws
shm              64M     0   64M   0% /dev/shm
tmpfs           1.9G   12K  1.9G   1% /run/secrets/kubernetes.io/serviceaccount
tmpfs           1.9G     0  1.9G   0% /proc/acpi
tmpfs           1.9G     0  1.9G   0% /sys/firmware
```

#### 생성된 Persistence Volume 이 pod 내 정상 mount 되었음을 확인할 수 있다. 
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

#### Liveness Prove 설정 정상 적용여부를 확인하기 위해서 기존에 생성된 request pod 삭제시

정상적으로 재생성 되는지 여부를 확인한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl get pod
NAME                              READY   STATUS    RESTARTS   AGE
account-6b844c4f44-gdsvd          1/1     Running   0          84m
auth-7c55b8b7b9-9r6bb             1/1     Running   0          85m
efs-provisioner-fbcc88cb8-zrlzx   1/1     Running   0          19m
gateway-55bd75dfb9-cwlvg          1/1     Running   0          82m
history-77cc54b895-v5nqm          1/1     Running   0          84m
mypage-7bc648bd4d-5psgz           1/1     Running   0          82m
request-675f455d5c-t8lzd          1/1     Running   0          2m10s
siege                             1/1     Running   0          137m
```

#### request pod 를 삭제한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl delete pod request-675f455d5c-t8lzd
pod "request-675f455d5c-t8lzd" deleted
```

#### request pod 삭제 후 pod 정보를 재조회 한다. 

```
root@labs-579721623:/home/project/online-bank/yaml# kubectl get pod
NAME                              READY   STATUS    RESTARTS   AGE
account-6b844c4f44-gdsvd          1/1     Running   0          85m
auth-7c55b8b7b9-9r6bb             1/1     Running   0          85m
efs-provisioner-fbcc88cb8-zrlzx   1/1     Running   0          19m
gateway-55bd75dfb9-cwlvg          1/1     Running   0          83m
history-77cc54b895-v5nqm          1/1     Running   0          85m
mypage-7bc648bd4d-5psgz           1/1     Running   0          83m
request-675f455d5c-zqhwq          0/1     Running   0          13s
siege                             1/1     Running   0          138m
```

#### request pod Liveness Prove 설정이 적용되어 삭제 후 다른 이름으로 재생성 되었음을 확인할 수 있다. 
