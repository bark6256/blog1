package com.cos.blogapp.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// save() 인서트, 업데이트를 해준다(primary key 값이 같으면 업데이트.
// findById(1)		: PK가 1인것 검색
// findAll				: 전체 검색
// deleteById(1)	: PK가 1인것 삭제
// DAO
//@Repository  <- 내부적으로 부모 클래스에서 적용되어있다.
public interface UserRepository extends JpaRepository<User, Integer>{
	
	@Query(value = "insert into user (username, password, email) values (:username, :password, :email)", nativeQuery = true)
	void join(String username, String password, String email);
	
	@Query(value = "select * from user where username = :username and password = :password", nativeQuery = true)
	User mLogin(String username, String password);
}
