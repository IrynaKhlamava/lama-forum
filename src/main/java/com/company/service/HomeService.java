package com.company.service;

import com.company.dto.HomePageDto;

public interface HomeService {

    HomePageDto getHomePageData(int page, int size, String message);

}
