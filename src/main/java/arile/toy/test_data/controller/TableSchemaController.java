package arile.toy.test_data.controller;

import arile.toy.test_data.dto.request.TableSchemaRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TableSchemaController {

    @GetMapping("/table-schema")
    public String tableSchema(TableSchemaRequest tableSchemaRequest) {
        return "table-schema";
    }

    @PostMapping("/table-schema")
    public String createOrUpdateTableSchema(
            TableSchemaRequest tableSchemaRequest,
            RedirectAttributes redirectAttrs
    ) { // tableSchemaRequest를 form data로 받아 작업을 하고, "/table-schema"로 redirect할 때 이 정보를 전달하면 어떻까?
        // redirection하면서 열릴 페이지에 내가 만들었던 것 유지하고 싶다. -> 이를 위해 RedirectAttributes가 필요
        redirectAttrs.addFlashAttribute("tableSchemaRequest", tableSchemaRequest);

        return "redirect:/table-schema";
    }

    @GetMapping("/table-schema/my-schemas")
    public String mySchemas() {
        return "my-schemas";
    }

    @PostMapping("/table-schema/my-schemas/{schemaName}")
    public String deleteMySchema(@PathVariable String schemaName,
                                 RedirectAttributes redirectAttrs
    ) {
        return "redirect:/my-schemas";
    }

    //@ResponseBody
    @GetMapping("/table-schema/export")
    public ResponseEntity<String> exportTableSchema(TableSchemaRequest tableSchemaRequest) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=table-schema.txt")
                .body("download complete!"); // TODO: 니증에 데이터 바꿔야 함
    }
}
