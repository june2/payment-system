## 카드사 결제 시스템

### 개발 프레임워크 & libs
- Java8
- Spring Boot & Spring
  - spring-boot-starter-test 
  - spring-boot-data-jpa
  - spring-boot-data-rest 
  - spring-boot-starter-integration
  - spring-integration-redis
- h2database
- redis
- modelmapper
- Lombok



### Build && Run

```zsh
$ ./gradlew clean build
$ ./gradlew bootRun
```

---
### 체크 리스트
- [x] 모델 정의하기
- [x] H2 DB 연동 (in memory)
- [x] API 기능구현 
- [x] 시스템 정책
   - [x] string data 명세 
   - [x] 부가가치세 계산 
   - [x] 카드정보 암/복호화 
   - [x] 트랜잭션 데이터관리
- [x] 테스트 
   - [x] api 테스트
   - [x] 시나리오 테스트
   - [x] 멀티스레드 테스트  
---

## DB 설계 

### 고려사항   
1.저장하는 string 데이터 
  - 카드사로 전송하는 string 데이터를 공통헤더부문과 데이터부문을 합쳐 하나의 string(450자리)으로 만들어서 DB에 저장. 

2.트랜잭션 데이터 관리
  - 각 결제, 결제취소 데이터는 관리번호(unique id, 20자리)로 관리.
  - 취소데이터는 결제데이터와 연결.
  - 관리번호(unique id, 20자리)로 저장되어 있는 데이터를 조회.
  - 관리번호로 데이터 조회시에는 결제 혹은 결제취소 데이터 1건만 조회.

### 설계 설명
- PAYMENT 
  - 결제 정보 테이블
  - TYPE:PAYMENT 데이터와 TYPE:CANCEL 데이터 1:N 관계
- PAYLOAD
  - 카드사 전송 정보 테이블
  - DATA: 카드사에 전송하는 내역저장(공통헤더부문 + 데이터부문)

### ERD
![image](https://user-images.githubusercontent.com/5827617/103494723-60635a80-4e7b-11eb-9055-e826c7a48c02.png)

## API SPEC

### 결제 API
* 카드정보과 금액정보를 입력받아서 카드사와 협의된 string 데이터로 DB에 저장합니다.<br/>
* `POST /api/payment/v1/apply`
* request
```json
{
  "price": "[결제금액(100원 이상, 10억원 이하, 숫자)]",
  "vat": "[부가가치세:optional]",
  "card": {
    "number": "[카드번호(10 ~ 16자리 숫자)]",
    "expiryDate": "[유효기간(4자리 숫자, mmyy)]",
    "verificationCode": "[cvc(3자리 숫자)]"
  },
  "month": "[할부개월수 : 0(일시불), 1 ~ 12]"
}
```
* response
```json
{
  "id": "[관리번호(unique id, 20자리)]",
  "data" : "[카드사에 전달한 string 데이터 : 공통 헤더부문 + 데이터 부문]"
}
```

### 취소 API
* 결제에 대한 전체취소는 1번만 가능합니다. 부가가치세 정보를 넘기지 않는 경우, 결제데이터의 부가가치세 금액으로 취소합니다. 할부개월수 데이터는 00(일시불)로 저장합니다.<br/>
* `POST /api/payment/v1/cancel`
* request
```json
{
  "price": "[취소금액]",
  "vat": "[부가가치세:optional]",
  "paymentId": "[관리번호(unique id, 20자리)]"
}
```
* response
```json
{  
  "id": "[관리번호(unique id, 20자리)]",
  "data" : "[카드사에 전달한 string 데이터 : 공통 헤더부문 + 데이터 부문]"
}
```

### 조회 API
* DB에 저장된 데이터를 조회해서 응답값으로 만들어줍니다.<br/>
* `POST /api/payment/v1/search`
* request
```json
{
  "id": "[관리번호(unique id, 20자리)]"
}
```
* response
```json
{
  "id": "[관리번호(unique id)]",
  "cardInfo": {
    "number": "[카드번호 : 앞 6자리와 뒤 3자리를 제외한 나머지를 마스킹처리]",
    "expiryDate": "[카드유효기간]",
    "verificationCode": "[카드CVC]"
  },
  "paymentType": "[타입(결제: PAYMENT, 취소: CANCEL)]",
  "price": "[결제/취소 금액 (타입 결제시 : 취소되고 남은 금액 표시, 타입 취소시 : 취소 금액 표시)]",
  "vat": "[부가가치세 (타입 결제시 : 취소되고 남은 금액 표시, 타입 취소시 : 취소 금액 표시)]",
  "month": "[할부개월수 : 01~12]",
  "optional": "타입결제(PAYMENT)이고, 취소 내역이 있으면 취소 내역리스트 추가"
}
```
### 오류 코드 
```
ERROR(00, "오류"),
INVALID_PARAMETER(01, "누락 또는 잘못된 형식으로 입력했습니다."),
CARD_LOCKED(02, "현재 결제가 진행 중인 카드 정보는 사용할 수 없습니다."),
PAYMENT_LOCKED(03, "현재 취소가 진행 중인 결제 ID는 사용할 수 없습니다."),
PAYMENT_NOT_FOUND(04, "결제 정보를 찾을 수 없습니다."),
NOT_ENOUGH_PRICE(05, "금액이 부족합니다."),
NOT_ENOUGH_VAT(06, "VAT가 부족합니다."),
VAT_GREATER_THAN_PRICE(07, "VAT가 금액보다 더 큽니다.");
```

---

## 시스템 정책

### 저장하는 string 데이터 명세
- `model\payload\PayloadSerializerTest.java` : 카드사 정보 string 데이터 변환 테스트

### 부가가치세
- optional 데이터이므로 값을 받지 않은 경우, 자동계산 
  - `utill\CardUtilTest.java` : 부가가치세 계산 테스트  
- 부가가치세는 결제금액보다 클 수 없다.
  - `validator/PriceGreaterThanVat.java`  

### 카드정보 암/복호화트
- 암/복호화 방식 : DESede
- 카드 정보 구분자 처리 : ex. encrypt(카드정보|유효기간|cvc)
- `utill\CardUtilTest.java` : 카드사 정보 암/복호화 테스트

--- 

## 테스트 case 작성

### unit 테스트
- `request/**`
- `PaymentTest.java`
- `SearchTest.java`

### 시나리오(부분취소) 테스트
- `Scenario1Test.java` 
- `Scenario2Test.java` 
- `Scenario3Test.java` 

### Multi Thread 테스트
- 제약조건
  - 결제 : 하나의 카드번호로 동시에 결제를 할 수 없습니다.
  - 전체취소 : 결제 한 건에 대한 전체취소를 동시에 할 수 없습니다.
  - 부분취소 : 결제 한 건에 대한 부분취소를 동시에 할 수 없습니다.
- 해결 방안
  - spring-integration-redis
  - redis에 카드 정보키를 저장하여 분산락 설정, 동시 처리 제약 처리
- `MultiThreadTest.java` 

