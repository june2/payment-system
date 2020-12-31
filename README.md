3. 필수 구현 API 기능명세
1. 결제 API

2. 결제취소 API

3. 데이터 조회 API

4. API 요청 실패시
자유롭게 정의한 에러응답, 에러코드등을 내려줍니다.


## API SPEC

### 1. 결제 API
* 카드정보과 금액정보를 입력받아서 카드사와 협의된 string 데이터로 DB에 저장합니다.<br/>
* POST /api/payment/v1/apply<br/>
* request
```json
{
  "price": [결제금액(100원 이상, 10억원 이하, 숫자)],
  "vat": [부가가치세:optional],
  "card": {
    "number": "[카드번호(10 ~ 16자리 숫자)]",
    "expiryDate": "[유효기간(4자리 숫자, mmyy)]",
    "verificationCode": "[cvc(3자리 숫자)]"
  },
  "months": [할부개월수 : 0(일시불), 1 ~ 12]
}
```
* response
```json
{
  "id": "[관리번호(unique id, 20자리)]",
  "data" : "[카드사에 전달한 string 데이터 : 공통 헤더부문 + 데이터 부문]"
}
```

### 2. 취소 API
* 결제에 대한 전체취소는 1번만 가능합니다. 부가가치세 정보를 넘기지 않는 경우, 결제데이터의 부가가치세 금액으로 취소합니다. 할부개월수 데이터는 00(일시불)로 저장합니다.<br/>
* POST /api/payment/v1/cancel<br/>
* request
```json
{
  "price": [취소금액],
  "vat": [부가가치세:optional],
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

### 3. 조회 API
* DB에 저장된 데이터를 조회해서 응답값으로 만들어줍니다.<br/>
* POST /api/payment/v1/search<br/>
* request
```json
{
  "id": "[관리번호(unique id, 20자리)]",
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
  "price": [결제/취소 금액 (타입 결제시 : 취소되고 남은 금액 표시, 타입 취소시 : 취소 금액 표시)],
  "vat": [부가가치세 (타입 결제시 : 취소되고 남은 금액 표시, 타입 취소시 : 취소 금액 표시)]
}
```

---

## 오류 코드 
```
**ERROR** : code = 1, description = "오류"<br/>
**INVALID_PARAMETER** : code = 2, description = "누락 또는 잘못된 형식으로 입력했습니다."<br/>
**CARD_LOCKED** : code = 11, description = "현재 결제가 진행 중인 카드 정보는 사용할 수 없습니다."<br/>
**PAYMENT_LOCKED** : code = 21, description = "현재 취소가 진행 중인 결제 ID는 사용할 수 없습니다."<br/>
**PAYMENT_NOT_FOUND** : code = 22, description = "결제 정보를 찾을 수 없습니다."<br/>
**NOT_ENOUGH_PRICE** : code = 31, description = "금액이 부족합니다."<br/>
**NOT_ENOUGH_VAT** : code = 32, description = "VAT가 부족합니다."<br/>
**VAT_GREATER_THAN_PRICE** : code = 33, description = "VAT가 금액보다 더 큽니다."<br/>
```

---
## DB 설계 & ERD

### 고려사항   
1.저장하는 string 데이터 
  - 카드사로 전송하는 string 데이터를 공통헤더부문과 데이터부문을 합쳐 하나의 string(450자리)으로 만들어
서 DB에 저장해주세요. 

2.트랜잭션 데이터 관리
  - 각 결제, 결제취소 데이터는 관리번호(unique id, 20자리)로 관리되어야 합니다.
  - 취소데이터는 결제데이터와 연결되어있어야 합니다.
  - 관리번호(unique id, 20자리)로 저장되어 있는 데이터를 조회할 수 있어야 합니다.
  - 관리번호로 데이터 조회시에는 결제 혹은 결제취소 데이터 1건만 조회합니다.



---

카드정보 암/복호화
- 암/복호화 방식 : DESede
- 카드 정보 구분자 처리 : ex. encrypt(카드정보|유효기간|cvc)

