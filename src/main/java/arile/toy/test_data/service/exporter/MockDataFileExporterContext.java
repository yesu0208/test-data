package arile.toy.test_data.service.exporter;

import arile.toy.test_data.domain.constant.ExportFileType;
import arile.toy.test_data.dto.TableSchemaDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MockDataFileExporterContext {

    // Service bean이 등록 될 때 Mao을 한 번 초기화 -> 이때 빈들을 불러와서 생성자로 주입됨.

    private final Map<ExportFileType, MockDataFileExporter> mockDataFileExporterMap;

    public MockDataFileExporterContext(List<MockDataFileExporter> mockDataFileExporters){
        this.mockDataFileExporterMap = mockDataFileExporters.stream()
                .collect(Collectors.toMap(MockDataFileExporter::getType, Function.identity()));
    }

    public String export(ExportFileType fileType, TableSchemaDto dto, Integer rowCount) {
        MockDataFileExporter fileExporter = mockDataFileExporterMap.get(fileType);

        return fileExporter.export(dto, rowCount);
    }



}
