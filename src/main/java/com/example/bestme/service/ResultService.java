package com.example.bestme.service;

import com.example.bestme.domain.Result;
import com.example.bestme.dto.api.ResultRequest;
import com.example.bestme.dto.api.ResultResponse;


import java.util.List;

public interface ResultService {
    //결과 생성
    Result createResult(Long userId, Long colorId, ResultRequest.CreateResultDTO createResultDTO);

    //결과 전체 조회
    List<Result> readResults(Long userId);

    //가장 많은 퍼스널 컬러 조회
    ResultResponse.ReadColorResponseDTO readColorId(Long userId);
}
