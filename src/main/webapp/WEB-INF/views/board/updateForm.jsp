<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp" %>

<div class="container">
	<form onsubmit="update(event, ${boardEntity.id})">
	  <div class="form-group">
	    <input type="text" id="title" value="${boardEntity.title}" class="form-control" placeholder="Enter title">
	  </div>
	  <div class="form-group">
	  	<textarea id="content" class="form-control" rows="5">
	  		${boardEntity.content}
	  	</textarea>
	  </div>
	  <button type="submit" class="btn btn-primary">수정하기</button>
	</form>
</div>

  <script>
		async function update(event, id){
			event.preventDefault();
			// 주소 : PUT board/3
			let boardUpdateDto = {
				title: document.querySelecter('#title').value,
				content: document.querySelecter('#content').value
			};
			
			// JSON.stringify(자바스크립트 오브젝트) => 리턴 json 문자열
            // JSON.parse(제이슨 문자열) => 리턴 자바스크립트 함수

			let response = await fetch("http://localhost:8080/board/" + id,{
				method: "put",
				body: JSON.stringify(boardUpdateDto),
				headers:{
					"Content-Type":"application/json; charset=utf-8"
				}
			});
			
			let parseResponse = await response.json();
			
			if(parseResponse.code == 1){
				alert("업데이트 성공");
				location.href = "/board/" + id;
			} else {
				alert("업데이트 실패 - " + parseResponse.msg);
			}
		}
  
        $('#content').summernote({
             height: 350
        });
  </script>
<%@ include file="../layout/footer.jsp" %>



    