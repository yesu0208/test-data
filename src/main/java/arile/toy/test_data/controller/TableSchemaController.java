package arile.toy.test_data.controller;

import arile.toy.test_data.domain.constant.ExportFileType;
import arile.toy.test_data.domain.constant.MockDataType;
import arile.toy.test_data.dto.request.TableSchemaExportRequest;
import arile.toy.test_data.dto.request.TableSchemaRequest;
import arile.toy.test_data.dto.response.SchemaFieldResponse;
import arile.toy.test_data.dto.response.SimpleTableSchemaResponse;
import arile.toy.test_data.dto.response.TableSchemaResponse;
import arile.toy.test_data.dto.security.GithubUser;
import arile.toy.test_data.service.SchemaExportService;
import arile.toy.test_data.service.TableSchemaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class TableSchemaController {

    private final TableSchemaService tableSchemaService;
    private final SchemaExportService schemaExportService;
    private final ObjectMapper mapper;

    // 비로그인을 하면 sample schema, 로그인 하고 schema 이름 알면 그 schema를 단건 조회
    @GetMapping("/table-schema")
    public String tableSchema(
            @AuthenticationPrincipal GithubUser githubUser,
            @RequestParam(required = false) String schemaName,
            Model model) {
        // tableSchema의 기본 화면 정보 값들
        TableSchemaResponse tableSchema = (githubUser != null && schemaName != null) ?
                TableSchemaResponse.fromDto(tableSchemaService.loadMySchema(githubUser.id(), schemaName)) :
                defaultTableSchema(schemaName);

        model.addAttribute("tableSchema", tableSchema);
        model.addAttribute("mockDataTypes", MockDataType.toObjects());
        model.addAttribute("fileTypes", Arrays.stream(ExportFileType.values()).toList());

        return "table-schema";
    }


    @PostMapping("/table-schema")
    public String createOrUpdateTableSchema(
            @AuthenticationPrincipal GithubUser githubUser,
            TableSchemaRequest tableSchemaRequest,
            RedirectAttributes redirectAttrs
    ) { // tableSchemaRequest를 form data로 받아 작업을 하고, "/table-schema"로 redirect할 때 이 정보를 전달하면 어떻까?
        // redirection하면서 열릴 페이지에 내가 만들었던 것 유지하고 싶다. -> 이를 위해 RedirectAttributes가 필요
        redirectAttrs.addAttribute("schemaName", tableSchemaRequest.schemaName());

        tableSchemaService.upsertTableSchema(tableSchemaRequest.toDto(githubUser.id()));

        return "redirect:/table-schema";
    }

    @GetMapping("/table-schema/my-schemas")
    public String mySchemas(
            @AuthenticationPrincipal GithubUser githubUser,
            Model model) {
        List<SimpleTableSchemaResponse> tableSchemas = tableSchemaService.loadMySchemas(githubUser.id())
                .stream()
                .map(SimpleTableSchemaResponse::fromDto)
                .toList();

        model.addAttribute("tableSchemas", tableSchemas);

        return "my-schemas";
    }


    @PostMapping("/table-schema/my-schemas/{schemaName}")
    public String deleteMySchema(
            @AuthenticationPrincipal GithubUser githubUser,
            @PathVariable String schemaName
    ) {
        tableSchemaService.deleteTableSchema(githubUser.id(), schemaName);
        return "redirect:/table-schema/my-schemas";
    }

    //@ResponseBody
    @GetMapping("/table-schema/export")
    public ResponseEntity<String> exportTableSchema(
            @AuthenticationPrincipal GithubUser githubUser,
            TableSchemaExportRequest tableSchemaExportRequest) {
        String body = schemaExportService.export(
                tableSchemaExportRequest.fileType(),
                tableSchemaExportRequest.toDto(githubUser != null ? githubUser.id() : null), // null인 경우 고려(로그인 안 한 경우)
                tableSchemaExportRequest.rowCount());

        String fileName = tableSchemaExportRequest.schemaName() + "." + tableSchemaExportRequest.fileType().name().toLowerCase();


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=table-schema.txt")
                .body(body); // TODO: 니증에 데이터 바꿔야 함
    }


    private String json(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException(jpe); // RuntimeException 으로 감싸서 내보내면 위로 전파해도 문제 x
        }
    }


    private TableSchemaResponse defaultTableSchema(String schemaName) {
        return new TableSchemaResponse(
                schemaName != null ? schemaName : "schema_name",
                "Arile",
                List.of(
                        new SchemaFieldResponse("id", MockDataType.ROW_NUMBER, 1, 0, null, null),
                        new SchemaFieldResponse("name", MockDataType.NAME, 2, 10, null, null),
                        new SchemaFieldResponse("age", MockDataType.NUMBER, 3, 20, null, null),
                        new SchemaFieldResponse("my_car", MockDataType.CAR, 4, 50, null, null)

                )
        );
    }

    private static List<SimpleTableSchemaResponse> mySampleSchemas() {
        return List.of(
                new SimpleTableSchemaResponse("schema_name1", "Arile", LocalDate.of(2024, 1, 1).atStartOfDay()),
                new SimpleTableSchemaResponse("schema_name2", "Arile", LocalDate.of(2024, 2, 2).atStartOfDay()),
                new SimpleTableSchemaResponse("schema_name3", "Arile", LocalDate.of(2024, 3, 3).atStartOfDay())
        );
    }
}
