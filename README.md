# :movie_camera:  Review <br>


[주제 : 커뮤니티 과제 기반 "국내 영화 리뷰 작성 사이트"]

국내영화의 간략한 정보와 리뷰를 작성하여 국내 영화를 시청을 장려해 볼 수 있는 사이트
<br>
<br>

## 🗒 기능
<br>
<br>

### :sparkles: 회원 관련 기능 <br>


> 회원가입 및 로그인 : 이름, 전화번호, 이메일, ID, 비밀번호로 가입 (중복 ID 불가능 / ID 글자수 10자 제한)

       + 가입자 권한
      
           i. 영화 정보등록 (해당 영화의 리뷰 최초 등록자에 한해서만 진행)
                a. 영화의 제목
                b. 영화의 출시년도
                c. 카테고리 선택
                d. 감독
                e. 출연진 (3명까지로 제한/네이버 검색기준)
                f. 줄거리
           
           ii. 리뷰 등록
                a. 등록된 리뷰는 등록 순서대로 고유의 번호 ID를 부여
                b. 리뷰 글자수 300자 제한
                c. 영화 최초 등록시에 한해서만 'i. 영화 정보등록' 과 'ii. 리뷰 등록' 은 동시에 진행되며 
                   이미 등록된 영화 정보가 있는경우 리뷰 등록만 진행.
                   "이미 등록된 영화정보가 존재합니다. 리뷰 작성만 가능합니다." 멘트 제공
                
           iii. 리뷰 수정
                a. 리뷰 고유의 번호 ID를 통해 수정
                b. 리뷰 수정은 comment에 대해서만 가능하며 영화 정보등록에 대해서는 수정 불가능하다.
                
           iv. 리뷰 삭제
                a. 리뷰 고유의 번호 ID를 통해서 삭제
                
           v. 내가 작성한 리뷰 목록보기


       + 관리자 권한
      
           i. 영화 정보수정 (해당 영화의 리뷰 최초 등록자에 한해서만 진행)
                a. 최초 리뷰 등록자가 작성한 해당 영화의 정보가 잘못됐을 경우 관리자가 수정가능 
           
           ii. 리뷰 검열 단어 추가 / 등록
            
               
> 회원가입을 하지않은 경우 미디어 검색과 작성된 리뷰 읽기만 가능

> 탈퇴

       i. 탈퇴 이용자가 본인의 리뷰를 별도로 삭제하지않고 탈퇴시 리뷰 보존
      


<br>
<br>

### :sparkles: 미디어 검색 기능 <br>


> 전체 영화 목록을 1차 카테고리별로 나누어 제공 (장편 영화 / 단편 영화)

       i. 1차 카테고리 선택 후 이용자의 필요에 의해 2차 카테고리 선택가능하도록 제공 (영화 제목 기준 한글 / 영어) 
       ii. 카테고리를 선택하면 해당 영화의 전체 목록 제공

        
> 미디어 제목으로 검색 가능

       i. 해당 미디어의 전체 리뷰 목록 제공
       ii. 동일 제목의 미디어의 경우
              a. 년도로 구분할 수 있도록 함.
              b. "검색을 희망하는 미디어의 년도를 입력하세요." 멘트 제공 


> 출연배우 이름으로 검색 가능 (검색은 1명만)

       i. 해당 배우의 출연 미디어 목록 제공

> 모든 검색시 목록은 페이지 형식으로 제공하며 하나의 page 에는 최대 20개의 데이터 노출

> 기본 정렬 순서는 (한글 가나다 순 -> 영어 ABC 순)으로 제공



<br>
<br>

  
### :sparkles: 리뷰 검열 기능 <br>

> 등록된 리뷰에 특정 부정적인 단어가 포함되어있을 경우 검열/미등록 처리

      i. 미등록 처리시 "부정적인 언어의 사용으로 리뷰 등록이 불가능합니다." 멘트 제공

<br>
<br>

  
### :sparkles: 책갈피 기능 <br>

> 가능하다면 북마크 기능을 추가하여 나중에 볼 영화 목록을 저장하는 기능 제공

<br>
<br>

## :sunny: ERD

<br>
<br>


<img width="780" alt="스크린샷 2024-08-20 오후 7 06 59" src="https://github.com/user-attachments/assets/1d1e9ba5-ca45-4619-b9c7-729755e72be1">

수정 예정


연동






