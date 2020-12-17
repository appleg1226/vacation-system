# 휴가 신청 시스템

시나리오 및 프로젝트 설명은 [SCRIPT.md](SCRIPT.md)에 있습니다. 

## Open Endpoints
* 로그인: `POST /login`

  * request body(example)
  
  ```
  {
    "id": "testUser",
    "password": "1q2w3e4r"
  }
  ```
  
  * response body(example)
  
  ```
  {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTYwNjY3MDUwNiwiZXhwIjoxNjA2Njc0MTA2fQ._Zj9QBHRdKJazM_D_MIDBb6qp7nR3MxK-W9Bl4qXrUM"
  }
  ```

## Endpoints that require Authentication

### 휴일 관련

* 공휴일 추가: `POST /holiday/add X-AUTH-TOKEN: {token}`

  * request body(example)
  
  ```
  {
    "year": "2020",
    "content": "성탄절",
    "localDate": "2020-12-25"
  }
  ```
  
  * response body(example)
  
  ```
    "2020-12-25 added to system"
  ```
    
* 공휴일 조회: `GET /holiday/:year X-AUTH-TOKEN: {token}`

  * response body(example)
  
  ```
  [
    {"id":1,"year":2020,"content":"설연휴","localDate":"2020-01-24"},
    {"id":2,"year":2020,"content":"설연휴","localDate":"2020-01-27"},
    {"id":3,"year":2020,"content":"선거","localDate":"2020-04-15"},
    {"id":4,"year":2020,"content":"석가탄신일","localDate":"2020-04-30"},
    {"id":5,"year":2020,"content":"어린이날","localDate":"2020-05-05"},
    ...
  ]
  ```

### 휴가 관련

* 휴가 신청: `POST /vacation/register/:userId X-AUTH-TOKEN: {token}`

  * request body(example)
  
  ```
  {
    "type": "FULL", ("FULL(연차)", HALF(반차)", "HALFHALF(반반차)" 셋 중 하나)
    "comment": "연차 사용",
    "startDate": "2020-04-01",
    "endDate": "2020-04-03" ("반차/반반차는 생략)
  }
  ```
  
  * response body(example)
  
  ```
  {
    "left_vacation": "5.0"
  }
  ```
  
* 남은 휴가 날짜 조회: `GET /vacation/day/:userId/:year X-AUTH-TOKEN: {token}`

  * response body(example)
  
  ```
  {
    "left_vacation": "12.0"
  }
  ```
  
* 신청한 휴가 목록 조회: `GET /vacation/list/:userId/:year X-AUTH-TOKEN: {token}`

  * response body(example)
  
  ```
  [
    {"id":1,"type":"FULL","comment":"쉬고싶어요!","startDate":"2020-01-03","endDate":"2020-01-05","registerDate":"2020-11-30T02:41:11"},
    {"id":2,"type":"FULL","comment":"쉬고싶어요!","startDate":"2020-07-10","endDate":"2020-07-13","registerDate":"2020-11-30T02:41:11"},
    {"id":3,"type":"FULL","comment":"연말휴가","startDate":"2020-12-24","endDate":"2020-12-29","registerDate":"2020-11-30T02:41:11"}
  ]
  ```
  
* 휴가 내용 조회: `GET /vacation/:vacationId X-AUTH-TOKEN: {token}`

  * response body(example)
  
  ```
  {
    "id":1,
    "type":"FULL", 
    "comment":"쉬고싶어요!",
    "startDate":"2020-01-03",
    "endDate":"2020-01-05",
    "registerDate":"2020-11-30T02:41:11"
  }
  ```
  
* 휴가 취소: `DELETE /vacation/cancel/:vacationId X-AUTH-TOKEN: {token}`

  * response body(example)
  
  ```
  {
    "left_vacation": "11.0"
  }
  ```
