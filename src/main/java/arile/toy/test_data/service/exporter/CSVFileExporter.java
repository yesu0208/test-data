package arile.toy.test_data.service.exporter;

import arile.toy.test_data.domain.constant.ExportFileType;
import arile.toy.test_data.service.generator.MockDataGeneratorContext;
import org.springframework.stereotype.Component;

@Component
public class CSVFileExporter extends DelimiterBasedFileExporter implements MockDataFileExporter {

    public CSVFileExporter(MockDataGeneratorContext mockDataGeneratorContext) {
        super(mockDataGeneratorContext);
    }

    @Override
    public ExportFileType getType() {
        return ExportFileType.CSV;
    }

    @Override
    public String getDelimiter() {
        return ",";
    }
}
