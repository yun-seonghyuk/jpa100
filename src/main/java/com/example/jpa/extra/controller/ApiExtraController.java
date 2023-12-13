package com.example.jpa.extra.controller;

import com.example.jpa.common.message.ResponseResult;
import com.example.jpa.extra.model.AirInput;
import com.example.jpa.extra.model.OpenApiResult;
import com.example.jpa.extra.model.PharmacySearch;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiExtraController {

    /*
    * 86. restTemplate을 이용한 공공데이더 토털의 공공api 연동하여 전국 약국목록을 가져오는 api
    * */
    @GetMapping("/extra/pharmacy")
    public String pharmacy(){
        String apiKey = "nqNLNu1cY6ZLQGqoRzDwjFcPWvheCkhtfU%2FQ05Zcr1AiEl5%2B4EhAbYZcR7L68wjAZW4FeKima%2BfP4FOxa6EyNQ%3D%3D";
        String url = "https://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10";
        String apiResult = "";

        try {
            URI uri = new URI(String.format(url, apiKey));
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            apiResult = restTemplate.getForObject(uri, String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }

    /*
     * 87. restTemplate을 이용한 공공데이더 토털의 공공api 연동하여 전국 약국목록을 가져오는 api
     * 결과데이터를 모델로 매핑하여 처리
     * */

    @GetMapping("/extra/pharmacy2")
    public ResponseEntity<?> pharmacy2(){
        String apiKey = "nqNLNu1cY6ZLQGqoRzDwjFcPWvheCkhtfU%2FQ05Zcr1AiEl5%2B4EhAbYZcR7L68wjAZW4FeKima%2BfP4FOxa6EyNQ%3D%3D";
        String url = "https://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10";
        String apiResult = "";

        try {
            URI uri = new URI(String.format(url, apiKey));
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            apiResult = restTemplate.getForObject(uri, String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        OpenApiResult jsonResult = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            jsonResult = objectMapper.readValue(apiResult, OpenApiResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseResult.success(jsonResult);
    }

    /*
    * 88. 회원가입후 이용가능
    * 전국 약국 정보 조회서비스 키워드 검색이후 활용신청후 조회가능
    * 시도/구군 단위 검색기능에 대한 구현을 추가
    * 결과 데이터를 모델로 매핑하여 처리
    * */
    @GetMapping("/extra/pharmacy3")
    public ResponseEntity<?> pharmacy3(@RequestBody PharmacySearch pharmacySearch){
        String apiKey = "nqNLNu1cY6ZLQGqoRzDwjFcPWvheCkhtfU%2FQ05Zcr1AiEl5%2B4EhAbYZcR7L68wjAZW4FeKima%2BfP4FOxa6EyNQ%3D%3D";
        String url = String.format("https://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyFullDown?serviceKey=%s&pageNo=1&numOfRows=10",apiKey);
        String apiResult = "";

        try {
            url += String.format("&Q0=%s&Q1=%s"
                    , URLEncoder.encode(pharmacySearch.getSearchSido(), StandardCharsets.UTF_8)
                    , URLEncoder.encode(pharmacySearch.getSearchGugun(), StandardCharsets.UTF_8));

            URI uri = new URI(url);
            log.info(url);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            apiResult = restTemplate.getForObject(uri, String.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        OpenApiResult jsonResult = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            jsonResult = objectMapper.readValue(apiResult, OpenApiResult.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return ResponseResult.success(jsonResult);
    }


    /*
    * 89. 미세먼지 정보제공
    * */
    @GetMapping("/extra/air")
    public String air(@RequestBody AirInput airInput){
        String apiKey = "nqNLNu1cY6ZLQGqoRzDwjFcPWvheCkhtfU%2FQ05Zcr1AiEl5%2B4EhAbYZcR7L68wjAZW4FeKima%2BfP4FOxa6EyNQ%3D%3D";
        String url = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=%s&pageNo=1&numOfRows=10&sidoName=%s";
        String apiResult = "";

        try {
            URI uri = new URI(String.format(url, apiKey, URLEncoder.encode(airInput.getSearchSido(), StandardCharsets.UTF_8)));
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            apiResult = restTemplate.getForObject(uri, String.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return apiResult;
    }

}
