package arile.toy.test_data.controller;

import arile.toy.test_data.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("[Controller] 메인 컨트룰러 테스트")
@Import(SecurityConfig.class)
@WebMvcTest(MainController.class)
record MainControllerTest(
        @Autowired MockMvc mvc
) {

    @DisplayName("[GET] 메인(루트) 페이지 -> 메인 뷰 (정상)")
    @Test
    void givenNothing_whenEnteringRootPage_thenShowsMainView() throws Exception {
        // Given

        // When & Then : controller test(WebMvcTest에서 이렇게 쓴다)
        mvc.perform(get("/")) // https://my-service.com/
                .andExpect(status().isOk()) // 200 ok
                //.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)) // 자료 형식은 TEXT_HTML
                //.andExpect(view().name("index")); // view 이름은 index
                .andExpect(forwardedUrl("/table-schema"));
                //.andDo(print()); // 로그 출력용(정보 너무 많으니 뺀다)
    }
}