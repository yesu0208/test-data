package arile.toy.test_data.controller;

import arile.toy.test_data.config.SecurityConfig;
import arile.toy.test_data.domain.constant.ExportFileType;
import arile.toy.test_data.domain.constant.MockDataType;
import arile.toy.test_data.dto.TableSchemaDto;
import arile.toy.test_data.dto.request.SchemaFieldRequest;
import arile.toy.test_data.dto.request.TableSchemaExportRequest;
import arile.toy.test_data.dto.request.TableSchemaRequest;
import arile.toy.test_data.dto.security.GithubUser;
import arile.toy.test_data.service.TableSchemaService;
import arile.toy.test_data.util.FormDataEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@Disabled("아직 테스트만 다루므로 테스트를 먼저 작성함. 테스트의 스펙을 전달하고, 아직 구현이 없으므로 빟활성화.")
@DisplayName("[Controller] 테이블 스키마 컨트룰러 테스트")
@Import({SecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(TableSchemaController.class)
class TableSchemaControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private FormDataEncoder formDataEncoder;
    @Autowired private ObjectMapper mapper;

    @MockitoBean private TableSchemaService tableSchemaService;


    // String parameter(schemaName)가 안 오는 경우(비로그인)
    @DisplayName("[GET] 테이블 스키마 페이지 -> 테이블 스키마 뷰 (정상)")
    @Test
    void givenNothing_whenRequesting_thenShowsTableSchemaView() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/table-schema"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("tableSchema"))
                .andExpect(model().attributeExists("mockDataTypes"))
                .andExpect(model().attributeExists("fileTypes"))
                .andExpect(view().name("table-schema"));
        then(tableSchemaService).shouldHaveNoInteractions();
    }
    // String parameter(schemaName가 오는 경우(로그인)
    @DisplayName("[GET] 테이블 스키마 조회, 로그인 -> 특정 테이블 스키마 (정상)")
    @Test
    void givenAuthenticatedUserAndSchemaName_whenRequesting_thenShowsTableSchemaView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        var schemaName = "test_schema";
        given(tableSchemaService.loadMySchema(githubUser.id(), schemaName)).willReturn(TableSchemaDto.of(schemaName, githubUser.id(), null, Set.of()));

        // When & Then
        mvc.perform(
                get("/table-schema")
                        .queryParam("schemaName", schemaName)
                        .with(oauth2Login().oauth2User(githubUser))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("tableSchema"))
//                .andExpect(model().attribute("tableSchema", hasProperty("schemaName", is(schemaName)))) // dto가 record여서 불가능한 방식
                .andExpect(model().attributeExists("mockDataTypes"))
                .andExpect(model().attributeExists("fileTypes"))
                .andExpect(content().string(containsString(schemaName))) // html 전체 검사하므로 정확하지 않은 테스트 방식 (위의 테스트를 못하므로, 간접적으로)
                .andExpect(view().name("table-schema"));
        then(tableSchemaService).should().loadMySchema(githubUser.id(), schemaName);
    }

    @DisplayName("[POST] 테이블 스키마 생성, 변경 (정상)") // 한 페이지에서, 저장되지 않은 경우는 새롭게 생성하고, 저장한 것을 불러온 경우라면 수정하고 : ui는 동일한데
    @Test
    void givenTableSchemaRequest_whenCreatingOrUpdating_thenRedirectsToTableSchemaView() throws Exception { // redirect : 저장, 수정 후 table schema view로 돌아올 것
        // Given (테이블 스키마 request 생성)
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        TableSchemaRequest request = TableSchemaRequest.of(
                "test-schema",
                List.of(
                        SchemaFieldRequest.of("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                        SchemaFieldRequest.of("name", MockDataType.NAME, 2, 10, null, null),
                        SchemaFieldRequest.of("age", MockDataType.NUMBER, 3, 20, null, null)
                )
        );
        willDoNothing().given(tableSchemaService).upsertTableSchema(request.toDto(githubUser.id()));

        // When & Then
        mvc.perform(post("/table-schema")
                        .content(formDataEncoder.encode(request)) // post에 data가 들어갈 것 : 여기는 나중에 바꿔야 함. (변경 완료)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED) // APPLICATION_JSON type이 아닌 form 요청
                        .with(csrf()) // 이 POST 요청은 자동으로 csrf정보가 포함해서 들어가게 될 것.
                        .with(oauth2Login().oauth2User(githubUser))
                )
                .andExpect(status().is3xxRedirection()) // 3xx : 정상 응답이지만, redirection이 일어났다는 http status code
                //.andExpect(flash().attribute("tableSchemaRequest", request)) // FlashAttribute 검증
                .andExpect(redirectedUrlTemplate("/table-schema?schemaName={schemaName}", request.schemaName()));
        then(tableSchemaService).should().upsertTableSchema(request.toDto(githubUser.id()));
    }

    // 1
    @DisplayName("[GET] 내 스키마 목록 페이지 -> 내 스키마 목록 뷰 (정상)")
    @Test
    void givenAuthenticatedUser_whenRequesting_thenShowsMySchemaView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        given(tableSchemaService.loadMySchemas(githubUser.id())).willReturn(List.of());
        // When & Then
        mvc.perform(get("/table-schema/my-schemas")
                        .with(oauth2Login().oauth2User(githubUser))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect((model().attribute("tableSchemas", List.of())))
                .andExpect(view().name("my-schemas"));
        then(tableSchemaService).should().loadMySchemas(githubUser.id());
    }

    // 2
    @DisplayName("[GET] 내 스키마 목록 페이지 -> 내 스키마 목록 뷰 (비로그인 상태에서는?) -> redirection")
    @Test
    void givenNothing_whenRequesting_thenRedirectsToLogin() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/table-schema/my-schemas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/oauth2/authorization/github"));
        then(tableSchemaService).shouldHaveNoInteractions();
    }


    @DisplayName("[POST] 내 스키마 삭제 (정상)") // DELETE 안쓰고 POST 쓸 것
    @Test
    void givenAuthenticatedUserAndSchemaName_whenDeleting_thenRedirectsToTableSchemaView() throws Exception {
        // Given
        var githubUser = new GithubUser("test-id", "test-name", "test@email.com");
        String schemaName = "test_schema";

        // When & Then
        mvc.perform(post("/table-schema/my-schemas/{schemaName}", schemaName)
                        .with(csrf())
                        .with(oauth2Login().oauth2User(githubUser))
                )
                .andExpect(status().is3xxRedirection()) // 3xx : 정상 응답이지만, redirection이 일어났다는 http status code
                .andExpect(redirectedUrl("/table-schema/my-schemas"));
    }

    @DisplayName("[GET] 테이블 스키마 파일 다운로드 -> 테이블 스키마 파일 (정상)")
    @Test
    void givenTableSchema_whenDownloading_thenReturnsFile() throws Exception {
        // Given
        TableSchemaExportRequest request = TableSchemaExportRequest.of(
                "test",
                77,
                ExportFileType.JSON,
                List.of(
                        SchemaFieldRequest.of("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                        SchemaFieldRequest.of("name", MockDataType.STRING, 1, 0, "option", "well"),
                        SchemaFieldRequest.of("age", MockDataType.NUMBER, 3, 20, null, null)
                )
        );
        String queryParam = formDataEncoder.encode(request, false);
        // When & Then
        mvc.perform(get("/table-schema/export?" + queryParam))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN)) // csv, tsv든 모두 plain type
                // "Content-Disposition" 헤더에 "attachment; filename=table-schema.txt"이 들어있어야. (파일은 table-schema.txt으로 나올 것)
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=table-schema.txt")) // 파일 다운로드 하려면 헤더에 반드시 추가되어야 하는 내용
                .andExpect(content().json(mapper.writeValueAsString(request))); // TODO: 나중에 데이터 바꿔야 함
    }
}
