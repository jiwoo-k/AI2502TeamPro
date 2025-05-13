package com.lec.spring.service;

import com.lec.spring.domain.Tag;

public interface TagService {

    int save (Tag tag);

    int update(Tag tag);

    int delete (Long id);




}
