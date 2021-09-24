<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp" %>

<div class="container">
	<form> 
		<div class="form-group">
			<input type="text" value="${sessionScope.principal.username}" name="username" class="form-control" placeholder="Enter username" required maxlength="20" readonly>
		</div>
		<div class="form-group">
			<input type="password" "${sessionScope.principal.password}"  name="password" class="form-control"	placeholder="Enter password" required maxlength="20">
		</div>
		<div class="form-group">
			<input type="email" "${sessionScope.principal.email}"  name="email" class="form-control"	placeholder="Enter email" required maxlength="50">
		</div>
		<button type="submit" class="btn btn-primary">회원수정</button>
	</form>
</div>

<%@ include file="../layout/footer.jsp" %>
