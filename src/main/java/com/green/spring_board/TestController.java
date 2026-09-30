package com.green.spring_board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 해당 클래스를 API 전용으로 설정함
@RestController
// 클래스 아래 작성될 모든 API의 부모 URL을 설정
// 해당 글래스 아래 작성되는 모든 API의 URL은 무조건 /api/test/{만든API} 로 지정됨
@RequestMapping("/api/test") // 이쪽으로 들어오는 모든 요청들을 TestController가 처리한다

public class TestController {

    @GetMapping
    public String test() {
        return "Hello World";
    }

}
