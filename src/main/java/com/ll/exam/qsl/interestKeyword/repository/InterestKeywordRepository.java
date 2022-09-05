package com.ll.exam.qsl.interestKeyword.repository;

import com.ll.exam.qsl.interestKeyword.entity.InterestKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestKeywordRepository extends JpaRepository<InterestKeyword,String> {
    List<InterestKeyword> findByUser_Id(long id);
}
